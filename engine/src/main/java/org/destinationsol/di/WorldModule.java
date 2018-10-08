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

import dagger.Module;
import dagger.Provides;
import org.destinationsol.CommonDrawer;
import org.destinationsol.GameOptions;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.di.Qualifier.NewGame;
import org.destinationsol.di.Qualifier.ShipName;
import org.destinationsol.di.Qualifier.Tut;
import org.destinationsol.di.components.SolGameComponent;
import org.destinationsol.di.scope.GameScope;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.BeaconHandler;
import org.destinationsol.game.FactionManager;
import org.destinationsol.game.GalaxyFiller;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.Hero;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.PlayerCreator;
import org.destinationsol.game.RespawnState;
import org.destinationsol.game.SaveManager;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolContactListener;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.game.chunk.ChunkManager;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.ui.TutorialManager;

import java.util.Optional;

@Module
public class WorldModule {


    @GameScope
    @Provides
    public SolGame provideSolGame(SolGameComponent gameComponent,
                                      @ShipName String shipName,
                                      @Tut boolean tut,
                                      @NewGame boolean isNewGame) {
        return new SolGame(gameComponent, shipName, tut, isNewGame);
    }

    @GameScope
    @Provides
    public ItemManager provideItemManager(OggSoundManager oggSoundManager, EffectTypes effectTypes, GameColors colors) {
        return new ItemManager(oggSoundManager, effectTypes, colors);
    }


    @GameScope
    @Provides
    public BeaconHandler provideBeaconHandler() {
        return new BeaconHandler();
    }

    @GameScope
    @Provides
    public AbilityCommonConfigs provideAbilityConfig(EffectTypes effectTypes, GameColors cols, OggSoundManager soundManager) {
        return new AbilityCommonConfigs(effectTypes, cols, soundManager);
    }

    @GameScope
    @Provides
    public HullConfigManager provideHullConfigProvider(ItemManager itemManager, AbilityCommonConfigs abilityCommonConfigs) {
        return new HullConfigManager(itemManager, abilityCommonConfigs);
    }

    @GameScope
    @Provides
    public PlanetManager providePlanetManager(HullConfigManager hullConfigs, GameColors cols, ItemManager itemManager) {
        return new PlanetManager(hullConfigs, cols, itemManager);
    }

    @GameScope
    @Provides
    public SolContactListener provideSolContactListener(SolGame game){
        return new SolContactListener(game);
    }


    @GameScope
    @Provides
    public FactionManager provideFactionManager(){
        return new FactionManager();
    }

    @GameScope
    @Provides
    public ObjectManager provideObjectManager(SolContactListener contactListener, FactionManager factionManager){
        return new ObjectManager(contactListener,factionManager);
    }

    @GameScope
    @Provides
    public Optional<TutorialManager> provideTutorialManager(@Tut boolean isTut, GameScreens gameScreens, GameOptions gameOptions, SolGame solGame) {
        if (isTut)
            return Optional.of(new TutorialManager(gameScreens, isTut, gameOptions, solGame));
        return Optional.empty();
    }

    @Provides
    @GameScope
    public GalaxyFiller provideGalaxyFiller(HullConfigManager hullConfigManager){
        return new GalaxyFiller(hullConfigManager);
    }


    @GameScope
    @Provides
    public ChunkManager provideChunkManager(){
        return new ChunkManager();
    }
}
