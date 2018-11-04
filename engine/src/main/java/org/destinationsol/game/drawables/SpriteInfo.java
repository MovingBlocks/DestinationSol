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

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * This class specifies the information needed to store and construct a sprite
 */
public class SpriteInfo {
    public String displayName;
    public Animation<TextureAtlas.AtlasRegion> frames;

    /**
     * Creates a SpriteInfo instance
     *
     * @param displayName The name that the sprite should be known as
     * @param frames The frames of the sprite (only one frame for static sprites)
     */
    public SpriteInfo(String displayName, Animation<TextureAtlas.AtlasRegion> frames) {
        this.displayName = displayName;
        this.frames = frames;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
