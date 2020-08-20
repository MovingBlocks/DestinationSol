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
package org.destinationsol.rubble.components;

import org.destinationsol.game.Rubble;
import org.destinationsol.location.components.Position;
import org.terasology.gestalt.entitysystem.component.EmptyComponent;

/**
 * Indicates that when an entity dies, {@link Rubble} should be created where the entity was. The entity needs to have
 * a {@link Position} component for this to function.
 */
public class CreatesRubbleOnDestruction extends EmptyComponent<CreatesRubbleOnDestruction> {
}
