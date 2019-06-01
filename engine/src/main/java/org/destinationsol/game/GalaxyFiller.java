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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.json.Validator;
import org.json.JSONObject;
import com.google.gson.JsonParseException;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.ExplorerDestProvider;
import org.destinationsol.game.input.Guardian;
import org.destinationsol.game.input.MoveDestProvider;
import org.destinationsol.game.input.NoDestProvider;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.TradeConfig;
import org.destinationsol.game.maze.Maze;
import org.destinationsol.game.planet.ConsumedAngles;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.planet.SolSystem;
import org.destinationsol.game.planet.SysConfig;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.ArrayList;

public class GalaxyFiller {
    private static final float STATION_CONSUME_SECTOR = 45f;
    private final HullConfigManager hullConfigManager;
    private Vector2 mainStationPos = new Vector2();
    private HullConfig mainStationHc;

    public GalaxyFiller(HullConfigManager hullConfigManager) {
        this.hullConfigManager = hullConfigManager;
    }

    private Vector2 getPosForStation(SolSystem sys, boolean mainStation, ConsumedAngles angles) {
        Planet planet;
        ArrayList<Planet> planets = sys.getPlanets();
        float angleToSun;
        if (mainStation) {
            planet = planets.get(planets.size() - 2);
            angleToSun = planet.getAngleInSystem() + 20 * SolMath.toInt(planet.getRotationSpeedInSystem() > 0);
        } else {
            int planetIndex = SolRandom.seededRandomInt(planets.size() - 1);
            planet = planets.get(planetIndex);
            angleToSun = 0;
            for (int i = 0; i < 10; i++) {
                angleToSun = SolRandom.seededRandomFloat(180);
                if (!angles.isConsumed(angleToSun, STATION_CONSUME_SECTOR)) {
                    break;
                }
            }
        }
        angles.add(angleToSun, STATION_CONSUME_SECTOR);
        float stationDist = planet.getDistance() + planet.getFullHeight() + Const.PLANET_GAP;
        Vector2 stationPos = new Vector2();
        SolMath.fromAl(stationPos, angleToSun, stationDist);
        stationPos.add(planet.getSystem().getPosition());
        return stationPos;
    }

    private FarShip build(SolGame game, ShipConfig config, Faction faction, boolean mainStation, SolSystem system,
                          ConsumedAngles angles) {
        HullConfig hullConf = config.hull;

        MoveDestProvider destProvider;
        Vector2 position;
        float detectionDist = Const.AI_DET_DIST;
        TradeConfig tradeConfig = null;
        if (hullConf.getType() == HullConfig.Type.STATION) {
            position = getPosForStation(system, mainStation, angles);
            destProvider = new NoDestProvider();
            tradeConfig = system.getConfig().tradeConfig;
        } else {
            position = getEmptySpace(game, system);
            boolean isBig = hullConf.getType() == HullConfig.Type.BIG;
            destProvider = new ExplorerDestProvider(position, !isBig, hullConf, system);
            if (isBig) {
                if (faction == Faction.LAANI) {
                    tradeConfig = system.getConfig().tradeConfig;
                }
            } else {
                detectionDist *= 1.5;
            }
        }
        Pilot pilot = new AiPilot(destProvider, true, faction, true, "something", detectionDist);
        float angle = mainStation ? 0 : SolRandom.seededRandomFloat(180);
        boolean hasRepairer;
        hasRepairer = faction == Faction.LAANI;
        int money = config.money;
        FarShip ship = game.getShipBuilder().buildNewFar(game, position, null, angle, 0, pilot, config.items, hullConf, null, hasRepairer, money, tradeConfig, true);
        game.getObjectManager().addFarObjNow(ship);
        ShipConfig guardConf = config.guard;
        if (guardConf != null) {
            ConsumedAngles consumedAngles = new ConsumedAngles();
            for (int i = 0; i < guardConf.density; i++) {
                float guardianAngle = 0;
                for (int j = 0; j < 5; j++) {
                    guardianAngle = SolRandom.randomFloat(180);
                    if (!consumedAngles.isConsumed(guardianAngle, guardConf.hull.getApproxRadius())) {
                        consumedAngles.add(guardianAngle, guardConf.hull.getApproxRadius());
                        break;
                    }
                }
                createGuard(game, ship, guardConf, faction, guardianAngle);
            }
        }
        return ship;
    }

    public JSONObject getRootNode(Json json) {
        JSONObject node = json.getJsonValue();
        return node;
    }

    public void fill(SolGame game, HullConfigManager hullConfigManager, ItemManager itemManager, String moduleName) {
        if (DebugOptions.NO_OBJS) {
            return;
        }
        createStarPorts(game);
        ArrayList<SolSystem> systems = game.getPlanetManager().getSystems();

        JSONObject rootNode = Validator.getValidatedJSON(moduleName + ":startingStation", "engine:schemaStartingStation");

        ShipConfig mainStationCfg = ShipConfig.load(hullConfigManager, rootNode, itemManager);

        ConsumedAngles angles = new ConsumedAngles();
        FarShip mainStation = build(game, mainStationCfg, Faction.LAANI, true, systems.get(0), angles);
        mainStationPos.set(mainStation.getPosition());
        mainStationHc = mainStation.getHullConfig();

        for (SolSystem system : systems) {
            SysConfig sysConfig = system.getConfig();

            for (ShipConfig shipConfig : sysConfig.constAllies) {
                int count = (int) (shipConfig.density);
                for (int i = 0; i < count; i++) {
                    build(game, shipConfig, Faction.LAANI, false, system, angles);
                }
            }

            for (ShipConfig shipConfig : sysConfig.constEnemies) {
                int count = (int) (shipConfig.density);
                for (int i = 0; i < count; i++) {
                    build(game, shipConfig, Faction.EHAR, false, system, angles);
                }
            }

            angles = new ConsumedAngles();
        }
    }

    private void createStarPorts(SolGame game) {
        PlanetManager planetManager = game.getPlanetManager();
        ArrayList<Planet> biggest = new ArrayList<>();

        for (SolSystem system : planetManager.getSystems()) {
            float minHeight = 0;
            Planet biggestPlanet = null;
            int biggestPlanetIndex = -1;
            ArrayList<Planet> planets = system.getPlanets();

            for (int i = 0; i < planets.size(); i++) {
                Planet planet = planets.get(i);
                float groundHeight = planet.getGroundHeight();
                if (minHeight < groundHeight) {
                    minHeight = groundHeight;
                    biggestPlanet = planet;
                    biggestPlanetIndex = i;
                }
            }

            for (int i = 0; i < planets.size(); i++) {
                if (biggestPlanetIndex == i || biggestPlanetIndex == i - 1 || biggestPlanetIndex == i + 1) {
                    continue;
                }

                Planet planet = planets.get(i);
                link(game, planet, biggestPlanet);
            }

            for (Planet planet : biggest) {
                link(game, planet, biggestPlanet);
            }

            biggest.add(biggestPlanet);
        }

    }

    private void link(SolGame game, Planet firstPlanet, Planet secondPlanet) {
        if (firstPlanet == secondPlanet) {
            throw new AssertionError("Linking planet to itself");
        }
        Vector2 firstPlanetPosition = StarPort.getDesiredPosition(firstPlanet, secondPlanet, false);
        StarPort.FarStarPort starPort = new StarPort.FarStarPort(firstPlanet, secondPlanet, firstPlanetPosition, false);
        SolMath.free(firstPlanetPosition);
        game.getObjectManager().addFarObjNow(starPort);
        Vector2 secondPlanetPosition = StarPort.getDesiredPosition(secondPlanet, firstPlanet, false);
        starPort = new StarPort.FarStarPort(secondPlanet, firstPlanet, secondPlanetPosition, false);
        SolMath.free(secondPlanetPosition);
        game.getObjectManager().addFarObjNow(starPort);
    }

    private void createGuard(SolGame game, FarShip target, ShipConfig guardConfig, Faction faction, float guardRelAngle) {
        Guardian dp = new Guardian(game, guardConfig.hull, target.getPilot(), target.getPosition(), target.getHullConfig(), guardRelAngle);
        Pilot pilot = new AiPilot(dp, true, faction, false, null, Const.AI_DET_DIST);
        boolean hasRepairer = faction == Faction.LAANI;
        int money = guardConfig.money;
        FarShip enemy = game.getShipBuilder().buildNewFar(game, dp.getDestination(), null, guardRelAngle, 0, pilot, guardConfig.items,
                guardConfig.hull, null, hasRepairer, money, null, true);
        game.getObjectManager().addFarObjNow(enemy);
    }

    private Vector2 getEmptySpace(SolGame game, SolSystem system) {
        Vector2 result = new Vector2();
        Vector2 systemPosition = system.getPosition();
        float systemRadius = system.getConfig().hard ? system.getRadius() : system.getInnerRadius();

        for (int i = 0; i < 100; i++) {
            SolMath.fromAl(result, SolRandom.seededRandomFloat(180), SolRandom.seededRandomFloat(systemRadius));
            result.add(systemPosition);
            if (game.isPlaceEmpty(result, true)) {
                return result;
            }
        }
        throw new AssertionError("could not generate ship position");
    }

    public Vector2 getPlayerSpawnPos(SolGame game) {
        Vector2 position = new Vector2(Const.SUN_RADIUS * 2, 0);

        if ("planet".equals(DebugOptions.SPAWN_PLACE)) {
            Planet planet = game.getPlanetManager().getPlanets().get(0);
            position.set(planet.getPosition());
            position.x += planet.getFullHeight();
        } else if (DebugOptions.SPAWN_PLACE.isEmpty() && mainStationPos != null) {
            SolMath.fromAl(position, 90, mainStationHc.getSize() / 2);
            position.add(mainStationPos);
        } else if ("maze".equals(DebugOptions.SPAWN_PLACE)) {
            Maze maze = game.getPlanetManager().getMazes().get(0);
            position.set(maze.getPos());
            position.x += maze.getRadius();
        } else if ("trader".equals(DebugOptions.SPAWN_PLACE)) {
            HullConfig config = hullConfigManager.getConfig("core:bus");
            for (FarObjData farObjData : game.getObjectManager().getFarObjs()) {
                FarObject farObject = farObjData.fo;
                if (!(farObject instanceof FarShip)) {
                    continue;
                }
                if (((FarShip) farObject).getHullConfig() != config) {
                    continue;
                }
                position.set(farObject.getPosition());
                position.add(config.getApproxRadius() * 2, 0);
                break;
            }
        }
        return position;
    }

    public Vector2 getMainStationPosition() {
        return mainStationPos;
    }

}
