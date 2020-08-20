/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.drawables.DrawableLevel;

/**
 * Contains a {@link TextureAtlas.AtlasRegion} (an image), along with information about how it should be drawn.
 */
public class RenderableElement {

    /** Represents the image of this element. */
    public TextureAtlas.AtlasRegion texture;

    /** The width of the texture when drawn. */
    private float width;

    /** The height of the texture when drawn. */
    private float height;

    /** Represents the depth at which this element renders, as well as its logical grouping. */
    public DrawableLevel drawableLevel;

    /** Represents the spatial offset of this element from the entity that it is associated with. */
    public Vector2 relativePosition;

    /** Represents the rotational offset of this element from the entity that it is associated with. */
    public float relativeAngle;

    /** The tint that the texture should be given. */
    public Color tint;

    /**
     * Sets the larger of the dimensions of the texture to the passed-in size, maintaining proportions and
     * recalculating other fields as needed to fit.
     * @param size the new size for the sprite
     */
    public void setSize(float size) {
        size /= drawableLevel.depth; // Scales the texture size for background objects
        float dimensionsRatio = texture.getRegionWidth() / texture.getRegionHeight();
        if (dimensionsRatio > 1) {
            width = size;
            height = size / dimensionsRatio;
        } else {
            width = size / dimensionsRatio;
            height = size;
        }
//        originalX = textureSizeX / 2 + size * originalPercentageX;
//        originalY = textureSizeY / 2 + size * originalPercentageY;
//
//        float relativeX = textureSizeX / 2 + size * SolMath.abs(originalPercentageX);
//        float relativeY = textureSizeY / 2 + size * SolMath.abs(originalPercentageY);
//        radius = SolMath.sqrt(relativeX * relativeX + relativeY * relativeY);
    }

    public void copy(RenderableElement other) {
        this.texture = new TextureAtlas.AtlasRegion(other.texture);
        this.drawableLevel = other.drawableLevel;
        this.relativePosition = other.relativePosition.cpy();
        this.relativeAngle = other.relativeAngle;
        this.width = other.getWidth();
        this.height = other.getHeight();
        this.tint = other.tint.cpy();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}

