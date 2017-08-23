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
package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.SolNames;
import org.destinationsol.game.maze.Maze;
import org.destinationsol.game.maze.MazeConfig;
import org.destinationsol.game.maze.MazeConfigs;

import java.util.ArrayList;
import java.util.List;

public class SystemsBuilder {
    public static int SYS_COUNT = 2;
    public static final int MAZE_COUNT = SYS_COUNT * 2;
    public static final int PLANET_COUNT = 5;
    public static final float PLANET_SPD = .2f;
    private static final float GROUND_SPD = .2f;
    private static final float MAX_MAZE_RADIUS = 40f;
    private static final float MAZE_GAP = 10f;
    private static final float BELT_HALF_WIDTH = 20f;

    public List<SolSystem> build(List<SolSystem> systems, List<Planet> planets, ArrayList<SystemBelt> belts,
                                 PlanetConfigs planetConfigs,
                                 MazeConfigs mazeConfigs, ArrayList<Maze> mazes, SysConfigs sysConfigs, SolNames names) {
        int sysLeft = SYS_COUNT;
        int mazesLeft = MAZE_COUNT;
        while (sysLeft > 0 || mazesLeft > 0) {
            boolean createSys = sysLeft > 0;
            if (createSys && mazesLeft > 0 && !systems.isEmpty()) {
                createSys = SolMath.test(.5f);
            }
            if (createSys) {
                List<Float> ghs = generatePlanetGhs();
                float sysRadius = calcSysRadius(ghs);
                Vector2 pos = getBodyPos(systems, mazes, sysRadius);
                SolSystem s = createSystem(ghs, pos, planets, belts, planetConfigs, sysRadius, sysConfigs, names, systems.isEmpty());
                systems.add(s);
                sysLeft--;
            } else {
                MazeConfig mc = SolMath.elemRnd(mazeConfigs.configs);
                float mazeRadius = SolMath.rnd(.7f, 1) * MAX_MAZE_RADIUS;
                Vector2 pos = getBodyPos(systems, mazes, mazeRadius + MAZE_GAP);
                Maze m = new Maze(mc, pos, mazeRadius);
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
            boolean createBelt = !beltCreated && 0 < i && i < .5f * PLANET_COUNT && SolMath.test(.6f);
            float gh;
            if (!createBelt) {
                gh = SolMath.rnd(.5f, 1) * Const.MAX_GROUND_HEIGHT;
            } else {
                gh = -BELT_HALF_WIDTH;
                beltCreated = true;
            }
            res.add(gh);
        }
        return res;
    }

    private float calcSysRadius(List<Float> ghs) {
        float r = 0;
        r += Const.SUN_RADIUS;
        for (Float gh : ghs) {
            r += Const.PLANET_GAP;
            if (gh > 0) {
                r += Const.ATM_HEIGHT;
                r += gh;
                r += gh;
                r += Const.ATM_HEIGHT;
            } else {
                r -= gh;
                r -= gh;
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
                float angle = SolMath.rnd(180);
                SolMath.fromAl(res, angle, dist);
                boolean good = true;
                for (SolSystem system : systems) {
                    if (system.getPos().dst(res) < system.getRadius() + bodyRadius) {
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

    private SolSystem createSystem(List<Float> ghs, Vector2 sysPos, List<Planet> planets, ArrayList<SystemBelt> belts,
                                   PlanetConfigs planetConfigs,
                                   float sysRadius, SysConfigs sysConfigs, SolNames names, boolean firstSys) {
        boolean hard = !firstSys;
        String st = DebugOptions.FORCE_SYSTEM_TYPE;
        SysConfig sysConfig;
        if (st.isEmpty()) {
            sysConfig = sysConfigs.getRandomCfg(hard);
        } else {
            sysConfig = sysConfigs.getConfig(st);
        }
        String name = firstSys ? SolMath.elemRnd(names.systems) : "Sol"; //hack
        SolSystem s = new SolSystem(sysPos, sysConfig, name, sysRadius);
        float planetDist = Const.SUN_RADIUS;
        for (Float gh : ghs) {
            float reserved;
            if (gh > 0) {
                reserved = Const.PLANET_GAP + Const.ATM_HEIGHT + gh;
            } else {
                reserved = Const.PLANET_GAP - gh;
            }
            planetDist += reserved;
            if (gh > 0) {
                String pt = DebugOptions.FORCE_PLANET_TYPE;
                PlanetConfig planetConfig;
                if (pt.isEmpty()) {
                    boolean inner = planetDist < sysRadius / 2;
                    planetConfig = planetConfigs.getRandom(!inner && !hard, inner && hard);
                } else {
                    planetConfig = planetConfigs.getConfig(pt);
                }
                Planet p = createPlanet(planetDist, s, gh, planetConfig, names);
                planets.add(p);
                s.getPlanets().add(p);
            } else {
                SysConfig beltConfig = sysConfigs.getRandomBelt(hard);
                SystemBelt belt = new SystemBelt(-gh, planetDist, s, beltConfig);
                belts.add(belt);
                s.addBelt(belt);
            }
            planetDist += reserved;
        }
        if (SolMath.abs(sysRadius - planetDist) > .1f) {
            throw new AssertionError(sysRadius + " " + planetDist);
        }
        return s;
    }

    private Planet createPlanet(float planetDist, SolSystem s, float groundHeight, PlanetConfig planetConfig,
                                SolNames names) {
        float toSysRotSpd = SolMath.arcToAngle(PLANET_SPD, planetDist) * SolMath.toInt(SolMath.test(.5f));
        float rotSpd = SolMath.arcToAngle(GROUND_SPD, groundHeight) * SolMath.toInt(SolMath.test(.5f));
        String name = SolMath.elemRnd(names.planets);
        return new Planet(s, SolMath.rnd(180), planetDist, SolMath.rnd(180), toSysRotSpd, rotSpd, groundHeight, false, planetConfig, name);
    }
}
