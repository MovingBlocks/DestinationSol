package com.miloshpetrov.sol2.game.chunk;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.asteroid.Asteroid;
import com.miloshpetrov.sol2.game.asteroid.AsteroidBuilder;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.input.*;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.ArrayList;

public class ChunkFiller {
  public static final float DUST_DENSITY = .2f;
  public static final float ASTEROID_DENSITY = .004f;

  public static final float JUNK_DENSITY = .06f;
  public static final float JUNK_MAX_SZ = .3f;
  public static final float JUNK_MAX_ROT_SPD = 45f;
  public static final float JUNK_MAX_SPD_LEN = .3f;

  public static final float FAR_JUNK_DENSITY = .01f;
  public static final float FAR_JUNK_MAX_SZ = 2f;
  public static final float FAR_JUNK_MAX_ROT_SPD = 10f;

  public static final float ENEMY_DENSITY = .0012f;
  public static final float ENEMY_MAX_SPD = .3f;
  public static final float ENEMY_MAX_ROT_SPD = 1f;
  public static final float DUST_SZ = .02f;


  public void fill(SolGame game, Vector2 chunk, RemoveController remover) {
    if (DebugAspects.NO_OBJS) return;
    createPack(game, chunk, remover, null);
    createPack(game, chunk, remover, DraLevel.FAR_BG_3);
    createPack(game, chunk, remover, DraLevel.FAR_BG_2);
    createPack(game, chunk, remover, DraLevel.FAR_BG_1);
    fillMovingJunk(game, chunk, remover);

    Vector2 pPos = game.getGalaxyFiller().getMainStation().getPos();
    Vector2 chPos = SolMath.getVec(chunk);
    chPos.add(Const.CHUNK_SIZE / 2, Const.CHUNK_SIZE / 2);
    float dst = chPos.dst(pPos);
    SolMath.free(chPos);
    if (dst > Const.CHUNK_SIZE) {
      fillAsteroids(game, chunk, remover);
      fillEnemies(game, chunk, remover);
    }
  }

  private void fillEnemies(SolGame game, Vector2 chunk, RemoveController remover) {

    int count = getEntityCount(ENEMY_DENSITY);
    if (count == 0) return;
    for (int i = 0; i < count; i++) {
      Vector2 enemyPos = getRndPos(chunk);
      SolShip ship = buildSpaceEnemy(game, enemyPos, remover);
      if (ship != null) game.getObjMan().addObjDelayed(ship);
    }
  }

  public SolShip buildSpaceEnemy(SolGame game, Vector2 pos, RemoveController remover) {
    if (!game.isPlaceEmpty(pos)) return null;

    Vector2 spd = new Vector2();
    SolMath.fromAl(spd, SolMath.rnd(180), SolMath.rnd(0, ENEMY_MAX_SPD));
    float rotSpd = SolMath.rnd(ENEMY_MAX_ROT_SPD);

    if (SolMath.test(.9f)) {
      return buildHawk1(game, pos, spd, rotSpd, remover);
    } else {
      return buildDragon1(game, pos, spd, rotSpd, remover);
    }
  }

  private SolShip buildHawk1(SolGame game, Vector2 pos, Vector2 spd, float rotSpd, RemoveController remover) {
    float detectionDist = game.getCam().getSpaceViewDist();
    Pilot provider = new AiPilot(new NoDestProvider(), false, Fraction.EHAR, true, null, detectionDist);

    HullConfig config = game.getHullConfigs().hawk;
    return game.getShipBuilder().buildNew(game, pos, new Vector2(spd), 0, rotSpd, provider, "wbo s:.1 b:.3 rep:.5", config, false, false,
      remover, false, 20f, null);
  }



  private SolShip buildDragon1(SolGame game, Vector2 pos, Vector2 spd, float rotSpd, RemoveController remover) {
    float detectionDist = game.getCam().getSpaceViewDist();
    Pilot provider = new AiPilot(new NoDestProvider(), false, Fraction.EHAR, true, null, detectionDist);

    HullConfig config = game.getHullConfigs().dragon;
    return game.getShipBuilder().buildNew(game, pos, new Vector2(spd), 0, rotSpd, provider, "mg|sg rl|bo:.3 b:.7:4 r:.5:2 rep:.8", config, false, false,
      remover, true, 100f, null);
  }

  private void fillAsteroids(SolGame game, Vector2 chunk, RemoveController remover) {
    int count = getEntityCount(ASTEROID_DENSITY);
    if (count == 0) return;
    for (int i = 0; i < count; i++) {
      Vector2 asteroidPos = getRndPos(chunk);
      if (!game.isPlaceEmpty(asteroidPos)) continue;
      int modelNr = SolMath.intRnd(AsteroidBuilder.VARIANT_COUNT);
      Asteroid a = game.getAsteroidBuilder().build(game, asteroidPos, modelNr, remover);
      game.getObjMan().addObjDelayed(a);
    }
  }

  private void createPack(SolGame game, Vector2 chunk, RemoveController remover, DraLevel farDraLevel) {
    Vector2 packPos = new Vector2(chunk);
    packPos.x += Const.CHUNK_SIZE/2;
    packPos.y += Const.CHUNK_SIZE/2;
    ArrayList<Dra> dras = new ArrayList<Dra>();
    if (farDraLevel != null) {
      fillFarJunk(game, chunk, packPos, dras, farDraLevel);
    } else {
      fillDust(game, chunk, packPos, dras);
    }
    DrasObj so = new DrasObj(dras, packPos, new Vector2(), remover, false, true);
    game.getObjMan().addObjDelayed(so);
  }

  private void fillMovingJunk(SolGame game, Vector2 chunk, RemoveController remover) {
    int count = getEntityCount(JUNK_DENSITY);
    if (count == 0) return;

    for (int i = 0; i < count; i++) {
      Vector2 junkPos = getRndPos(chunk);

      TextureAtlas.AtlasRegion tex = game.getTexMan().getRndTex("junks/junk", false);
      float sz = SolMath.rnd(.3f, 1) * JUNK_MAX_SZ;
      float rotSpd = SolMath.rnd(JUNK_MAX_ROT_SPD);
      RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DraLevel.JUNK, SolMath.rnd(180), rotSpd, Col.G);
      ArrayList<Dra> dras = new ArrayList<Dra>();
      dras.add(s);

      Vector2 spd = new Vector2();
      SolMath.fromAl(spd, SolMath.rnd(180), SolMath.rnd(JUNK_MAX_SPD_LEN));
      DrasObj so = new DrasObj(dras, junkPos, spd, remover, false, true);
      game.getObjMan().addObjDelayed(so);
    }
  }

  private void fillDust(SolGame game, Vector2 chunk, Vector2 packPos, ArrayList<Dra> dras) {
    int count = getEntityCount(DUST_DENSITY);
    if (count == 0) return;
    TextureAtlas.AtlasRegion tex = game.getTexMan().whiteTex;
    for (int i = 0; i < count; i++) {
      Vector2 dustPos = getRndPos(chunk);
      dustPos.sub(packPos);
      RectSprite s = new RectSprite(tex, DUST_SZ, 0, 0, dustPos, DraLevel.JUNK, 0, 0, Col.G);
      dras.add(s);
    }
  }

  private void fillFarJunk(SolGame game, Vector2 chunk, Vector2 packPos, ArrayList<Dra> dras, DraLevel draLevel) {
    int count = getEntityCount(FAR_JUNK_DENSITY);
    if (count == 0) return;
    TexMan texMan = game.getTexMan();
    for (int i = 0; i < count; i++) {
      TextureAtlas.AtlasRegion tex = texMan.getRndTex("farJunks/junk", false);
      float sz = SolMath.rnd(.3f, 1) * FAR_JUNK_MAX_SZ;
      Vector2 junkPos = getRndPos(chunk);
      junkPos.sub(packPos);
      RectSprite s = new RectSprite(tex, sz, 0, 0, junkPos, draLevel, SolMath.rnd(180), SolMath.rnd(FAR_JUNK_MAX_ROT_SPD), Col.DG);
      dras.add(s);
    }
  }

  private Vector2 getRndPos(Vector2 chunk) {
    Vector2 pos = new Vector2(chunk);
    pos.x += SolMath.rnd(0, Const.CHUNK_SIZE);
    pos.y += SolMath.rnd(0, Const.CHUNK_SIZE);
    return pos;
  }

  private int getEntityCount(float density) {
    float amt = Const.CHUNK_SIZE * Const.CHUNK_SIZE * density;
    if (amt >= 1) return (int) amt;
    return SolMath.test(amt) ? 1 : 0;
  }


}
