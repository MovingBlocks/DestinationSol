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
package org.destinationsol.meshBuilder;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.destinationsol.Const;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.body.components.BodyLinked;
import org.destinationsol.body.events.BodyCreatedEvent;
import org.destinationsol.body.events.GenerateBodyEvent;
import org.destinationsol.common.In;
import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.location.components.Angle;
import org.destinationsol.location.components.Position;
import org.destinationsol.rendering.RenderableElement;
import org.destinationsol.rendering.components.Renderable;
import org.destinationsol.size.components.Size;
import org.json.JSONArray;
import org.json.JSONObject;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This system creates a {@link Body} for an entity with a {@link BodyLinked} component.
 * <p>
 * The system loads the collision mesh data from a specified JSON file (if the data hasn't already been loaded), and
 * then builds a {@link Body} using that data.
 * <p>
 * Bodies should only be created during an update sent by an {@link UpdateAwareSystem}. Attempting to create a body at
 * any other time may cause the game to crash.
 */
public class CollisionMeshBuilderSystem implements EventReceiver {

    private HashMap<String, CollisionMeshLoader.Model> storedData = new HashMap<>();

    @In
    private World world;

    @In
    private EntitySystemManager entitySystemManager;

    // Reusable objects, used for constructing Body instances
    private final List<Vector2> vectorPool = new ArrayList<>();
    Vector2 reusableVector = new Vector2();
    private final PolygonShape reusablePolygonShape = new PolygonShape();
    private final CircleShape reusableCircleShape = new CircleShape();

    @ReceiveEvent(components = {BodyLinked.class, Size.class, Position.class, Angle.class, Renderable.class})
    public EventResult onGenerateBodyEvent(GenerateBodyEvent event, EntityRef entity) {
        BodyLinked bodyLinkedComponent = entity.getComponent(BodyLinked.class).get();
        String jsonPath = bodyLinkedComponent.getJsonPath();
        if (!storedData.containsKey(jsonPath)) {
            loadDataFromFile(bodyLinkedComponent, jsonPath);
        }
        CollisionMeshLoader.Model model = storedData.get(jsonPath);
        if (model == null) {
            return EventResult.CANCEL;
        }

        float size = entity.getComponent(Size.class).get().size;
        Vector2 position = entity.getComponent(Position.class).get().position;
        float angle = entity.getComponent(Angle.class).get().getAngle();
        ArrayList<RenderableElement> renderableElements = entity.getComponent(Renderable.class).get().elements;

        return createBody(entity, size, position, angle, renderableElements, model);
    }

    /**
     * This loads all the collision mesh data from a file.
     */
    private void loadDataFromFile(BodyLinked bodyLinkedComponent, String jsonPath) {
        String schemaFileName = bodyLinkedComponent.getJsonSchemaFileName();

        if (schemaFileName.equals("engine:schemaCollisionMesh")) {
            JSONObject rootNode = Validator.getValidatedJSON(jsonPath, schemaFileName);
            JSONArray rbNode = rootNode.getJSONArray("rigidBodies");
            for (int i = 0; i < rbNode.length(); i++) {
                JSONObject jsonObject = rbNode.getJSONObject(i);
                readIndividualMeshData(jsonObject, jsonPath, jsonObject.getString("name"));
            }
        } else {
            readIndividualMeshData(Validator.getValidatedJSON(jsonPath, schemaFileName), jsonPath, jsonPath);
        }
    }

    /**
     * This reads a particular collision mesh's data from a given file, and stores it in a model.
     */
    private void readIndividualMeshData(JSONObject rbNode, String jsonPath, String identifier) {
        CollisionMeshLoader.RigidBodyModel rbModel = new CollisionMeshLoader.RigidBodyModel();
        rbModel.name = identifier;

        JSONObject originNode = rbNode.getJSONObject("origin");
        rbModel.origin.x = (float) originNode.getDouble("x");
        rbModel.origin.y = 1 - (float) originNode.getDouble("y");

        // Polygons
        JSONArray polygonNodeArray = rbNode.getJSONArray("polygons");
        for (int i = 0; i < polygonNodeArray.length(); i++) {
            JSONArray polygonNode = polygonNodeArray.getJSONArray(i);
            CollisionMeshLoader.PolygonModel polygonModel = new CollisionMeshLoader.PolygonModel();
            rbModel.polygons.add(polygonModel);

            for (int j = 0; j < polygonNode.length(); j++) {
                JSONObject vertexNode = polygonNode.getJSONObject(j);
                float x = (float) vertexNode.getDouble("x");
                float y = 1 - (float) vertexNode.getDouble("y");
                polygonModel.vertices.add(new Vector2(x, y));
            }

            polygonModel.tmpArray = new Vector2[polygonModel.vertices.size()];
        }

        // Shapes
        JSONArray shapeNodeArray = rbNode.getJSONArray("shapes");
        for (int i = 0; i < shapeNodeArray.length(); i++) {
            JSONObject shapeNode = shapeNodeArray.getJSONObject(i);
            String type = shapeNode.getString("type");
            if (!type.equals("POLYGON")) {
                continue;
            }

            CollisionMeshLoader.PolygonModel shapeModel = new CollisionMeshLoader.PolygonModel();
            rbModel.shapes.add(shapeModel);
            JSONArray vertices = shapeNode.getJSONArray("vertices");
            for (int j = 0; j < vertices.length(); j++) {
                JSONObject vertexNode = vertices.getJSONObject(j);
                float x = (float) vertexNode.getDouble("x");
                float y = 1 - (float) vertexNode.getDouble("y");
                shapeModel.vertices.add(new Vector2(x, y));
            }

            shapeModel.tmpArray = new Vector2[shapeModel.vertices.size()];
        }

        // Circles
        JSONArray circles = rbNode.getJSONArray("circles");
        for (int i = 0; i < circles.length(); i++) {
            CollisionMeshLoader.CircleModel circleModel = new CollisionMeshLoader.CircleModel();
            rbModel.circles.add(circleModel);

            JSONObject circleNode = circles.getJSONObject(i);

            circleModel.center.x = (float) circleNode.getDouble("cx");
            circleModel.center.y = 1 - (float) circleNode.getDouble("cy");
            circleModel.radius = (float) circleNode.getDouble("r");
        }

        CollisionMeshLoader.Model model = new CollisionMeshLoader.Model();
        model.rigidBodies.put(rbModel.name, rbModel);
        storedData.put(jsonPath, model);
    }

    /**
     * Creates a {@link Body} for an entity from a JSON-defined {@link CollisionMeshLoader.Model}.
     */
    private EventResult createBody(EntityRef entity, float size, Vector2 position, float angle, ArrayList<RenderableElement> renderableElements, CollisionMeshLoader.Model model) {
        //This creates an entity with a generic Body. The fixtures, which provide the collision meshes, are attached later.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.angle = angle * MathUtils.degRad;
        bodyDef.angularDamping = 0;
        bodyDef.position.set(position);
        bodyDef.linearDamping = 0;
        Body body = world.createBody(bodyDef);

        //This sets a reference to an entity in the Body, so that the entity can be retrieved from the body during collision handling.
        body.setUserData(entity);

        //This attaches collision meshes to the Body of an entity, based on its graphics.
        for (RenderableElement element : renderableElements) {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = element.density;
            fixtureDef.friction = Const.FRICTION;
            attachFixture(body, element.texture.name, fixtureDef, size, model);

            /*
             * This calculates the offset of the renderable element from "the origin" (as defined in the JSON that the
             * CollisionMeshLoader reads from).
             * The origin is where the center of the object should be. This system creates Fixtures (collision meshes)
             * using that information, so the sprites need to be adjusted to overlay the mesh properly.
             * LibGDX draws sprites from the bottom left corner. Since the position information is from the center, it
             * needs to be adjusted to be at the bottom left of the sprite. To do so, (.5, .5) is subtracted from the
             * origin. (The coordinates are scaled to range from zero to one, so (.5, .5) represents the center.)
             * The originInformation is the information that was read from the JSON, which is used to calculate the
             * graphics offset information.
             */
            CollisionMeshLoader.RigidBodyModel rbModel = model.rigidBodies.get(element.texture.name);
            if (rbModel == null) {
                reusableVector.set(.5f, .5f);
            } else {
                reusableVector.set(rbModel.origin);
            }
            reusableVector.scl(size);
            element.graphicsOffset = reusableVector.cpy();

        }

        entitySystemManager.sendEvent(new BodyCreatedEvent(body), entity);

        return EventResult.CONTINUE;
    }

    /**
     * Creates and applies the fixtures defined in the editor. The name
     * parameter is used to retrieve the right fixture from the loaded file.
     * <br/><br/>
     * <p>
     * The body reference point (the red cross in the tool) is by default
     * located at the bottom left corner of the image. This reference point
     * will be put right over the BodyDef position point. Therefore, you should
     * place this reference point carefully to let you place your body in your
     * world easily with its BodyDef.position point. Note that to draw an image
     * at the position of your body, you will need to know this reference point.
     * <br/><br/>
     * <p>
     * Also, saved shapes are normalized. As shown in the tool, the width of
     * the image is considered to be always 1 meter. Thus, you need to provide
     * a scale factor so the polygons get resized according to your needs.
     *
     * @param body  The Box2d body you want to attach the fixture to.
     * @param name  The name of the fixture you want to load.
     * @param fd    The fixture parameters to apply to the created body fixture.
     * @param scale The desired scale of the body. The default width is 1.
     */
    private boolean attachFixture(Body body, String name, FixtureDef fd, float scale, CollisionMeshLoader.Model model) {
        CollisionMeshLoader.RigidBodyModel rbModel = model.rigidBodies.get(name);
        if (rbModel == null) {
            return false;
        }

        Vector2 origin = reusableVector.set(rbModel.origin).scl(scale);

        for (CollisionMeshLoader.PolygonModel polygon : rbModel.polygons) {
            Vector2[] points = polygon.tmpArray;

            int pointCount = points.length;
            for (int i = 0; i < pointCount; i++) {
                Vector2 origPoint = polygon.vertices.get(pointCount - i - 1);
                points[i] = newVector(origPoint).scl(scale);
                points[i].sub(origin);
            }

            reusablePolygonShape.set(points);
            fd.shape = reusablePolygonShape;
            body.createFixture(fd);

            for (Vector2 point : points) {
                free(point);
            }
        }

        for (CollisionMeshLoader.CircleModel circle : rbModel.circles) {
            Vector2 center = newVector(circle.center).scl(scale).sub(origin);
            float radius = circle.radius * scale;

            reusableCircleShape.setPosition(center);
            reusableCircleShape.setRadius(radius);
            fd.shape = reusableCircleShape;
            body.createFixture(fd);

            free(center);
        }

        return true;
    }

    /**
     * Returns a vector from the vector pool if one exists, or creates a new one if the pool is empty. If a vector is
     * passed in, the returned vector is assigned the same values as the original vector.
     */
    private Vector2 newVector(Vector2 vector) {
        Vector2 res = vectorPool.isEmpty() ? new Vector2() : vectorPool.remove(0);
        if (vector != null) {
            res.set(vector);
        }
        return res;
    }

    /**
     * Adds a vector to the vector pool.
     */
    private void free(Vector2 vector) {
        vectorPool.add(vector);
    }

}
