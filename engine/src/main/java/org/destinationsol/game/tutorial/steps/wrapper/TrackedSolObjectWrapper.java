/*
 * Copyright 2023 The Terasology Foundation
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

package org.destinationsol.game.tutorial.steps.wrapper;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.FarObject;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;

import java.util.List;

/**
 * When you obtain a {@link SolObject} instance, it only remains valid until it transitions to a {@link FarObject}.
 * When the {@link FarObject} transitions back into a {@link SolObject}, it will not use the same instance as before.
 * This makes it difficult to track objects off-screen.
 *
 * This class attempts to work around this issue by keeping track of the most recent {@link SolObject} and {@link FarObject}
 * instances in-use from a given {@link SolObject}.
 */
public class TrackedSolObjectWrapper implements SolObject {
    private final TrackedFarObjectWrapper trackedFarObjectWrapper;
    private SolObject trackedSolObject;

    public TrackedSolObjectWrapper(SolObject trackedSolObject) {
        this.trackedFarObjectWrapper = new TrackedFarObjectWrapper(this, null);
        this.trackedSolObject = trackedSolObject;
    }

    @Override
    public void update(SolGame game) {
        trackedSolObject.update(game);
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return trackedSolObject.shouldBeRemoved(game);
    }

    @Override
    public void onRemove(SolGame game) {
        trackedSolObject.onRemove(game);
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 position, DmgType dmgType) {
        trackedSolObject.receiveDmg(dmg, game, position, dmgType);
    }

    @Override
    public boolean receivesGravity() {
        return trackedSolObject.receivesGravity();
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
        trackedSolObject.receiveForce(force, game, acc);
    }

    @Override
    public Vector2 getPosition() {
        return trackedSolObject.getPosition();
    }

    @Override
    public FarObject toFarObject() {
        trackedFarObjectWrapper.setTrackedFarObject(trackedSolObject.toFarObject());
        return trackedFarObjectWrapper;
    }

    @Override
    public List<Drawable> getDrawables() {
        return trackedSolObject.getDrawables();
    }

    @Override
    public float getAngle() {
        return trackedSolObject.getAngle();
    }

    @Override
    public Vector2 getVelocity() {
        return trackedSolObject.getVelocity();
    }

    @Override
    public void handleContact(SolObject other, float absImpulse, SolGame game, Vector2 collPos) {
        trackedSolObject.handleContact(other, absImpulse, game, collPos);
    }

    @Override
    public Boolean isMetal() {
        return trackedSolObject.isMetal();
    }

    @Override
    public boolean hasBody() {
        return trackedSolObject.hasBody();
    }

    public void setTrackedSolObject(SolObject trackedSolObject) {
        this.trackedSolObject = trackedSolObject;
    }
}
