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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.Assets;
import org.destinationsol.body.components.BodyLinked;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.game.Rubble;
import org.destinationsol.game.RubbleBuilder;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.health.components.Health;
import org.destinationsol.location.components.Angle;
import org.destinationsol.location.components.Position;
import org.destinationsol.location.components.Velocity;
import org.destinationsol.moneyDropping.components.DropsMoneyOnDestruction;
import org.destinationsol.removal.events.DeletionEvent;
import org.destinationsol.removal.systems.DestructionSystem;
import org.destinationsol.rendering.RenderableElement;
import org.destinationsol.rendering.components.Renderable;
import org.destinationsol.rubble.components.CreatesRubbleOnDestruction;
import org.destinationsol.rubble.components.RubbleMesh;
import org.destinationsol.size.components.Size;
import org.destinationsol.stasis.components.Stasis;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Before;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import javax.inject.Inject;

/**
 * When an entity with a {@link CreatesRubbleOnDestruction} component is destroyed, this system creates {@link Rubble}s
 * where the entity was.
 */
public class RubbleCreationSystem implements EventReceiver {

    public static final float SIZE_TO_RUBBLE_COUNT = 8f;
    public static final float MIN_DIVISIBLE_SIZE = .1f;
    public static final float MIN_SCALE = .1f;
    public static final float MAX_SCALE = .3f;
    private static final float MAX_SPD = 40f;

    @Inject
    protected RubbleBuilder rubbleBuilder;

    @Inject
    protected EntitySystemManager entitySystemManager;

    @Inject
    public RubbleCreationSystem() {
    }

    /**
     * When an entity with a {@link CreatesRubbleOnDestruction} component is destroyed, this creates {@link Rubble} where
     * the entity was, unless the entity is in {@link Stasis}.
     */
    @ReceiveEvent(components = {CreatesRubbleOnDestruction.class, Position.class, Size.class})
    @Before(DestructionSystem.class)
    public EventResult onDeletion(DeletionEvent event, EntityRef entity) {
        if (!entity.hasComponent(Stasis.class)) {
            Velocity velocityComponent;
            Angle angleComponent;

            if (entity.hasComponent(Velocity.class)) {
                velocityComponent = entity.getComponent(Velocity.class).get();
            } else {
                velocityComponent = new Velocity();
                velocityComponent.velocity = new Vector2();
            }

            if (entity.hasComponent(Angle.class)) {
                angleComponent = entity.getComponent(Angle.class).get();
            } else {
                angleComponent = new Angle();
                angleComponent.setAngle(0f);
            }

            buildRubblePieces(entity.getComponent(Position.class).get(), velocityComponent, angleComponent, entity.getComponent(Size.class).get());
        }
        return EventResult.CONTINUE;
    }

    /**
     * This method creates pieces of rubble using values from the object that they are being creating from. It
     * initializes entities for each piece of rubble with the relevant component
     * @param pos Position component of parent entity
     * @param vel Velocity component of parent entity
     * @param angle Angle component of parent entity
     * @param size Size component of parent entity, used to determine amount of rubble to generate
     */
    private void buildRubblePieces(Position pos, Velocity vel, Angle angle, Size size) {
        int count = (int) (size.size * SIZE_TO_RUBBLE_COUNT);
        Vector2 basePos = pos.position;
        for (int i = 0; i < count; i++) {

            //Create graphics component
            RenderableElement element = new RenderableElement();
            element.texture = SolRandom.randomElement(Assets.listTexturesMatching("engine:rubble.*"));
            element.drawableLevel = DrawableLevel.PROJECTILES;
            element.graphicsOffset = new Vector2();

            float scale = SolRandom.randomFloat(MIN_SCALE, MAX_SCALE);
            float scaledSize = scale * size.size;
            element.setSize(scaledSize);

            element.relativePosition = new Vector2();
            element.tint = Color.WHITE;
            Renderable graphicsComponent = new Renderable();
            graphicsComponent.elements.add(element);

            //Create position component
            float velocityAngle = SolRandom.randomFloat(180);
            Vector2 position = new Vector2();
            SolMath.fromAl(position, velocityAngle, SolRandom.randomFloat(scaledSize));
            position.add(basePos);
            Position positionComponent = new Position();
            positionComponent.position = position;

            //Create health component
            Health health = new Health();
            health.currentHealth = 1;

            //Create size component
            Size sizeComponent = new Size();
            sizeComponent.size = scaledSize;

            //Create velocity component
            Velocity velocityComponent = new Velocity();
            Vector2 velocity = SolMath.fromAl(velocityAngle, SolRandom.randomFloat(MAX_SPD));
            velocity.add(vel.velocity);
            velocityComponent.velocity = velocity;

            EntityRef entityRef = entitySystemManager.getEntityManager().createEntity(graphicsComponent, positionComponent,
                    sizeComponent, angle, velocityComponent, new RubbleMesh(), health);

            if (scaledSize > MIN_DIVISIBLE_SIZE) {
                entityRef.setComponent(new CreatesRubbleOnDestruction());
                entityRef.setComponent(new DropsMoneyOnDestruction());
            }

            SolMath.free(velocity);
            entityRef.setComponent(new BodyLinked());
        }
    }
}
