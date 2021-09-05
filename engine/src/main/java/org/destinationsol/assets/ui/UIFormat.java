/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.assets.ui;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.destinationsol.assets.Assets;
import org.joml.Vector2i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.format.AbstractAssetFileFormat;
import org.terasology.gestalt.assets.format.AssetDataFile;
import org.terasology.gestalt.assets.management.AssetManager;
import org.terasology.gestalt.assets.module.annotations.RegisterAssetFileFormat;
import org.terasology.input.Keyboard;
import org.terasology.nui.Color;
import org.terasology.nui.LayoutHint;
import org.terasology.nui.UILayout;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.UIWidget;
import org.terasology.nui.asset.UIData;
import org.terasology.nui.asset.font.Font;
import org.terasology.nui.reflection.WidgetLibrary;
import org.terasology.nui.skin.UISkin;
import org.terasology.nui.skin.UISkinAsset;
import org.terasology.nui.widgets.UILabel;
import org.terasology.reflection.ReflectionUtil;
import org.terasology.reflection.metadata.ClassMetadata;
import org.terasology.reflection.metadata.FieldMetadata;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * Handles loading UI widgets from json format files.
 * NOTE: Taken from Terasology for compatibility with NUI JSON files.
 */
@RegisterAssetFileFormat
public class UIFormat extends AbstractAssetFileFormat<UIData> {

    public static final String CONTENTS_FIELD = "contents";
    public static final String LAYOUT_INFO_FIELD = "layoutInfo";
    public static final String ID_FIELD = "id";
    public static final String TYPE_FIELD = "type";

    private static final Logger logger = LoggerFactory.getLogger(UIFormat.class);
    private static WidgetLibrary library;

    public UIFormat(WidgetLibrary library) {
        super("ui");

        UIFormat.library = library;
    }

    @Override
    public UIData load(ResourceUrn resourceUrn, List<AssetDataFile> inputs) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(inputs.get(0).openStream(), Charsets.UTF_8))) {
            reader.setLenient(true);
            UIData data = load(new JsonParser().parse(reader));
            data.setSource(inputs.get(0));
            return data;
        }
    }

    public UIData load(JsonElement element) throws IOException {
        return load(element, null);
    }

    public UIData load(JsonElement element, Locale otherLocale) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapterFactory(new CaseInsensitiveEnumTypeAdapterFactory())
                .registerTypeAdapter(UIData.class, new UIDataTypeAdapter())
                .registerTypeAdapter(UISkin.class, new JsonDeserializer<UISkin>() {
                    @Override
                    public UISkin deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return Assets.getAssetHelper().get(new ResourceUrn(json.getAsString()), UISkinAsset.class).get().getSkin();
                    }
                })
                .registerTypeAdapter(UITextureRegion.class, new UISkinFormat.TextureRegionTypeAdapter())
                .registerTypeAdapter(Optional.class, new UISkinFormat.OptionalTextureRegionTypeAdapter())
                .registerTypeAdapter(Font.class, new UISkinFormat.FontTypeAdapter())
                .registerTypeAdapter(Color.class, new UISkinFormat.ColorTypeHandler())
                .registerTypeAdapter(Vector2i.class, new Vector2iTypeAdaptor())
                .registerTypeAdapter(Keyboard.Key.class, new KeyboardKeyTypeAdapter())
                .registerTypeHierarchyAdapter(UIWidget.class, new UIWidgetTypeAdapter());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(element, UIData.class);
    }

    /**
     * Load UIData with a single, root widget
     */
    private static final class UIDataTypeAdapter implements JsonDeserializer<UIData> {

        @Override
        public UIData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new UIData((UIWidget) context.deserialize(json, UIWidget.class));
        }
    }

    private static final class Vector2iTypeAdaptor implements JsonDeserializer<Vector2i> {

        @Override
        public Vector2i deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            return new Vector2i(array.get(0).getAsInt(), array.get(1).getAsInt());
        }
    }

    /**
     * Loads a widget. This requires the following custom handling:
     * <ul>
     * <li>The class of the widget is determined through a URI in the "type" attribute</li>
     * <li>If the "id" attribute is present, it is passed to the constructor</li>
     * <li>If the widget is a layout, then a "contents" attribute provides a list of widgets for content.
     * Each contained widget may have a "layoutInfo" attribute providing the layout hint for its container.</li>
     * </ul>
     */
    private static final class UIWidgetTypeAdapter implements JsonDeserializer<UIWidget> {
        @Override
        public UIWidget deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                return new UILabel(json.getAsString());
            }

            JsonObject jsonObject = json.getAsJsonObject();

            String type = jsonObject.get(TYPE_FIELD).getAsString();
            ClassMetadata<? extends UIWidget, ?> elementMetadata = library.resolve(type);
            if (elementMetadata == null) {
                logger.error("Unknown UIWidget type {}", type);
                return null;
            }

            String id = null;
            if (jsonObject.has(ID_FIELD)) {
                id = jsonObject.get(ID_FIELD).getAsString();
            }

            UIWidget element = elementMetadata.newInstance();
            if (id != null) {
                FieldMetadata<?, ?> fieldMetadata = elementMetadata.getField(ID_FIELD);
                if (fieldMetadata == null) {
                    logger.warn("UIWidget type {} lacks id field", elementMetadata.getUri());
                } else {
                    fieldMetadata.setValue(element, id);
                }
            }

            // Deserialize normal fields.
            Set<String> unknownFields = new HashSet<>();
            for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String name = entry.getKey();
                if (!ID_FIELD.equals(name)
                    && !CONTENTS_FIELD.equals(name)
                    && !TYPE_FIELD.equals(name)
                    && !LAYOUT_INFO_FIELD.equals(name)) {
                    unknownFields.add(name);
                }
            }

            for (FieldMetadata<? extends UIWidget, ?> field : elementMetadata.getFields()) {
                if (jsonObject.has(field.getSerializationName())) {
                    unknownFields.remove(field.getSerializationName());
                    if (field.getName().equals(CONTENTS_FIELD) && UILayout.class.isAssignableFrom(elementMetadata.getType())) {
                        continue;
                    }
                    try {
                        if (List.class.isAssignableFrom(field.getType())) {
                            Type contentType = ReflectionUtil.getTypeParameter(field.getField().getGenericType(), 0);
                            if (contentType != null) {
                                List<Object> result = Lists.newArrayList();
                                JsonArray list = jsonObject.getAsJsonArray(field.getSerializationName());
                                for (JsonElement item : list) {
                                    result.add(context.deserialize(item, contentType));
                                }
                                field.setValue(element, result);
                            }
                        } else {
                            field.setValue(element, context.deserialize(jsonObject.get(field.getSerializationName()), field.getType()));
                        }
                    } catch (RuntimeException e) {
                        logger.error("Failed to deserialize field {} of {}", field.getName(), type, e);
                    }
                }
            }

            for (String key : unknownFields) {
                logger.warn("Field '{}' not recognized for {} in {}", key, typeOfT, json);
            }

            // Deserialize contents and layout hints
            if (UILayout.class.isAssignableFrom(elementMetadata.getType())) {
                UILayout<LayoutHint> layout = (UILayout<LayoutHint>) element;

                Class<? extends LayoutHint> layoutHintType = (Class<? extends LayoutHint>)
                    ReflectionUtil.getTypeParameter(elementMetadata.getType().getGenericSuperclass(), 0);
                if (jsonObject.has(CONTENTS_FIELD)) {
                    for (JsonElement child : jsonObject.getAsJsonArray(CONTENTS_FIELD)) {
                        UIWidget childElement = context.deserialize(child, UIWidget.class);
                        if (childElement != null) {
                            LayoutHint hint = null;
                            if (child.isJsonObject()) {
                                JsonObject childObject = child.getAsJsonObject();
                                if (layoutHintType != null && !layoutHintType.isInterface() && !Modifier.isAbstract(layoutHintType.getModifiers())
                                    && childObject.has(LAYOUT_INFO_FIELD)) {
                                    hint = context.deserialize(childObject.get(LAYOUT_INFO_FIELD), layoutHintType);
                                }
                            }
                            layout.addWidget(childElement, hint);
                        }
                    }
                }
            }
            return element;
        }
    }
}
