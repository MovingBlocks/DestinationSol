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
package org.destinationsol.di;

import com.google.common.collect.Sets;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.di.Qualifier.OnPauseUpdate;
import org.destinationsol.di.Qualifier.OnUpdate;
import org.destinationsol.game.BeaconHandler;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.MountDetectDrawer;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.chunk.ChunkManager;
import org.destinationsol.game.drawables.DrawableDebugger;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.ui.TutorialManager;

import java.util.Optional;
import java.util.Set;

@Module
public class UpdateModule {

    @OnUpdate
    @Provides
    @ElementsIntoSet
    public static Set<UpdateAwareSystem> provideUpdateSystems(
            PlanetManager planetManager,
            SolCam camera,
            ChunkManager chunkManager,
            MountDetectDrawer mountDetectDrawer,
            MapDrawer mapDrawer,
            ObjectManager objectManager,
            Optional<TutorialManager> tutorialManager,
            OggSoundManager soundManager,
            BeaconHandler beaconHandler,
            DrawableDebugger drawableDebugger) {
        // the ordering of update aware systems is very important, switching them up can cause bugs!
        Set<UpdateAwareSystem> systems = Sets.newLinkedHashSet();
        systems.add(planetManager);
        systems.add(camera);
        systems.add(chunkManager);
        systems.add(mountDetectDrawer);
        systems.add(objectManager);
        systems.add(mapDrawer);
        systems.add(soundManager);
        systems.add(beaconHandler);
        systems.add(drawableDebugger );
        tutorialManager.ifPresent(systems::add);
        return systems;
    }

    @OnPauseUpdate
    @Provides
    @ElementsIntoSet
    public static Set<UpdateAwareSystem> provideUpdatePauseSystems(MapDrawer mapDrawer, SolCam solCam){
        return Sets.newHashSet(mapDrawer,solCam);
    }
}
