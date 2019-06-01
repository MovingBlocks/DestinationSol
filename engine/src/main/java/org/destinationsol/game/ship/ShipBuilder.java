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
package org.destinationsol.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.Faction;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.gun.GunMount;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.Armor;
import org.destinationsol.game.item.Clip;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.item.TradeConfig;
import org.destinationsol.game.item.TradeContainer;
import org.destinationsol.game.particle.LightSource;
import org.destinationsol.game.ship.hulls.Hull;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.ArrayList;
import java.util.List;

public class ShipBuilder {
    public static final float SHIP_DENSITY = 3f;
    public static final float AVG_BATTLE_TIME = 30f;
    public static final float AVG_ALLY_LIFE_TIME = 75f;

    private final CollisionMeshLoader myCollisionMeshLoader;

    public ShipBuilder() {
        myCollisionMeshLoader = new CollisionMeshLoader();
    }

    private static Fixture getBase(boolean hasBase, Body body) {
        if (!hasBase) {
            return null;
        }
        Fixture base = null;
        Vector2 v = SolMath.getVec();
        float lowestX = Float.MAX_VALUE;
        for (Fixture f : body.getFixtureList()) {
            Shape s = f.getShape();
            if (!(s instanceof PolygonShape)) {
                continue;
            }
            PolygonShape poly = (PolygonShape) s;
            int pointCount = poly.getVertexCount();
            for (int i = 0; i < pointCount; i++) {
                poly.getVertex(i, v);
                if (v.x < lowestX) {
                    base = f;
                    lowestX = v.x;
                }
            }
        }
        SolMath.free(v);
        return base;
    }

    public FarShip buildNewFar(SolGame game, Vector2 position, Vector2 velocity, float angle, float rotationSpeed, Pilot pilot,
                               String items, HullConfig hullConfig,
                               RemoveController removeController,
                               boolean hasRepairer, float money, TradeConfig tradeConfig, boolean giveAmmo) {

        if (velocity == null) {
            velocity = new Vector2();
        }
        ItemContainer itemContainer = new ItemContainer();
        game.getItemMan().fillContainer(itemContainer, items);
        Engine.Config ec = hullConfig.getEngineConfig();
        Engine ei = ec == null ? null : ec.exampleEngine.copy();
        TradeContainer tc = tradeConfig == null ? null : new TradeContainer(tradeConfig);

        Gun g1 = null;
        Gun g2 = null;
        Shield shield = null;
        Armor armor = null;

        // For the player use new logic that better respects what was explicitly equipped
        if (pilot.isPlayer()) {
            for (List<SolItem> group : itemContainer) {
                for (SolItem i : group) {
                    if (i instanceof Shield) {
                        if (i.isEquipped() > 0) {
                            shield = (Shield) i;
                            continue;
                        }
                    }
                    if (i instanceof Armor) {
                        if (i.isEquipped() > 0) {
                            armor = (Armor) i;
                            continue;
                        }
                    }
                    if (i instanceof Gun) {
                        Gun g = (Gun) i;
                        if (i.isEquipped() > 0) {
                            int slot = i.isEquipped();
                            if (g1 == null && hullConfig.getGunSlot(0).allowsRotation() != g.config.fixed && slot == 1) {
                                g1 = g;
                                continue;
                            }
                            if (hullConfig.getNrOfGunSlots() > 1 && g2 == null && hullConfig.getGunSlot(1).allowsRotation() != g.config.fixed && slot == 2) {
                                g2 = g;
                            }
                            if (g1 != g && g2 != g) {
                                i.setEquipped(0); // The gun couldn't fit in either slot
                            }
                        }
                    }
                }
            }
        } else {
            // For NPCs use the old logic that just equips whatever
            for (List<SolItem> group : itemContainer) {
                for (SolItem i : group) {
                    if (i instanceof Shield) {
                        shield = (Shield) i;
                        continue;
                    }
                    if (i instanceof Armor) {
                        armor = (Armor) i;
                        continue;
                    }
                    if (i instanceof Gun) {
                        Gun g = (Gun) i;
                        if (g1 == null && hullConfig.getGunSlot(0).allowsRotation() != g.config.fixed) {
                            g1 = g;
                            continue;
                        }
                        if (hullConfig.getNrOfGunSlots() > 1 && g2 == null && hullConfig.getGunSlot(1).allowsRotation() != g.config.fixed) {
                            g2 = g;
                        }
                    }
                }
            }

        }

        if (giveAmmo) {
            addAbilityCharges(itemContainer, hullConfig, pilot);
            addAmmo(itemContainer, g1, pilot);
            addAmmo(itemContainer, g2, pilot);
        }
        return new FarShip(new Vector2(position), new Vector2(velocity), angle, rotationSpeed, pilot, itemContainer, hullConfig, hullConfig.getMaxLife(),
                g1, g2, removeController, ei, hasRepairer ? new ShipRepairer() : null, money, tc, shield, armor);
    }

    private void addAmmo(ItemContainer ic, Gun g, Pilot pilot) {
        if (g == null) {
            return;
        }
        Gun.Config gc = g.config;
        Clip.Config cc = gc.clipConf;
        if (cc.infinite) {
            return;
        }
        float clipUseTime = cc.size * gc.timeBetweenShots + gc.reloadTime;
        float lifeTime = pilot.getFaction() == Faction.LAANI ? AVG_ALLY_LIFE_TIME : AVG_BATTLE_TIME;
        int count = 1 + (int) (lifeTime / clipUseTime) + SolRandom.randomInt(0, 2);
        for (int i = 0; i < count; i++) {
            if (ic.canAdd(cc.example)) {
                ic.add(cc.example.copy());
            }
        }
    }

    private void addAbilityCharges(ItemContainer ic, HullConfig hc, Pilot pilot) {
        if (hc.getAbility() != null) {
            SolItem ex = hc.getAbility().getChargeExample();
            if (ex != null) {
                int count;
                if (pilot.isPlayer()) {
                    count = 3;
                } else {
                    float lifeTime = pilot.getFaction() == Faction.LAANI ? AVG_ALLY_LIFE_TIME : AVG_BATTLE_TIME;
                    count = (int) (lifeTime / hc.getAbility().getRechargeTime() * SolRandom.randomFloat(.3f, 1));
                }
                for (int i = 0; i < count; i++) {
                    ic.add(ex.copy());
                }
            }
        }
    }

    public SolShip build(SolGame game, Vector2 position, Vector2 velocity, float angle, float rotationSpeed, Pilot pilot,
                         ItemContainer container, HullConfig hullConfig, float life, Gun gun1,
                         Gun gun2, RemoveController removeController, Engine engine,
                         ShipRepairer repairer, float money, TradeContainer tradeContainer, Shield shield,
                         Armor armor) {
        ArrayList<Drawable> drawables = new ArrayList<>();
        Hull hull = buildHull(game, position, velocity, angle, rotationSpeed, hullConfig, life, drawables);
        SolShip ship = new SolShip(game, pilot, hull, removeController, drawables, container, repairer, money, tradeContainer, shield, armor);
        hull.getBody().setUserData(ship);
        for (Door door : hull.getDoors()) {
            door.getBody().setUserData(ship);
        }

        hull.setParticleEmitters(game, ship);

        if (engine != null) {
            hull.setEngine(engine);
        }
        if (gun1 != null) {
            GunMount gunMount0 = hull.getGunMount(false);
            if (gunMount0.isFixed() == gun1.config.fixed) {
                gunMount0.setGun(game, ship, gun1, hullConfig.getGunSlot(0).isUnderneathHull(), 1);
            }
        }
        if (gun2 != null) {
            GunMount gunMount1 = hull.getGunMount(true);
            if (gunMount1 != null) {
                if (gunMount1.isFixed() == gun2.config.fixed) {
                    gunMount1.setGun(game, ship, gun2, hullConfig.getGunSlot(1).isUnderneathHull(), 2);
                }
            }
        }
        return ship;
    }

    private Hull buildHull(SolGame game, Vector2 position, Vector2 velocity, float angle, float rotationSpeed, HullConfig hullConfig,
                           float life, ArrayList<Drawable> drawables) {
        //TODO: This logic belongs in the HullConfigManager/HullConfig
        String shipName = hullConfig.getInternalName();

        JSONObject rootNode = Validator.getValidatedJSON(shipName, "engine:schemaHullConfig");

        JSONObject rigidBodyNode = rootNode.getJSONObject("rigidBody");
        myCollisionMeshLoader.readRigidBody(rigidBodyNode, hullConfig);

        BodyDef.BodyType bodyType = hullConfig.getType() == HullConfig.Type.STATION ? BodyDef.BodyType.KinematicBody : BodyDef.BodyType.DynamicBody;
        DrawableLevel level = hullConfig.getType() == HullConfig.Type.STD ? DrawableLevel.BODIES : hullConfig.getType() == HullConfig.Type.BIG ? DrawableLevel.BIG_BODIES : DrawableLevel.STATIONS;
        Body body = myCollisionMeshLoader.getBodyAndSprite(game, hullConfig, hullConfig.getSize(), bodyType, position, angle,
                drawables, SHIP_DENSITY, level, hullConfig.getTexture());
        Fixture shieldFixture = createShieldFixture(hullConfig, body);

        GunMount gunMount0 = new GunMount(hullConfig.getGunSlot(0));
        GunMount gunMount1 = (hullConfig.getNrOfGunSlots() > 1)
                ? new GunMount(hullConfig.getGunSlot(1))
                : null;

        List<LightSource> lCs = new ArrayList<>();
        for (Vector2 p : hullConfig.getLightSourcePositions()) {
            LightSource lc = new LightSource(.35f, true, .7f, p, game.getCols().hullLights);
            lc.collectDrawables(drawables);
            lCs.add(lc);
        }

        ArrayList<ForceBeacon> beacons = new ArrayList<>();
        for (Vector2 relPos : hullConfig.getForceBeaconPositions()) {
            ForceBeacon fb = new ForceBeacon(game, relPos, position, velocity);
            fb.collectDras(drawables);
            beacons.add(fb);
        }

        ArrayList<Door> doors = new ArrayList<>();
        for (Vector2 doorRelPos : hullConfig.getDoorPositions()) {
            Door door = createDoor(game, position, angle, body, doorRelPos);
            door.collectDras(drawables);
            doors.add(door);
        }

        Fixture base = getBase(hullConfig.hasBase(), body);
        Hull hull = new Hull(game, hullConfig, body, gunMount0, gunMount1, base, lCs, life, beacons, doors, shieldFixture);
        body.setLinearVelocity(velocity);
        body.setAngularVelocity(rotationSpeed * MathUtils.degRad);
        return hull;
    }

    private Fixture createShieldFixture(HullConfig hullConfig, Body body) {
        CircleShape shieldShape = new CircleShape();
        shieldShape.setRadius(Shield.SIZE_PERC * hullConfig.getSize());
        FixtureDef shieldDef = new FixtureDef();
        shieldDef.shape = shieldShape;
        shieldDef.isSensor = true;
        Fixture shieldFixture = body.createFixture(shieldDef);
        shieldShape.dispose();
        return shieldFixture;
    }

    private Door createDoor(SolGame game, Vector2 position, float angle, Body body, Vector2 doorRelPos) {
        World w = game.getObjectManager().getWorld();
        TextureAtlas.AtlasRegion tex = Assets.getAtlasRegion("engine:door");
        PrismaticJoint joint = createDoorJoint(body, w, position, doorRelPos, angle);
        RectSprite s = new RectSprite(tex, Door.DOOR_LEN, 0, 0, new Vector2(doorRelPos), DrawableLevel.BODIES, 0, 0, SolColor.WHITE, false);
        return new Door(joint, s);
    }

    private PrismaticJoint createDoorJoint(Body shipBody, World w, Vector2 shipPos, Vector2 doorRelPos, float shipAngle) {
        Body doorBody = createDoorBody(w, shipPos, doorRelPos, shipAngle);
        PrismaticJointDef jd = new PrismaticJointDef();
        jd.initialize(shipBody, doorBody, shipPos, Vector2.Zero);
        jd.localAxisA.set(1, 0);
        jd.collideConnected = false;
        jd.enableLimit = true;
        jd.enableMotor = true;
        jd.lowerTranslation = 0;
        jd.upperTranslation = Door.DOOR_LEN;
        jd.maxMotorForce = 2;
        return (PrismaticJoint) w.createJoint(jd);
    }

    private Body createDoorBody(World world, Vector2 shipPos, Vector2 doorRelPos, float shipAngle) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.angle = shipAngle * MathUtils.degRad;
        bd.angularDamping = 0;
        bd.linearDamping = 0;
        SolMath.toWorld(bd.position, doorRelPos, shipAngle, shipPos);
        Body body = world.createBody(bd);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Door.DOOR_LEN / 2, .03f);
        body.createFixture(shape, SHIP_DENSITY);
        shape.dispose();
        return body;
    }

    public Vector2 getOrigin(String name) {
        return myCollisionMeshLoader.getOrigin(name + ".png", 1);
    }

}
