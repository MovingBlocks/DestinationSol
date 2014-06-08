package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.ship.FarShip;
import com.miloshpetrov.sol2.save.SaveData;

import java.util.*;

public class ObjMan {
  private static final float MAX_RADIUS_RECALC_AWAIT = 1f;
  private final List<SolObj> myObjs;
  private final List<SolObj> myToRemove;
  private final List<SolObj> myToAdd;
  private final List<FarObjData> myFarObjs;
  private final List<FarShip> myFarShips;
  private final List<StarPort.MyFar> myFarPorts;
  private final World myWorld;
  private final Box2DDebugRenderer myDr;
  private final HashMap<SolObj, Float> myRadii;

  private float myFarEndDist;
  private float myFarBeginDist;
  private float myRadiusRecalcAwait;

  public ObjMan(SolContactListener contactListener, FractionMan fractionMan) {
    myObjs = new ArrayList<SolObj>();
    myToRemove = new ArrayList<SolObj>();
    myToAdd = new ArrayList<SolObj>();
    myFarObjs = new ArrayList<FarObjData>();
    myFarShips = new ArrayList<FarShip>();
    myFarPorts = new ArrayList<StarPort.MyFar>();
    myWorld = new World(new Vector2(0, 0), true);
    myWorld.setContactListener(contactListener);
    myWorld.setContactFilter(new SolContactFilter(fractionMan));
    myDr = new Box2DDebugRenderer();
    myRadii = new HashMap<SolObj, Float>();
  }

  public boolean containsFarObj(FarObj fo) {
    for (int i = 0, myFarObjsSize = myFarObjs.size(); i < myFarObjsSize; i++) {
      FarObjData fod = myFarObjs.get(i);
      if (fod.fo == fo) return true;
    }
    return false;
  }

  public void fill(SolGame game, SaveData sd) {
    if (sd != null) {
      for (FarObj fo : sd.farObjs) {
        addFarObjNow(fo);
      }
    } else {
      // build initial objs
//      addObjNow(game, new SolObj.Test(game, new Vector2(), 0));
    }
  }

  public void update(SolGame game) {

    addRemove(game);

    float ts = game.getTimeStep();
    myWorld.step(ts, 6, 2);

    SolCam cam = game.getCam();
    Vector2 camPos = cam.getPos();
    myFarEndDist = 1.5f * cam.getViewDist();
    myFarBeginDist = 1.33f * myFarEndDist;

    boolean recalcRad = false;
    if (myRadiusRecalcAwait > 0) {
      myRadiusRecalcAwait -= ts;
    } else {
      myRadiusRecalcAwait = MAX_RADIUS_RECALC_AWAIT;
      recalcRad = true;
    }

    for (int i1 = 0, myObjsSize = myObjs.size(); i1 < myObjsSize; i1++) {
      SolObj o = myObjs.get(i1);
      o.update(game);
      SolMath.checkVectorsTaken(o);
      List<Dra> dras = o.getDras();
      for (int i = 0, drasSize = dras.size(); i < drasSize; i++) {
        Dra dra = dras.get(i);
        dra.update(game, o);
      }

      if (o.shouldBeRemoved(game)) {
        removeObjDelayed(o);
        continue;
      }
      if (isFar(o, camPos)) {
        FarObj fo = o.toFarObj();
        if (fo != null) addFarObjNow(fo);
        removeObjDelayed(o);
        continue;
      }
      if (recalcRad) recalcRadius(o);
    }

    for (Iterator<FarObjData> it = myFarObjs.iterator(); it.hasNext(); ) {
      FarObjData fod = it.next();
      FarObj fo = fod.fo;
      fo.update(game);
      SolMath.checkVectorsTaken(fo);
      if (fo.shouldBeRemoved(game)) {
        removeFo(it, fo);
        continue;
      }
      if (isNear(fod, camPos, ts)) {
        SolObj o = fo.toObj(game);
        addObjDelayed(o);
        removeFo(it, fo);
      }
    }
    addRemove(game);
  }

  private void removeFo(Iterator<FarObjData> it, FarObj fo) {
    it.remove();
    if (fo instanceof FarShip) myFarShips.remove(fo);
    if (fo instanceof StarPort.MyFar) myFarPorts.remove(fo);
  }

  private void recalcRadius(SolObj o) {
    float rad = DraMan.radiusFromDras(o.getDras());
    myRadii.put(o, rad);
  }

  public float getRadius(SolObj o) {
    Float res = myRadii.get(o);
    if (res == null) throw new AssertionError("no radius for " + o);
    return res + Const.MAX_MOVE_SPD * (MAX_RADIUS_RECALC_AWAIT - myRadiusRecalcAwait);
  }

  private void addRemove(SolGame game) {
    for (int i = 0, myToRemoveSize = myToRemove.size(); i < myToRemoveSize; i++) {
      SolObj o = myToRemove.get(i);
      removeObjNow(game, o);
    }
    myToRemove.clear();

    for (int i = 0, myToAddSize = myToAdd.size(); i < myToAddSize; i++) {
      SolObj o = myToAdd.get(i);
      addObjNow(game, o);
    }
    myToAdd.clear();
  }

  private void removeObjNow(SolGame game, SolObj o) {
    myObjs.remove(o);
    myRadii.remove(o);
    o.onRemove(game);
    game.getDraMan().objRemoved(o);
  }

  public void addObjNow(SolGame game, SolObj o) {
    if (DebugOptions.ASSERTIONS && myObjs.contains(o)) throw new AssertionError();
    myObjs.add(o);
    recalcRadius(o);
    game.getDraMan().objAdded(o);
  }

  private boolean isNear(FarObjData fod, Vector2 camPos, float ts) {
    if (fod.delay > 0) {
      fod.delay -= ts;
      return false;
    }
    FarObj fo = fod.fo;
    float r = fo.getRadius() * fod.depth;
    float dst = fo.getPos().dst(camPos) - r;
    if (dst < myFarEndDist) return true;
    fod.delay = (dst - myFarEndDist) / (2 * Const.MAX_MOVE_SPD);
    return false;
  }

  private boolean isFar(SolObj o, Vector2 camPos) {
    float r = getRadius(o);
    List<Dra> dras = o.getDras();
    if (dras != null && dras.size() > 0) r *= dras.get(0).getLevel().depth;
    float dst = o.getPos().dst(camPos) - r;
    return myFarBeginDist < dst;
  }

  public void drawDebug(GameDrawer drawer, SolGame game) {
    if (DebugOptions.DRAW_OBJ_BORDERS) {
      drawDebug0(drawer, game);
    }
    if (DebugOptions.OBJ_INFO) {
      drawDebugStrings(drawer, game);
    }

    if (DebugOptions.DRAW_PHYSIC_BORDERS) {
      drawer.end();
      myDr.render(myWorld, game.getCam().getMtx());
      drawer.begin();
    }
  }

  private void drawDebugStrings(GameDrawer drawer, SolGame game) {
    float fontSize = game.getCam().getDebugFontSize();
    for (SolObj o : myObjs) {
      Vector2 pos = o.getPos();
      String ds = o.toDebugString();
      if (ds != null) drawer.drawString(ds, pos.x, pos.y, fontSize, true, Col.W);
    }
    for (FarObjData fod : myFarObjs) {
      FarObj fo = fod.fo;
      Vector2 pos = fo.getPos();
      String ds = fo.toDebugString();
      if (ds != null) drawer.drawString(ds, pos.x, pos.y, fontSize, true, Col.G);
    }
  }

  private void drawDebug0(GameDrawer drawer, SolGame game) {
    SolCam cam = game.getCam();
    float lineWidth = cam.getRealLineWidth();
    float vh = cam.getViewHeight();
    for (SolObj o : myObjs) {
      Vector2 pos = o.getPos();
      float r = getRadius(o);
      drawer.drawCircle(drawer.debugWhiteTex, pos, r, DebugCol.OBJ, lineWidth, vh);
      drawer.drawLine(drawer.debugWhiteTex, pos.x, pos.y, o.getAngle(), r, DebugCol.OBJ, lineWidth);
    }
    for (FarObjData fod : myFarObjs) {
      FarObj fo = fod.fo;
      drawer.drawCircle(drawer.debugWhiteTex, fo.getPos(), fo.getRadius(), DebugCol.OBJ_FAR, lineWidth, vh);
    }
    drawer.drawCircle(drawer.debugWhiteTex, cam.getPos(), myFarBeginDist, Col.W, lineWidth, vh);
    drawer.drawCircle(drawer.debugWhiteTex, cam.getPos(), myFarEndDist, Col.W, lineWidth, vh);
  }

  public List<SolObj> getObjs() {
    return myObjs;
  }


  public void addObjDelayed(SolObj p) {
    if (DebugOptions.ASSERTIONS && myToAdd.contains(p)) throw new AssertionError();
    myToAdd.add(p);
  }

  public void removeObjDelayed(SolObj obj) {
    if (DebugOptions.ASSERTIONS && myToRemove.contains(obj)) throw new AssertionError();
    myToRemove.add(obj);
  }

  public World getWorld() {
    return myWorld;
  }

  public void resetDelays() {
    for (int i = 0, myFarObjsSize = myFarObjs.size(); i < myFarObjsSize; i++) {
      FarObjData data = myFarObjs.get(i);
      data.delay = 0;
    }

  }

  public List<FarObjData> getFarObjs() {
    return myFarObjs;
  }

  public void addFarObjNow(FarObj fo) {
    float depth = 1f;
    if (fo instanceof FarDras) {
      List<Dra> dras = ((FarDras)fo).getDras();
      if (dras != null && dras.size() > 0) depth = dras.get(0).getLevel().depth;
    }
    FarObjData fod = new FarObjData(fo, depth);
    myFarObjs.add(fod);
    if (fo instanceof FarShip) myFarShips.add((FarShip) fo);
    if (fo instanceof StarPort.MyFar) myFarPorts.add((StarPort.MyFar) fo);
  }

  public List<FarShip> getFarShips() {
    return myFarShips;
  }

  public List<StarPort.MyFar> getFarPorts() {
    return myFarPorts;
  }

  public void dispose() {
    myWorld.dispose();
  }
}
