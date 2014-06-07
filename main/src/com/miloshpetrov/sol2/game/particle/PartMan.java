package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.item.Shield;
import com.miloshpetrov.sol2.game.ship.ShipHull;

import java.util.ArrayList;

public class PartMan {
  public static final float EXPL_LIGHT_MAX_SZ = .4f;
  public static final float EXPL_LIGHT_MAX_FADE_TIME = .8f;
  public static final float SZ_TO_BLINK_COUNT = 18f;

  public PartMan() {
  }

  public void finish(SolGame game, ParticleSrc src, Vector2 basePos) {
    if (src.isContinuous()) src.setWorking(false);
    ArrayList<Dra> dras = new ArrayList<Dra>();
    dras.add(src);
    DrasObj o = new DrasObj(dras, new Vector2(basePos), new Vector2(), null, true, false);
    game.getObjMan().addObjDelayed(o);
  }

  public void blinks(Vector2 pos, SolGame game, float sz) {
    int count = (int) (SZ_TO_BLINK_COUNT * sz * sz);
    for (int i = 0; i < count; i++) {
      Vector2 lightPos = new Vector2();
      SolMath.fromAl(lightPos, SolMath.rnd(180), SolMath.rnd(0, sz /2));
      lightPos.add(pos);
      float lightSz = SolMath.rnd(.5f, 1) * EXPL_LIGHT_MAX_SZ;
      float fadeTime = SolMath.rnd(.5f, 1) * EXPL_LIGHT_MAX_FADE_TIME;
      LightObj light = new LightObj(game, lightSz, true, 1, lightPos, fadeTime, game.getCols().fire);
      game.getObjMan().addObjDelayed(light);
    }
  }

  public void shieldSpark(SolGame game, Vector2 collPos, ShipHull hull, TextureAtlas.AtlasRegion shieldTex, float perc) {
    if (perc <= 0) return;
    Vector2 pos = hull.getPos();
    float angle = SolMath.angle(pos, collPos);
    float sz = hull.config.size * Shield.SIZE_PERC * 2;
    float alphaSum = perc * 3;
    RectSprite s = null;
    int count = (int) alphaSum + 1;
    for (int i = 0; i < count; i++) {
      s = blip(game, pos, angle, sz, .5f, hull.getSpd(), shieldTex);
    }
    float lastTint = SolMath.clamp(alphaSum - (int) alphaSum);
    if (s != null) {
      s.tint.a = lastTint;
      s.baseAlpha = lastTint;
    }
  }

  public RectSprite blip(SolGame game, Vector2 pos, float angle, float sz, float fadeTime, Vector2 spd,
    TextureAtlas.AtlasRegion tex)
  {
    RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DraLevel.PART_FG_0, angle, 0, Col.W, true);
    ArrayList<Dra> dras = new ArrayList<Dra>();
    dras.add(s);
    DrasObj o = new DrasObj(dras, new Vector2(pos), new Vector2(spd), null, false, false);
    o.fade(fadeTime);
    game.getObjMan().addObjDelayed(o);
    return s;
  }

}
