/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game;

import java.util.ArrayList;

import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.google.gson.JsonParseException;

public class GalaxyFiller {
    private static final float STATION_CONSUME_SECTOR = 45f;
    private Vector2 myMainStationPos = new Vector2();
    private HullConfig myMainStationHc;

    private Vector2 getPosForStation(SolSystem sys, boolean mainStation, ConsumedAngles angles) {
        Planet p;
        ArrayList<Planet> planets = sys.getPlanets();
        float angleToSun;
        if (mainStation) {
            p = planets.get(planets.size() - 2);
            angleToSun = p.getAngleToSys() + 20 * SolMath.toInt(p.getToSysRotSpd() > 0);
        } else {
            int pIdx = SolMath.intRnd(planets.size() - 1);
            p = planets.get(pIdx);
            angleToSun = 0;
            for (int i = 0; i < 10; i++) {
                angleToSun = SolMath.rnd(180);
                if (!angles.isConsumed(angleToSun, STATION_CONSUME_SECTOR)) {
                    break;
                }
            }
        }
        angles.add(angleToSun, STATION_CONSUME_SECTOR);
        float stationDist = p.getDist() + p.getFullHeight() + Const.PLANET_GAP;
        Vector2 stationPos = new Vector2();
        SolMath.fromAl(stationPos, angleToSun, stationDist);
        stationPos.add(p.getSys().getPos());
        return stationPos;
    }

    private FarShip build(SolGame game, ShipConfig cfg, Faction faction, boolean mainStation, SolSystem sys,
                          ConsumedAngles angles) {
        HullConfig hullConf = cfg.hull;

        MoveDestProvider dp;
        Vector2 pos;
        float detectionDist = Const.AI_DET_DIST;
        TradeConfig tradeConfig = null;
        if (hullConf.getType() == HullConfig.Type.STATION) {
            pos = getPosForStation(sys, mainStation, angles);
            dp = new NoDestProvider();
            tradeConfig = sys.getConfig().tradeConfig;
        } else {
            pos = getEmptySpace(game, sys);
            boolean isBig = hullConf.getType() == HullConfig.Type.BIG;
            dp = new ExplorerDestProvider(game, pos, !isBig, hullConf, sys);
            if (isBig) {
                if (faction == Faction.LAANI) {
                    tradeConfig = sys.getConfig().tradeConfig;
                }
            } else {
                detectionDist *= 1.5;
            }
        }
        Pilot pilot = new AiPilot(dp, true, faction, true, "something", detectionDist);
        float angle = mainStation ? 0 : SolMath.rnd(180);
        boolean hasRepairer;
        hasRepairer = faction == Faction.LAANI;
        int money = cfg.money;
        FarShip s = game.getShipBuilder().buildNewFar(game, pos, null, angle, 0, pilot, cfg.items, hullConf, null, hasRepairer, money, tradeConfig, true);
        game.getObjMan().addFarObjNow(s);
        ShipConfig guardConf = cfg.guard;
        if (guardConf != null) {
            ConsumedAngles ca = new ConsumedAngles();
            for (int i = 0; i < guardConf.density; i++) {
                float guardianAngle = 0;
                for (int j = 0; j < 5; j++) {
                    guardianAngle = SolMath.rnd(180);
                    if (!ca.isConsumed(guardianAngle, guardConf.hull.getApproxRadius())) {
                        ca.add(guardianAngle, guardConf.hull.getApproxRadius());
                        break;
                    }
                }
                createGuard(game, s, guardConf, faction, guardianAngle);
            }
        }
        return s;
    }
    
    public JsonValue getRootNode(Json json) {
    	JsonValue node = json.getJsonValue();
    	if (node.isNull()) {
    		throw new JsonParseException(String.format("Root node was not found in asset %s", node.name, json.toString()));
    	} else {
    		return node;
    	}
    }

    public void fill(SolGame game, HullConfigManager hullConfigManager, ItemManager itemManager) {
        if (DebugOptions.NO_OBJS) {
            return;
        }
        createStarPorts(game);
        ArrayList<SolSystem> systems = game.getPlanetMan().getSystems();
        
        String shipName = game.getShipName();
        String moduleName = shipName.split(":")[0];
        
        Json json = Assets.getJson(moduleName + ":startingStation");
        JsonValue rootNode = getRootNode(json);

        ShipConfig mainStationCfg = ShipConfig.load(hullConfigManager, rootNode, itemManager);

        json.dispose();

        ConsumedAngles angles = new ConsumedAngles();
        FarShip mainStation = build(game, mainStationCfg, Faction.LAANI, true, systems.get(0), angles);
        myMainStationPos.set(mainStation.getPos());
        myMainStationHc = mainStation.getHullConfig();

        for (SolSystem sys : systems) {
            SysConfig sysConfig = sys.getConfig();

            for (ShipConfig shipConfig : sysConfig.constAllies) {
                int count = (int) (shipConfig.density);
                for (int i = 0; i < count; i++) {
                    build(game, shipConfig, Faction.LAANI, false, sys, angles);
                }
            }

            for (ShipConfig shipConfig : sysConfig.constEnemies) {
                int count = (int) (shipConfig.density);
                for (int i = 0; i < count; i++) {
                    build(game, shipConfig, Faction.EHAR, false, sys, angles);
                }
            }

            angles = new ConsumedAngles();
        }
    }

    private void createStarPorts(SolGame game) {
        PlanetManager planetManager = game.getPlanetMan();
        ArrayList<Planet> biggest = new ArrayList<>();

        for (SolSystem s : planetManager.getSystems()) {
            float minH = 0;
            Planet biggestP = null;
            int bi = -1;
            ArrayList<Planet> ps = s.getPlanets();

            for (int i = 0; i < ps.size(); i++) {
                Planet p = ps.get(i);
                float gh = p.getGroundHeight();
                if (minH < gh) {
                    minH = gh;
                    biggestP = p;
                    bi = i;
                }
            }

            for (int i = 0; i < ps.size(); i++) {
                if (bi == i || bi == i - 1 || bi == i + 1) {
                    continue;
                }

                Planet p = ps.get(i);
                link(game, p, biggestP);
            }

            for (Planet p : biggest) {
                link(game, p, biggestP);
            }

            biggest.add(biggestP);
        }

    }

    private void link(SolGame game, Planet a, Planet b) {
        if (a == b) {
            throw new AssertionError("Linking planet to itself");
        }
        Vector2 aPos = StarPort.getDesiredPos(a, b, false);
        StarPort.MyFar sp = new StarPort.MyFar(a, b, aPos, false);
        SolMath.free(aPos);
        game.getObjMan().addFarObjNow(sp);
        Vector2 bPos = StarPort.getDesiredPos(b, a, false);
        sp = new StarPort.MyFar(b, a, bPos, false);
        SolMath.free(bPos);
        game.getObjMan().addFarObjNow(sp);
    }

    private void createGuard(SolGame game, FarShip target, ShipConfig guardConf, Faction faction, float guardRelAngle) {
        Guardian dp = new Guardian(game, guardConf.hull, target.getPilot(), target.getPos(), target.getHullConfig(), guardRelAngle);
        Pilot pilot = new AiPilot(dp, true, faction, false, null, Const.AI_DET_DIST);
        boolean hasRepairer = faction == Faction.LAANI;
        int money = guardConf.money;
        FarShip e = game.getShipBuilder().buildNewFar(game, dp.getDest(), null, guardRelAngle, 0, pilot, guardConf.items,
                guardConf.hull, null, hasRepairer, money, null, true);
        game.getObjMan().addFarObjNow(e);
    }

    private Vector2 getEmptySpace(SolGame game, SolSystem s) {
        Vector2 res = new Vector2();
        Vector2 sPos = s.getPos();
        float sRadius = s.getConfig().hard ? s.getRadius() : s.getInnerRad();

        for (int i = 0; i < 100; i++) {
            SolMath.fromAl(res, SolMath.rnd(180), SolMath.rnd(sRadius));
            res.add(sPos);
            if (game.isPlaceEmpty(res, true)) {
                return res;
            }
        }
        throw new AssertionError("could not generate ship position");
    }

    public Vector2 getPlayerSpawnPos(SolGame game) {
        Vector2 pos = new Vector2(Const.SUN_RADIUS * 2, 0);

        if ("planet".equals(DebugOptions.SPAWN_PLACE)) {
            Planet p = game.getPlanetMan().getPlanets().get(0);
            pos.set(p.getPos());
            pos.x += p.getFullHeight();
        } else if (DebugOptions.SPAWN_PLACE.isEmpty() && myMainStationPos != null) {
            SolMath.fromAl(pos, 90, myMainStationHc.getSize() / 2);
            pos.add(myMainStationPos);
        } else if ("maze".equals(DebugOptions.SPAWN_PLACE)) {
            Maze m = game.getPlanetMan().getMazes().get(0);
            pos.set(m.getPos());
            pos.x += m.getRadius();
        } else if ("trader".equals(DebugOptions.SPAWN_PLACE)) {
            HullConfig cfg = game.getHullConfigs().getConfig("core:bus");
            for (FarObjData fod : game.getObjMan().getFarObjs()) {
                FarObj fo = fod.fo;
                if (!(fo instanceof FarShip)) {
                    continue;
                }
                if (((FarShip) fo).getHullConfig() != cfg) {
                    continue;
                }
                pos.set(fo.getPos());
                pos.add(cfg.getApproxRadius() * 2, 0);
                break;
            }
        }
        return pos;
    }

    public Vector2 getMainStationPos() {
        return myMainStationPos;
    }

}
