/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol.engine;

import org.destinationsol.game.context.Context;
import org.destinationsol.rendering.CanvasRenderer;
import org.terasology.assets.management.AssetTypeManager;
import org.terasology.entitysystem.core.EntityManager;
import org.terasology.entitysystem.event.impl.DelayedEventSystem;
import org.terasology.entitysystem.event.impl.ImmediateEventSystem;
import org.terasology.module.Module;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * A factory provider that builds the core systems for the engine. 
 */
public interface EngineFactory {

    CanvasRenderer canvas();
    EntityManager entityManager();
    DelayedEventSystem delayedEventSystem();
    ImmediateEventSystem immediateEventSystem();
    AssetTypeManager assetTypeManager();
    Context context();
    Set<Module> modules() throws IOException, URISyntaxException;
}
