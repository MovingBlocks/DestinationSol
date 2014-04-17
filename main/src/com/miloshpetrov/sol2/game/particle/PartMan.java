package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TexMan;
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
  private final TextureAtlas.AtlasRegion myShieldTex;

  public PartMan(TexMan texMan) {
    myShieldTex = texMan.getTex("misc/shield", null);
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

  public void shieldSpark(SolGame game, Vector2 pos, ShipHull hull) {
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
