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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.SolSystem;

public interface CamRotStrategy {
    float getRotation(Vector2 position, SolGame game);

    class Static implements CamRotStrategy {
        public float getRotation(Vector2 position, SolGame game) {
            return 0;
        }
    }

    class ToPlanet implements CamRotStrategy {

        public float getRotation(Vector2 position, SolGame game) {
            Planet np = game.getPlanetManager().getNearestPlanet();
            float fh = np.getFullHeight();
            Vector2 npPos = np.getPos();
            if (npPos.dst(position) < fh) {
                return SolMath.angle(position, npPos, true) - 90;
            }
            SolSystem sys = game.getPlanetManager().getNearestSystem(position);
            Vector2 sysPos = sys.getPosition();
            if (sysPos.dst(position) < Const.SUN_RADIUS) {
                return SolMath.angle(position, sysPos, true) - 90;
            }
            return 0;
        }
    }
}
