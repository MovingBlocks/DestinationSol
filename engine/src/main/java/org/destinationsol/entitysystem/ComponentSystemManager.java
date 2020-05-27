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
package org.destinationsol.entitysystem;

import com.google.common.collect.Lists;
import org.destinationsol.game.context.Context;
import org.destinationsol.util.InjectionHelper;
import org.terasology.gestalt.module.ModuleEnvironment;

import java.util.ArrayList;

public class ComponentSystemManager {

    private ArrayList<ComponentSystem> componentSystems = Lists.newArrayList();

    public ComponentSystemManager(ModuleEnvironment environment, Context context) {
        for (Class<? extends ComponentSystem> componentSystem : environment.getSubtypesOf(ComponentSystem.class)) {
            try {
                ComponentSystem system = componentSystem.newInstance();
                InjectionHelper.inject(system, context);
                componentSystems.add(system);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void preBegin() {
        componentSystems.forEach(ComponentSystem::preBegin);
    }
}
