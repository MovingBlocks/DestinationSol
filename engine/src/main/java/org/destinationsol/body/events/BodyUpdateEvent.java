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
package org.destinationsol.body.events;

import org.destinationsol.body.components.BodyLinked;
import org.destinationsol.body.systems.BodyHandlerSystem;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Tells the {@link BodyHandlerSystem} that it should update each entity with a {@link BodyLinked} component.
 */
public class BodyUpdateEvent implements Event {
}
