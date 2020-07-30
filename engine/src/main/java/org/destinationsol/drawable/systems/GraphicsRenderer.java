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
package org.destinationsol.drawable.systems;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.In;
import org.destinationsol.drawable.GraphicsElement;
import org.destinationsol.drawable.components.Graphics;
import org.destinationsol.drawable.components.Invisibility;
import org.destinationsol.drawable.events.RenderEvent;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.location.components.Angle;
import org.destinationsol.location.components.Position;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * This handles the drawing of each entity with a {@link Graphics} component.
 */
public class GraphicsRenderer implements EventReceiver {

    @In
    private EntitySystemManager entitySystemManager;

    @In
    private GameDrawer drawer;

    @ReceiveEvent(components = {Graphics.class, Position.class})
    public EventResult onRender(RenderEvent event, EntityRef entity) {

        if (!entity.hasComponent(Invisibility.class)) {

            Graphics graphics = entity.getComponent(Graphics.class).get();
            Vector2 basePosition = entity.getComponent(Position.class).get().position;

            float baseAngle = 0;
            if (entity.hasComponent(Position.class)) {
                baseAngle = entity.getComponent(Angle.class).get().getAngle();
            }

            for (GraphicsElement graphicsElement : graphics.elements) {
                float angle = graphicsElement.relativeAngle + baseAngle;

                drawer.draw(graphicsElement.texture, graphicsElement.width,
                        graphicsElement.height, graphicsElement.width / 2, graphicsElement.height / 2,
                        basePosition.x, basePosition.y, angle, graphicsElement.tint);
            }
        }

        return EventResult.CONTINUE;
    }
}
