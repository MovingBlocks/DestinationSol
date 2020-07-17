package org.destinationsol.assets.ui;

import com.google.common.base.Charsets;
import com.google.common.primitives.UnsignedInts;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.fonts.UIFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.format.AbstractAssetFileFormat;
import org.terasology.gestalt.assets.format.AssetDataFile;
import org.terasology.gestalt.assets.module.annotations.RegisterAssetFileFormat;
import org.terasology.nui.Color;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.UIWidget;
import org.terasology.nui.asset.font.Font;
import org.terasology.nui.skin.UISkin;
import org.terasology.nui.skin.UISkinBuilder;
import org.terasology.nui.skin.UISkinData;
import org.terasology.nui.skin.UIStyleFragment;
import org.terasology.reflection.metadata.ClassLibrary;
import org.terasology.reflection.metadata.ClassMetadata;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * NOTE: Taken from Terasology for compatibility with NUI JSON files.
 */
@RegisterAssetFileFormat
public class UISkinFormat extends AbstractAssetFileFormat<UISkinData> {

    private static final Logger logger = LoggerFactory.getLogger(UISkinFormat.class);
    private Gson gson;
    private static ClassLibrary<UIWidget> widgetClassLibrary;

    public UISkinFormat(ClassLibrary<UIWidget> widgetClassLibrary) {
        super("skin");
        gson = new GsonBuilder()
                .registerTypeAdapter(UISkinData.class, new UISkinTypeAdapter())
                .registerTypeAdapterFactory(new CaseInsensitiveEnumTypeAdapterFactory())
                .registerTypeAdapter(UITextureRegion.class, new TextureRegionTypeAdapter())
                .registerTypeAdapter(Optional.class, new OptionalTextureRegionTypeAdapter())
                .registerTypeAdapter(Font.class, new FontTypeAdapter())
                .registerTypeAdapter(Color.class, new ColorTypeHandler())
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .create();
        UISkinFormat.widgetClassLibrary = widgetClassLibrary;
    }

    @Override
    public UISkinData load(ResourceUrn urn, List<AssetDataFile> inputs) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(inputs.get(0).openStream(), Charsets.UTF_8))) {
            reader.setLenient(true);
            UISkinData data = gson.fromJson(reader, UISkinData.class);
            data.setSource(inputs.get(0));
            return data;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new IOException("Failed to load skin '" + urn + "'", e);
        }
    }

    public UISkinData load(JsonElement element) throws IOException {
        return gson.fromJson(element, UISkinData.class);
    }

    public static class TextureRegionTypeAdapter implements JsonDeserializer<UITextureRegion> {

        @Override
        public UITextureRegion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String uri = json.getAsString();
            return Assets.getDSTexture(uri).getUiTexture();
        }
    }

    private static class UISkinTypeAdapter implements JsonDeserializer<UISkinData> {
        @Override
        public UISkinData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                UISkinBuilder builder = new UISkinBuilder();
                DefaultInfo defaultInfo = null;
                defaultInfo = context.deserialize(json, DefaultInfo.class);
                defaultInfo.apply(builder);
                return builder.build();
            }
            return null;
        }
    }

    private static class DefaultInfo extends FamilyInfo {
        public String inherit;
        public Map<String, FamilyInfo> families;

        @Override
        public void apply(UISkinBuilder builder) {
            super.apply(builder);
            if (inherit != null) {
                Optional<? extends UISkin> skin = Assets.getAssetHelper().get(new ResourceUrn(inherit), UISkin.class);
                if (skin.isPresent()) {
                    builder.setBaseSkin(skin.get());
                }
            }
            if (families != null) {
                for (Map.Entry<String, FamilyInfo> entry : families.entrySet()) {
                    builder.setFamily(entry.getKey());
                    entry.getValue().apply(builder);
                }
            }
        }
    }

    private static class FamilyInfo extends StyleInfo {
        public Map<String, ElementInfo> elements;

        public void apply(UISkinBuilder builder) {
            super.apply(builder);
            if (elements != null) {
                for (Map.Entry<String, ElementInfo> entry : elements.entrySet()) {
                    ClassLibrary<UIWidget> library = widgetClassLibrary;
                    ClassMetadata<? extends UIWidget, ?> metadata = library.resolve(entry.getKey());
                    if (metadata != null) {
                        builder.setElementClass(metadata.getType());
                        entry.getValue().apply(builder);
                    } else {
                        logger.warn("Failed to resolve UIWidget class {}, skipping style information", entry.getKey());
                    }

                }
            }
        }
    }

    private static class PartsInfo extends StyleInfo {
        public Map<String, StyleInfo> modes;

        public void apply(UISkinBuilder builder) {
            super.apply(builder);
            if (modes != null) {
                for (Map.Entry<String, StyleInfo> entry : modes.entrySet()) {
                    builder.setElementMode(entry.getKey());
                    entry.getValue().apply(builder);
                }
            }
        }
    }

    private static class ElementInfo extends StyleInfo {
        public Map<String, PartsInfo> parts;
        public Map<String, StyleInfo> modes;

        public void apply(UISkinBuilder builder) {
            super.apply(builder);
            if (modes != null) {
                for (Map.Entry<String, StyleInfo> entry : modes.entrySet()) {
                    builder.setElementMode(entry.getKey());
                    entry.getValue().apply(builder);
                }
            }
            if (parts != null) {
                for (Map.Entry<String, PartsInfo> entry : parts.entrySet()) {
                    builder.setElementPart(entry.getKey());
                    entry.getValue().apply(builder);
                }
            }
        }
    }

    private static class StyleInfo extends UIStyleFragment {

        private void apply(UISkinBuilder builder) {
            builder.setStyleFragment(this);
        }
    }

    public static class OptionalTextureRegionTypeAdapter implements JsonDeserializer<Optional<?>> {
        @Override
        public Optional<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String name = json.getAsString();
            if (name.isEmpty()) {
                return Optional.empty();
            }

            if (!name.contains(":")) {
                name = "engine:" + name;
            }
            return Optional.of(Assets.getDSTexture(name).getUiTexture());
        }
    }

    public static class FontTypeAdapter implements JsonDeserializer<Font> {
        @Override
        public Font deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String name = json.getAsString();
            if (!name.contains(":")) {
                name = "engine:" + name;
            }
            return new UIFont(Assets.getFont(name));
        }
    }

    /**
     * Serializes {@link Color} instances to an int array <code>[r, g, b, a]</code>.
     * De-serializing also supports hexadecimal strings such as <code>"AAAAAAFF"</code>.
     */
    public static class ColorTypeHandler implements JsonDeserializer<Color> {
        @Override
        public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                return new Color(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat());
            }
            if (json.isJsonPrimitive()) {
                // NOTE: Integer.parseUnsignedInt is not available on Android API 24 (7.0).
                //       Since we're still pulling-in Guava, we use it's equivalent.
                return new Color(UnsignedInts.parseUnsignedInt(json.getAsString(), 16));
            }

            return null;
        }
    }
}
