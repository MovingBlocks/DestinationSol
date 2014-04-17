package com.miloshpetrov.sol2.game.chunk;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.asteroid.Asteroid;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.input.*;
import com.miloshpetrov.sol2.game.maze.Maze;
import com.miloshpetrov.sol2.game.planet.*;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.ArrayList;

public class ChunkFiller {
  public static final float DUST_DENSITY = .2f;
  public static final float ASTEROID_DENSITY = .004f;
  public static final float MIN_SYS_A_SZ = .5f;
  public static final float MAX_SYS_A_SZ = 1.5f;
  public static final float MIN_BELT_A_SZ = .5f;
  public static final float MAX_BELT_A_SZ = 3.5f;
  private static final float MAX_A_SPD = .2f;

  private static final float BELT_A_DENSITY = .08f;

  public static final float JUNK_MAX_SZ = .3f;
  public static final float JUNK_MAX_ROT_SPD = 45f;
  public static final float JUNK_MAX_SPD_LEN = .3f;

  public static final float FAR_JUNK_MAX_SZ = 2f;
  public static final float FAR_JUNK_MAX_ROT_SPD = 10f;

  public static final float ENEMY_MAX_SPD = .3f;
  public static final float ENEMY_MAX_ROT_SPD = 1f;
  public static final float DUST_SZ = .02f;
  private static final float MAZE_ZONE_BORDER = 20;

  public ChunkFiller(HullConfigs hullConfigs, TexMan texMan) {
  }


  public void fill(SolGame game, Vector2 chunk, RemoveController remover, boolean farBg) {
    if (DebugAspects.NO_OBJS) return;

    Vector2 chCenter = new Vector2(chunk);
    chCenter.scl(Const.CHUNK_SIZE);
    chCenter.add(Const.CHUNK_SIZE / 2, Const.CHUNK_SIZE / 2);

    float[] densityMul = {1};
    SpaceEnvConfig conf = getConfig(game, chCenter, densityMul, chunk, remover, farBg);
    if (farBg) {
      fillFarJunk(game, chCenter, remover, DraLevel.FAR_BG_3, conf, densityMul[0]);
      fillFarJunk(game, chCenter, remover, DraLevel.FAR_BG_2, conf, densityMul[0]);
      fillFarJunk(game, chCenter, remover, DraLevel.FAR_BG_1, conf, densityMul[0]);
      return;
    }
    fillDust(game, chCenter, remover);
    fillJunk(game, remover, conf, chCenter);

  }

  private SpaceEnvConfig getConfig(SolGame game, Vector2 chCenter, float[] densityMul, Vector2 chunk,
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
      SysConfig sysConfig = sys.getConfig();
      if (!farBg) fillForSys(game, chCenter, chunk, remover, sysConfig);
      return sysConfig.envConfig;
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

  private void fillForSys(SolGame game, Vector2 chCenter, Vector2 chunk, RemoveController remover, SysConfig conf) {
    SolShip mainStation = game.getGalaxyFiller().getMainStation();
    Vector2 startPos = mainStation == null ? new Vector2() : mainStation.getPos();
    float dst = chCenter.dst(startPos);
    if (dst > Const.CHUNK_SIZE) {
      fillAsteroids(game, remover, false, chCenter);
      for (ShipConfig enemyConf : conf.tempEnemies) {
        fillEnemies(game, remover, enemyConf, chCenter);
      }
    }
  }

  private void fillEnemies(SolGame game, RemoveController remover, ShipConfig enemyConf, Vector2 chCenter) {
    int count = getEntityCount(enemyConf.density);
    if (count == 0) return;
    for (int i = 0; i < count; i++) {
      Vector2 enemyPos = getRndPos(chCenter);
      SolShip ship = buildSpaceEnemy(game, enemyPos, remover, enemyConf);
      if (ship != null) game.getObjMan().addObjDelayed(ship);
    }
  }

  public SolShip buildSpaceEnemy(SolGame game, Vector2 pos, RemoveController remover, ShipConfig enemyConf) {
    if (!game.isPlaceEmpty(pos)) return null;
    Vector2 spd = new Vector2();
    SolMath.fromAl(spd, SolMath.rnd(180), SolMath.rnd(0, ENEMY_MAX_SPD));
    float rotSpd = SolMath.rnd(ENEMY_MAX_ROT_SPD);
    float detectionDist = Const.AI_DET_DIST_SPACE;
    Pilot provider = new AiPilot(new NoDestProvider(), false, Fraction.EHAR, true, null, detectionDist);
    HullConfig config = enemyConf.hull;
    boolean mountFixed1, mountFixed2, hasRepairer;
    mountFixed1 = enemyConf.isMountFixed1;
    mountFixed2 = enemyConf.isMountFixed2;
    hasRepairer = enemyConf.hasRepairer;
    int money = enemyConf.money;
    return game.getShipBuilder().buildNew(game, pos, spd, 0, rotSpd, provider, enemyConf.items, config, mountFixed1, mountFixed2,
      remover, hasRepairer, money, null);
  }

  private void fillAsteroids(SolGame game, RemoveController remover, boolean forBelt, Vector2 chCenter) {
    float density = forBelt ? BELT_A_DENSITY : ASTEROID_DENSITY;
    int count = getEntityCount(density);
    if (count == 0) return;
    for (int i = 0; i < count; i++) {
      Vector2 asteroidPos = getRndPos(chCenter);
      if (!game.isPlaceEmpty(asteroidPos)) continue;
      float minSz = forBelt ? MIN_BELT_A_SZ : MIN_SYS_A_SZ;
      float maxSz = forBelt ? MAX_BELT_A_SZ : MAX_SYS_A_SZ;
      float sz = SolMath.rnd(minSz, maxSz);
      Vector2 spd = new Vector2();
      SolMath.fromAl(spd, SolMath.rnd(180), MAX_A_SPD);

      Asteroid a = game.getAsteroidBuilder().buildNew(game, asteroidPos, spd, sz, remover);
      game.getObjMan().addObjDelayed(a);
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
      RectSprite s = new RectSprite(tex, sz, 0, 0, junkPos, draLevel, SolMath.rnd(180), SolMath.rnd(FAR_JUNK_MAX_ROT_SPD), Col.G);
      dras.add(s);
    }
    DrasObj so = new DrasObj(dras, new Vector2(chCenter), new Vector2(), remover, false, true);
    game.getObjMan().addObjDelayed(so);
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
      RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DraLevel.JUNK, SolMath.rnd(180), rotSpd, Col.LG);
      ArrayList<Dra> dras = new ArrayList<Dra>();
      dras.add(s);

      Vector2 spd = new Vector2();
      SolMath.fromAl(spd, SolMath.rnd(180), SolMath.rnd(JUNK_MAX_SPD_LEN));
      DrasObj so = new DrasObj(dras, junkPos, spd, remover, false, true);
      game.getObjMan().addObjDelayed(so);
    }
  }

  private void fillDust(SolGame game, Vector2 chCenter, RemoveController remover) {
    ArrayList<Dra> dras = new ArrayList<Dra>();
    int count = getEntityCount(DUST_DENSITY);
    if (count == 0) return;
    TextureAtlas.AtlasRegion tex = game.getTexMan().whiteTex;
    for (int i = 0; i < count; i++) {
      Vector2 dustPos = getRndPos(chCenter);
      dustPos.sub(chCenter);
      RectSprite s = new RectSprite(tex, DUST_SZ, 0, 0, dustPos, DraLevel.JUNK, 0, 0, Col.W);
      dras.add(s);
    }
    DrasObj so = new DrasObj(dras, chCenter, new Vector2(), remover, false, true);
    game.getObjMan().addObjDelayed(so);
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
