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

public final class SpriteManager {
    private SpriteManager() {

    }

    private static Map<String, SpriteInfo> sprites = new HashMap<String, SpriteInfo>();

    public static RectSprite createSprite(String name, DrawableLevel drawLevel) {
        return createSprite(name, 1, 0, 0, new Vector2(), drawLevel, 0, 0, Color.WHITE, false);
    }

    public static RectSprite createSprite(String name, DrawableLevel drawLevel, Vector2 relativePosition) {
        return createSprite(name, 1, 0, 0, relativePosition, drawLevel, 0, 0, Color.WHITE, false);
    }

    public static RectSprite createSprite(String name, DrawableLevel drawLevel, Vector2 relativePosition, Color color) {
        return createSprite(name, 1, 0, 0, relativePosition, drawLevel, 0, 0, color, false);
    }

    public static RectSprite createSprite(String name, DrawableLevel drawLevel, Vector2 relativePosition, Color color, boolean additive) {
        return createSprite(name, 1, 0, 0, relativePosition, drawLevel, 0, 0, color, additive);
    }

    public static RectSprite createSprite(String name, float size,
                                                          float originalXPercentage, float originalYPercentage,
                                                          Vector2 relativePosition, DrawableLevel drawLevel, float angle,
                                                          float rotateSpeed, Color tint, boolean additive) {
        if (!sprites.containsKey(name)) {
            try {
                TextureAtlas.AtlasRegion region = Assets.getAtlasRegion(name);
                Animation<TextureAtlas.AtlasRegion> frames = Assets.getAnimation(region, name + "Animation");
                if (frames == null) {
                    //This sprite has not defined animations.
                    frames = new Animation<TextureAtlas.AtlasRegion>(Float.MAX_VALUE, region);
                }
                sprites.put(region.name, new SpriteInfo(region.name, frames));
            } catch (Exception e) {
                throw new IllegalArgumentException("There is no sprite called \"" + name + "\"!");
            }
        }

        SpriteInfo sprite = sprites.get(name);
        return new AnimatedRectSprite(sprite.frames, size, originalXPercentage, originalYPercentage, relativePosition,
                drawLevel, angle, rotateSpeed, tint, additive);
    }

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

    public static AnimatedRectSprite createAnimatedSprite(Animation<TextureAtlas.AtlasRegion> animation, float size,
                                                          float originalXPercentage, float originalYPercentage,
                                                          Vector2 relativePosition, DrawableLevel drawLevel, float angle,
                                                          float rotateSpeed, Color tint, boolean additive) {
        return new AnimatedRectSprite(animation, size, originalXPercentage, originalYPercentage, relativePosition,
                drawLevel, angle, rotateSpeed, tint, additive);
    }

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
