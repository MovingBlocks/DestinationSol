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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;

import java.util.List;

public class PlanetSprites implements SolObject {

    private final Planet planet;
    private final float distance;
    private final List<Drawable> drawables;
    private final float RotationSpeedToPlanet;
    private final Vector2 position;
    private float relativeAngleToPlanet;
    private float angle;

    PlanetSprites(Planet planet, float relAngleToPlanet, float dist, List<Drawable> drawables, float toPlanetRotationSpeed) {
        this.planet = planet;
        relativeAngleToPlanet = relAngleToPlanet;
        distance = dist;
        this.drawables = drawables;
        RotationSpeedToPlanet = toPlanetRotationSpeed;
        position = new Vector2();
        setDependentParams();
    }

    @Override
    public void update(SolGame game) {
        setDependentParams();
        relativeAngleToPlanet += RotationSpeedToPlanet * game.getTimeStep();
    }

    private void setDependentParams() {
        float angleToPlanet = planet.getAngle() + relativeAngleToPlanet;
        SolMath.fromAl(position, angleToPlanet, distance);
        position.add(planet.getPosition());
        angle = angleToPlanet + 90;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
    }

    @Override
    public void onRemove(SolGame game) {
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
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
        return new FarPlanetSprites(planet, relativeAngleToPlanet, distance, drawables, RotationSpeedToPlanet);
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
    public void handleContact(SolObject other, float absImpulse,
                              SolGame game, Vector2 collPos) {
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
        return false;
    }

}
