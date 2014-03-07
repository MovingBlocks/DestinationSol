package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.*;

import java.util.List;

public class LightSrc {
  public static final float DEFAULT_FADE_TIME = .1f;
  public static final float A_RATIO = .5f;
  public static final float SZ_RATIO = .8f;

  private final RectSprite myCircle;
  private final RectSprite myHalo;
  private final float mySz;
  private float myWorkPerc;
  private final float myIntensity;
  private float myFadeTime;

  /** doesn't consume relPos
   */
  public LightSrc(SolGame game, float sz, boolean hasHalo, float intensity, Vector2 relPos) {
    TextureAtlas.AtlasRegion tex = game.getTexMan().getTex("particles/lightCircle", null);
    mySz = sz;
    myCircle = new RectSprite(tex, 0, 0, 0, new Vector2(relPos), DraLevel.PART_BG_0, 0, 0, Col.W);
    tex = game.getTexMan().getTex("particles/lightHalo", null);
    myHalo = hasHalo ? new RectSprite(tex, 0, 0, 0, new Vector2(relPos), DraLevel.PART_FG_0, 0, 0, Col.W) : null;
    myIntensity = intensity;
    myFadeTime = DEFAULT_FADE_TIME;
  }

  public void update(boolean working, float baseAngle, SolGame game) {
    if (working) {
      myWorkPerc = 1f;
    } else {
      myWorkPerc = SolMath.approach(myWorkPerc, 0, game.getTimeStep() / myFadeTime);
    }
    float baseA = SolMath.rnd(.5f, 1) * myWorkPerc * myIntensity;
    myCircle.tint.a = baseA * A_RATIO;
    float sz = (1 + SolMath.rnd(.2f * myIntensity)) * mySz;
    myCircle.setTexSz(SZ_RATIO * sz);
    if (myHalo != null) {
      myHalo.tint.a = baseA;
      myHalo.relAngle = game.getCam().getAngle() - baseAngle;
      myHalo.setTexSz(sz);
    }
  }

  public boolean isFinished() {
    return myWorkPerc <= 0;
  }

  public void collectDras(List<Dra> dras) {
    dras.add(myCircle);
    if (myHalo != null) dras.add(myHalo);
  }

  public void setFadeTime(float fadeTime) {
    myFadeTime = fadeTime;
  }

  public void setWorking() {
    myWorkPerc = 1;
  }

  public void setRelPos(Vector2 relPos) {
    myCircle.relPos.set(relPos);
    if (myHalo != null) myHalo.relPos.set(relPos);
  }
}
