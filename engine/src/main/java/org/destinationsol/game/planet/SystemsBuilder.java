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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.SolNames;
import org.destinationsol.game.maze.Maze;
import org.destinationsol.game.maze.MazeConfig;
import org.destinationsol.game.maze.MazeConfigs;

import java.util.ArrayList;
import java.util.List;

public class SystemsBuilder {
    public static final int DEFAULT_SYSTEM_COUNT = 2;
    public static final int PLANET_COUNT = 5;
    public static final float PLANET_SPD = .2f;
    private static final float GROUND_SPD = .2f;
    private static final float MAX_MAZE_RADIUS = 40f;
    private static final float MAZE_GAP = 10f;
    private static final float BELT_HALF_WIDTH = 20f;

    public List<SolSystem> build(List<SolSystem> systems, List<Planet> planets, ArrayList<SystemBelt> belts, PlanetConfigs planetConfigs,
                                 MazeConfigs mazeConfigs, ArrayList<Maze> mazes, SysConfigs sysConfigs, SolNames names, int systemCount) {
        
        int sysLeft = systemCount;
        int mazesLeft = systemCount * 2;
        while (sysLeft > 0 || mazesLeft > 0) {
            boolean createSys = sysLeft > 0;
            if (createSys && mazesLeft > 0 && !systems.isEmpty()) {
                createSys = SolRandom.seededTest(.5f);
            }
            if (createSys) {
                List<Float> ghs = generatePlanetGhs();
                float sysRadius = calcSysRadius(ghs);
                Vector2 position = getBodyPos(systems, mazes, sysRadius);
                SolSystem s = createSystem(ghs, position, planets, belts, planetConfigs, sysRadius, sysConfigs, names, systems.isEmpty());
                systems.add(s);
                sysLeft--;
            } else {
                MazeConfig mc = SolRandom.seededRandomElement(mazeConfigs.configs);
                float mazeRadius = SolRandom.seededRandomFloat(.7f, 1) * MAX_MAZE_RADIUS;
                Vector2 position = getBodyPos(systems, mazes, mazeRadius + MAZE_GAP);
                Maze m = new Maze(mc, position, mazeRadius);
                mazes.add(m);
                mazesLeft--;
            }
        }
        return systems;
    }

    private List<Float> generatePlanetGhs() {
        ArrayList<Float> res = new ArrayList<>();
        boolean beltCreated = false;
        for (int i = 0; i < PLANET_COUNT; i++) {
            boolean createBelt = !beltCreated && 0 < i && i < .5f * PLANET_COUNT && SolRandom.seededTest(.6f);
            float groundHeight;
            if (!createBelt) {
                groundHeight = SolRandom.seededRandomFloat(.5f, 1) * Const.MAX_GROUND_HEIGHT;
            } else {
                groundHeight = -BELT_HALF_WIDTH;
                beltCreated = true;
            }
            res.add(groundHeight);
        }
        return res;
    }

    private float calcSysRadius(List<Float> ghs) {
        float r = 0;
        r += Const.SUN_RADIUS;
        for (Float groundHeight : ghs) {
            r += Const.PLANET_GAP;
            if (groundHeight > 0) {
                r += Const.ATM_HEIGHT;
                r += groundHeight;
                r += groundHeight;
                r += Const.ATM_HEIGHT;
            } else {
                r -= groundHeight;
                r -= groundHeight;
            }
            r += Const.PLANET_GAP;
        }
        return r;
    }

    private Vector2 getBodyPos(List<SolSystem> systems, ArrayList<Maze> mazes, float bodyRadius) {
        Vector2 res = new Vector2();
        float dist = 0;
        while (true) {
            for (int i = 0; i < 20; i++) {
                float angle = SolRandom.seededRandomFloat(180);
                SolMath.fromAl(res, angle, dist);
                boolean good = true;
                for (SolSystem system : systems) {
                    if (system.getPosition().dst(res) < system.getRadius() + bodyRadius) {
                        good = false;
                        break;
                    }
                }
                for (Maze maze : mazes) {
                    if (maze.getPos().dst(res) < maze.getRadius() + bodyRadius) {
                        good = false;
                        break;
                    }
                }
                if (good) {
                    return res;
                }
            }
            dist += Const.SUN_RADIUS;
        }
    }

    private SolSystem createSystem(List<Float> groundHeights, Vector2 systemPosition, List<Planet> planets, ArrayList<SystemBelt> belts,
                                   PlanetConfigs planetConfigs,
                                   float systemRadius, SysConfigs sysConfigs, SolNames names, boolean firstSys) {
        boolean hard = !firstSys;
        String systemType = DebugOptions.FORCE_SYSTEM_TYPE;
        SysConfig sysConfig;
        if (systemType.isEmpty()) {
            sysConfig = sysConfigs.getRandomCfg(hard);
        } else {
            sysConfig = sysConfigs.getConfig(systemType);
        }
        String name = firstSys ? SolRandom.seededRandomElement(names.systems) : "Sol"; //hack
        SolSystem system = new SolSystem(systemPosition, sysConfig, name, systemRadius);
        float planetDist = Const.SUN_RADIUS;
        for (Float groundHeight : groundHeights) {
            float reserved;
            if (groundHeight > 0) {
                reserved = Const.PLANET_GAP + Const.ATM_HEIGHT + groundHeight;
            } else {
                reserved = Const.PLANET_GAP - groundHeight;
            }
            planetDist += reserved;
            if (groundHeight > 0) {
                String pt = DebugOptions.FORCE_PLANET_TYPE;
                PlanetConfig planetConfig;
                if (pt.isEmpty()) {
                    boolean inner = planetDist < systemRadius / 2;
                    planetConfig = planetConfigs.getRandom(!inner && !hard, inner && hard);
                } else {
                    planetConfig = planetConfigs.getConfig(pt);
                }
                Planet planet = createPlanet(planetDist, system, groundHeight, planetConfig, names);
                planets.add(planet);
                system.getPlanets().add(planet);
            } else {
                SysConfig beltConfig = sysConfigs.getRandomBelt(hard);
                SystemBelt belt = new SystemBelt(-groundHeight, planetDist, system, beltConfig);
                belts.add(belt);
                system.addBelt(belt);
            }
            planetDist += reserved;
        }
        if (SolMath.abs(systemRadius - planetDist) > .1f) {
            throw new AssertionError(systemRadius + " " + planetDist);
        }
        return system;
    }

    private Planet createPlanet(float planetDist, SolSystem s, float groundHeight, PlanetConfig planetConfig,
                                SolNames names) {
        float toSysRotationSpeed = SolMath.arcToAngle(PLANET_SPD, planetDist) * SolMath.toInt(SolRandom.seededTest(.5f));
        float rotationSpeed = SolMath.arcToAngle(GROUND_SPD, groundHeight) * SolMath.toInt(SolRandom.seededTest(.5f));
        String name = SolRandom.seededRandomElement(names.planets.get(planetConfig.moduleName));
        return new Planet(s, SolRandom.seededRandomFloat(180), planetDist, SolRandom.seededRandomFloat(180), toSysRotationSpeed, rotationSpeed, groundHeight, false, planetConfig, name);
    }
}
