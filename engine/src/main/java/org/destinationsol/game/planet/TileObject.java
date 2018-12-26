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
package org.destinationsol.game.planet;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class TileObject implements SolObject {

    private final Planet planet;
    private final float relativeAngleToPlanet;
    private final float distance;
    private final List<Drawable> drawables;
    private final Body body;
    private final Vector2 position;

    // for far objs {
    private final float size;
    private final Tile tile;
    // }

    private float angle;

    TileObject(Planet planet, float relativeAngleToPlanet, float distance, float size, RectSprite sprite, Body body, Tile tile) {
        this.tile = tile;
        drawables = new ArrayList<>();

        this.planet = planet;
        this.relativeAngleToPlanet = relativeAngleToPlanet;
        this.distance = distance;
        this.size = size;
        this.body = body;
        position = new Vector2();

        drawables.add(sprite);
        setDependentParams();
    }

    @Override
    public void update(SolGame game) {
        setDependentParams();

        if (body != null) {
            float timeStep = game.getTimeStep();
            Vector2 velocity = SolMath.getVec(position);
            velocity.sub(body.getPosition());
            velocity.scl(1f / timeStep);
            body.setLinearVelocity(velocity);
            SolMath.free(velocity);
            float bodyAngle = body.getAngle() * MathUtils.radDeg;
            float angularVelocity = SolMath.norm(angle - bodyAngle) * MathUtils.degRad / timeStep;
            body.setAngularVelocity(angularVelocity);
        }
    }

    private void setDependentParams() {
        float toPlanetAngle = planet.getAngle() + relativeAngleToPlanet;
        SolMath.fromAl(position, toPlanetAngle, distance);
        position.add(planet.getPosition());
        angle = toPlanetAngle + 90;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
    }

    @Override
    public void onRemove(SolGame game) {
        if (body != null) {
            body.getWorld().destroyBody(body);
        }
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
        game.getSpecialSounds().playHit(game, this, position, dmgType);
    }

    @Override
    public boolean receivesGravity() {
        return false;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public FarObject toFarObject() {
        return new FarTileObject(planet, relativeAngleToPlanet, distance, size, tile);
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
        return null;
    }

    @Override
    public void handleContact(SolObject other, float absImpulse, SolGame game, Vector2 collPos) {
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return false;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    public Planet getPlanet() {
        return planet;
    }

    public float getSz() {
        return size;
    }

    public Tile getTile() {
        return tile;
    }
}
