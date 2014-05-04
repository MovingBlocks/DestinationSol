package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.gun.GunMount;
import com.miloshpetrov.sol2.game.item.Armor;
import com.miloshpetrov.sol2.game.item.Shield;
import com.miloshpetrov.sol2.game.maze.Maze;
import com.miloshpetrov.sol2.game.maze.MazeBuilder;
import com.miloshpetrov.sol2.game.planet.*;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.ArrayList;

public class MapDrawer {
  public static final float MIN_ZOOM = 8f;
  public static final float MUL_FACTOR = 2f;
  public static final float MAX_ZOOM = 512f;
  public static final float ICON_SZ = .04f;
  public static final float STAR_NODE_SZ = .003f;
  private static final float MAX_TIME = .75f;
  public static final float INNER_ICON_PERC = .6f;
  public static final float GRID_SZ = 40f;
  private final TextureAtlas.AtlasRegion myGroundTex;
  private final TextureAtlas.AtlasRegion myAtmTex;
  private final TextureAtlas.AtlasRegion myPlanetTex;
  private final TextureAtlas.AtlasRegion myPlanetCoreTex;
  private final TextureAtlas.AtlasRegion myStarTex;
  private final TextureAtlas.AtlasRegion myMazeTex;
  private final TextureAtlas.AtlasRegion mySkullTex;
  private final TextureAtlas.AtlasRegion myStarPortTex;
  private final TextureAtlas.AtlasRegion myBeltTex;
  private boolean myToggled;
  private final TextureAtlas.AtlasRegion myIconBg;
  private float myZoom;
  private float myTime;

  public MapDrawer(TexMan texMan) {
    myIconBg = texMan.getTex(TexMan.ICONS_DIR + "bg", null);
    myGroundTex = texMan.getTex(TexMan.ICONS_DIR + "ground", null);
    myAtmTex = texMan.getTex("mapObjs/atm", null);
    myPlanetTex = texMan.getTex("mapObjs/planet", null);
    myPlanetCoreTex = texMan.getTex("mapObjs/planetCore", null);
    myStarTex = texMan.getTex("mapObjs/star", null);
    myMazeTex = texMan.getTex("mapObjs/maze", null);
    mySkullTex = texMan.getTex(TexMan.ICONS_DIR + "skull", null);
    myStarPortTex = texMan.getTex(TexMan.ICONS_DIR + "starPort", null);
    myBeltTex = texMan.getTex("mapObjs/asteroids", null);
    myZoom = MAX_ZOOM / MUL_FACTOR / MUL_FACTOR;
  }

  public boolean isToggled() {
    return myToggled;
  }

  public void draw(Drawer drawer, SolGame game) {
    SolCam cam = game.getCam();
    float iconSz = cam.getViewHeight(myZoom) * ICON_SZ;
    float starNodeW = cam.getViewHeight(myZoom) * STAR_NODE_SZ;
    float viewDist = cam.getViewDist(myZoom);
    FractionMan fractionMan = game.getFractionMan();
    SolShip hero = game.getHero();
    Planet np = game.getPlanetMan().getNearestPlanet();
    Vector2 camPos = cam.getPos();

    drawer.begin(game);
    game.getGridDrawer().draw(drawer, game, GRID_SZ);
    drawPlanets(drawer, game, viewDist, np, camPos);
    drawMazes(drawer, game, viewDist, np, camPos);
    drawStarNodes(drawer, game, viewDist, camPos, starNodeW);
    drawIcons(drawer, game, iconSz, viewDist, fractionMan, hero, camPos);
    drawer.end();
  }

  private void drawMazes(Drawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos) {
    ArrayList<Maze> mazes = game.getPlanetMan().getMazes();
    for (Maze maze : mazes) {
      Vector2 mazePos = maze.getPos();
      float rad = maze.getRadius() - MazeBuilder.BORDER;
      if (viewDist < camPos.dst(mazePos) - rad) continue;
      drawer.draw(myMazeTex, 2 * rad, 2 * rad, rad, rad, mazePos.x, mazePos.y, 0, Col.W);
    }

  }

  private void drawPlanets(Drawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos) {
    ArrayList<SolSystem> systems = game.getPlanetMan().getSystems();
    for (SolSystem sys : systems) {
      drawer.drawCircle(sys.getPos(), sys.getRadius(), Col.W25, game.getCam().getRealLineWidth());
    }
    for (SolSystem sys : systems) {
      Vector2 sysPos = sys.getPos();
      float rad = Const.SUN_RADIUS;
      if (camPos.dst(sysPos) - rad < viewDist) {
        drawer.draw(myStarTex, 2 * rad, 2 * rad, rad, rad, sysPos.x, sysPos.y, 0, Col.W);
      }

      Vector2 beltIconPos = SolMath.getVec();
      for (SystemBelt belt : sys.getBelts()) {
        float beltRad = belt.getRadius();
        float halfWidth = belt.getHalfWidth();
        int beltIconCount = (int) (.1f * beltRad);
        for (int i = 0; i < beltIconCount; i++) {
          float angle = 360f * i / beltIconCount;
          SolMath.fromAl(beltIconPos, angle, beltRad);
          beltIconPos.add(sysPos);
          drawer.draw(myBeltTex, 2 * halfWidth, 2 * halfWidth, halfWidth, halfWidth, beltIconPos.x, beltIconPos.y, angle * 3, Col.W);
        }
      }
      SolMath.free(beltIconPos);
    }

    for (Planet planet : game.getPlanetMan().getPlanets()) {
      Vector2 planetPos = planet.getPos();
      float fh = planet.getFullHeight();
      if (viewDist < camPos.dst(planetPos) - fh) continue;
      drawer.draw(myAtmTex, 2*fh, 2*fh, fh, fh, planetPos.x, planetPos.y, 0, Col.G);
      if (planet == np && np.isObjsCreated()) {
        float gh = planet.getMinGroundHeight();
        drawer.draw(myPlanetCoreTex, 2*gh, 2*gh, gh, gh, planetPos.x, planetPos.y, planet.getAngle(), Col.W);
        drawNpGround(drawer, game, viewDist, np, camPos);
      } else {
        float gh = planet.getGroundHeight();
        drawer.draw(myPlanetTex, 2*gh, 2*gh, gh, gh, planetPos.x, planetPos.y, planet.getAngle(), Col.W);
      }
    }
  }

  private void drawIcons(Drawer drawer, SolGame game, float iconSz, float viewDist, FractionMan fractionMan,
    SolShip hero, Vector2 camPos)
  {
    float heroToughness = hero == null ? Float.MAX_VALUE : MapDrawer.getToughness(hero);

    for (SolObj o : game.getObjMan().getObjs()) {
      Vector2 oPos = o.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      if ((o instanceof SolShip)) {
        SolShip ship = (SolShip) o;
        String hint = ship.getPilot().getMapHint();
        if (hint == null && !DebugAspects.DETAILED_MAP) continue;
        drawObjIcon(drawer, iconSz, oPos, ship.getAngle(), fractionMan, hero, ship.getPilot().getFraction(), heroToughness, o, ship.getHull().config.icon);
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
        if (hint == null && !DebugAspects.DETAILED_MAP) continue;
        drawObjIcon(drawer, iconSz, oPos, ship.getAngle(), fractionMan, hero, ship.getPilot().getFraction(), heroToughness, o, ship.getHullConfig().icon);
      }
      if ((o instanceof StarPort.MyFar)) {
        StarPort.MyFar sp = (StarPort.MyFar) o;
        drawStarPortIcon(drawer, iconSz, sp.getFrom(), sp.getTo());
      }
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
    drawer.drawLine(pos1, pos2, Col.W25, starNodeW);
    SolMath.free(pos1);
    SolMath.free(pos2);
  }

  private void drawNpGround(Drawer drawer, SolGame game, float viewDist, Planet np, Vector2 camPos) {
    ObjMan objMan = game.getObjMan();
    for (SolObj o : objMan.getObjs()) {
      if (!(o instanceof TileObj)) continue;
      Vector2 oPos = o.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      TileObj to = (TileObj) o;
      if (to.getPlanet() != np) continue;
      float sz = to.getSz();
      drawer.draw(myGroundTex, sz, sz, sz/2, sz/2, oPos.x, oPos.y, to.getAngle(), Col.W);
    }

    for (FarObj o : objMan.getFarObjs()) {
      if (!(o instanceof FarTileObj)) continue;
      Vector2 oPos = o.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      FarTileObj to = (FarTileObj) o;
      if (to.getPlanet() != np) continue;
      float sz = to.getSz();
      drawer.draw(myGroundTex, sz, sz, sz/2, sz/2, oPos.x, oPos.y, to.getAngle(), Col.W);
    }
  }

  public void drawObjIcon(TexDrawer drawer, float iconSz, Vector2 pos, float objAngle,
    FractionMan fractionMan, SolShip hero, Fraction objFrac, float heroToughness,
    Object shipHack, TextureAtlas.AtlasRegion icon)
  {
    boolean enemy = hero != null && fractionMan.areEnemies(objFrac, hero.getPilot().getFraction());
    float angle = objAngle;
    if (enemy && myTime > 0 && isTough(heroToughness, shipHack)) {
      icon = mySkullTex;
      angle = 0;
    }
    drawer.draw(myIconBg, iconSz, iconSz, iconSz/2, iconSz/2, pos.x, pos.y, 0, Col.W);
    iconSz *= INNER_ICON_PERC;
    drawer.draw(icon, iconSz, iconSz, iconSz/2, iconSz/2, pos.x, pos.y, angle, enemy ? Col.B : Col.W);
  }

  private boolean isTough(float heroToughness, Object shipHack) {
    float dps = shipHack instanceof SolShip ? getDps((SolShip) shipHack) : getDps((FarShip) shipHack);
    float killTime = heroToughness / dps;
    return killTime < 5;
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
    myTime += game.getTimeStep();
    if (myTime > MAX_TIME) myTime = -MAX_TIME;
  }

  public static float getDps(SolShip s) {
    ShipHull h = s.getHull();
    GunMount m1 = s.getHull().getGunMount(false);
    GunMount m2 = s.getHull().getGunMount(true);
    float dps1 = getDps(m1.getGun(), m1.isFixed());
    float dps2 = m2 == null ? 0 : getDps(m2.getGun(), m2.isFixed());
    return dps1 + dps2;
  }

  public static float getDps(FarShip s) {
    return getDps(s.getGun(false), s.isMountFixed(false)) + getDps(s.getGun(true), s.isMountFixed(true));
  }

  private static float getDps(GunItem g, boolean fixed) {
    if (g == null || !g.canShoot()) return 0;
    float projSpd = g.config.projConfig.spdLen;
    float hitPerc = SolMath.clamp(projSpd - 4, 0, 4) / 4;
    if (fixed) hitPerc *= .7f;
    return g.config.dps * hitPerc;
  }

  public static float getToughness(SolShip s) {
    ShipHull h = s.getHull();
    return getToughness(s.getLife(), s.getArmor(), s.getShield());
  }

  public static float getToughness(FarShip s) {
    return getToughness(s.getLife(), s.getArmor(), s.getShield());
  }

  private static float getToughness(float life, Armor armor, Shield shield) {
    float r = life;
    if (armor != null) r /= (1 - armor.getPerc());
    if (shield != null) r += shield.getLife() * 1.2f;
    return r;
  }

  public TextureAtlas.AtlasRegion getStarPortTex() {
    return myStarPortTex;
  }
}
