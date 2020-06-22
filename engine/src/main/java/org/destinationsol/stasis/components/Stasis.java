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
package org.destinationsol.stasis.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Stasis components are a way to flag a component to indicate that it should be handled in a more resource-efficient
 * way. Generally speaking, every entity that gets too far away from a player either gets a stasis component or is
 * deleted. Entities in stasis remain that way until a player gets close to them again, at which point the stasis
 * component is removed.
 *
 * For systems that shouldn't operate on an entity in stasis, there should be a method annotated with "@Before" that
 * consumes that event if the entity has a stasis component.
 */
public class Stasis implements Component<Stasis> {

    @Override
    public void copy(Stasis other) {
    }
}
