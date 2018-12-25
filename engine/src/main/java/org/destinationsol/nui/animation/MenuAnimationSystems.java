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

package org.destinationsol.nui.animation;

import org.terasology.config.Config;
import org.terasology.config.RenderingConfig;
import org.terasology.registry.CoreRegistry;
import org.destinationsol.nui.animation.SwipeMenuAnimationSystem.Direction;

import java.util.function.Supplier;

/**
 * Controls animations to and from different screens
 */
public final class MenuAnimationSystems {

    private MenuAnimationSystems() {
        // no instances
    }

    public static MenuAnimationSystem createDefaultSwipeAnimation() {
        RenderingConfig config = CoreRegistry.get(Config.class).getRendering();
        MenuAnimationSystem swipe = new SwipeMenuAnimationSystem(0.25f, Direction.LEFT_TO_RIGHT);
        MenuAnimationSystem instant = new MenuAnimationSystemStub();
        Supplier<MenuAnimationSystem> provider = () -> config.isAnimatedMenu() ? swipe : instant;
        return new DeferredMenuAnimationSystem(provider);
    }
}
