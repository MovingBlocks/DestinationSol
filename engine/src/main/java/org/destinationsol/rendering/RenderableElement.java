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
import org.destinationsol.size.components.Size;

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
     * The amount that the sprite should be moved to line up accurately with the mesh. This should be scaled according
     * to the {@link Size}. This is different from the relativePosition, because this is a practical adjustment to align
     * the mesh with the sprite, as opposed to moving the sprite relative to the base entity. To draw the sprite
     * accurately, it needs to be drawn from the bottom-left of the actual image, not the bottom left of the .png file,
     * so this contains the information for calculating the actual start of the sprite.
     * <p>
     * Modification of this can create mesh misalignments, so only change this if you know what you're doing.
     */
    public Vector2 graphicsOffset;

    /** Represents the density of this element. */
    public float density;


    //TODO this should be automatically called when the Size component is changed, e.g. the entity shrinks or grows
    /**
     * Resizes the renderable element to the given size. The larger dimension of the texture is set to the size, and the
     * smaller one is scaled down proportionally.
     */
    public void setSize(float size) {
        size /= drawableLevel.depth; // Scales the texture size for objects that are in the background
        float dimensionsRatio = (float) texture.getRegionWidth() / texture.getRegionHeight();
        if (dimensionsRatio > 1) {
            width = size;
            height = size / dimensionsRatio;
        } else {
            width = size / dimensionsRatio;
            height = size;
        }
    }

    public void copy(RenderableElement other) {
        this.texture = new TextureAtlas.AtlasRegion(other.texture);
        this.drawableLevel = other.drawableLevel;
        this.relativePosition = other.relativePosition.cpy();
        this.relativeAngle = other.relativeAngle;
        this.width = other.getWidth();
        this.height = other.getHeight();
        this.tint = other.tint.cpy();
        this.graphicsOffset = other.graphicsOffset.cpy();
        this.density = other.density;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}

