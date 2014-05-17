package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.input.Pilot;
import com.miloshpetrov.sol2.game.planet.PlanetBind;
import com.miloshpetrov.sol2.game.ship.FarShip;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class BeaconHandler {

  public static final float TEX_SZ = .5f;
  public static final float ROT_SPD = 30f;

  private final RectSprite myAttackSprite;
  private final RectSprite myFollowSprite;
  private final RectSprite myMoveSprite;
  private final Vector2 myTargetRelPos;

  private DrasObj myD;
  private FarDras myFarD;
  private Pilot myTargetPilot;
  private SolShip myTarget;
  private FarShip myFarTarget;
  private Action myCurrAction;
  private PlanetBind myPlanetBind;

  public BeaconHandler(TexMan texMan) {
    TextureAtlas.AtlasRegion attackTex = texMan.getTex("misc/beaconAttack", null);
    myAttackSprite = new RectSprite(attackTex, TEX_SZ, 0, 0, new Vector2(), DraLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
    TextureAtlas.AtlasRegion followTex = texMan.getTex("misc/beaconFollow", null);
    myFollowSprite = new RectSprite(followTex, TEX_SZ, 0, 0, new Vector2(), DraLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
    TextureAtlas.AtlasRegion moveTex = texMan.getTex("misc/beaconMove", null);
    myMoveSprite = new RectSprite(moveTex, TEX_SZ, 0, 0, new Vector2(), DraLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
    myTargetRelPos = new Vector2();
  }

  public void init(SolGame game, Vector2 pos) {
    ArrayList<Dra> dras = new ArrayList<Dra>();
    dras.add(myAttackSprite);
    dras.add(myFollowSprite);
    dras.add(myMoveSprite);
    myD = new DrasObj(dras, new Vector2(pos), new Vector2(), null, false, false);
    game.getObjMan().addObjDelayed(myD);
  }

  public void update(SolGame game) {
    updateD(game);
    if (maybeUpdateTargetPos(game)) return;
    maybeUpdatePlanetPos(game);
  }

  private void maybeUpdatePlanetPos(SolGame game) {
    Vector2 beaconPos = getPos0();
    if (myPlanetBind == null) {
      myPlanetBind = PlanetBind.tryBind(game, beaconPos, 0);
      return;
    }
    Vector2 vec = SolMath.getVec();
    myPlanetBind.setDiff(vec, beaconPos, false);
    beaconPos.add(vec);
    SolMath.free(vec);
  }

  private boolean maybeUpdateTargetPos(SolGame game) {
    updateTarget(game);
    if (myTargetPilot == null) return false;
    Vector2 beaconPos = getPos0();
    if (myTarget != null) {
      SolMath.toWorld(beaconPos, myTargetRelPos, myTarget.getAngle(), myTarget.getPos(), false);
    } else {
      beaconPos.set(myFarTarget.getPos());
    }
    return true;
  }

  private void updateTarget(SolGame game) {
    if (myTargetPilot == null) return;
    ObjMan om = game.getObjMan();
    List<SolObj> objs = om.getObjs();
    List<FarObj> farObjs = om.getFarObjs();
    if (myTarget != null) {
      if (objs.contains(myTarget)) return;
      myTarget = null;
      for (FarObj fo : farObjs) {
        if (!(fo instanceof FarShip)) continue;
        FarShip ship = (FarShip) fo;
        if (ship.getPilot() != myTargetPilot) continue;
        myFarTarget = ship;
        return;
      }
      applyAction(Action.MOVE);
      return;
    }
    if (myFarTarget == null) throw new AssertionError();
    if (farObjs.contains(myFarTarget)) return;
    myFarTarget = null;
    for (SolObj o : objs) {
      if ((o instanceof SolShip)) {
        SolShip ship = (SolShip) o;
        if (ship.getPilot() != myTargetPilot) continue;
        myTarget = ship;
        return;
      }
    }
    applyAction(Action.MOVE);
  }

  private void updateD(SolGame game) {
    ObjMan om = game.getObjMan();
    List<SolObj> objs = om.getObjs();
    List<FarObj> farObjs = om.getFarObjs();

    if (myD != null) {
      if (objs.contains(myD)) return;
      myD = null;
      for (FarObj fo : farObjs) {
        if (!(fo instanceof FarDras)) continue;
        List<Dra> dras = ((FarDras) fo).getDras();
        if (dras.size() != 3) continue;
        Dra dra = dras.get(0);
        if (dra != myAttackSprite) continue;
        myFarD = (FarDras) fo;
        return;
      }
      throw new AssertionError();
    }
    if (myFarD == null) throw new AssertionError();
    if (farObjs.contains(myFarD)) return;
    myFarD = null;
    for (SolObj o : objs) {
      if ((o instanceof DrasObj)) {
        List<Dra> dras = o.getDras();
        if (dras.size() != 3) continue;
        Dra dra = dras.get(0);
        if (dra != myAttackSprite) continue;
        myD = (DrasObj) o;
        return;
      }
    }
    throw new AssertionError();
  }

  public Action processMouse(SolGame g, Vector2 pos, boolean clicked, boolean onMap) {
    Action action;
    Pilot targetPilot = findPilotInPos(g, pos, onMap, clicked);
    if (targetPilot != null) {
      boolean enemies = g.getFractionMan().areEnemies(targetPilot.getFraction(), g.getHero().getPilot().getFraction());
      if (enemies) {
        action = Action.ATTACK;
        if (clicked) {
          myTargetRelPos.set(0, 0);
        }
      } else {
        action = Action.FOLLOW;
        if (clicked) {
          if (myTarget == null) {
            myTargetRelPos.set(0, 0);
          } else {
            SolMath.toRel(pos, myTargetRelPos, myTarget.getAngle(), myTarget.getPos());
          }
        }
      }
    } else {
      action = Action.MOVE;
    }

    if (clicked) {
      applyAction(action);
      getPos0().set(pos);
    }
    return action;
  }

  private void applyAction(Action action) {
    myCurrAction = action;
    myAttackSprite.tint.a = myCurrAction == Action.ATTACK ? 1 : 0;
    myMoveSprite.tint.a = myCurrAction == Action.MOVE ? 1 : 0;
    myFollowSprite.tint.a = myCurrAction == Action.FOLLOW ? 1 : 0;
    myPlanetBind = null;
    if (myCurrAction == Action.MOVE) {
      myTargetPilot = null;
      myTarget = null;
      myFarTarget = null;
    }
  }

  private Pilot findPilotInPos(SolGame g, Vector2 pos, boolean onMap, boolean clicked) {
    ObjMan om = g.getObjMan();
    float iconRad = onMap ? g.getMapDrawer().getIconRadius(g.getCam()) : 0;
    for (SolObj o : om.getObjs()) {
      if (!(o instanceof SolShip)) continue;
      float dst = o.getPos().dst(pos);
      SolShip s = (SolShip) o;
      float rad = iconRad == 0 ? s.getHull().config.size : iconRad;
      if (dst < rad) {
        Pilot pilot = s.getPilot();
        if (clicked) {
          myTargetPilot = pilot;
          myTarget = s;
        }
        return pilot;
      }
    }
    for (FarObj fo : om.getFarObjs()) {
      if (!(fo instanceof FarShip)) continue;
      float dst = fo.getPos().dst(pos);
      FarShip s = (FarShip) fo;
      float rad = iconRad == 0 ? s.getHullConfig().approxRadius : iconRad;
      if (dst < rad) {
        Pilot pilot = s.getPilot();
        if (clicked) {
          myTargetPilot = pilot;
          myFarTarget = s;
        }
        return pilot;
      }
    }
    return null;
  }

  public Vector2 getPos() {
    return getPos0();
  }

  // returns Vector itself
  private Vector2 getPos0() {
    return myD == null ? myFarD.getPos() : myD.getPos();
  }

  public Action getCurrAction() {
    return myCurrAction;
  }

  public static enum Action {
    MOVE, ATTACK, FOLLOW
  }
}
