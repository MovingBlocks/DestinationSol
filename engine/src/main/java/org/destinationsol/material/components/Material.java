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
package org.destinationsol.material.components;

import org.destinationsol.material.MaterialType;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Indicates what type of material the entity is made of.
 */
public class Material implements Component<Material> {

    public MaterialType materialType;

    @Override
    public void copy(Material other) {
        this.materialType = other.materialType;
    }
}
