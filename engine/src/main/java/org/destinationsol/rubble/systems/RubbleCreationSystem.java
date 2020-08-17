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
package org.destinationsol.rubble.systems;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.In;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.game.Rubble;
import org.destinationsol.game.RubbleBuilder;
import org.destinationsol.game.SolGame;
import org.destinationsol.location.components.Position;
import org.destinationsol.location.components.Velocity;
import org.destinationsol.removal.events.DeletionEvent;
import org.destinationsol.removal.systems.DestructionSystem;
import org.destinationsol.rubble.components.CreatesRubbleOnDestruction;
import org.destinationsol.size.components.Size;
import org.destinationsol.stasis.components.Stasis;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Before;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * When an entity with a {@link CreatesRubbleOnDestruction} component is destroyed, this system creates {@link Rubble}s
 * where the entity was.
 */
public class RubbleCreationSystem implements EventReceiver {

    @In
    private RubbleBuilder rubbleBuilder;

    @In
    private SolGame solGame;

    //TODO once Shards are entities, this needs to be refactored to replace ShardBuilder

    /**
     * When an entity with a {@link CreatesRubbleOnDestruction} component is destroyed, this creates {@link Rubble}s where
     * the entity was, unless the entity is in {@link Stasis}.
     */
    @ReceiveEvent(components = {CreatesRubbleOnDestruction.class, Position.class, Size.class})
    @Before(DestructionSystem.class)
    public EventResult onDeletion(DeletionEvent event, EntityRef entity) {
        if (!entity.hasComponent(Stasis.class)) {
            Vector2 position = entity.getComponent(Position.class).get().position;
            Vector2 velocity;
            if (entity.hasComponent(Velocity.class)) {
                velocity = entity.getComponent(Velocity.class).get().velocity;
            } else {
                velocity = new Vector2();
            }
            float size = entity.getComponent(Size.class).get().size;
            rubbleBuilder.buildExplosionShards(solGame, position, velocity, size);
        }
        return EventResult.CONTINUE;
    }
}
