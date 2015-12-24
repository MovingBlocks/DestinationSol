/*
 * Copyright 2015 MovingBlocks
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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.Const;
import org.destinationsol.TextureManager;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.*;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.maze.Maze;
import org.destinationsol.game.maze.MazeConfigs;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.Hull;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.ArrayList;
import java.util.List;

public class PlanetManager {

  private final ArrayList<SolSystem> mySystems;
  private final ArrayList<Planet> myPlanets;
  private final ArrayList<SystemBelt> myBelts;
  private final FlatPlaceFinder myFlatPlaceFinder;
  private final PlanetConfigs myPlanetConfigs;
  private final MazeConfigs myMazeConfigs;
  private final ArrayList<Maze> myMazes;
  private final SunSingleton mySunSingleton;
  private final SysConfigs mySysConfigs;
  private final PlanetCoreSingleton myPlanetCore;
  private Planet myNearestPlanet;

  public PlanetManager(TextureManager textureManager, HullConfigManager hullConfigs, GameColors cols, ItemManager itemManager) {
    myPlanetConfigs = new PlanetConfigs(textureManager, hullConfigs, cols, itemManager);
    mySysConfigs = new SysConfigs(textureManager, hullConfigs, itemManager);
    myMazeConfigs = new MazeConfigs(textureManager, hullConfigs, itemManager);

    mySystems = new ArrayList<SolSystem>();
    myMazes = new ArrayList<Maze>();
    myPlanets = new ArrayList<Planet>();
    myBelts = new ArrayList<SystemBelt>();
    myFlatPlaceFinder = new FlatPlaceFinder();
    mySunSingleton = new SunSingleton(textureManager);
    myPlanetCore = new PlanetCoreSingleton(textureManager);
  }

  public void fill(SolNames names) {
    new SystemsBuilder().build(mySystems, myPlanets, myBelts, myPlanetConfigs, myMazeConfigs, myMazes, mySysConfigs, names);
  }

  public void update(SolGame game) {
    Vector2 camPos = game.getCam().getPos();
    for (int i = 0, myPlanetsSize = myPlanets.size(); i < myPlanetsSize; i++) {
      Planet p = myPlanets.get(i);
      p.update(game);
    }
    for (int i = 0, myMazesSize = myMazes.size(); i < myMazesSize; i++) {
      Maze m = myMazes.get(i);
      m.update(game);
    }

    myNearestPlanet = getNearestPlanet(camPos);

    SolSystem nearestSys = getNearestSystem(camPos);
    applyGrav(game, nearestSys);
  }

  public Planet getNearestPlanet(Vector2 pos) {
    float minDst = Float.MAX_VALUE;
    Planet res = null;
    for (int i = 0, myPlanetsSize = myPlanets.size(); i < myPlanetsSize; i++) {
      Planet p = myPlanets.get(i);
      float dst = pos.dst(p.getPos());
      if (dst < minDst) {
        minDst = dst;
        res = p;
      }
    }
    return res;
  }

  private void applyGrav(SolGame game, SolSystem nearestSys) {
    float npGh = myNearestPlanet.getGroundHeight();
    float npFh = myNearestPlanet.getFullHeight();
    float npMinH = myNearestPlanet.getMinGroundHeight();
    Vector2 npPos = myNearestPlanet.getPos();
    Vector2 sysPos = nearestSys.getPos();
    float npGravConst = myNearestPlanet.getGravConst();

    List<SolObject> objs = game.getObjMan().getObjs();
    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      SolObject obj = objs.get(i);
      if (!obj.receivesGravity()) continue;

      Vector2 objPos = obj.getPos();
      float minDist;
      Vector2 srcPos;
      float gravConst;
      boolean onPlanet;
      float toNp = npPos.dst(objPos);
      float toSys = sysPos.dst(objPos);
      if (toNp < npFh) {
        if (recoverObj(obj, toNp, npMinH)) continue;
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
        mySunSingleton.doDmg(game, obj, toSys);
      }
    }

  }

  private boolean recoverObj(SolObject obj, float toNp, float npMinH) {
    if (npMinH < toNp) return false;
    if (!(obj instanceof SolShip)) return false;
    SolShip ship = (SolShip) obj;
    Hull hull = ship.getHull();
    if (hull.config.getType() == HullConfig.Type.STATION) return false;
    float fh = myNearestPlanet.getFullHeight();
    Vector2 npPos = myNearestPlanet.getPos();
    Vector2 toShip = SolMath.distVec(npPos, ship.getPos());
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
    return myNearestPlanet;
  }

  public void drawDebug(GameDrawer drawer, SolGame game) {
    if (DebugOptions.DRAW_PLANET_BORDERS) {
      SolCam cam = game.getCam();
      float lineWidth = cam.getRealLineWidth();
      float vh = cam.getViewHeight();
      for (Planet p : myPlanets) {
        Vector2 pos = p.getPos();
        float angle = p.getAngle();
        float fh = p.getFullHeight();
        Color col = p == myNearestPlanet ? SolColor.W : SolColor.G;
        drawer.drawCircle(drawer.debugWhiteTex, pos, p.getGroundHeight(), col, lineWidth, vh);
        drawer.drawCircle(drawer.debugWhiteTex, pos, fh, col, lineWidth, vh);
        drawer.drawLine(drawer.debugWhiteTex, pos.x, pos.y, angle, fh, col, lineWidth);
      }

    }
  }

  public ArrayList<Planet> getPlanets() {
    return myPlanets;
  }

  public ArrayList<SystemBelt> getBelts() {
    return myBelts;
  }

  public ArrayList<SolSystem> getSystems() {
    return mySystems;
  }

  public Vector2 findFlatPlace(SolGame game, Planet p, ConsumedAngles takenAngles,
    float objHalfWidth) {
    return myFlatPlaceFinder.find(game, p, takenAngles, objHalfWidth);
  }

  public ArrayList<Maze> getMazes() {
    return myMazes;
  }

  public SolSystem getNearestSystem(Vector2 pos) {
    float minDst = Float.MAX_VALUE;
    SolSystem res = null;
    for (int i = 0, mySystemsSize = mySystems.size(); i < mySystemsSize; i++) {
      SolSystem s = mySystems.get(i);
      float dst = pos.dst(s.getPos());
      if (dst < minDst) {
        minDst = dst;
        res = s;
      }
    }
    return res;
  }

  public Maze getNearestMaze(Vector2 pos) {
    float minDst = Float.MAX_VALUE;
    Maze res = null;
    for (int i = 0, myMazesSize = myMazes.size(); i < myMazesSize; i++) {
      Maze m = myMazes.get(i);
      float dst = pos.dst(m.getPos());
      if (dst < minDst) {
        minDst = dst;
        res = m;
      }
    }
    return res;
  }

  public void drawSunHack(SolGame game, GameDrawer drawer) {
    mySunSingleton.draw(game, drawer);
  }

  public void drawPlanetCoreHack(SolGame game, GameDrawer drawer) {
    myPlanetCore.draw(game, drawer);
  }
}
