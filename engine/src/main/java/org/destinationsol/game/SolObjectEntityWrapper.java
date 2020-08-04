/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.game.drawables.Drawable;
import org.terasology.gestalt.entitysystem.entity.EntityRef;

import java.util.Collections;
import java.util.List;

/**
 * This is a {@link SolObject} wrapper for an {@link EntityRef}. This is is a patch for the in-between period between
 * Object-Oriented design and Entity-Component-System architecture, so once everything has been converted, this can be
 * deleted.
 *
 * This class DOES NOT implement any of the functionality of SolObject. All of its inherited methods do nothing. To
 * interact with the entity that this class represents, use getEntity() to get the {@link EntityRef}, then use the
 * {@link EntitySystemManager} to send that entity events.
 */
public class SolObjectEntityWrapper implements SolObject {

    private final EntityRef entity;

    public SolObjectEntityWrapper(EntityRef entity) {
        this.entity = entity;
    }

    public EntityRef getEntity() {
        return entity;
    }

    @Override
    public void update(SolGame game) {

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
        return Vector2.Zero;
    }

    @Override
    public FarObject toFarObject() {
        return null;
    }

    @Override
    public List<Drawable> getDrawables() {
        return Collections.emptyList();
    }

    @Override
    public float getAngle() {
        return 0;
    }

    @Override
    public Vector2 getVelocity() {
        return Vector2.Zero;
    }

    @Override
    public void handleContact(SolObject other, float absImpulse, SolGame game, Vector2 collPos) {

    }

    @Override
    public Boolean isMetal() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return false;
    }
}
