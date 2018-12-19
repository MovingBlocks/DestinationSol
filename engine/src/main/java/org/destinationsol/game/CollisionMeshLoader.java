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
package org.destinationsol.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import org.destinationsol.assets.json.Validator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a Model that is used for collision testing from the given json file.
 */
public class CollisionMeshLoader {
    private final Model model = new Model();

    // Reusable stuff
    private final List<Vector2> vectorPool = new ArrayList<>();
    private final PolygonShape polygonShape  = new PolygonShape();
    private final CircleShape circleShape = new CircleShape();
    private final Vector2 vec = new Vector2();

    public CollisionMeshLoader() { }

    /**
     * Creates a Model from the given Hull (Collision Mesh), which can be created using Box2D.
     *
     * @param fileName A ResourceUrn pointing to the collision mesh to be loaded
     */
    public CollisionMeshLoader(String fileName) {
        JSONObject rootNode = Validator.getValidatedJSON(fileName, "engine:schemaCollisionMesh");

        readModel(rootNode);
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
     * at the position of your body, you will need to know this reference point
     * (see {@link #getOrigin(String, float)}.
     * <br/><br/>
     * <p>
     * Also, saved shapes are normalized. As shown in the tool, the width of
     * the image is considered to be always 1 meter. Thus, you need to provide
     * a scale factor so the polygons get resized according to your needs (not
     * every body is 1 meter large in your game, I guess).
     *
     * @param body  The Box2d body you want to attach the fixture to.
     * @param name  The name of the fixture you want to load.
     * @param fd    The fixture parameters to apply to the created body fixture.
     * @param scale The desired scale of the body. The default width is 1.
     */
    public boolean attachFixture(Body body, String name, FixtureDef fd, float scale) {
        RigidBodyModel rbModel = model.rigidBodies.get(name);
        if (rbModel == null) {
            return false;
        }

        Vector2 origin = vec.set(rbModel.origin).scl(scale);

        for (PolygonModel polygon : rbModel.polygons) {
            Vector2[] points = polygon.tmpArray;

            int pointCount = points.length;
            for (int i = 0; i < pointCount; i++) {
                Vector2 origPoint = polygon.vertices.get(pointCount - i - 1);
                points[i] = newVec(origPoint).scl(scale);
                points[i].sub(origin);
            }

            polygonShape.set(points);
            fd.shape = polygonShape;
            body.createFixture(fd);

            for (Vector2 point : points) {
                free(point);
            }
        }

        for (CircleModel circle : rbModel.circles) {
            Vector2 center = newVec(circle.center).scl(scale).sub(origin);
            float radius = circle.radius * scale;

            circleShape.setPosition(center);
            circleShape.setRadius(radius);
            fd.shape = circleShape;
            body.createFixture(fd);

            free(center);
        }

        return true;
    }

    /**
     * Gets the origin point attached to the given name. Since the point is
     * normalized in [0,1] coordinates, it needs to be scaled to your body
     * size. Warning: this method returns the same Vector2 object each time, so
     * copy it if you need it for later use.
     */
    public Vector2 getOrigin(String name, float scale) {
        RigidBodyModel rbModel = model.rigidBodies.get(name);
        if (rbModel == null) {
            vec.set(.5f, .5f);
        } else {
            vec.set(rbModel.origin);
        }
        vec.scl(scale);
        return vec;
    }

    /**
     * <b>For advanced users only.</b> Lets you access the internal model of
     * this loader and modify it. Be aware that any modification is permanent
     * and that you should really know what you are doing.
     */
    public Model getInternalModel() {
        return model;
    }

    private void readModel(JSONObject rootNode) {
        JSONArray rbNode = rootNode.getJSONArray("rigidBodies");
        for (int i = 0; i < rbNode.length(); i++) {
            readRigidBody(rbNode.getJSONObject(i));
        }
    }

    public void readRigidBody(JSONObject rbNode) {
        readRigidBody(rbNode, rbNode.getString("name"));
    }

    public void readRigidBody(JSONObject rbNode, HullConfig hullConfig) {
        String shipName = hullConfig.getInternalName();
        readRigidBody(rbNode, shipName);
    }

    private void readRigidBody(JSONObject rbNode, String shipName) {
        RigidBodyModel rbModel = new RigidBodyModel();
        rbModel.name = shipName;

        JSONObject originNode = rbNode.getJSONObject("origin");
        rbModel.origin.x = (float) originNode.getDouble("x");
        rbModel.origin.y = 1 - (float) originNode.getDouble("y");

        // Polygons
        JSONArray polygonNodeArray = rbNode.getJSONArray("polygons");
        for (int i = 0; i < polygonNodeArray.length(); i++) {
            JSONArray polygonNode = polygonNodeArray.getJSONArray(i);
            PolygonModel polygonModel = new PolygonModel();
            rbModel.polygons.add(polygonModel);

            for (int j = 0; j < polygonNode.length(); j++) {
                JSONObject vertexNode = polygonNode.getJSONObject(j);
                float x = (float) vertexNode.getDouble("x");
                float y = 1 - (float) vertexNode.getDouble("y");
                polygonModel.vertices.add(new Vector2(x, y));
            }

            // Why do we need this? Investigate.
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

            PolygonModel shapeModel = new PolygonModel();
            rbModel.shapes.add(shapeModel);
            JSONArray vertices = shapeNode.getJSONArray("vertices");
            for (int j = 0; j < vertices.length(); j++) {
                JSONObject vertexNode = vertices.getJSONObject(j);
                float x = (float) vertexNode.getDouble("x");
                float y = 1 - (float) vertexNode.getDouble("y");
                shapeModel.vertices.add(new Vector2(x, y));
            }

            // Why do we need this? Investigate.
            shapeModel.tmpArray = new Vector2[shapeModel.vertices.size()];
        }

        // Circles
        JSONArray circles = rbNode.getJSONArray("circles");
        for (int i = 0; i < circles.length(); i++) {
            CircleModel circleModel = new CircleModel();
            rbModel.circles.add(circleModel);

            JSONObject circleNode = circles.getJSONObject(i);

            circleModel.center.x = (float) circleNode.getDouble("cx");
            circleModel.center.y = 1 - (float) circleNode.getDouble("cy");
            circleModel.radius = (float) circleNode.getDouble("r");
        }

        model.rigidBodies.put(rbModel.name, rbModel);
    }

    private Vector2 newVec(Vector2 v) {
        Vector2 res = vectorPool.isEmpty() ? new Vector2() : vectorPool.remove(0);
        if (v != null) {
            res.set(v);
        }
        return res;
    }

    private void free(Vector2 v) {
        vectorPool.add(v);
    }

    /**
     * This needs refactoring...
     *
     * @param drawables a atlas will be added here
     */
    public Body getBodyAndSprite(SolGame game, HullConfig hullConfig, float scale, BodyDef.BodyType type,
                                 Vector2 position, float angle, List<Drawable> drawables, float density, DrawableLevel level, TextureAtlas.AtlasRegion tex) {
        final String name = hullConfig.getInternalName();

        BodyDef bd = new BodyDef();
        bd.type = type;
        bd.angle = angle * MathUtils.degRad;
        bd.angularDamping = 0;
        bd.position.set(position);
        bd.linearDamping = 0;
        Body body = game.getObjectManager().getWorld().createBody(bd);
        FixtureDef fd = new FixtureDef();
        fd.density = density;
        fd.friction = Const.FRICTION;
        Vector2 orig;
        boolean found = attachFixture(body, name, fd, scale);
        if (!found) {
            DebugOptions.MISSING_PHYSICS_ACTION.handle("Could not find physics data for " + name);
            fd.shape = new CircleShape();
            fd.shape.setRadius(scale / 2);
            body.createFixture(fd);
            fd.shape.dispose();
        }

        orig = hullConfig.getShipBuilderOrigin();
        if (tex == null) {
            tex = hullConfig.getTexture();
        }
        RectSprite s = new RectSprite(tex, scale, orig.x - .5f, orig.y - .5f, new Vector2(), level, 0, 0, SolColor.WHITE, false);
        drawables.add(s);
        return body;
    }

    /**
     * This needs refactoring...
     *
     * @param drawables a atlas will be added here
     */
    public Body getBodyAndSprite(SolGame game, TextureAtlas.AtlasRegion tex, float scale, BodyDef.BodyType type,
                                 Vector2 position, float angle, List<Drawable> drawables, float density, DrawableLevel level) {
        BodyDef bd = new BodyDef();
        bd.type = type;
        bd.angle = angle * MathUtils.degRad;
        bd.angularDamping = 0;
        bd.position.set(position);
        bd.linearDamping = 0;
        Body body = game.getObjectManager().getWorld().createBody(bd);
        FixtureDef fd = new FixtureDef();
        fd.density = density;
        fd.friction = Const.FRICTION;
        Vector2 orig;
        boolean found = attachFixture(body, tex.name, fd, scale);
        if (!found) {
            DebugOptions.MISSING_PHYSICS_ACTION.handle("Could not find physics data for " + tex.name);
            fd.shape = new CircleShape();
            fd.shape.setRadius(scale / 2);
            body.createFixture(fd);
            fd.shape.dispose();
        }

        orig = getOrigin(tex.name, 1);
        RectSprite s = new RectSprite(tex, scale, orig.x - .5f, orig.y - .5f, new Vector2(), level, 0, 0, SolColor.WHITE, false);
        drawables.add(s);

        return body;
    }

    public static class Model {
        public final Map<String, RigidBodyModel> rigidBodies = new HashMap<>();
    }

    public static class RigidBodyModel {
        public final Vector2 origin = new Vector2();
        public final List<PolygonModel> polygons = new ArrayList<>();
        public final List<PolygonModel> shapes = new ArrayList<>();
        public final List<CircleModel> circles = new ArrayList<>();
        public String name;
    }

    public static class PolygonModel {
        public final List<Vector2> vertices = new ArrayList<>();
        private Vector2[] tmpArray; // Used to avoid allocation in attachFixture()
    }

    public static class CircleModel {
        public final Vector2 center = new Vector2();
        public float radius;
    }
}
