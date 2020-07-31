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
package org.destinationsol.rendering.events;

import org.destinationsol.rendering.components.Renderable;
import org.destinationsol.rendering.systems.RenderingSystem;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Event that tells the {@link RenderingSystem} to update the drawings of each entity with a {@link Renderable}.
 */
public class RenderEvent implements Event {
}
