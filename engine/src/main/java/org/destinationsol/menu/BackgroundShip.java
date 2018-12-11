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
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.UiDrawer;

public class BackgroundShip {
    private TextureAtlas.AtlasRegion texture;

    private float scale;

    private Vector2 position;
    private Vector2 velocity;

    private float angle;

    private float orgX;
    private float orgY;

    private Color tint;

    private Body body;


    public BackgroundShip (TextureAtlas.AtlasRegion texture, float scale, Vector2 position, Vector2 velocity, float angle, float orgX, float orgY, Body body) {
        this.texture = texture;
        this.scale = scale;
        this.position = position;
        this.velocity = velocity;
        this.angle = angle;
        this.orgX = orgX;
        this.orgY = orgY;
        this.body = body;
        tint = SolColor.WHITE;
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
        drawer.draw(texture, scale, scale, orgX, orgY, position.x, position.y, angle, tint);
    }

    public Vector2 getPosition() {
        return position;
    }

}
