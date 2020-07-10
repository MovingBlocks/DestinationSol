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
package org.destinationsol.asteroids.systems;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.assets.Assets;
import org.destinationsol.asteroids.components.Asteroid;
import org.destinationsol.body.events.BodyCreatedEvent;
import org.destinationsol.body.events.BodyUpdateEvent;
import org.destinationsol.body.events.GenerateBodyEvent;
import org.destinationsol.common.In;
import org.destinationsol.common.SolRandom;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.location.components.Angle;
import org.destinationsol.location.components.Position;
import org.destinationsol.size.components.Size;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AsteroidBodyCreationSystem implements EventReceiver {

    private static final float DENSITY = 10f;

    @In
    private EntitySystemManager entitySystemManager;

    //TODO
    @In
    private World world;

    private final CollisionMeshLoader collisionMeshLoader = new CollisionMeshLoader("engine:asteroids");
    private final List<TextureAtlas.AtlasRegion> textures = Assets.listTexturesMatching("engine:asteroid_.*");

    @ReceiveEvent(components = {Asteroid.class, Size.class, Position.class, Angle.class})
    public EventResult onGenerateBody(GenerateBodyEvent event, EntityRef entity) {

        TextureAtlas.AtlasRegion texture = SolRandom.randomElement(textures);
        float size = entity.getComponent(Size.class).get().size;
        Vector2 position = entity.getComponent(Position.class).get().position;
        float angle = entity.getComponent(Angle.class).get().getAngle();

        ArrayList<Drawable> drawables = new ArrayList<>();

        Body body = collisionMeshLoader.getBodyAndSprite(world, texture, size, BodyDef.BodyType.DynamicBody, position, angle, drawables, DENSITY, DrawableLevel.BODIES);

        entitySystemManager.sendEvent(new BodyCreatedEvent(body), entity);

        return EventResult.CONTINUE;
    }
}
