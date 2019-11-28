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
package org.destinationsol.game.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.BuildableSystem;
import org.destinationsol.game.ConfigurationSystem;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolNames;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.Hull;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.modules.ModuleManager;
import org.json.JSONObject;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlanetManager implements UpdateAwareSystem {
    private final ArrayList<SolSystem> systems;
    private final ArrayList<Planet> planets;
    private final ArrayList<SystemBelt> belts;
    private final FlatPlaceFinder flatPlaceFinder;
    private final PlanetConfigs planetConfigs;
    private final ArrayList<ConfigurationSystem> configurationSystems;
    private final ArrayList<BuildableSystem> buildableSystems;
    private final SunSingleton sunSingleton;
    private final SysConfigs sysConfigs;
    private final PlanetCoreSingleton planetCoreSingleton;
    private Planet nearestPlanet;

    public PlanetManager(HullConfigManager hullConfigs, GameColors cols, ItemManager itemManager) {
        planetConfigs = new PlanetConfigs(hullConfigs, cols, itemManager);
        sysConfigs = new SysConfigs(hullConfigs, itemManager);

        // initialise all ConfigurationsSystems found by gestalt
        configurationSystems = new ArrayList<>();
        for (Class<?> configurationSystem : ModuleManager.getEnvironment().getSubtypesOf(ConfigurationSystem.class)) {
            try {
                ConfigurationSystem system = (ConfigurationSystem) configurationSystem.newInstance();
                final Set<ResourceUrn> configUrns = Assets.getAssetHelper().list(Json.class, system.getConfigurationLocations());

                // load all the configurations
                for (ResourceUrn configUrn : configUrns) {
                    String moduleName = configUrn.getModuleName().toString();
                    JSONObject rootNode = Validator.getValidatedJSON(configUrn.toString(), system.getJSONValidatorLocation());
                    for (String name : rootNode.keySet()) {
                        if (!(rootNode.get(name) instanceof JSONObject))
                            continue;
                        JSONObject node = rootNode.getJSONObject(name);
                        system.loadConfiguration(moduleName, name, node, hullConfigs, itemManager);
                    }
                }
                configurationSystems.add(system);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        systems = new ArrayList<>();
        buildableSystems = new ArrayList<>();
        planets = new ArrayList<>();
        belts = new ArrayList<>();
        flatPlaceFinder = new FlatPlaceFinder();
        sunSingleton = new SunSingleton();
        planetCoreSingleton = new PlanetCoreSingleton();
    }

    public void fill(SolNames names, int numberOfSystems) {
        new SystemsBuilder().build(systems, planets, belts, planetConfigs, configurationSystems, buildableSystems, sysConfigs, names, numberOfSystems);
    }

    @Override
    public void update(SolGame game, float timeStep) {
        Vector2 camPos = game.getCam().getPosition();
        for (Planet planet : planets) {
            planet.update(game, timeStep);
        }

        for (BuildableSystem system : buildableSystems) {
            system.update(game, timeStep);
        }

        nearestPlanet = getNearestPlanet(camPos);

        SolSystem nearestSys = getNearestSystem(camPos);
        applyGrav(game, nearestSys);
    }

    public Planet getNearestPlanet(Vector2 position) {
        float minDst = Float.MAX_VALUE;
        Planet res = null;
        for (Planet planet : planets) {
            float dst = position.dst(planet.getPosition());
            if (dst < minDst) {
                minDst = dst;
                res = planet;
            }
        }
        return res;
    }

    private void applyGrav(SolGame game, SolSystem nearestSys) {
        float npGh = nearestPlanet.getGroundHeight();
        float npFh = nearestPlanet.getFullHeight();
        float npMinH = nearestPlanet.getMinGroundHeight();
        Vector2 npPos = nearestPlanet.getPosition();
        Vector2 sysPos = nearestSys.getPosition();
        float npGravConst = nearestPlanet.getGravitationConstant();

        List<SolObject> objs = game.getObjectManager().getObjects();
        for (SolObject obj : objs) {
            if (!obj.receivesGravity()) {
                continue;
            }

            Vector2 objPos = obj.getPosition();
            float minDist;
            Vector2 srcPos;
            float gravConst;
            boolean onPlanet;
            float toNp = npPos.dst(objPos);
            float toSys = sysPos.dst(objPos);
            if (toNp < npFh) {
                if (recoverObj(obj, toNp, npMinH)) {
                    continue;
                }
                minDist = npGh;
                srcPos = npPos;
                gravConst = npGravConst;
                onPlanet = true;
            } else if (toSys < Const.SUN_RADIUS) {
                minDist = SunSingleton.SUN_HOT_RAD;
                srcPos = sysPos;
                gravConst = SunSingleton.GRAV_CONST;
                onPlanet = false;
            } else {
                continue;
            }

            Vector2 grav = SolMath.getVec(srcPos);
            grav.sub(objPos);
            float len = grav.len();
            grav.nor();
            if (len < minDist) {
                len = minDist;
            }
            float g = gravConst / len / len;
            grav.scl(g);
            obj.receiveForce(grav, game, true);
            SolMath.free(grav);
            if (!onPlanet) {
                sunSingleton.doDmg(game, obj, toSys);
            }
        }

    }

    private boolean recoverObj(SolObject obj, float toNp, float npMinH) {
        if (npMinH < toNp) {
            return false;
        }
        if (!(obj instanceof SolShip)) {
            return false;
        }
        SolShip ship = (SolShip) obj;
        Hull hull = ship.getHull();
        if (hull.config.getType() == HullConfig.Type.STATION) {
            return false;
        }
        float fh = nearestPlanet.getFullHeight();
        Vector2 npPos = nearestPlanet.getPosition();
        Vector2 toShip = SolMath.distVec(npPos, ship.getPosition());
        float len = toShip.len();
        if (len == 0) {
            toShip.set(0, fh);
        } else {
            toShip.scl(fh / len);
        }
        toShip.add(npPos);
        Body body = hull.getBody();
        body.setTransform(toShip, 0);
        body.setLinearVelocity(Vector2.Zero);
        SolMath.free(toShip);
        return true;
    }

    public Planet getNearestPlanet() {
        return nearestPlanet;
    }

    public void drawDebug(GameDrawer drawer, SolGame game) {
        if (DebugOptions.DRAW_PLANET_BORDERS) {
            SolCam cam = game.getCam();
            float lineWidth = cam.getRealLineWidth();
            float viewHeight = cam.getViewHeight();
            for (Planet planet : planets) {
                Vector2 position = planet.getPosition();
                float angle = planet.getAngle();
                float fullHeight = planet.getFullHeight();
                Color color = planet == nearestPlanet ? SolColor.WHITE : SolColor.G;
                drawer.drawCircle(drawer.debugWhiteTexture, position, planet.getGroundHeight(), color, lineWidth, viewHeight);
                drawer.drawCircle(drawer.debugWhiteTexture, position, fullHeight, color, lineWidth, viewHeight);
                drawer.drawLine(drawer.debugWhiteTexture, position.x, position.y, angle, fullHeight, color, lineWidth);
            }

        }
    }

    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    public ArrayList<SystemBelt> getBelts() {
        return belts;
    }

    public ArrayList<SolSystem> getSystems() {
        return systems;
    }

    public Vector2 findFlatPlace(SolGame game, Planet planet, ConsumedAngles takenAngles,
                                 float objHalfWidth) {
        return flatPlaceFinder.find(game, planet, takenAngles, objHalfWidth);
    }

    public ArrayList<BuildableSystem> getBuildableSystems() {
        return buildableSystems;
    }

    public SolSystem getNearestSystem(Vector2 position) {
        float minDst = Float.MAX_VALUE;
        SolSystem res = null;
        for (SolSystem system : systems) {
            float dst = position.dst(system.getPosition());
            if (dst < minDst) {
                minDst = dst;
                res = system;
            }
        }
        return res;
    }

    public BuildableSystem getNearestBuildableSystem(Vector2 position) {
        float minDst = Float.MAX_VALUE;
        BuildableSystem res = null;
        for (BuildableSystem system : buildableSystems) {
            float dst = position.dst(system.getPosition());
            if (dst < minDst) {
                minDst = dst;
                res = system;
            }
        }
        return res;
    }

    public void drawSunHack(SolGame game, GameDrawer drawer) {
        sunSingleton.draw(game, drawer);
    }

    public void drawPlanetCoreHack(SolGame game, GameDrawer drawer) {
        planetCoreSingleton.draw(game, drawer);
    }
}
