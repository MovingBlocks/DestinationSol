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
import org.destinationsol.di.Qualifier.NewGame;
import org.destinationsol.di.Qualifier.OnPauseUpdate;
import org.destinationsol.di.Qualifier.OnUpdate;
import org.destinationsol.di.Qualifier.ShipName;
import org.destinationsol.di.Qualifier.Tut;
import org.destinationsol.di.components.SolGameComponent;
import org.destinationsol.di.scope.GameScope;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.UpdateAwareSystem;

import javax.inject.Inject;
import java.util.Set;

@Module
public class GameModule {

    @Provides
    @GameScope
    static  SolGame provideSolGame(@OnUpdate Set<UpdateAwareSystem> updateAwareSystems, @OnPauseUpdate Set<UpdateAwareSystem> updatePauseAwareSystems, SolGameComponent gameComponent, @ShipName String shipName, @Tut boolean tut, @NewGame boolean isNewGame){
        return new SolGame(gameComponent,shipName,tut,isNewGame);
    }
}
