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
package org.destinationsol.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.game.drawables.Drawable;

import java.util.ArrayList;
import java.util.List;

public class Shard implements SolObject {

    private final Body body;
    private final Vector2 position;
    private final ArrayList<Drawable> drawables;
    private final float mass;

    private float angle;

    Shard(Body body, ArrayList<Drawable> drawables) {
        this.drawables = drawables;
        this.body = body;
        position = new Vector2();
        mass = this.body.getMass();
        setParamsFromBody();
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public FarObject toFarObject() {
        return null;
    }

    @Override
    public List<Drawable> getDrawables() {
        return drawables;
    }

    @Override
    public float getAngle() {
        return angle;
    }

    @Override
    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public void handleContact(SolObject other, float absImpulse,
                              SolGame game, Vector2 collPos) {
    }

    @Override
    public Boolean isMetal() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public void update(SolGame game) {
        setParamsFromBody();
    }

    private void setParamsFromBody() {
        position.set(body.getPosition());
        angle = body.getAngle() * MathUtils.radDeg;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
    }

    @Override
    public void onRemove(SolGame game) {
        body.getWorld().destroyBody(body);
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
    }

    @Override
    public boolean receivesGravity() {
        return true;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
        if (acc) {
            force.scl(mass);
        }
        body.applyForceToCenter(force, true);
    }

}
