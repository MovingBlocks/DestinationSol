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
import org.destinationsol.game.drawables.DrawableLevel;

/**
 * Contains a {@link TextureAtlas.AtlasRegion} (an image), along with information about how it should be drawn.
 */
public class RenderableElement {

    /** Represents the image of this element. */
    public TextureAtlas.AtlasRegion texture;

    /** The width of the texture when drawn. */
    public float width;

    /** The height of the texture when drawn. */
    public float height;

    /** Represents the depth at which this element renders, as well as its logical grouping. */
    public DrawableLevel drawableLevel;

    /** Represents the spatial offset of this element from the entity that it is associated with. */
    public Vector2 relativePosition;

    /** Represents the rotational offset of this element from the entity that it is associated with. */
    public float relativeAngle;

    /** The tint that the texture should be given. */
    public Color tint;

    public void copy(RenderableElement other) {
        this.texture = other.texture;
        this.drawableLevel = other.drawableLevel;
        this.relativePosition = other.relativePosition.cpy();
        this.relativeAngle = other.relativeAngle;
        this.width = other.width;
        this.height = other.height;
        this.tint = other.tint;
    }
}

