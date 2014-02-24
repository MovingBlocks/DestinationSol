package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.screens.MainScreen;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class SolCam {
  private static final float VIEWPORT_HEIGHT = 5f;
  private static final float MAX_ZOOM_SPD = 5f;
  private static final float MED_ZOOM_SPD = 3f;
  private static final float ZOOM_CHG_SPD = .1f;
  private static final float MOVE_SPD = 3f;

  private static final float MAX_SHAKE = .07f;
  private static final float SHAKE_DAMP = MAX_SHAKE;

  private final CamRotStrategy myCamRotStrategy;
  private final OrthographicCamera myCam;

  private float myPrevHeroLife;
  private float myShake;
  private float myAngle;
  private float myZoom;
  private Vector2 myPos;

  public SolCam(float r) {
    myCamRotStrategy = new CamRotStrategy.ToPlanet();
    myCam = new OrthographicCamera(VIEWPORT_HEIGHT * r, -VIEWPORT_HEIGHT);
    myZoom = Const.MED_ZOOM;
    myPos = new Vector2();
  }

  public Matrix4 getMtx() {
    return myCam.combined;
  }

  public float getZoom() {
    return myZoom;
  }

  public void update(SolGame game) {

    float desiredZoom = 1;
    float life = 0;

    SolShip hero = game.getHero();
    if (hero == null) {
      StarPort.Transcendent trans = game.getTranscendentHero();
      if (trans == null) {
        if (DebugAspects.DIRECT_CAM_CONTROL) {
          applyInput(game);
        }
      } else {
        desiredZoom = Const.MED_ZOOM;
        myPos.set(trans.getPos());
      }
    } else {
      myPos.set(hero.getPos());
      life = hero.getLife();

      float spd = hero.getSpd().len();

      desiredZoom = Const.MED_ZOOM;
      Planet np = game.getPlanetMan().getNearestPlanet(myPos);
      if (np.getFullHeight() < np.getPos().dst(myPos) && MAX_ZOOM_SPD < spd) {
        desiredZoom = Const.MAX_ZOOM;
      }
      else if (np.isNearGround(myPos) && spd < MED_ZOOM_SPD) {
        desiredZoom = 1;
      }
    }

    if (life < myPrevHeroLife) {
      float shakeDiff = .1f * MAX_SHAKE * (myPrevHeroLife - life);
      myShake = SolMath.approach(myShake, MAX_SHAKE, shakeDiff);
    } else {
      myShake = SolMath.approach(myShake, 0, SHAKE_DAMP * game.getTimeStep());
    }
    myPrevHeroLife = life;

    Vector2 pos = SolMath.fromAl(SolMath.rnd(180), myShake);
    pos.add(myPos);
    applyPos(pos.x, pos.y);
    SolMath.free(pos);

    myAngle = myCamRotStrategy.getRotation(myPos, game);
    applyAngle();

    myZoom = SolMath.approach(myZoom, desiredZoom, ZOOM_CHG_SPD * game.getTimeStep());
    applyZoom(game.getMapDrawer());
    myCam.update();
  }

  private void applyZoom(MapDrawer mapDrawer) {
    if (mapDrawer.isToggled()) {
      myCam.zoom = mapDrawer.getZoom();
      return;
    }
    if (DebugAspects.ZOOM != 0) {
      myCam.zoom = DebugAspects.ZOOM;
      return;
    }
    myCam.zoom = myZoom;
  }

  private void applyPos(float posX, float posY) {
    myCam.position.set(posX, posY, 0);
  }

  private void applyInput(SolGame game) {
    MainScreen s = game.getScreens().mainScreen;
    boolean d = s.isDown();
    boolean u = s.isUp();
    boolean l = s.isLeft();
    boolean r = s.isRight();
    Vector2 v = SolMath.getVec();
    if (l != r) v.x = SolMath.toInt(r);
    if (d != u) v.y = SolMath.toInt(d);
    v.scl(MOVE_SPD * game.getTimeStep());
    SolMath.rotate(v, myAngle);
    myPos.add(v);
    SolMath.free(v);
  }

  private void applyAngle() {
    Vector2 v = SolMath.getVec(0, 1);
    SolMath.rotate(v, myAngle);
    myCam.up.set(v.x, v.y, 0); // up is actually down, fcuk!!
    SolMath.free(v);
  }

  public float getViewDist() {
    return getViewDist(myZoom);
  }

  public float getGroundViewDist() {
    return .4f * getViewDist(1);
  }

  public float getSpaceViewDist() {
    return .8f * getViewDist(Const.MED_ZOOM);
  }

  public float getViewDist(float zoom) {
    float r = myCam.viewportWidth / myCam.viewportHeight;
    return .5f * VIEWPORT_HEIGHT * SolMath.sqrt(1 + r * r) * zoom;
  }

  public float getAngle() {
    return myAngle;
  }

  public Vector2 getPos() {
    return myPos;
  }

  public void drawDebug(Drawer drawer) {
    float hOver2 = VIEWPORT_HEIGHT * myZoom / 2;
    float wOver2 = hOver2 * drawer.r;
    Vector2 dr = SolMath.getVec(wOver2, hOver2);
    SolMath.rotate(dr, myAngle);
    Vector2 dl = SolMath.getVec(-wOver2, hOver2);
    SolMath.rotate(dl, myAngle);
    Vector2 ul = SolMath.getVec(dr);
    ul.scl(-1);
    Vector2 ur = SolMath.getVec(dl);
    ur.scl(-1);
    dr.add(myPos);
    dl.add(myPos);
    ul.add(myPos);
    ur.add(myPos);

    float lw = getRealLineWidth();
    drawer.drawLine(dr, dl, Col.W, lw);
    drawer.drawLine(dl, ul, Col.W, lw);
    drawer.drawLine(ul, ur, Col.W, lw);
    drawer.drawLine(ur, dr, Col.W, lw);

    SolMath.free(dr);
    SolMath.free(dl);
    SolMath.free(ul);
    SolMath.free(ur);
  }

  public float getRealLineWidth() {
    return getViewHeight(myCam.zoom) / Gdx.graphics.getHeight();
  }

  public float getViewHeight() {
    return getViewHeight(myZoom);
  }

  public float getViewHeight(float zoom) {
    float r = -myCam.viewportHeight * zoom;
    if (r < 0) throw new RuntimeException();
    return r;
  }

  public float getViewWidth() {
    float r = myCam.viewportWidth * myZoom;
    if (r < 0) throw new RuntimeException();
    return r;
  }

  public float getRealZoom() {
    return myCam.zoom;
  }

  public void setPos(Vector2 pos) {
    myPos.set(pos);
  }

  public boolean isVisible(Vector2 pos) {
    Vector2 rp = SolMath.toRel(pos, myAngle, myPos);
    boolean res = isRelVisible(rp);
    SolMath.free(rp);
    return res;
  }

  public boolean isRelVisible(Vector2 rp) {
    float wHalf = getViewWidth()/2;
    if (wHalf < SolMath.abs(rp.x)) return false;
    float hHalf = getViewHeight()/2;
    if (hHalf < SolMath.abs(rp.y)) return false;
    return true;
  }
}
