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
package org.destinationsol.game.projectile;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.ship.SolShip;

public interface ProjectileBody {
    void update(SolGame game);

    Vector2 getPosition();

    Vector2 getVelocity();

    void receiveForce(Vector2 force, SolGame game, boolean acc);

    void onRemove(SolGame game);

    float getAngle();

    void changeAngle(float diff);

    float getDesiredAngle(SolShip ne);
}
