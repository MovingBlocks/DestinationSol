package com.miloshpetrov.sol2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

interface TextureProvider {
    /**
     * Reads a image file and returns it as a usable texture.
     * @param textureFile The image file for the texture.
     * @return The texture.
     */
    TextureAtlas.AtlasRegion getTexture(FileHandle textureFile);

    /**
     *
     * @param fullName
     * @param configFile
     * @return
     * @deprecated this method uses hardcoded image locations. Use the more general FileHandle version instead.
     */
    @Deprecated
    TextureAtlas.AtlasRegion getTex(String fullName, FileHandle configFile);

    void dispose();
    Sprite createSprite(String name);
    ArrayList<TextureAtlas.AtlasRegion> getTexs(String name, FileHandle configFile);
    TextureAtlas.AtlasRegion getCopy(TextureAtlas.AtlasRegion tex);
}
