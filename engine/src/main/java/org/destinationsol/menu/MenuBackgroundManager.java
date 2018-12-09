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
package org.destinationsol.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.ui.DisplayDimensions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuBackgroundManager {

    private final Model model = new Model();

    private final List<Vector2> vectorPool = new ArrayList<>();
    private final PolygonShape polygonShape = new PolygonShape();
    private final CircleShape circleShape = new CircleShape();
    private final Vector2 vec = new Vector2();
    private final World world;

    private final DisplayDimensions displayDimensions;

    public MenuBackgroundManager(String fileName, DisplayDimensions displayDimensions) {
        world = new World(new Vector2(0, 0), true);
        MenuContactListener contactListener = new MenuContactListener();
        world.setContactListener(contactListener);
        this.displayDimensions = displayDimensions;

        Json json = Assets.getJson(fileName);
        JSONObject rootNode = json.getJsonValue();

        Validator.validate(rootNode, "engine:schemaCollisionMesh");

        readModel(rootNode);

        json.dispose();
    }

    public BackgroundAsteroid buildAsteroid() {

        TextureAtlas.AtlasRegion texture = Assets.getAtlasRegion(SolRandom.test(0.5f) ? "engine:asteroid_0" : "engine:asteroid_1");

        boolean small = SolRandom.test(.8f);
        float scale = (small ? .1f : .4f) * SolRandom.randomFloat(.5f, 1);
        Color tint = new Color();
        SolColorUtil.fromHSB(SolRandom.randomFloat(0, 1), .25f, 1, .7f, tint);

        float radiusX = (float) (texture.originalHeight) / displayDimensions.getWidth() * scale / 2;
        float radiusY = (float) (texture.originalHeight) / displayDimensions.getHeight() * scale / 2;

        float r = displayDimensions.getRatio();
        Vector2 velocity, position;
        if (SolRandom.test(0.5f)) {
            // Spawn to the left or right of screen
            boolean toLeft = SolRandom.test(1f);
            velocity = new Vector2((float) Math.pow(SolRandom.randomFloat(toLeft ? 0.025f : -0.1f, toLeft ? 0.1f : 0.025f), 2), (float) Math.pow(SolRandom.randomFloat(0.095f), 2));
            position = new Vector2(r / 2 + (toLeft ? -1 : 1) * (r / 2 + radiusX) - radiusX, 0.5f + SolRandom.randomFloat(0.5f + radiusY) - radiusY);
        } else {
            // Spawn at the top or bottom of screen
            boolean atTop = SolRandom.test(1f);
            velocity = new Vector2((float) Math.pow(SolRandom.randomFloat(0.095f), 3), (float) Math.pow(SolRandom.randomFloat(atTop ? -0.025f : 0.025f, atTop ? -0.1f : 0.1f), 2));
            position = new Vector2(r / 2 + SolRandom.randomFloat(r / 2 + radiusX) - radiusX, 0.5f + (atTop ? -1 : 1) * (0.5f + radiusY) - radiusY);
        }

        float angle = SolRandom.randomFloat((float) Math.PI);
        float angularVelocity = SolRandom.randomFloat(1.5f);
        velocity.scl(50);

        Body body = getBodyAndSprite(texture, scale*0.89f, BodyDef.BodyType.DynamicBody, position, angle, new ArrayList<>(), 10f, DrawableLevel.BODIES);
        body.setLinearVelocity(velocity);
        body.setAngularVelocity(angularVelocity);
        BackgroundAsteroid asteroid = new BackgroundAsteroid(texture, scale, tint, position, velocity, angle, angularVelocity, body);
        body.setUserData(asteroid);

        return asteroid;
    }


    public void update() {
        world.step(Const.REAL_TIME_STEP, 6, 2);
    }

    public Body getBodyAndSprite(TextureAtlas.AtlasRegion tex, float scale, BodyDef.BodyType type,
                                 Vector2 position, float angle, List<Drawable> drawables, float density, DrawableLevel level) {
        BodyDef bd = new BodyDef();
        bd.type = type;
        bd.angle = angle * MathUtils.degRad;
        bd.angularDamping = 0;
        bd.position.set(position);
        bd.linearDamping = 0;
        Body body = world.createBody(bd);
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

    public Model getInternalModel() {
        return model;
    }

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
