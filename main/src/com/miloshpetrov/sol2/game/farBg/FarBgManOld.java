package com.miloshpetrov.sol2.game.farBg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.*;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.planet.Planet;

import java.util.ArrayList;

public class FarBgManOld {

  private final TextureAtlas.AtlasRegion myNebTex;
  private final ArrayList<FarBgStar> myStars;
  private final float myNebAngle;
  private final Color myNebTint;

  public FarBgManOld(TexMan texMan) {
    myNebTex = texMan.getTex("farBgBig/nebulae2", SolMath.test(.5f), null);
    myNebAngle = SolMath.rnd(180);
    myStars = new ArrayList<FarBgStar>();
    for (int i = 0; i < 400; i++) {
      FarBgStar star = new FarBgStar(texMan);
      myStars.add(star);
    }
    myNebTint = Col.col(.5f, 1);
  }

  public void draw(GameDrawer drawer, SolCam cam, SolGame game) {
    Planet np = game.getPlanetMan().getNearestPlanet();
    Vector2 camPos = cam.getPos();
    float nebPerc = (camPos.dst(np.getPos()) - np.getGroundHeight()) / (4 * Const.ATM_HEIGHT);
    nebPerc = SolMath.clamp(nebPerc, 0, 1);
    myNebTint.a = nebPerc;

    float vd = cam.getViewDist();
    drawer.draw(myNebTex, vd * 2, vd * 2, vd, vd, camPos.x, camPos.y, myNebAngle, myNebTint);
    for (FarBgStar star : myStars) {
      star.draw(drawer, vd, camPos, cam.getAngle());
    }
  }

  private static class FarBgStar {

    private final Vector2 myShiftPerc;
    private final TextureAtlas.AtlasRegion myTex;
    private final float mySzPerc;
    private final Color myTint;
    private final Vector2 myPos;

    private FarBgStar(TexMan texMan) {
      myShiftPerc = new Vector2(SolMath.rnd(1), SolMath.rnd(1));
      myPos = new Vector2();
      boolean small = SolMath.test(.8f);
      myTex = texMan.getTex("deco/bigStar", null);
      mySzPerc = (small ? .01f : .04f) * SolMath.rnd(.5f, 1);
      myTint = new Color();
      ColUtil.fromHSB(SolMath.rnd(0, 1), .25f, 1, .7f, myTint);
    }

    public void draw(GameDrawer drawer, float vd, Vector2 camPos, float camAngle) {
      float sz = vd * mySzPerc;
      myPos.set(myShiftPerc).scl(vd).add(camPos);
      drawer.draw(myTex, sz, sz, sz /2, sz /2, myPos.x, myPos.y, camAngle, myTint);
    }
  }
}
