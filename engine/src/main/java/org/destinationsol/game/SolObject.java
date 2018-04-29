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
import org.destinationsol.common.Nullable;
import org.destinationsol.game.drawables.Drawable;

import java.util.List;

public interface SolObject {
    void update(SolGame game);

    boolean shouldBeRemoved(SolGame game);

    void onRemove(SolGame game);

    void receiveDmg(float dmg, SolGame game, @Nullable Vector2 position, DmgType dmgType);

    boolean receivesGravity();

    void receiveForce(Vector2 force, SolGame game, boolean acc);

    Vector2 getPosition();

    FarObject toFarObject();

    List<Drawable> getDrawables();

    float getAngle();

    Vector2 getSpeed();

    void handleContact(SolObject other, float absImpulse, SolGame game, Vector2 collPos);

    default String toDebugString() {
        return null;
    }

    Boolean isMetal();

    boolean hasBody();

    float getRadius();
}
