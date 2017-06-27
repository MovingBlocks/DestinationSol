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

package org.destinationsol.game.maze;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.DebugOptions;

import java.util.ArrayList;
import java.util.List;

public class MazeTile {
    public final TextureAtlas.AtlasRegion tex;
    public final List<List<Vector2>> points;
    public final boolean metal;
    public final TextureAtlas.AtlasRegion bgTex;

    public MazeTile(TextureAtlas.AtlasRegion tex, List<List<Vector2>> points, boolean metal,
                    TextureAtlas.AtlasRegion bgTex) {
        this.tex = tex;
        this.points = points;
        this.metal = metal;
        this.bgTex = bgTex;
    }

    public static MazeTile load(TextureAtlas.AtlasRegion tex, CollisionMeshLoader.RigidBodyModel tilePaths, boolean wall,
                                boolean metal, TextureAtlas.AtlasRegion bgTex) {
        ArrayList<List<Vector2>> points = new ArrayList<>();
        List<CollisionMeshLoader.PolygonModel> shapes = tilePaths == null ? new ArrayList<>() : tilePaths.polygons;
        for (CollisionMeshLoader.PolygonModel shape : shapes) {
            List<Vector2> vertices = new ArrayList<>(shape.vertices);
            points.add(vertices);
        }
        if (points.isEmpty() && wall) {
            DebugOptions.MISSING_PHYSICS_ACTION.handle("found no paths for " + tex.name);
            ArrayList<Vector2> wallPoints = new ArrayList<>();
            wallPoints.add(new Vector2(0, .4f));
            wallPoints.add(new Vector2(1, .45f));
            wallPoints.add(new Vector2(1, .55f));
            wallPoints.add(new Vector2(0, .6f));
            points.add(wallPoints);
        }
        return new MazeTile(tex, points, metal, bgTex);
    }
}
