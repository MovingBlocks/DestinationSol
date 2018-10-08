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

import com.badlogic.gdx.math.Vector2;
import dagger.BindsInstance;
import dagger.Component;
import org.destinationsol.di.LootModule;
import org.destinationsol.di.scope.SolObjectScope;
import org.destinationsol.game.item.Loot;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;

import javax.inject.Named;

@SolObjectScope
@Component(dependencies = SolGameComponent.class, modules = LootModule.class)
public interface SolObjectLootComponent {
    Loot loot();

    @Component.Builder
    interface Builder{
        SolObjectLootComponent build();
        Builder setGameComponent(SolGameComponent component);
        @BindsInstance Builder position(@Named("position") Vector2 position);
        @BindsInstance Builder solItem(SolItem solItem);
        @BindsInstance Builder speed(@Named("speed") Vector2 speed);
        @BindsInstance Builder life(@Named("life") int life);
        @BindsInstance Builder rotationSpeed(@Named("rotationSpeed") float rotationSpeed);
        @BindsInstance Builder shipOwnder(SolShip solShip);
    }
}
