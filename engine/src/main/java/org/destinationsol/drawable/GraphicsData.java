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
package org.destinationsol.drawable;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.drawables.DrawableLevel;
import org.terasology.gestalt.entitysystem.component.Component;

public class GraphicsData implements Component<GraphicsData> {

    /**
     * Represents the texture (graphics) of this drawable.
     */
    public TextureAtlas.AtlasRegion texture;

    /**
     * Represents the depth at which this drawable renders, as well as its logical grouping.
     */
    public DrawableLevel drawableLevel;

    /**
     * Represents the offset of this graphical entity from the object that it represents.
     */
    public Vector2 relativePosition;

    @Override
    public void copy(GraphicsData other) {
        this.texture = other.texture;
        this.drawableLevel = other.drawableLevel;
        this.relativePosition = other.relativePosition;
    }
}

