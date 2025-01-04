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
package org.destinationsol;

import org.destinationsol.assets.sound.SpecialSounds;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.SerialisationManager;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.BeaconHandler;
import org.destinationsol.game.FactionManager;
import org.destinationsol.game.GalaxyFiller;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.GridDrawer;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.MountDetectDrawer;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.RubbleBuilder;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolContactListener;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.StarPort;
import org.destinationsol.game.asteroid.AsteroidBuilder;
import org.destinationsol.game.chunk.ChunkManager;
import org.destinationsol.game.drawables.DrawableDebugger;
import org.destinationsol.game.drawables.DrawableManager;
import org.destinationsol.game.faction.FactionsConfigs;
import org.destinationsol.game.farBg.FarBackgroundManagerOld;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.LootBuilder;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.particle.PartMan;
import org.destinationsol.game.particle.SpecialEffects;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.ship.ShipBuilder;
import org.destinationsol.game.tutorial.TutorialManager;
import org.terasology.context.Lifetime;
import org.terasology.gestalt.di.ServiceRegistry;

public class SolGameServiceRegistry extends ServiceRegistry {
    public SolGameServiceRegistry(boolean isTutorial) {
        this.with(SolGame.class).lifetime(Lifetime.Singleton);
        if (isTutorial) {
            this.with(TutorialManager.class).lifetime(Lifetime.Singleton);
        }

        this.with(EntitySystemManager.class);
        this.with(SerialisationManager.class).lifetime(Lifetime.Singleton);

        this.with(DrawableManager.class).lifetime(Lifetime.Singleton);
        this.with(PlanetManager.class).lifetime(Lifetime.Singleton);
        this.with(ChunkManager.class).lifetime(Lifetime.Singleton);
        this.with(PartMan.class).lifetime(Lifetime.Singleton);
        this.with(AsteroidBuilder.class).lifetime(Lifetime.Singleton);
        this.with(LootBuilder.class).lifetime(Lifetime.Singleton);
        this.with(ShipBuilder.class).lifetime(Lifetime.Singleton);
        this.with(GridDrawer.class).lifetime(Lifetime.Singleton);
        this.with(FarBackgroundManagerOld.class).lifetime(Lifetime.Singleton);
        this.with(FactionsConfigs.class).lifetime(Lifetime.Singleton);
        this.with(FactionManager.class).lifetime(Lifetime.Singleton);
        this.with(MapDrawer.class).lifetime(Lifetime.Singleton);
        this.with(RubbleBuilder.class).lifetime(Lifetime.Singleton);
        this.with(ItemManager.class).lifetime(Lifetime.Singleton);
        this.with(EffectTypes.class).lifetime(Lifetime.Singleton);
        this.with(StarPort.Builder.class).lifetime(Lifetime.Singleton);
        this.with(DrawableDebugger.class).lifetime(Lifetime.Singleton);
        this.with(SpecialSounds.class).lifetime(Lifetime.Singleton);
        this.with(SpecialEffects.class).lifetime(Lifetime.Singleton);
        this.with(GameColors.class).lifetime(Lifetime.Singleton);
        this.with(BeaconHandler.class).lifetime(Lifetime.Singleton);
        this.with(MountDetectDrawer.class).lifetime(Lifetime.Singleton);
        this.with(GalaxyFiller.class).lifetime(Lifetime.Singleton);
        this.with(SolContactListener.class).lifetime(Lifetime.Singleton);
        this.with(GameScreens.class).lifetime(Lifetime.Singleton);
        this.with(SolCam.class).lifetime(Lifetime.Singleton);
        this.with(ObjectManager.class).lifetime(Lifetime.Singleton);
        this.with(GameDrawer.class).lifetime(Lifetime.Singleton);
        this.with(AbilityCommonConfigs.class).lifetime(Lifetime.Singleton);
    }
}
