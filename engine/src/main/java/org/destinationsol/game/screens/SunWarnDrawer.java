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
package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolNullOptionalException;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.SolShip;

import java.util.Optional;

public class SunWarnDrawer extends WarnDrawer {

    SunWarnDrawer(float r) {
        super(r, "Sun Near");
    }

    public boolean shouldWarn(SolGame game) {
        Optional<SolShip> hero = game.getHero();
        if (!hero.isPresent()) {
            return false;
        }
        Vector2 pos = hero.orElseThrow(SolNullOptionalException::new).getPosition();
        float toCenter = game.getPlanetManager().getNearestSystem(pos).getPos().dst(pos);
        return toCenter < Const.SUN_RADIUS;
    }
}
