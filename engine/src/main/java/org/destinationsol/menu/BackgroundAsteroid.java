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
package org.destinationsol.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.ui.UiDrawer;

public class BackgroundAsteroid {

    private TextureAtlas.AtlasRegion texture;

    private float scale;
    private Color tint;

    private Vector2 position;
    private Vector2 velocity;

    private float angle;

    private Body body;

    public BackgroundAsteroid(TextureAtlas.AtlasRegion texture, float scale, Color tint, Vector2 position, Vector2 velocity, float angle, Body body) {
        this.texture = texture;
        this.scale = scale;
        this.tint = tint;
        this.position = position;
        this.velocity = velocity;
        this.angle = angle;
        this.body = body;
    }

    public void update() {
        setParamsFromBody();
    }

    public void setParamsFromBody() {
        position.set(body.getPosition());
        velocity.set(body.getLinearVelocity());
        angle = body.getAngle() * MathUtils.radDeg;
    }

    public void draw(UiDrawer drawer) {
        drawer.draw(texture, scale, scale, scale / 2, scale / 2, position.x, position.y, angle, tint);
    }

    public TextureAtlas.AtlasRegion getTexture() {
        return texture;
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getAngle() {
        return angle;
    }

}
