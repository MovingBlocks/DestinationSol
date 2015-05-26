package org.destinationsol.game.planet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.TextureManager;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;

public class PlanetCoreSingleton {
  private final TextureAtlas.AtlasRegion myTex;

  public PlanetCoreSingleton(TextureManager textureManager) {
    myTex = textureManager.getTex("planetStarCommons/planetCore", null);
  }


  public void draw(SolGame game, GameDrawer drawer) {
    SolCam cam = game.getCam();
    Vector2 camPos = cam.getPos();
    Planet p = game.getPlanetMan().getNearestPlanet();
    Vector2 pPos = p.getPos();
    float toCamLen = camPos.dst(pPos);
    float vd = cam.getViewDist();
    float gh = p.getMinGroundHeight();
    if (toCamLen < gh + vd) {
      float sz = gh;
      drawer.draw(myTex, sz *2, sz *2, sz, sz, pPos.x, pPos.y, p.getAngle(), SolColor.W);
    }
  }
}
