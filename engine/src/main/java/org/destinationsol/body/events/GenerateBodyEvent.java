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

import com.badlogic.gdx.physics.box2d.Body;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * Indicates that a {@link Body} should be created for the entity that this event is sent against. Once a body has been
 * created for that entity, a reference to that Body should be sent in a {@link BodyCreatedEvent};
 */
public class GenerateBodyEvent implements Event {
}
