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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class TileObject implements SolObject {

    private final Planet myPlanet;
    private final float myToPlanetRelAngle;
    private final float myDist;
    private final List<Drawable> myDrawables;
    private final Body myBody;
    private final Vector2 myPos;

    // for far objs {
    private final float mySize;
    private final Tile myTile;
    // }

    private float myAngle;

    public TileObject(Planet planet, float toPlanetRelAngle, float dist, float size, RectSprite sprite, Body body, Tile tile) {
        myTile = tile;
        myDrawables = new ArrayList<>();

        myPlanet = planet;
        myToPlanetRelAngle = toPlanetRelAngle;
        myDist = dist;
        mySize = size;
        myBody = body;
        myPos = new Vector2();

        myDrawables.add(sprite);
        setDependentParams();
    }

    @Override
    public void update(SolGame game) {
        setDependentParams();

        if (myBody != null) {
            float ts = game.getTimeStep();
            Vector2 spd = SolMath.getVec(myPos);
            spd.sub(myBody.getPosition());
            spd.scl(1f / ts);
            myBody.setLinearVelocity(spd);
            SolMath.free(spd);
            float bodyAngle = myBody.getAngle() * SolMath.radDeg;
            float av = SolMath.norm(myAngle - bodyAngle) * SolMath.degRad / ts;
            myBody.setAngularVelocity(av);
        }
    }

    private void setDependentParams() {
        float toPlanetAngle = myPlanet.getAngle() + myToPlanetRelAngle;
        SolMath.fromAl(myPos, toPlanetAngle, myDist, true);
        myPos.add(myPlanet.getPos());
        myAngle = toPlanetAngle + 90;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
    }

    @Override
    public void onRemove(SolGame game) {
        if (myBody != null) {
            myBody.getWorld().destroyBody(myBody);
        }
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
        game.getSpecialSounds().playHit(game, this, pos, dmgType);
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
        return new FarTileObject(myPlanet, myToPlanetRelAngle, myDist, mySize, myTile);
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
        return true;
    }

    public Planet getPlanet() {
        return myPlanet;
    }

    public float getSz() {
        return mySize;
    }

    public Tile getTile() {
        return myTile;
    }
}
