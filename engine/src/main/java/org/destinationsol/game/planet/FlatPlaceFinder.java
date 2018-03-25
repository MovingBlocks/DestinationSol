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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.SolGame;

public class FlatPlaceFinder {
    private final Vector2 vector = new Vector2();
    private float deviation;

    private final RayCastCallback myRayBack = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (!(fixture.getBody().getUserData() instanceof TileObject)) {
                return -1;
            }
            vector.set(point);
            deviation = SolMath.abs(SolMath.angle(normal) + 90);
            return fraction;
        }
    };

    public Vector2 find(SolGame game, Planet planet, ConsumedAngles takenAngles, float objHalfWidth) {
        Vector2 pPos = planet.getPosition();

        Vector2 result = new Vector2(pPos);
        float minDeviation = 90;
        float resAngle = 0;
        float objAngularHalfWidth = SolMath.angularWidthOfSphere(objHalfWidth, planet.getGroundHeight());

        for (int i = 0; i < 20; i++) {
            float angle = SolRandom.randomFloat(180);
            if (takenAngles != null && takenAngles.isConsumed(angle, objAngularHalfWidth)) {
                continue;
            }
            deviation = angle;
            SolMath.fromAl(vector, angle, planet.getFullHeight());
            vector.add(pPos);
            game.getObjectManager().getWorld().rayCast(myRayBack, vector, pPos);
            if (deviation < minDeviation) {
                result.set(vector);
                minDeviation = deviation;
                resAngle = angle;
            }
        }

        if (takenAngles != null) {
            takenAngles.add(resAngle, objAngularHalfWidth);
        }
        result.sub(pPos);
        SolMath.rotate(result, -planet.getAngle());
        return result;
    }
}
