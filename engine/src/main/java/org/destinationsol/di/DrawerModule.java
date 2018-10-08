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
import org.destinationsol.di.scope.GameScope;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.GridDrawer;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.MountDetectDrawer;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.drawables.DrawableDebugger;
import org.destinationsol.game.drawables.DrawableManager;
import org.destinationsol.game.farBg.FarBackgroundManagerOld;
import org.destinationsol.game.particle.EffectType;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.particle.PartMan;
import org.destinationsol.game.particle.SpecialEffects;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.UiDrawer;

import javax.inject.Singleton;

@Module
public class DrawerModule {
    @GameScope
    @Provides
    static GameDrawer provideGameDrawer(CommonDrawer commonDrawer) {
        return new GameDrawer(commonDrawer);
    }

    @GameScope
    @Provides
    static  MapDrawer provideMapDrawer() {
        return new MapDrawer();
    }

    @GameScope
    @Provides
    static  GridDrawer provideGridDrawer() {
        return new GridDrawer();
    }

    @GameScope
    @Provides
    static  SolCam provideSolCam() {
        return new SolCam();
    }

    @GameScope
    @Provides
    static  DrawableDebugger provideDrawableDebugger() {
        return new DrawableDebugger();
    }

    @GameScope
    @Provides
    static  GameColors provideGameColors() {
        return new GameColors();
    }

    @GameScope
    @Provides
    static  MountDetectDrawer provideMountDetectDrawer() {
        return new MountDetectDrawer();
    }

    @GameScope
    @Provides
    static  PartMan providePartMan() {
        return new PartMan();
    }

    @GameScope
    @Provides
    static  DrawableManager provideDrawableManager(GameDrawer gameDrawer){
        return new DrawableManager(gameDrawer);
    }

    @GameScope
    @Provides
    static FarBackgroundManagerOld provideFarBackgroundManager(){
        return new FarBackgroundManagerOld();
    }

    @GameScope
    @Provides
    static EffectTypes provideEffectTypes(){
        return new EffectTypes();
    }

    @GameScope
    @Provides
    static SpecialEffects provideSpecialEffects(EffectTypes effectTypes,GameColors colors){
        return new SpecialEffects(effectTypes,colors);
    }
}

