/*
 * Copyright 2020 MovingBlocks
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
package org.destinationsol.menu.background;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.ui.UiDrawer;

/**
 * <h1>Base class for all Menu Background Objects</h1>
 * Contains common data holders required for all background objects.
 */
public class MenuBackgroundObject {
    TextureAtlas.AtlasRegion texture;

    float scale;

    Vector2 position;
    Vector2 velocity;
    Vector2 origin;

    float angle;

    Color tint;

    Body body;

    public MenuBackgroundObject(TextureAtlas.AtlasRegion texture, float scale, Color tint, Vector2 position, Vector2 velocity, Vector2 origin, float angle, Body body) {
        this.texture = texture;
        this.scale = scale;
        this.tint = tint;
        this.position = position;
        this.velocity = velocity;
        this.origin = origin;
        this.angle = angle;
        this.body = body;
    }

    public void setParamsFromBody() {
        position.set(body.getPosition());
        velocity.set(body.getLinearVelocity());
        angle = body.getAngle() * MathUtils.radDeg;
    }

    public void update() {
        setParamsFromBody();
    }

    public void draw(UiDrawer drawer) {
        drawer.draw(texture, scale, scale, origin.x, origin.y, position.x, position.y, angle, tint);
    }

    public Vector2 getPosition() {
        return position;
    }
}
