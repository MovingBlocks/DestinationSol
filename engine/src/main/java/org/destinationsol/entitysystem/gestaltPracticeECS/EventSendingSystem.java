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
package org.destinationsol.entitysystem.gestaltPracticeECS;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.In;
import org.destinationsol.entitysystem.ComponentSystem;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.attributes.RegisterUpdateSystem;
import org.destinationsol.game.console.annotations.Command;
import org.destinationsol.game.console.annotations.RegisterCommands;
import org.terasology.gestalt.entitysystem.entity.EntityIterator;


@RegisterUpdateSystem
@RegisterCommands
public class EventSendingSystem extends ComponentSystem  implements UpdateAwareSystem {

    @In
    private EntitySystemManager entitySystemManager;

    @Override
    @Command
    public void update(SolGame game, float timeStep) {
        EntityIterator iterator = entitySystemManager.getEntityManager().iterate(new LocationComponent());
        while (iterator.next()) {
            if (iterator.getEntity().getComponent(LocationComponent.class).isPresent()) {
                Vector2 position = iterator.getEntity().getComponent(LocationComponent.class).get().getPosition();
                entitySystemManager.sendEvent(new LocationUpdateEvent(position), new LocationComponent());
            }
        }
    }
}
