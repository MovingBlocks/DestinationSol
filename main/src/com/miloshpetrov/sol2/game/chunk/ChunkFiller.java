package com.miloshpetrov.sol2.game.chunk;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.asteroid.FarAsteroid;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.input.*;
import com.miloshpetrov.sol2.game.maze.Maze;
import com.miloshpetrov.sol2.game.planet.*;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.ArrayList;

public class ChunkFiller {
  public static final float DUST_DENSITY = .2f;
  public static final float ASTEROID_DENSITY = .008f;
  public static final float MIN_SYS_A_SZ = .5f;
  public static final float MAX_SYS_A_SZ = 1.2f;
  public static final float MIN_BELT_A_SZ = .4f;
  public static final float MAX_BELT_A_SZ = 2.4f;
  private static final float MAX_A_SPD = .2f;

  private static final float BELT_A_DENSITY = .04f;

  public static final float JUNK_MAX_SZ = .3f;
  public static final float JUNK_MAX_ROT_SPD = 45f;
  public static final float JUNK_MAX_SPD_LEN = .3f;

  public static final float FAR_JUNK_MAX_SZ = 2f;
  public static final float FAR_JUNK_MAX_ROT_SPD = 10f;

  public static final float ENEMY_MAX_SPD = .3f;
  public static final float ENEMY_MAX_ROT_SPD = 15f;
  public static final float DUST_SZ = .02f;
  private static final float MAZE_ZONE_BORDER = 20;
  private final TextureAtlas.AtlasRegion myDustTex;

  public ChunkFiller(TexMan texMan) {
    myDustTex = texMan.getTex("deco/space/dust", null);
  }


  public void fill(SolGame game, Vector2 chunk, RemoveController remover, boolean farBg) {
    if (DebugOptions.NO_OBJS) return;

    Vector2 chCenter = new Vector2(chunk);
    chCenter.scl(Const.CHUNK_SIZE);
    chCenter.add(Const.CHUNK_SIZE / 2, Const.CHUNK_SIZE / 2);

    float[] densityMul = {1};
    SpaceEnvConfig conf = getConfig(game, chCenter, densityMul, remover, farBg);
    if (farBg) {
      fillFarJunk(game, chCenter, remover, DraLevel.FAR_DECO_3, conf, densityMul[0]);
      fillFarJunk(game, chCenter, remover, DraLevel.FAR_DECO_2, conf, densityMul[0]);
      fillFarJunk(game, chCenter, remover, DraLevel.FAR_DECO_1, conf, densityMul[0]);
      return;
    }
    fillDust(game, chCenter, remover);
    fillJunk(game, remover, conf, chCenter);

  }

  private SpaceEnvConfig getConfig(SolGame game, Vector2 chCenter, float[] densityMul,
    RemoveController remover, boolean farBg) {
    PlanetMan pm = game.getPlanetMan();
    SolSystem sys = pm.getNearestSystem(chCenter);
    float toSys = sys.getPos().dst(chCenter);
    if (toSys < sys.getRadius()) {
      if (toSys < Const.SUN_RADIUS) return null;
      for (SystemBelt belt : sys.getBelts()) {
        if (belt.contains(chCenter)) {
          if (!farBg) fillAsteroids(game, remover, true, chCenter);
          SysConfig beltConfig = belt.getConfig();
          for (ShipConfig enemyConf : beltConfig.tempEnemies) {
            if (!farBg) fillEnemies(game, remover, enemyConf, chCenter);
          }
          return beltConfig.envConfig;
        }
      }
      Planet p = pm.getNearestPlanet(chCenter);
      float toPlanet = p.getPos().dst(chCenter);
      if (toPlanet < p.getFullHeight() + Const.CHUNK_SIZE) {
        return null;
      }
      float perc = toSys / sys.getRadius() * 2;
      if (perc > 1) perc = 2 - perc;
      densityMul[0] = perc;
      if (!farBg) fillForSys(game, chCenter, remover, sys);
      return sys.getConfig().envConfig;
    }
    Maze m = pm.getNearestMaze(chCenter);
    float dst = m.getPos().dst(chCenter);
    float zoneRad = m.getRadius() + MAZE_ZONE_BORDER;
    if (dst < zoneRad) {
      densityMul[0] = 1 - dst / zoneRad;
      return m.getConfig().envConfig;
    }
    return null;
  }

  private void fillForSys(SolGame game, Vector2 chCenter, RemoveController remover, SolSystem sys) {
    SysConfig conf = sys.getConfig();
    Vector2 mainStationPos = game.getGalaxyFiller().getMainStationPos();
    Vector2 startPos = mainStationPos == null ? new Vector2() : mainStationPos;
    float dst = chCenter.dst(startPos);
    if (dst > Const.CHUNK_SIZE) {
      fillAsteroids(game, remover, false, chCenter);
      ArrayList<ShipConfig> enemies = sys.getPos().dst(chCenter) < sys.getInnerRad() ? conf.innerTempEnemies : conf.tempEnemies;
      for (ShipConfig enemyConf : enemies) {
        fillEnemies(game, remover, enemyConf, chCenter);
      }
    }
  }

  private void fillEnemies(SolGame game, RemoveController remover, ShipConfig enemyConf, Vector2 chCenter) {
    int count = getEntityCount(enemyConf.density);
    if (count == 0) return;
    for (int i = 0; i < count; i++) {
      Vector2 enemyPos = getFreeRndPos(game, chCenter);
      FarShip ship = buildSpaceEnemy(game, enemyPos, remover, enemyConf);
      if (ship != null) game.getObjMan().addFarObjNow(ship);
    }
  }

  public FarShip buildSpaceEnemy(SolGame game, Vector2 pos, RemoveController remover,
    ShipConfig enemyConf)
  {
    if (pos == null) return null;
    Vector2 spd = new Vector2();
    SolMath.fromAl(spd, SolMath.rnd(180), SolMath.rnd(0, ENEMY_MAX_SPD));
    float rotSpd = SolMath.rnd(ENEMY_MAX_ROT_SPD);
    MoveDestProvider dp = new StillGuard(pos, game, enemyConf);
    Pilot provider = new AiPilot(dp, false, Fraction.EHAR, true, null, Const.AI_DET_DIST);
    HullConfig config = enemyConf.hull;
    int money = enemyConf.money;
    float angle = SolMath.rnd(180);
    return game.getShipBuilder().buildNewFar(game, pos, spd, angle, rotSpd, provider, enemyConf.items, config,
      remover, false, money, null);
  }

  private void fillAsteroids(SolGame game, RemoveController remover, boolean forBelt, Vector2 chCenter) {
    float density = forBelt ? BELT_A_DENSITY : ASTEROID_DENSITY;
    int count = getEntityCount(density);
    if (count == 0) return;
    for (int i = 0; i < count; i++) {
      Vector2 asteroidPos = getFreeRndPos(game, chCenter);
      if (asteroidPos == null) continue;
      float minSz = forBelt ? MIN_BELT_A_SZ : MIN_SYS_A_SZ;
      float maxSz = forBelt ? MAX_BELT_A_SZ : MAX_SYS_A_SZ;
      float sz = SolMath.rnd(minSz, maxSz);
      Vector2 spd = new Vector2();
      SolMath.fromAl(spd, SolMath.rnd(180), MAX_A_SPD);

      FarAsteroid a = game.getAsteroidBuilder().buildNewFar(asteroidPos, spd, sz, remover);
      game.getObjMan().addFarObjNow(a);
    }
  }

  private void fillFarJunk(SolGame game, Vector2 chCenter, RemoveController remover, DraLevel draLevel,
    SpaceEnvConfig conf, float densityMul)
  {
    if (conf == null) return;
    int count = getEntityCount(conf.farJunkDensity * densityMul);
    if (count == 0) return;

    ArrayList<Dra> dras = new ArrayList<Dra>();
    TexMan texMan = game.getTexMan();
    for (int i = 0; i < count; i++) {
      TextureAtlas.AtlasRegion tex = SolMath.elemRnd(conf.farJunkTexs);
      if (SolMath.test(.5f)) tex = texMan.getFlipped(tex);
      float sz = SolMath.rnd(.3f, 1) * FAR_JUNK_MAX_SZ;
      Vector2 junkPos = getRndPos(chCenter);
      junkPos.sub(chCenter);
      RectSprite s = new RectSprite(tex, sz, 0, 0, junkPos, draLevel, SolMath.rnd(180), SolMath.rnd(FAR_JUNK_MAX_ROT_SPD), Col.DDG, false);
      dras.add(s);
    }
    FarDras so = new FarDras(dras, new Vector2(chCenter), new Vector2(), remover, true);
    game.getObjMan().addFarObjNow(so);
  }

  private void fillJunk(SolGame game, RemoveController remover, SpaceEnvConfig conf, Vector2 chCenter) {
    if (conf == null) return;
    int count = getEntityCount(conf.junkDensity);
    if (count == 0) return;

    for (int i = 0; i < count; i++) {
      Vector2 junkPos = getRndPos(chCenter);

      TextureAtlas.AtlasRegion tex = SolMath.elemRnd(conf.junkTexs);
      if (SolMath.test(.5f)) tex = game.getTexMan().getFlipped(tex);
      float sz = SolMath.rnd(.3f, 1) * JUNK_MAX_SZ;
      float rotSpd = SolMath.rnd(JUNK_MAX_ROT_SPD);
      RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DraLevel.DECO, SolMath.rnd(180), rotSpd, Col.LG, false);
      ArrayList<Dra> dras = new ArrayList<Dra>();
      dras.add(s);

      Vector2 spd = new Vector2();
      SolMath.fromAl(spd, SolMath.rnd(180), SolMath.rnd(JUNK_MAX_SPD_LEN));
      FarDras so = new FarDras(dras, junkPos, spd, remover, true);
      game.getObjMan().addFarObjNow(so);
    }
  }

  private void fillDust(SolGame game, Vector2 chCenter, RemoveController remover) {
    ArrayList<Dra> dras = new ArrayList<Dra>();
    int count = getEntityCount(DUST_DENSITY);
    if (count == 0) return;
    TextureAtlas.AtlasRegion tex = myDustTex;
    for (int i = 0; i < count; i++) {
      Vector2 dustPos = getRndPos(chCenter);
      dustPos.sub(chCenter);
      RectSprite s = new RectSprite(tex, DUST_SZ, 0, 0, dustPos, DraLevel.DECO, 0, 0, Col.W, false);
      dras.add(s);
    }
    FarDras so = new FarDras(dras, chCenter, new Vector2(), remover, true);
    game.getObjMan().addFarObjNow(so);
  }

  private Vector2 getFreeRndPos(SolGame g, Vector2 chCenter) {
    Vector2 pos = new Vector2(chCenter);
    for (int i = 0; i < 100; i++) {
      pos.x += SolMath.rnd(Const.CHUNK_SIZE/2);
      pos.y += SolMath.rnd(Const.CHUNK_SIZE/2);
      if (g.isPlaceEmpty(pos)) return pos;
    }
    return null;
  }

  private Vector2 getRndPos(Vector2 chCenter) {
    Vector2 pos = new Vector2(chCenter);
    pos.x += SolMath.rnd(Const.CHUNK_SIZE/2);
    pos.y += SolMath.rnd(Const.CHUNK_SIZE/2);
    return pos;
  }

  private int getEntityCount(float density) {
    float amt = Const.CHUNK_SIZE * Const.CHUNK_SIZE * density;
    if (amt >= 1) return (int) amt;
    return SolMath.test(amt) ? 1 : 0;
  }

}
