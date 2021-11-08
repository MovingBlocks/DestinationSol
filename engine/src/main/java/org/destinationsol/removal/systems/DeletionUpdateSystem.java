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
package org.destinationsol.removal.systems;

import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.attributes.RegisterUpdateSystem;
import org.destinationsol.removal.components.SlatedForDeletion;
import org.destinationsol.removal.events.DeletionEvent;

import javax.inject.Inject;

/**
 * Every tick, this sends a {@link DeletionEvent} to each entity with a {@link SlatedForDeletion} component.
 */
@RegisterUpdateSystem
public class DeletionUpdateSystem implements UpdateAwareSystem {

    @Inject
    protected EntitySystemManager entitySystemManager;

    @Inject
    public DeletionUpdateSystem() {
    }

    @Override
    public void update(SolGame game, float timeStep) {
        entitySystemManager.sendEvent(new DeletionEvent(), new SlatedForDeletion());
    }
}
