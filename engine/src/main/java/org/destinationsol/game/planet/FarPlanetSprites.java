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
import org.destinationsol.common.SolMath;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableManager;

import java.util.List;

public class FarPlanetSprites implements FarObj {
    private final Planet myPlanet;
    private final float myDist;
    private final List<Drawable> myDrawables;
    private final float myRadius;
    private final float myToPlanetRotSpd;
    private float myRelAngleToPlanet;
    private Vector2 myPos;

    public FarPlanetSprites(Planet planet, float relAngleToPlanet, float dist, List<Drawable> drawables,
                            float toPlanetRotSpd) {
        myPlanet = planet;
        myRelAngleToPlanet = relAngleToPlanet;
        myDist = dist;
        myDrawables = drawables;
        myRadius = DrawableManager.radiusFromDras(myDrawables);
        myToPlanetRotSpd = toPlanetRotSpd;
        myPos = new Vector2();
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
    }

    @Override
    public SolObject toObj(SolGame game) {
        return new PlanetSprites(myPlanet, myRelAngleToPlanet, myDist, myDrawables, myToPlanetRotSpd);
    }

    @Override
    public void update(SolGame game) {
        myRelAngleToPlanet += myToPlanetRotSpd * game.getTimeStep();
        if (game.getPlanetMan().getNearestPlanet() == myPlanet) {
            SolMath.fromAl(myPos, myPlanet.getAngle() + myRelAngleToPlanet, myDist);
            myPos.add(myPlanet.getPos());
        }
    }

    @Override
    public float getRadius() {
        return myRadius;
    }

    @Override
    public Vector2 getPos() {
        return myPos;
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return false;
    }
}
