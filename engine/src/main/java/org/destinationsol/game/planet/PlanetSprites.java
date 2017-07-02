/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;

import java.util.List;

public class PlanetSprites implements SolObject {

    private final Planet myPlanet;
    private final float myDist;
    private final List<Drawable> myDrawables;
    private final float myToPlanetRotSpd;
    private final Vector2 myPos;
    private float myRelAngleToPlanet;
    private float myAngle;

    public PlanetSprites(Planet planet, float relAngleToPlanet, float dist, List<Drawable> drawables, float toPlanetRotSpd) {
        myPlanet = planet;
        myRelAngleToPlanet = relAngleToPlanet;
        myDist = dist;
        myDrawables = drawables;
        myToPlanetRotSpd = toPlanetRotSpd;
        myPos = new Vector2();
        setDependentParams();
    }

    @Override
    public void update(SolGame game) {
        setDependentParams();
        myRelAngleToPlanet += myToPlanetRotSpd * game.getTimeStep();
    }

    private void setDependentParams() {
        float angleToPlanet = myPlanet.getAngle() + myRelAngleToPlanet;
        SolMath.fromAl(myPos, angleToPlanet, myDist, true);
        myPos.add(myPlanet.getPos());
        myAngle = angleToPlanet + 90;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
    }

    @Override
    public void onRemove(SolGame game) {
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
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
        return myPos;
    }

    @Override
    public FarObj toFarObj() {
        return new FarPlanetSprites(myPlanet, myRelAngleToPlanet, myDist, myDrawables, myToPlanetRotSpd);
    }

    @Override
    public List<Drawable> getDrawables() {
        return myDrawables;
    }

    @Override
    public float getAngle() {
        return myAngle;
    }

    @Override
    public Vector2 getSpd() {
        return null;
    }

    @Override
    public void handleContact(SolObject other, ContactImpulse impulse, boolean isA, float absImpulse,
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
