package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.save.SaveData;

import java.util.*;

public class ObjMan {
  private final List<SolObj> myObjs;
  private final Set<SolObj> myToRemove;
  private final Set<SolObj> myToAdd;
  private final List<FarObj> myFarObjs;
  private final Set<FarObj> myFarToAdd;
  private final Set<FarObj> myFarToRemove;
  private final Map<FarObj, FarObjData> myFarObjDelays;
  private final World myWorld;
  private final Box2DDebugRenderer myDr;
  private float myFarEndDist;
  private float myFarBeginDist;

  public ObjMan(SolContactListener contactListener) {
    myObjs = new ArrayList<SolObj>();
    myToRemove = new HashSet<SolObj>();
    myToAdd = new HashSet<SolObj>();
    myFarObjs = new ArrayList<FarObj>();
    myFarToRemove = new HashSet<FarObj>();
    myFarToAdd = new HashSet<FarObj>();
    myWorld = new World(new Vector2(0, 0), true);
    myWorld.setContactListener(contactListener);
    myWorld.setContactFilter(new SolContactFilter());
    myDr = new Box2DDebugRenderer();
    myFarObjDelays = new HashMap<FarObj, FarObjData>();
  }

  public void fill(SolGame game, SaveData sd) {
    if (sd != null) {
      myFarToAdd.addAll(sd.farObjs);
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

    for (SolObj o : myObjs) {
      o.update(game);
      SolMath.checkVectorsTaken(o);
      for (Dra dra : o.getDras()) {
        dra.update(game, o);
      }

      if (o.shouldBeRemoved(game)) {
        myToRemove.add(o);
        continue;
      }
      if (isFar(o, camPos)) {
        FarObj fo = o.toFarObj();
        if (fo != null) myFarToAdd.add(fo);
        myToRemove.add(o);
      }
    }

    for (FarObj fo : myFarObjs) {
      fo.update(game);
      SolMath.checkVectorsTaken(fo);
      if (fo.shouldBeRemoved(game)) {
        myFarToRemove.add(fo);
        continue;
      }
      if (isNear(fo, camPos, ts)) {
        SolObj o = fo.toObj(game);
        myToAdd.add(o);
        myFarToRemove.add(fo);
      }
    }
  }

  private void addRemove(SolGame game) {
    for (SolObj o : myToRemove) {
      removeObjNow(game, o);
    }
    myToRemove.clear();

    for (SolObj o : myToAdd) {
      addObjNow(game, o);
    }
    myToAdd.clear();

    for (FarObj fo : myFarToRemove) {
      myFarObjs.remove(fo);
      myFarObjDelays.remove(fo);
    }
    myFarToRemove.clear();

    for (FarObj fo : myFarToAdd) {
      if (myFarObjs.contains(fo)) throw new AssertionError();
      myFarObjs.add(fo);
      myFarObjDelays.put(fo, new FarObjData());
    }
    myFarToAdd.clear();
  }

  private void removeObjNow(SolGame game, SolObj o) {
    myObjs.remove(o);
    o.onRemove(game);
    game.getDraMan().objRemoved(o);
  }

  public void addObjNow(SolGame game, SolObj o) {
    if (myObjs.contains(o)) throw new AssertionError();
    myObjs.add(o);
    game.getDraMan().objAdded(o);
  }

  private boolean isNear(FarObj fo, Vector2 camPos, float ts) {
    FarObjData fod = myFarObjDelays.get(fo);
    if (fod.delay > 0) {
      fod.delay -= ts;
      return false;
    }
    float r = fo.getRadius();
    float dst = fo.getPos().dst(camPos) - r;
    if (dst < myFarEndDist) return true;
    fod.delay = (dst - myFarEndDist) / (2 * Const.MAX_MOVE_SPD);
    return false;
  }

  private boolean isFar(SolObj o, Vector2 camPos) {
    float r = o.getRadius();
    float dst = o.getPos().dst(camPos) - r;
    return myFarBeginDist < dst;
  }

  public void drawDebug(Drawer drawer, SolGame game) {
    if (DebugAspects.OBJECT_BORDERS) {
      drawDebug0(drawer, game);
    }
    if (DebugAspects.TO_STRING) {
      drawDebugStrings(drawer, game);
    }

    if (DebugAspects.PHYSIC_BODIES) {
      drawer.end();
      myDr.render(myWorld, game.getCam().getMtx());
      drawer.begin(game);
    }
  }

  private void drawDebugStrings(Drawer drawer, SolGame game) {
    float fontSize = game.getCam().getDebugFontSize();
    for (SolObj o : myObjs) {
      Vector2 pos = o.getPos();
      String ds = o.toDebugString();
      if (ds != null) drawer.drawString(ds, pos.x, pos.y, fontSize, true, Col.W);
    }
    for (FarObj fo : myFarObjs) {
      Vector2 pos = fo.getPos();
      String ds = fo.toDebugString();
      if (ds != null) drawer.drawString(ds, pos.x, pos.y, fontSize, true, Col.G);
    }
  }

  private void drawDebug0(Drawer drawer, SolGame game) {
    float lineWidth = game.getCam().getRealLineWidth();
    for (SolObj o : myObjs) {
      Vector2 pos = o.getPos();
      float r = o.getRadius();
      drawer.drawCircle(pos, r, DebugCol.OBJ, lineWidth);
      drawer.drawLine(pos.x, pos.y, o.getAngle(), r, DebugCol.OBJ, lineWidth);
    }
    for (FarObj fo : myFarObjs) {
      drawer.drawCircle(fo.getPos(), fo.getRadius(), DebugCol.OBJ_FAR, lineWidth);
    }
    drawer.drawCircle(game.getCam().getPos(), myFarBeginDist, Col.W, lineWidth);
    drawer.drawCircle(game.getCam().getPos(), myFarEndDist, Col.W, lineWidth);
  }

  public List<SolObj> getObjs() {
    return myObjs;
  }


  public void addObjDelayed(SolObj p) {
    myToAdd.add(p);
  }

  public void removeObjDelayed(SolObj obj) {
    myToRemove.add(obj);
  }

  public void removeFarObjDelayed(FarObj obj) {
    myFarToRemove.add(obj);
  }

  public World getWorld() {
    return myWorld;
  }

  public static SolObj asSolObj(Object o) {
    return o instanceof SolObj ? (SolObj)o : null;
  }

  public void resetDelays() {
    for (FarObjData data : myFarObjDelays.values()) {
      data.delay = 0;
    }

  }

  public List<FarObj> getFarObjs() {
    return myFarObjs;
  }

  public static class FarObjData {
    public float delay;
  }

  public void dispose() {
    myWorld.dispose();
  }
}
