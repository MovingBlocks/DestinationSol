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
package org.destinationsol.nui.input.events;

import com.badlogic.gdx.math.Vector3;
import org.terasology.entitysystem.core.EntityRef;
import org.terasology.entitysystem.core.NullEntityRef;
import org.terasology.entitysystem.event.Event;


public abstract class InputEvent implements Event {
    private float delta;
    private boolean consumed;

    private EntityRef target = NullEntityRef.INSTANCE;
    private Vector3 targetBlockPosition;
    private Vector3 hitPosition;
    private Vector3 hitNormal;

    public InputEvent(float delta) {
        this.delta = delta;
    }

    public EntityRef getTarget() {
        return target;
    }

    /**
     *
     * @return This event's target world position.
     */
    public Vector3 getHitPosition() {
        return hitPosition;
    }

    /**
     *
     * @return The hit normal/direction of this event (usually from player camera).
     */
    public Vector3 getHitNormal() {
        return hitNormal;
    }

    public Vector3 getTargetBlockPosition() {
        return targetBlockPosition;
    }

    /**
     *
     * @return The time since the event was fired (also the game update loop's delta time).
     */
    public float getDelta() {
        return delta;
    }

//    @Override
//    public boolean isConsumed() {
//        return consumed;
//    }
//
//    @Override
//    public void consume() {
//        this.consumed = true;
//    }

    /**
     * Sets this event's target information.
     * @param newTarget This event's target entity.
     * @param targetBlockPos This event's target block coordinates.
     * @param targetHitPosition This event's target world position.
     * @param targetHitNormal The hit normal/direction of this event (usually from player camera).
     */
    public void setTargetInfo(EntityRef newTarget, Vector3 targetBlockPos, Vector3 targetHitPosition, Vector3 targetHitNormal) {
        this.target = newTarget;
        this.targetBlockPosition = targetBlockPos;
        this.hitPosition = targetHitPosition;
        this.hitNormal = targetHitNormal;
    }

    protected void reset(float newDelta) {
        consumed = false;
        this.delta = newDelta;
        this.target = NullEntityRef.INSTANCE;
    }


}
