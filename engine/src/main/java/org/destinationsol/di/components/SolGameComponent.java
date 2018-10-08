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
import org.destinationsol.di.DrawerModule;
import org.destinationsol.di.GameModule;
import org.destinationsol.di.Qualifier.NewGame;
import org.destinationsol.di.Qualifier.OnPauseUpdate;
import org.destinationsol.di.Qualifier.OnUpdate;
import org.destinationsol.di.Qualifier.ShipName;
import org.destinationsol.di.Qualifier.Tut;
import org.destinationsol.di.SolObjectFactoryModule;
import org.destinationsol.di.UpdateModule;
import org.destinationsol.di.WorldModule;
import org.destinationsol.di.scope.GameScope;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.WorldConfig;

import javax.inject.Inject;
import java.util.Set;

@GameScope
@Component(dependencies = SolApplicationComponent.class,modules = {DrawerModule.class, SolObjectFactoryModule.class, WorldModule.class, GameModule.class, UpdateModule.class})
public interface SolGameComponent {

    void inject(SolGame game);
    SolGame game();
    WorldConfig worldConfig();

    @OnUpdate
    Set<UpdateAwareSystem> updateSystems();
    @OnPauseUpdate
    Set<UpdateAwareSystem> onPausedUpdateSystems();

    @Component.Builder
    interface Builder{
        SolGameComponent build();
        Builder setApplicationComponent(SolApplicationComponent component);
        @BindsInstance Builder tutorial(@Tut boolean tutorial);
        @BindsInstance Builder newGame(@NewGame boolean newGame);
        @BindsInstance Builder shipName(@ShipName String shipName);

    }
}
