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
package org.destinationsol.rendering.components;

import org.destinationsol.rendering.RenderableElement;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.ArrayList;

/**
 * Contains a list of the different elements that combine to visually represent an entity.
 */
public final class Renderable implements Component<Renderable> {

    public ArrayList<RenderableElement> elements = new ArrayList<>();

    @Override
    public void copy(Renderable other) {
        ArrayList<RenderableElement> newElements = new ArrayList<>();
        for (int index = 0; index < other.elements.size(); index++) {
            RenderableElement data = new RenderableElement();
            data.copy(other.elements.get(index));
            newElements.add(data);
        }

        this.elements = newElements;
    }
}
