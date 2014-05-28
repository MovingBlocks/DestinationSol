package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;

public class SunSingleton {
  private static final float SUN_DMG = 4f;
  public static final float SUN_HOT_RAD = .75f * Const.SUN_RADIUS;
  public static final float GRAV_CONST = 2000;

  private final TextureAtlas.AtlasRegion myGradTex;
  private final TextureAtlas.AtlasRegion myWhiteTex;
  private final Color myGradTint;
  private final Color myFillTint;

  public SunSingleton(TexMan texMan) {
    myGradTex = texMan.getTex("planetStarCommons/grad", null);
    myWhiteTex = texMan.getTex("planetStarCommons/whiteTex", null);
    myGradTint = Col.col(1, 1);
    myFillTint = Col.col(1, 1);
  }


  public void draw(SolGame game, GameDrawer drawer) {
    Vector2 camPos = game.getCam().getPos();
    SolSystem sys = game.getPlanetMan().getNearestSystem(camPos);
    Vector2 toCam = SolMath.getVec(camPos);
    toCam.sub(sys.getPos());
    float toCamLen = toCam.len();
    if (toCamLen < Const.SUN_RADIUS) {
      float closeness = 1 - toCamLen / Const.SUN_RADIUS;
      myGradTint.a = SolMath.clamp(closeness * 4, 0, 1);
      myFillTint.a = SolMath.clamp((closeness - .25f) * 4, 0, 1);

      float sz = 2 * game.getCam().getViewDist();
      float gradAngle = SolMath.angle(toCam) - 90;
      drawer.draw(myWhiteTex, sz*2, sz*2, sz, sz, camPos.x, camPos.y, 0, myFillTint);
      drawer.draw(myGradTex, sz*2, sz*2, sz, sz, camPos.x, camPos.y, gradAngle, myGradTint);
    }
    SolMath.free(toCam);
  }

  public void doDmg(SolGame game, SolObj obj, float toSys) {
    float dmg = SUN_DMG * game.getTimeStep();
    if (SUN_HOT_RAD < toSys) return;
    obj.receiveDmg(dmg, game, null, DmgType.FIRE);
  }
}
