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
package org.destinationsol.drawable;

//TODO: Rename this class "Drawable" once the currently existing Drawable interface is no longer needed

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.entitysystem.entity.EntityRef;

import java.util.ArrayList;

/**
 * Contains a reference to an entity's texture and drawable level.
 */
public final class DrawableComponent implements Component<DrawableComponent> {

    public ArrayList<EntityRef> drawables = new ArrayList<>();

    @Override
    public void copy(DrawableComponent other) {
        drawables.clear();
        for (EntityRef drawable : other.drawables) {
            if (drawable.hasComponent(DrawableData.class)) {
                drawables.add(drawable);
            }
        }
    }


}
