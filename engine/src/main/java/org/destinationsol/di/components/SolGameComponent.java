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
package org.destinationsol.di.components;

import dagger.BindsInstance;
import dagger.Component;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.audio.SpecialSounds;
import org.destinationsol.di.GameModule;
import org.destinationsol.di.Qualifier.NewGame;
import org.destinationsol.di.Qualifier.OnPauseUpdate;
import org.destinationsol.di.Qualifier.OnUpdate;
import org.destinationsol.di.Qualifier.ShipName;
import org.destinationsol.di.Qualifier.Tutorial;
import org.destinationsol.di.SystemsModule;
import org.destinationsol.di.scope.GameScope;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.BeaconHandler;
import org.destinationsol.game.FactionManager;
import org.destinationsol.game.GalaxyFiller;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.GridDrawer;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.MountDetectDrawer;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.ShardBuilder;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.StarPort;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.game.asteroid.AsteroidBuilder;
import org.destinationsol.game.chunk.ChunkManager;
import org.destinationsol.game.drawables.DrawableDebugger;
import org.destinationsol.game.drawables.DrawableManager;
import org.destinationsol.game.farBg.FarBackgroundManagerOld;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.LootBuilder;
import org.destinationsol.game.particle.PartMan;
import org.destinationsol.game.particle.SpecialEffects;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.ship.ShipBuilder;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.TutorialManager;

import java.util.Optional;
import java.util.Set;

@GameScope
@Component(dependencies = SolApplicationComponent.class,modules = {GameModule.class, SystemsModule.class})
public interface SolGameComponent {
    WorldConfig worldConfig();
    SolInputManager inputManager();
    PlanetManager planetManager();
    GalaxyFiller galaxyFiller();
    GameScreens gameScreens();
    SolCam camera();
    ObjectManager objectManager();
    DrawableManager drawableManager();
    ChunkManager chunkManager();
    PartMan partMan();
    AsteroidBuilder asteroidBuilder();
    LootBuilder lootBuilder();
    ShipBuilder shipBuilder();
    HullConfigManager hullConfigManager();
    GridDrawer gridDrawer();
    FarBackgroundManagerOld farBackgroundManagerOld();
    FactionManager factionManager();
    MapDrawer mapDrawer();
    ShardBuilder shardBuilder();
    ItemManager itemManager();
    StarPort.Builder starPortBuilder();
    OggSoundManager soundManager();
    DrawableDebugger drawableDebugger();
    SpecialSounds specialSounds();
    SpecialEffects specialEffects();
    GameColors gameColors();
    BeaconHandler beaconHandler();
    MountDetectDrawer mountDetectDrawer();
    Optional<TutorialManager> tutorialManager();
    GameOptions gameOptions();
    SolApplication solApplication();
    SolGame game();

    @OnUpdate
    Set<UpdateAwareSystem> updateSystems();
    @OnPauseUpdate
    Set<UpdateAwareSystem> onPausedUpdateSystems();

    @Component.Builder
    interface Builder{
        SolGameComponent build();
        Builder setApplicationComponent(SolApplicationComponent component);
        @BindsInstance Builder tutorial(@Tutorial boolean tutorial);
        @BindsInstance Builder newGame(@NewGame boolean newGame);
        @BindsInstance Builder shipName(@ShipName String shipName);

    }
}
