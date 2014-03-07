package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.item.Shield;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.ShipHull;

import java.io.*;
import java.util.ArrayList;

public class PartMan {
  public static final float EXPL_LIGHT_MAX_DIST = .2f;
  public static final float EXPL_LIGHT_MAX_SZ = .4f;
  public static final float EXPL_LIGHT_MAX_FADE_TIME = .8f;
  private final ParticleEmitter myFlameEmitter;
  private final ParticleEmitter myExplosionEmitter;
  private final ParticleEmitter mySparkEmitter;
  private final ParticleEmitter mySmokeEmitter;
  private final ParticleEmitter myExplInnerEmitter;
  private final TextureAtlas.AtlasRegion myShieldTex;

  public PartMan(TexMan texMan) {
    myFlameEmitter = loadEmitter("flame", texMan);
    myExplosionEmitter = loadEmitter("explosion", texMan);
    mySparkEmitter = loadEmitter("spark", texMan);
    mySmokeEmitter = loadEmitter("smoke", texMan);
    myExplInnerEmitter = loadEmitter("explInner", texMan);
    myShieldTex = texMan.getTex("misc/shield", null);
  }

  private ParticleEmitter loadEmitter(final String name, TexMan texMan) {
    FileHandle effectFile = SolFiles.readOnly("res/emitters/" + name + ".p");
    InputStream input = effectFile.read();
    BufferedReader reader = null;
    ParticleEmitter emitter;
    try {
      reader = new BufferedReader(new InputStreamReader(input), 512);
      emitter = new ParticleEmitter(reader);
      emitter.flipY();
      reader.readLine();
      String imagePath = reader.readLine();
      emitter.setImagePath(imagePath);
    } catch (IOException ex) {
      throw new GdxRuntimeException("Error loading effect: " + effectFile, ex);
    } finally {
      try {
        if (reader != null) reader.close();
      } catch (IOException ignore) {
      }
    }

    String imagePath = emitter.getImagePath();
    String imageName = new File(imagePath.replace('\\', '/')).getName();
    int lastDotIndex = imageName.lastIndexOf('.');
    if (lastDotIndex != -1) imageName = imageName.substring(0, lastDotIndex);
    imageName = "particles/" + imageName;
    Sprite sprite = texMan.createSprite(imageName);
    if (sprite == null) throw new IllegalArgumentException("SpriteSheet missing image: " + imageName);
    emitter.setSprite(sprite);
    return emitter;
  }

  /**
   * consumes relPos
   */
  public ParticleSrc buildFlameSrc(SolGame cmp, Vector2 relPos) {
    return new ParticleSrc(myFlameEmitter, true, DraLevel.PART_BG_0, relPos);
  }

  public ParticleSrc buildSmokeSrc(SolGame cmp, Vector2 relPos) {
    return new ParticleSrc(mySmokeEmitter, true, DraLevel.PART_FG_0, relPos);
  }

  /**
  * doesnt' consume basePos
  * */
  public void finish(SolGame game, ParticleSrc src, Vector2 basePos) {
    if (src.isContinuous()) src.setWorking(false);
    ArrayList<Dra> dras = new ArrayList<Dra>();
    dras.add(src);
    DrasObj o = new DrasObj(dras, new Vector2(basePos), new Vector2(), null, true, false);
    game.getObjMan().addObjDelayed(o);
  }

  public void explode(Vector2 pos, SolGame game, boolean withSmoke) {
    if (withSmoke) {
      ParticleSrc src = addEff(myExplosionEmitter, DraLevel.PART_BG_0);
      Planet np = game.getPlanetMan().getNearestPlanet();
      if (np != null) {
        Vector2 spd = np.getSmokeSpd(pos);
        if (spd != null) {
          src.setSpd(spd);
          SolMath.free(spd);
        }
      }
      finish(game, src, pos);
    }

    ParticleSrc srcInner = addEff(myExplInnerEmitter, DraLevel.PART_BG_1);
    finish(game, srcInner, pos);


    for (int i = 0; i < 3; i++) {
      Vector2 lightPos = new Vector2();
      SolMath.fromAl(lightPos, SolMath.rnd(180), SolMath.rnd(0, EXPL_LIGHT_MAX_DIST));
      lightPos.add(pos);
      float sz = SolMath.rnd(.5f, 1) * EXPL_LIGHT_MAX_SZ;
      float fadeTime = SolMath.rnd(.5f, 1) * EXPL_LIGHT_MAX_FADE_TIME;
      LightObj light = new LightObj(game, sz, true, 1, lightPos, fadeTime);
      game.getObjMan().addObjDelayed(light);
    }
  }

  private ParticleSrc addEff(ParticleEmitter e, DraLevel draLevel) {
    ParticleSrc src = new ParticleSrc(e, false, draLevel, new Vector2());
    src.setWorking(true);
    return src;
  }

  public void spark(Vector2 pos, SolGame game) {
    ParticleSrc src = addEff(mySparkEmitter, DraLevel.PART_BG_0);
    finish(game, src, pos);
  }

  public void shieldSpark(SolGame game, Vector2 pos, ShipHull hull) {
//    explode(pos, game);
    Vector2 hullPos = hull.getPos();
    float shieldRadius = hull.config.size * Shield.SIZE_PERC;
    float toEdge = SolMath.angle(hullPos, pos);
    RectSprite s = new RectSprite(myShieldTex, shieldRadius*2, 0, 0, new Vector2(), DraLevel.PART_FG_0, toEdge, 0, Col.W);
    ArrayList<Dra> dras = new ArrayList<Dra>();
    dras.add(s);
    DrasObj o = new DrasObj(dras, new Vector2(hullPos), new Vector2(hull.getSpd()), null, false, false);
    o.fade(.5f);
    game.getObjMan().addObjDelayed(o);
  }
}
