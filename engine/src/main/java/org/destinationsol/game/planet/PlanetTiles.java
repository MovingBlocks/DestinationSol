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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.DebugOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetTiles {
    private final Map<SurfaceDirection, Map<SurfaceDirection, List<Tile>>> myGroundTiles;

    public PlanetTiles(String groundName) {
        myGroundTiles = new HashMap<>();
        loadGround(groundName);
    }

    private void loadGround(String groundName) {
        CollisionMeshLoader collisionMeshLoader = new CollisionMeshLoader(groundName);
        CollisionMeshLoader.Model paths = collisionMeshLoader.getInternalModel();

        for (SurfaceDirection from : SurfaceDirection.values()) {
            HashMap<SurfaceDirection, List<Tile>> fromMap = new HashMap<>();
            myGroundTiles.put(from, fromMap);
            for (SurfaceDirection to : SurfaceDirection.values()) {
                if (from == SurfaceDirection.DOWN && to == SurfaceDirection.DOWN) {
                    continue;
                }
                boolean inverted = from == SurfaceDirection.DOWN || to == SurfaceDirection.UP;
                String fromL = from.getLetter();
                String toL = to.getLetter();
                String tileDescName = inverted ? toL + fromL : fromL + toL;

                String tileVariant = groundName + "_" + tileDescName;
                List<TextureAtlas.AtlasRegion> texs = Assets.listTexturesMatching(tileVariant + "_.*");
                ArrayList<Tile> tileVariants = buildTiles(paths, inverted, tileDescName, from, to, texs);
                fromMap.put(to, tileVariants);
            }
        }
    }

    private ArrayList<Tile> buildTiles(CollisionMeshLoader.Model paths, boolean inverted, String tileDescName,
                                       SurfaceDirection from, SurfaceDirection to, List<TextureAtlas.AtlasRegion> texs) {
        ArrayList<Tile> tileVariants = new ArrayList<>();
        for (TextureAtlas.AtlasRegion tex : texs) {
            if (inverted) {
                tex.flip(!tex.isFlipX(), !tex.isFlipY());
            }
            String tileName = tileDescName + "_" + tex.name.substring(tex.name.lastIndexOf('_') + 1) + ".png";
            List<Vector2> points = new ArrayList<>();
            List<Vector2> rawPoints;
            CollisionMeshLoader.RigidBodyModel tilePaths = paths == null ? null : paths.rigidBodies.get(tileName);
            List<CollisionMeshLoader.PolygonModel> shapes = tilePaths == null ? null : tilePaths.shapes;
            if (shapes != null && !shapes.isEmpty()) {
                rawPoints = shapes.get(0).vertices;
            } else {
                rawPoints = getDefaultRawPoints(inverted ? to : from, inverted ? from : to, tileName);
            }
            int sz = rawPoints.size();
            for (int j = 0; j < sz; j++) {
                Vector2 v = rawPoints.get(inverted ? sz - j - 1 : j);
                Vector2 point = new Vector2(v.x - .5f, v.y - .5f);
                if (inverted) {
                    point.x *= -1;
                }
                points.add(point);
            }
            tileVariants.add(new Tile(tex, points, from, to));
        }
        return tileVariants;
    }

    private List<Vector2> getDefaultRawPoints(SurfaceDirection from, SurfaceDirection to, String tileName) {
        ArrayList<Vector2> res = new ArrayList<>();

        if (from == SurfaceDirection.UP && to == SurfaceDirection.UP) {
            return res;
        }

        DebugOptions.MISSING_PHYSICS_ACTION.handle("no path found for " + tileName);

        res.add(new Vector2(.25f, .75f));

        if (from == SurfaceDirection.FWD) {
            res.add(new Vector2(.25f, .5f));
        } else {
            res.add(new Vector2(.25f, .25f));
            res.add(new Vector2(.5f, .25f));
        }

        res.add(new Vector2(.5f, .5f));

        if (to == SurfaceDirection.FWD) {
            res.add(new Vector2(.75f, .5f));
            res.add(new Vector2(.75f, .75f));
        } else {
            res.add(new Vector2(.5f, .75f));
        }

        return res;
    }

    public Tile getGround(SurfaceDirection from, SurfaceDirection to) {
        List<Tile> list = myGroundTiles.get(from).get(to);
        return SolMath.elemRnd(list);
    }

    public Tile getDungeonEntrance(boolean down, boolean left, boolean right) {
        return null;
    }
}
