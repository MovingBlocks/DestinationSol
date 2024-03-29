/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.assets.music.OggMusic;
import org.destinationsol.assets.sound.OggSound;
import org.destinationsol.assets.emitters.Emitter;
import org.destinationsol.assets.fonts.Font;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.textures.DSTexture;
import org.json.JSONArray;
import org.destinationsol.game.drawables.SpriteManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.assets.Asset;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.entitysystem.prefab.Prefab;
import org.terasology.nui.asset.UIElement;
import org.terasology.nui.skin.UISkin;
import org.terasology.nui.skin.UISkinAsset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A high-level wrapper over the AssetHelper class.
 *
 * This class allows loading of assets without the need of explicit error handling, using static methods.
 */
public abstract class Assets {
    private static AssetHelper assetHelper;
    private static Set<ResourceUrn> textureList;

    private static Logger logger = LoggerFactory.getLogger(Assets.class);

    /**
     * Initializes the class for loading assets using the given environment.
     * This function -has- to be called upon startup, and whenever the environment is changed.
     *
     * @param helper the helper used to initialise the asset system
     */
    public static void initialize(AssetHelper helper) {
        assetHelper = helper;
    }

    public static AssetHelper getAssetHelper() {
        return assetHelper;
    }

    /**
     * Loads an Prefab (.prefab) from the current environment. Throws an exception if the asset is not found.
     * @param path A String specifying the desired asset.
     * @return The loaded Prefab.
     */
    public static Prefab getPrefab(String path) {
        Optional<Prefab> prefabOptional = assetHelper.get(new ResourceUrn(path), Prefab.class);

        if (prefabOptional.isPresent()) {
            return prefabOptional.get();
        }

        throw new RuntimeException("Prefab " + path + " not found!");
    }

    /**
     * Loads an OggSound (.ogg) from the current environment. Throws an exception if the asset is not found.
     * @param path A String specifying the desired asset.
     * @return The loaded OggSound.
     */
    public static OggSound getSound(String path) {
        Optional<OggSound> oggSoundOptional = assetHelper.get(new ResourceUrn(path), OggSound.class);

        if (oggSoundOptional.isPresent()) {
            return oggSoundOptional.get();
        }

        // DebugOptions.MISSING_SOUND_ACTION.handle("OggSound " + path + " not found!");
        throw new RuntimeException("OggSound " + path + " not found!");
    }

    /**
     * Loads an OggMusic (.ogg) from the current environment. Throws an exception if the asset is not found.
     *
     * @param path A String specifying the desired asset.
     * @return The loaded OggMusic.
     */
    public static OggMusic getMusic(String path) {
        Optional<OggMusic> oggMusicOptional = assetHelper.get(new ResourceUrn(path), OggMusic.class);

        if (oggMusicOptional.isPresent()) {
            return oggMusicOptional.get();
        }

        throw new RuntimeException("OggMusic " + path + " not found!");
    }

    /**
     * Loads a BitmapFont (.font) from the current environment. Throws an exception if the asset is not found.
     *
     * @param path A String specifying the desired asset.
     * @return The loaded Font.
     */
    public static Font getFont(String path) {
        Optional<Font> fontOptional = assetHelper.get(new ResourceUrn(path), Font.class);

        if (fontOptional.isPresent()) {
            return fontOptional.get();
        }

        throw new RuntimeException("Font " + path + " not found!");
    }

    public static UISkin getSkin(String path) {
        Optional<UISkinAsset> skinOptional = assetHelper.get(new ResourceUrn(path), UISkinAsset.class);

        if (skinOptional.isPresent()) {
            return skinOptional.get().getSkin();
        }

        throw new RuntimeException("UISkin " + path + " not found!");
    }

    /**
     * Loads an emitter (.emitter) from the current environment. Throws an exception if the asset is not found.
     *
     * @param path A String specifying the desired asset.
     * @return The loaded Emitter.
     */
    public static Emitter getEmitter(String path) {
        Optional<Emitter> emitterOptional = assetHelper.get(new ResourceUrn(path), Emitter.class);

        if (emitterOptional.isPresent()) {
            return emitterOptional.get();
        }

        throw new RuntimeException("Emitter " + path + " not found!");
    }

    /**
     * Loads a Json (.json) from the current environment. Throws an exception if the asset is not found.
     *
     * @param path A String specifying the desired asset.
     * @return The loaded Json.
     */
    public static Json getJson(String path) {
        Optional<Json> jsonOptional = assetHelper.get(new ResourceUrn(path), Json.class);

        if (jsonOptional.isPresent()) {
            return jsonOptional.get();
        }

        throw new RuntimeException("Json " + path + " not found!");
    }

    /**
     * Loads a Texture (.png) from the current environment. Throws an exception if the asset is not found.
     *
     * @param path A String specifying the desired asset.
     * @return The loaded Texture.
     */
    public static DSTexture getDSTexture(String path) {
        Optional<DSTexture> dsTextureOptional = assetHelper.get(new ResourceUrn(path), DSTexture.class);

        if (dsTextureOptional.isPresent()) {
            return dsTextureOptional.get();
        }

        throw new RuntimeException("DSTexture " + path + " not found!");
    }

    /**
     * A wrapper function over getDSTexture() that creates an AtlasRegion out of the given Texture, to use in drawing functions.
     *
     * @param path A String specifying the desired asset.
     * @param textureFilter The texture filtering method for minification and magnification.
     * @return An AtlasRegion representing the loaded Texture.
     */
    public static TextureAtlas.AtlasRegion getAtlasRegion(String path, Texture.TextureFilter textureFilter) {
        // TODO: Remove this sanitisation when no gestalt resource urns contain slashes.
        String sanitisedPath = path;
        if (path.contains("/")) {
            sanitisedPath = path.replace(path.substring(path.indexOf(':')+1, path.lastIndexOf('/')+1), "");
        }
        Texture texture = getDSTexture(sanitisedPath).getTexture();
        texture.setFilter(textureFilter, textureFilter);
        TextureAtlas.AtlasRegion atlasRegion = new TextureAtlas.AtlasRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
        atlasRegion.flip(false, true);
        atlasRegion.name = path;
        return atlasRegion;
    }

    /**
     * A wrapper function over getDSTexture() that creates an AtlasRegion out of the given Texture, to use in drawing functions.
     * This overloaded variant of the function defaults to the Nearest texture filtering method, which is the default for DestSol.
     *
     * @param path A String specifying the desired asset.
     * @return An AtlasRegion representing the loaded Texture.
     */
    public static TextureAtlas.AtlasRegion getAtlasRegion(String path) {
        return getAtlasRegion(path, Texture.TextureFilter.Nearest);
    }

    public static Animation<TextureAtlas.AtlasRegion> getAnimation(String texturePath) {
        if (!assetHelper.get(new ResourceUrn(texturePath), DSTexture.class).isPresent()) {
            return null;
        }

        String animationPath = texturePath + "Animation";
        if (!assetHelper.get(new ResourceUrn(animationPath), DSTexture.class).isPresent()) {
            return new Animation<>(Float.MAX_VALUE, getAtlasRegion(texturePath));
        }

        Texture originalTexture = getDSTexture(texturePath).getTexture();

        Json animationInfoJson = getJson(animationPath);
        JSONObject animationInfo = animationInfoJson.getJsonValue();
        int frameWidth = animationInfo.optInt("frameWidth", 256);
        int frameHeight = animationInfo.optInt("frameHeight", 256);
        int frameCount = animationInfo.optInt("frameCount", 1);
        float framesPerSecond = (float) animationInfo.optDouble("framesPerSecond", 24);
        boolean autoGenerateFrames = animationInfo.optBoolean("autoGenerateFrames", false);
        TextureAtlas.AtlasRegion[] frames = new TextureAtlas.AtlasRegion[frameCount];
        if (autoGenerateFrames) {
            frameCount = (originalTexture.getWidth() / frameWidth) * (originalTexture.getHeight() / frameHeight);
            TextureAtlas.AtlasRegion region = new TextureAtlas.AtlasRegion(originalTexture, 0, 0, frameWidth, frameHeight);
            region.name = texturePath;
            frames = SpriteManager.getSequentialRegions(region, frameCount, frameWidth, frameHeight);
        } else {
            ArrayList<TextureAtlas.AtlasRegion> regions = new ArrayList<TextureAtlas.AtlasRegion>();
            JSONArray framesArray = animationInfo.optJSONArray("frames");
            if (framesArray != null) {
                for (int frame = 0; frame < framesArray.length(); frame++) {
                    JSONObject frameObject = framesArray.getJSONObject(frame);
                    int x = frameObject.optInt("x", 0);
                    int y = frameObject.optInt("y", 0);
                    int regionWidth = frameObject.optInt("width", frameWidth);
                    int regionHeight = frameObject.optInt("height", frameHeight);
                    TextureAtlas.AtlasRegion region = new TextureAtlas.AtlasRegion(originalTexture, x, y, regionWidth, regionHeight);
                    region.flip(false, true);
                    region.name = texturePath + " frame " + frame;
                    regions.add(region);
                }
            }

            frames = regions.toArray(frames);
        }

        animationInfoJson.dispose();
        Animation<TextureAtlas.AtlasRegion> animation = new Animation<TextureAtlas.AtlasRegion>(1.0f / framesPerSecond, frames);
        return animation;
    }

    /**
     * Retrieves the specified UIElement asset, if it exists. Otherwise, throws a RuntimeException.
     * NOTE: It is the caller's responsibility for initialising the retrieved element, if needed.
     * @see org.destinationsol.ui.nui.NUIManager#createScreen
     * @param path the asset path, in the {@link ResourceUrn} format.
     * @return the retrieved asset
     */
    public static UIElement getUIElement(String path) {
        Optional<UIElement> optionalUIElement = assetHelper.get(new ResourceUrn(path), UIElement.class);

        if (optionalUIElement.isPresent()) {
            return optionalUIElement.get();
        }

        throw new RuntimeException("UIElement " + path + " not found!");
    }

    /**
     * Returns true if the asset specified has already been loaded.
     * @param path the asset urn
     * @param type the asset type
     * @return true, if the asset has been loaded yet, otherwise false
     */
    public static boolean isLoaded(String path, Class<? extends Asset<?>> type) {
        return assetHelper.isAssetLoaded(new ResourceUrn(path), type);
    }

    public static void cacheLists() {
        textureList = assetHelper.list(DSTexture.class);
    }

    public static void uncacheLists() {
        textureList.clear();
        textureList = null;
    }

    public static synchronized List<TextureAtlas.AtlasRegion> listTexturesMatching(String regex) {
        boolean listsCached = true;
        if (textureList == null) {
            listsCached = false;
            cacheLists();
        }

        List<TextureAtlas.AtlasRegion> textures = new ArrayList<>();

        for (ResourceUrn resourceUrn : textureList) {
            if (resourceUrn.toString().matches(regex)) {
                textures.add(getAtlasRegion(resourceUrn.toString()));
            }
        }

        if (!listsCached) {
            uncacheLists();
        }

        return textures;
    }

    /**
     * A function that converts a JSONArray into a String ArrayList
     *
     * @param arr A JSONArray containing values to be converted into a String List
     * @return An ArrayList containing all String values of arr
     */
    public static ArrayList<String> convertToStringList(JSONArray arr) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(arr.getString(i));
        }
        return list;
    }
}
