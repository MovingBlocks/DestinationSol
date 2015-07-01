/*
 * Copyright 2015 MovingBlocks
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
 
 package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.DebugOptions;
import com.miloshpetrov.sol2.game.PathLoader;
import com.miloshpetrov.sol2.game.asteroid.AsteroidBuilder;
import com.miloshpetrov.sol2.ui.DebugCollector;

import java.util.ArrayList;
import java.util.List;

public class MazeTile {
  public final TextureAtlas.AtlasRegion tex;
  public final List<List<Vector2>> points;
  public final boolean metal;
  public final TextureAtlas.AtlasRegion bgTex;

  public MazeTile(TextureAtlas.AtlasRegion tex, List<List<Vector2>> points, boolean metal,
    TextureAtlas.AtlasRegion bgTex)
  {
    this.tex = tex;
    this.points = points;
    this.metal = metal;
    this.bgTex = bgTex;
  }

  public static MazeTile load(TextureAtlas.AtlasRegion tex, PathLoader.Model paths, boolean wall, String pathEntryName,
    boolean metal, TextureAtlas.AtlasRegion bgTex)
  {
    ArrayList<List<Vector2>> points = new ArrayList<List<Vector2>>();
    PathLoader.RigidBodyModel tilePaths = paths.rigidBodies.get(AsteroidBuilder.removePath(pathEntryName));
    List<PathLoader.PolygonModel> shapes = tilePaths == null ? new ArrayList<PathLoader.PolygonModel>() : tilePaths.shapes;
    for (PathLoader.PolygonModel shape : shapes) {
      List<Vector2> vertices = new ArrayList<Vector2>(shape.vertices);
      points.add(vertices);
    }
    if (points.isEmpty() && wall) {
      DebugOptions.MISSING_PHYSICS_ACTION.handle("found no paths for " + pathEntryName);
      ArrayList<Vector2> wallPoints = new ArrayList<Vector2>();
      wallPoints.add(new Vector2(0, .4f));
      wallPoints.add(new Vector2(1, .45f));
      wallPoints.add(new Vector2(1, .55f));
      wallPoints.add(new Vector2(0, .6f));
      points.add(wallPoints);
    }
    return new MazeTile(tex, points, metal, bgTex);
  }
}
