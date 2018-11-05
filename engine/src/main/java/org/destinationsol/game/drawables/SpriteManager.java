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
package org.destinationsol.game.drawables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.Assets;
import org.destinationsol.game.drawables.animated.AnimatedRectSprite;

import java.util.HashMap;
import java.util.Map;

/**
 * The SpriteManager class is responsible for the construction and initialisation of sprites
 * @see RectSprite for more information
 */
public final class SpriteManager {
    private SpriteManager() {
    }

    private static Map<String, SpriteInfo> sprites = new HashMap<String, SpriteInfo>();

    /**
     * Creates the specified sprite at the specified depth
     *
     * @param name The name of the sprite the new sprite should be based on
     * @param drawLevel The depth that the sprite should be drawn at
     * @return A new sprite
     */
    public static RectSprite createSprite(String name, DrawableLevel drawLevel) {
        return createSprite(name, 1, 0, 0, new Vector2(), drawLevel, 0, 0, Color.WHITE, false);
    }

    /**
     * Creates the specified sprite at the specified depth at a certain position
     *
     * @param name The name of the sprite the new sprite should be based on
     * @param drawLevel The depth that the sprite should be drawn at
     * @param relativePosition The relative position to draw the sprite at
     * @return A new sprite
     */
    public static RectSprite createSprite(String name, DrawableLevel drawLevel, Vector2 relativePosition) {
        return createSprite(name, 1, 0, 0, relativePosition, drawLevel, 0, 0, Color.WHITE, false);
    }

    /**
     * Creates the specified sprite, with the specified depth and relative position, which is tinted the specified colour
     *
     * @param name The name of the sprite the new sprite should be based on
     * @param drawLevel The depth that the sprite should be drawn at
     * @param relativePosition The relative position to draw the sprite at
     * @param color The colour to tint the sprite when drawn
     * @return A new sprite
     */
    public static RectSprite createSprite(String name, DrawableLevel drawLevel, Vector2 relativePosition, Color color) {
        return createSprite(name, 1, 0, 0, relativePosition, drawLevel, 0, 0, color, false);
    }

    /**
     * Creates the specified sprite, at the specified depth and position, which is tinted the specified colour, where additive blending is possible
     *
     * @param name The name of the sprite the new sprite should be based on
     * @param drawLevel The depth that the sprite should be drawn at
     * @param relativePosition The relative position to draw the sprite at
     * @param color The colour to tint the sprite when drawn
     * @param additive Should the sprite be blended additively
     * @return A new sprite
     */
    public static RectSprite createSprite(String name, DrawableLevel drawLevel, Vector2 relativePosition, Color color, boolean additive) {
        return createSprite(name, 1, 0, 0, relativePosition, drawLevel, 0, 0, color, additive);
    }

    public static RectSprite createSprite(String name, float size,
                                                          float originalXPercentage, float originalYPercentage,
                                                          Vector2 relativePosition, DrawableLevel drawLevel, float angle,
                                                          float rotateSpeed, Color tint, boolean additive) {
        if (!sprites.containsKey(name)) {
            try {
                Animation<TextureAtlas.AtlasRegion> frames = Assets.getAnimation(name);
                if (frames == null) {
                    //This sprite has not defined animations.
                    frames = new Animation<TextureAtlas.AtlasRegion>(Float.MAX_VALUE, Assets.getAtlasRegion(name));
                }
                sprites.put(name, new SpriteInfo(name, frames));
            } catch (Exception e) {
                throw new IllegalArgumentException("There is no sprite called \"" + name + "\"!");
            }
        }

        SpriteInfo sprite = sprites.get(name);
        return new AnimatedRectSprite(sprite.frames, size, originalXPercentage, originalYPercentage, relativePosition,
                drawLevel, angle, rotateSpeed, tint, additive);
    }

    /**
     * Creates an RectSprite
     * @see RectSprite
     *
     * @param texture The texture for the sprite
     * @param size The size of the sprite
     * @param originalXPercentage The original X percentage for the sprite texture
     * @param originalYPercentage The original Y percentage for the sprite texture
     * @param relativePosition The relative position of the sprite
     * @param drawLevel The depth for the sprite to be drawn at
     * @param angle The angle for the sprite to be rotated
     * @param rotateSpeed The speed that the sprite should be rotated at
     * @param tint The colour that the sprite should be tinted
     * @param additive Should the sprite be blended additively
     * @return The sprite created
     */
    public static RectSprite createStaticSprite(TextureAtlas.AtlasRegion texture, float size,
                                          float originalXPercentage, float originalYPercentage,
                                          Vector2 relativePosition, DrawableLevel drawLevel, float angle,
                                          float rotateSpeed, Color tint, boolean additive) {
        return new RectSprite(texture, size, originalXPercentage, originalYPercentage, relativePosition,
                drawLevel, angle, rotateSpeed, tint, additive);
    }

    public static AnimatedRectSprite createAnimatedSprite(TextureAtlas.AtlasRegion initialRegion, int frameWidth, int frameHeight,
                                                          float framesPerSecond, int frameCount, float size,
                                                          float originalXPercentage, float originalYPercentage,
                                                          Vector2 relativePosition, DrawableLevel drawLevel, float angle,
                                                          float rotateSpeed, Color tint, boolean additive) {
        TextureAtlas.AtlasRegion[] regions = getSequentialRegions(initialRegion, frameCount, frameWidth, frameHeight);
        Animation<TextureAtlas.AtlasRegion> animation = new Animation<TextureAtlas.AtlasRegion>(1 / framesPerSecond, regions);
        return createAnimatedSprite(animation, size, originalXPercentage, originalYPercentage, relativePosition,
                drawLevel, angle, rotateSpeed, tint, additive);
    }

    /**
     * Creates an AnimatedRectSprite
     * @see AnimatedRectSprite
     *
     * @param animation The frames for the sprite
     * @param size The size of the sprite
     * @param originalXPercentage The original X percentage for the sprite texture
     * @param originalYPercentage The original Y percentage for the sprite texture
     * @param relativePosition The relative position of the sprite
     * @param drawLevel The depth for the sprite to be drawn at
     * @param angle The angle for the sprite to be rotated
     * @param rotateSpeed The speed that the sprite should be rotated at
     * @param tint The colour that the sprite should be tinted
     * @param additive Should the sprite be blended additively
     * @return The sprite created
     */
    public static AnimatedRectSprite createAnimatedSprite(Animation<TextureAtlas.AtlasRegion> animation, float size,
                                                          float originalXPercentage, float originalYPercentage,
                                                          Vector2 relativePosition, DrawableLevel drawLevel, float angle,
                                                          float rotateSpeed, Color tint, boolean additive) {
        return new AnimatedRectSprite(animation, size, originalXPercentage, originalYPercentage, relativePosition,
                drawLevel, angle, rotateSpeed, tint, additive);
    }

    /**
     * Divides an image into regions with a constant width and height
     *
     * @param initialRegion A region to get a texture from
     * @param regionCount The number of regions to divide into
     * @param sequentialWidth The width of a divided region
     * @param sequentialHeight The height of a divided region
     * @return An array of regions taken from the whole texture
     */
    public static TextureAtlas.AtlasRegion[] getSequentialRegions(TextureAtlas.AtlasRegion initialRegion, int regionCount, int sequentialWidth, int sequentialHeight) {
        Texture texture = initialRegion.getTexture();
        int textureWidth = texture.getWidth();
        TextureAtlas.AtlasRegion[] regions = new TextureAtlas.AtlasRegion[regionCount];
        for (int i = 0; i < regionCount; i++) {
            int x = (i * sequentialWidth) % textureWidth;
            int y = ((i * sequentialWidth) / textureWidth) * sequentialHeight;
            regions[i] = new TextureAtlas.AtlasRegion(texture, x, y, sequentialWidth, sequentialHeight);
            regions[i].flip(false, true);
            regions[i].name = initialRegion.name + " frame " + i;
        }

        return regions;
    }
}
