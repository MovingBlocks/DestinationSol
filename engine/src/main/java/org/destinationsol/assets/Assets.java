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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.assets.audio.OggMusic;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.emitters.Emitter;
import org.destinationsol.assets.fonts.Font;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.textures.DSTexture;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.ResourceUrn;
import org.terasology.module.ModuleEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.lang.System.exit;

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
     * @param environment The ModuleEnvironment to load assets from.
     */
    public static void initialize(ModuleEnvironment environment) {
        assetHelper = new AssetHelper(environment);
    }

    public static AssetHelper getAssetHelper() {
        return assetHelper;
    }

    private static ResourceUrn parsePath(String path) {
        String[] strings = path.split(":");

        if (strings.length < 2) {
            throw new RuntimeException("Invalid resource name (missing namespace?) `" + path + "`");
        }

        String module = strings[0];
        String file = strings[1];

        strings = file.split("/");
        if (strings.length > 1) {
            assetHelper.setFolders(Arrays.copyOfRange(strings, 0, strings.length - 1));
        } else {
            assetHelper.setFolders();
        }

        return new ResourceUrn(module + ":" + strings[strings.length - 1]);
    }

    /**
     * Loads an OggSound (.ogg) from the current environment. Throws an exception if the asset is not found.
     * @param path A String specifying the desired asset.
     * @return The loaded OggSound.
     */
    public static OggSound getSound(String path) {
        Optional<OggSound> oggSoundOptional = assetHelper.get(parsePath(path), OggSound.class);

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
        Optional<OggMusic> oggMusicOptional = assetHelper.get(parsePath(path), OggMusic.class);

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
        Optional<Font> fontOptional = assetHelper.get(parsePath(path), Font.class);

        if (fontOptional.isPresent()) {
            return fontOptional.get();
        }

        throw new RuntimeException("Font " + path + " not found!");
    }

    /**
     * Loads an emitter (.emitter) from the current environment. Throws an exception if the asset is not found.
     *
     * @param path A String specifying the desired asset.
     * @return The loaded Emitter.
     */
    public static Emitter getEmitter(String path) {
        Optional<Emitter> emitterOptional = assetHelper.get(parsePath(path), Emitter.class);

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
        Optional<Json> jsonOptional = assetHelper.get(parsePath(path), Json.class);

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
        Optional<DSTexture> dsTextureOptional = assetHelper.get(parsePath(path), DSTexture.class);

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
        Texture texture = getDSTexture(path).getTexture();
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

    public static void cacheLists() {
        textureList = assetHelper.list(DSTexture.class);
    }

    public static void uncacheLists() {
        textureList.clear();
        textureList = null;
    }

    public static List<TextureAtlas.AtlasRegion> listTexturesMatching(String regex) {
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
