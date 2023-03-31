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
import org.destinationsol.game.FarObject;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

/**
 * This is the {@link FarObject} equivalent of {@link TrackedSolObjectWrapper}.
 * @see TrackedSolObjectWrapper
 */
public class TrackedFarObjectWrapper implements FarObject {
    private final TrackedSolObjectWrapper trackedSolObjectWrapper;
    private FarObject trackedFarObject;

    public TrackedFarObjectWrapper(TrackedSolObjectWrapper trackedSolObjectWrapper, FarObject trackedFarObject) {
        this.trackedSolObjectWrapper = trackedSolObjectWrapper;
        this.trackedFarObject = trackedFarObject;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return trackedFarObject.shouldBeRemoved(game);
    }

    @Override
    public SolObject toObject(SolGame game) {
        trackedSolObjectWrapper.setTrackedSolObject(trackedFarObject.toObject(game));
        return trackedSolObjectWrapper;
    }

    @Override
    public void update(SolGame game) {
        trackedFarObject.update(game);
    }

    @Override
    public float getRadius() {
        return trackedFarObject.getRadius();
    }

    @Override
    public Vector2 getPosition() {
        return trackedFarObject.getPosition();
    }

    @Override
    public String toDebugString() {
        return trackedFarObject.toDebugString();
    }

    @Override
    public boolean hasBody() {
        return trackedFarObject.hasBody();
    }

    public void setTrackedFarObject(FarObject trackedFarObject) {
        this.trackedFarObject = trackedFarObject;
    }
}
