package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.maze.Maze;
import com.miloshpetrov.sol2.game.maze.MazeBuilder;
import com.miloshpetrov.sol2.game.planet.*;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.ArrayList;

public class MapDrawer {
  public static final float MIN_ZOOM = 8f;
  public static final float MUL_FACTOR = 2f;
  public static final float MAX_ZOOM = 512f;
  public static final float ICON_RAD = .02f;
  public static final float STAR_NODE_SZ = .003f;
  private static final float MAX_SKULL_TIME = .75f;
  public static final float INNER_ICON_PERC = .6f;
  public static final float GRID_SZ = 40f;
  private final TextureAtlas.AtlasRegion myAtmTex;
  private final TextureAtlas.AtlasRegion myPlanetTex;
  private final TextureAtlas.AtlasRegion myPlanetCoreTex;
  private final TextureAtlas.AtlasRegion myStarTex;
  private final TextureAtlas.AtlasRegion myMazeTex;
  private final TextureAtlas.AtlasRegion mySkullTex;
  private final TextureAtlas.AtlasRegion myStarPortTex;
  private final TextureAtlas.AtlasRegion myBeltTex;
  private final TextureAtlas.AtlasRegion myBeaconAttackTex;
  private final TextureAtlas.AtlasRegion myBeaconMoveTex;
  private final TextureAtlas.AtlasRegion myBeaconFollowTex;
  private boolean myToggled;
  private final TextureAtlas.AtlasRegion myIconBg;
  private float myZoom;
  private float mySkullTime;

  public MapDrawer(TexMan texMan) {
    myIconBg = texMan.getTex(TexMan.ICONS_DIR + "bg", null);
    myAtmTex = texMan.getTex("mapObjs/atm", null);
    myPlanetTex = texMan.getTex("mapObjs/planet", null);
    myPlanetCoreTex = texMan.getTex("mapObjs/planetCore", null);
    myStarTex = texMan.getTex("mapObjs/star", null);
    myMazeTex = texMan.getTex("mapObjs/maze", null);
    mySkullTex = texMan.getTex(TexMan.ICONS_DIR + "skull", null);
    myStarPortTex = texMan.getTex(TexMan.ICONS_DIR + "starPort", null);
    myBeltTex = texMan.getTex("mapObjs/asteroids", null);
    myBeaconAttackTex = texMan.getTex("mapObjs/beaconAttack", null);
    myBeaconMoveTex = texMan.getTex("mapObjs/beaconMove", null);
    myBeaconFollowTex = texMan.getTex("mapObjs/beaconFollow", null);
    myZoom = MAX_ZOOM / MUL_FACTOR / MUL_FACTOR;
  }

  public boolean isToggled() {
    return myToggled;
  }

  public void draw(Drawer drawer, SolGame game) {
    SolCam cam = game.getCam();
    float iconSz = getIconRadius(cam) * 2;
    float starNodeW = cam.getViewHeight(myZoom) * STAR_NODE_SZ;
    float viewDist = cam.getViewDist(myZoom);
    FractionMan fractionMan = game.getFractionMan();
    SolShip hero = game.getHero();
    Planet np = game.getPlanetMan().getNearestPlanet();
    Vector2 camPos = cam.getPos();
    float heroDmgCap = hero == null ? Float.MAX_VALUE : HardnessCalc.getShipDmgCap(hero);

    drawer.begin(game);
    game.getGridDrawer().draw(drawer, game, GRID_SZ);
    drawPlanets(drawer, game, viewDist, np, camPos, heroDmgCap);
    drawMazes(drawer, game, viewDist, np, camPos, heroDmgCap);
    drawStarNodes(drawer, game, viewDist, camPos, starNodeW);
    drawIcons(drawer, game, iconSz, viewDist, fractionMan, hero, camPos, heroDmgCap);
    drawer.end();
  }

  public float getIconRadius(SolCam cam) {
    return cam.getViewHeight(myZoom) * ICON_RAD;
  }

  private void drawMazes(Drawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos, float heroDmgCap) {
    ArrayList<Maze> mazes = game.getPlanetMan().getMazes();
    for (Maze maze : mazes) {
      Vector2 mazePos = maze.getPos();
      float rad = maze.getRadius() - MazeBuilder.BORDER;
      if (viewDist < camPos.dst(mazePos) - rad) continue;
      drawer.draw(myMazeTex, 2 * rad, 2 * rad, rad, rad, mazePos.x, mazePos.y, 45, Col.W);
      if (HardnessCalc.isDangerous(heroDmgCap, maze.getDps())) {
        drawAreaDanger(drawer, rad, mazePos);
      }
    }

  }

  private void drawPlanets(Drawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos, float heroDmgCap) {
    ArrayList<SolSystem> systems = game.getPlanetMan().getSystems();
    for (SolSystem sys : systems) {
      drawer.drawCircle(sys.getPos(), sys.getRadius(), Col.UI_MED, game.getCam().getRealLineWidth());
    }
    for (SolSystem sys : systems) {
      float dangerRad = HardnessCalc.isDangerous(heroDmgCap, sys.getDps()) ? sys.getRadius() : 0;
      Vector2 sysPos = sys.getPos();
      float rad = Const.SUN_RADIUS;
      if (camPos.dst(sysPos) - rad < viewDist) {
        drawer.draw(myStarTex, 2 * rad, 2 * rad, rad, rad, sysPos.x, sysPos.y, 0, Col.W);
      }

      Vector2 beltIconPos = SolMath.getVec();
      for (SystemBelt belt : sys.getBelts()) {
        float beltRad = belt.getRadius();
        float halfWidth = belt.getHalfWidth();
        int beltIconCount = (int) (.12f * beltRad);
        for (int i = 0; i < beltIconCount; i++) {
          float angle = 360f * i / beltIconCount;
          SolMath.fromAl(beltIconPos, angle, beltRad);
          beltIconPos.add(sysPos);
          drawer.draw(myBeltTex, 2 * halfWidth, 2 * halfWidth, halfWidth, halfWidth, beltIconPos.x, beltIconPos.y, angle * 3, Col.W);
        }
        float outerRad = beltRad + halfWidth;
        if (dangerRad < outerRad && HardnessCalc.isDangerous(heroDmgCap, belt.getDps())) dangerRad = outerRad;
      }
      SolMath.free(beltIconPos);
      if (dangerRad < sys.getInnerRad() && HardnessCalc.isDangerous(heroDmgCap, sys.getInnerDps())) dangerRad = sys.getInnerRad();
      if (dangerRad > 0) {
        drawAreaDanger(drawer, dangerRad, sysPos);
      }
    }

    for (Planet planet : game.getPlanetMan().getPlanets()) {
      Vector2 planetPos = planet.getPos();
      float fh = planet.getFullHeight();
      float dstToPlanetAtm = camPos.dst(planetPos) - fh;
      if (viewDist < dstToPlanetAtm) continue;
      drawer.draw(myAtmTex, 2*fh, 2*fh, fh, fh, planetPos.x, planetPos.y, 0, Col.UI_DARK);
      float gh;
      if (dstToPlanetAtm < 0) {
        gh = planet.getMinGroundHeight();
        drawer.draw(myPlanetCoreTex, 2*gh, 2*gh, gh, gh, planetPos.x, planetPos.y, planet.getAngle(), Col.W);
        drawNpGround(drawer, game, viewDist, np, camPos);
      } else {
        gh = planet.getGroundHeight();
        drawer.draw(myPlanetTex, 2*gh, 2*gh, gh, gh, planetPos.x, planetPos.y, planet.getAngle(), Col.W);
      }
      float dangerRad = HardnessCalc.isDangerous(heroDmgCap, planet.getAtmDps()) ? fh : 0;
      if (dangerRad < gh && HardnessCalc.isDangerous(heroDmgCap, planet.getGroundDps())) dangerRad = gh;
      if (dangerRad > 0) {
        drawAreaDanger(drawer, dangerRad, planetPos);
      }
    }
  }

  private void drawAreaDanger(Drawer drawer, float rad, Vector2 pos) {
    if (mySkullTime < 0) return;
    drawer.draw(myIconBg, rad *2, rad *2, rad, rad, pos.x, pos.y, 0, Col.UI_WARN);
    rad *= INNER_ICON_PERC;
    drawer.draw(mySkullTex, rad *2, rad *2, rad, rad, pos.x, pos.y, 0, Col.W);
  }

  private void drawIcons(Drawer drawer, SolGame game, float iconSz, float viewDist, FractionMan fractionMan,
    SolShip hero, Vector2 camPos, float heroDmgCap)
  {

    for (SolObj o : game.getObjMan().getObjs()) {
      Vector2 oPos = o.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      if ((o instanceof SolShip)) {
        SolShip ship = (SolShip) o;
        String hint = ship.getPilot().getMapHint();
        if (hint == null && !DebugOptions.DETAILED_MAP) continue;
        drawObjIcon(drawer, iconSz, oPos, ship.getAngle(), fractionMan, hero, ship.getPilot().getFraction(), heroDmgCap, o, ship.getHull().config.icon);
      }
      if ((o instanceof StarPort)) {
        StarPort sp = (StarPort) o;
        drawStarPortIcon(drawer, iconSz, sp.getFrom(), sp.getTo());
      }
    }

    for (FarObj o : game.getObjMan().getFarObjs()) {
      Vector2 oPos = o.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      if ((o instanceof FarShip)) {
        FarShip ship = (FarShip) o;
        String hint = ship.getPilot().getMapHint();
        if (hint == null && !DebugOptions.DETAILED_MAP) continue;
        drawObjIcon(drawer, iconSz, oPos, ship.getAngle(), fractionMan, hero, ship.getPilot().getFraction(), heroDmgCap, o, ship.getHullConfig().icon);
      }
      if ((o instanceof StarPort.MyFar)) {
        StarPort.MyFar sp = (StarPort.MyFar) o;
        drawStarPortIcon(drawer, iconSz, sp.getFrom(), sp.getTo());
      }
    }
    BeaconHandler bh = game.getBeaconHandler();
    BeaconHandler.Action bhAction = bh.getCurrAction();
    if (bhAction != null) {
      Vector2 beaconPos = bh.getPos();
      TextureRegion icon = myBeaconMoveTex;
      if (bhAction == BeaconHandler.Action.ATTACK) icon = myBeaconAttackTex;
      else if (bhAction == BeaconHandler.Action.FOLLOW) icon = myBeaconFollowTex;
      float beaconSz = iconSz * 1.5f;
      drawer.draw(icon, beaconSz, beaconSz, beaconSz/2, beaconSz/2, beaconPos.x, beaconPos.y, 0, Col.W);
    }
  }

  public void drawStarPortIcon(Drawer drawer, float iconSz, Planet from, Planet to) {
    float angle = SolMath.angle(from.getPos(), to.getPos());
    Vector2 pos = StarPort.getDesiredPos(from, to, false);
    drawObjIcon(drawer, iconSz, pos, angle, null, null, null, -1, null, myStarPortTex);
    SolMath.free(pos);
  }

  private void drawStarNodes(Drawer drawer, SolGame game, float viewDist, Vector2 camPos, float starNodeW)
  {
    for (SolObj o : game.getObjMan().getObjs()) {
      if (!(o instanceof StarPort)) continue;
      Vector2 oPos = o.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      StarPort sp = (StarPort) o;
      drawStarNode(drawer, sp.getFrom(), sp.getTo(), starNodeW);
    }

    for (FarObj o : game.getObjMan().getFarObjs()) {
      if (!(o instanceof StarPort.MyFar)) continue;
      Vector2 oPos = o.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      StarPort.MyFar sp = (StarPort.MyFar) o;
      if (!sp.isSecondary()) drawStarNode(drawer, sp.getFrom(), sp.getTo(), starNodeW);
    }
  }

  private void drawStarNode(Drawer drawer, Planet from, Planet to, float starNodeW) {
    Vector2 pos1 = StarPort.getDesiredPos(from, to, false);
    Vector2 pos2 = StarPort.getDesiredPos(to, from, false);
    drawer.drawLine(pos1, pos2, Col.UI_LIGHT, starNodeW);
    SolMath.free(pos1);
    SolMath.free(pos2);
  }

  private void drawNpGround(Drawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos) {
    ObjMan objMan = game.getObjMan();
    TextureAtlas.AtlasRegion wt = game.getTexMan().whiteTex;
    for (SolObj o : objMan.getObjs()) {
      if (!(o instanceof TileObj)) continue;
      TileObj to = (TileObj) o;
      if (to.getPlanet() != np) continue;
      Vector2 oPos = o.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      float sz = to.getSz();
      drawPlanetTile(to.getTile(), sz, drawer, wt, oPos, to.getAngle());
    }

    for (FarObj o : objMan.getFarObjs()) {
      if (!(o instanceof FarTileObj)) continue;
      FarTileObj to = (FarTileObj) o;
      if (to.getPlanet() != np) continue;
      Vector2 oPos = o.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      float sz = to.getSz();
      drawPlanetTile(to.getTile(), sz, drawer, wt, oPos, to.getAngle());
    }
  }

  public void drawObjIcon(TexDrawer drawer, float iconSz, Vector2 pos, float objAngle,
    FractionMan fractionMan, SolShip hero, Fraction objFrac, float heroDmgCap,
    Object shipHack, TextureAtlas.AtlasRegion icon)
  {
    boolean enemy = hero != null && fractionMan.areEnemies(objFrac, hero.getPilot().getFraction());
    float angle = objAngle;
    if (enemy && mySkullTime > 0 && HardnessCalc.isDangerous(heroDmgCap, shipHack)) {
      icon = mySkullTex;
      angle = 0;
    }
    drawer.draw(myIconBg, iconSz, iconSz, iconSz/2, iconSz/2, pos.x, pos.y, 0, enemy ? Col.UI_WARN : Col.UI_LIGHT);
    iconSz *= INNER_ICON_PERC;
    drawer.draw(icon, iconSz, iconSz, iconSz/2, iconSz/2, pos.x, pos.y, angle, Col.W);
  }

  public void setToggled(boolean toggled) {
    myToggled = toggled;
  }

  public void changeZoom(boolean zoomIn) {
    if (zoomIn) myZoom /= MUL_FACTOR; else myZoom *= MUL_FACTOR;
    myZoom = SolMath.clamp(myZoom, MIN_ZOOM, MAX_ZOOM);
  }

  public float getZoom() {
    return myZoom;
  }

  public void update(SolGame game) {
    mySkullTime += game.getTimeStep();
    if (mySkullTime > MAX_SKULL_TIME) mySkullTime = -MAX_SKULL_TIME;
  }

  private void drawPlanetTile(Tile t, float sz, Drawer drawer, TextureAtlas.AtlasRegion wt, Vector2 p, float angle) {
    float szh = .6f * sz;
    Color col = t.from == SurfDir.UP && t.to == SurfDir.UP ? Col.W : Col.UI_GROUND;
    if (t.from == SurfDir.FWD || t.from == SurfDir.UP) {
      if (t.from == SurfDir.UP) drawer.draw(wt, szh, szh, 0, 0, p.x, p.y, angle - 90, col);
      drawer.draw(wt, szh, szh, 0, 0, p.x, p.y, angle, col);
    }
    if (t.to == SurfDir.FWD || t.to == SurfDir.UP) {
      if (t.to == SurfDir.UP) drawer.draw(wt, szh, szh, 0, 0, p.x, p.y, angle + 180, col);
      drawer.draw(wt, szh, szh, 0, 0, p.x, p.y, angle + 90, col);
    }
  }

  public TextureAtlas.AtlasRegion getStarPortTex() {
    return myStarPortTex;
  }

}
