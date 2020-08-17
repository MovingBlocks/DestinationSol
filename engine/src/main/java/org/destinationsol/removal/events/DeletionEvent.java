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
package org.destinationsol.removal.events;

import org.destinationsol.removal.systems.DeletionUpdateSystem;
import org.destinationsol.removal.systems.DestructionSystem;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Every tick, the {@link DeletionUpdateSystem} sends a DeletionEvent to each entity with a SlatedForDeletion component,
 * which causes the entity to be deleted by the {@link DestructionSystem}. This is different than a
 * {@link ShouldBeDestroyedEvent} - that is for systems to respond to the destruction of an entity before it happens,
 * e.g. to prevent it from being destroyed. This event is the actual deletion of the entity.
 */
public class DeletionEvent implements Event {
}
