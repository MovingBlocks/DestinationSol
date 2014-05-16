package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class BeaconHandler {

  public static final float TEX_SZ = 1f;
  public static final float ROT_SPD = 45f;
  private final TextureAtlas.AtlasRegion myAttackTex;
  private final TextureAtlas.AtlasRegion myFollowTex;
  private final TextureAtlas.AtlasRegion myMoveTex;
  private Action myCurrAction;

  private DrasObj myD;
  private FarDras myFarD;
  private RectSprite myAttackSprite;
  private RectSprite myFollowSprite;
  private RectSprite myMoveSprite;

  public BeaconHandler(TexMan texMan) {
    myAttackTex = texMan.getTex("misc/beaconAttack", null);
    myFollowTex = texMan.getTex("misc/beaconFollow", null);
    myMoveTex = texMan.getTex("misc/beaconMove", null);
    myCurrAction = Action.MOVE;
  }

  public void init(SolGame game, Vector2 pos) {
    ArrayList<Dra> dras = new ArrayList<Dra>();
    myAttackSprite = new RectSprite(myAttackTex, TEX_SZ, 0, 0, new Vector2(), DraLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
    dras.add(myAttackSprite);
    myFollowSprite = new RectSprite(myFollowTex, TEX_SZ, 0, 0, new Vector2(), DraLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
    dras.add(myFollowSprite);
    myMoveSprite = new RectSprite(myMoveTex, TEX_SZ, 0, 0, new Vector2(), DraLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
    dras.add(myMoveSprite);
    myD = new DrasObj(dras, new Vector2(pos), new Vector2(), null, false, false);
    game.getObjMan().addObjDelayed(myD);
  }

  public void update(SolGame game) {
    updateObjs(game);
    // todo fade if hero is near

    // todo update pos in case of ship or planet
    // todo center on enemy, fix on trader or planet
  }

  private void updateObjs(SolGame game) {
    myD = null;
    myFarD = null;
    ObjMan om = game.getObjMan();
    for (SolObj o : om.getObjs()) {
      if ((o instanceof DrasObj)) {
        List<Dra> dras = o.getDras();
        if (dras.size() != 3) continue;
        Dra dra = dras.get(0);
        if (dra != myAttackSprite) continue;
        myD = (DrasObj) o;
        return;
      }
    }
    for (FarObj fo : om.getFarObjs()) {
      if (!(fo instanceof FarDras)) continue;
      List<Dra> dras = ((FarDras) fo).getDras();
      if (dras.size() != 3) continue;
      Dra dra = dras.get(0);
      if (dra != myAttackSprite) continue;
      myFarD = (FarDras) fo;
      return;
    }
  }

  public Action processMouse(SolGame g, Vector2 pos, boolean clicked) {
    ObjMan om = g.getObjMan();
    SolShip targetShip = null;
    for (SolObj o : om.getObjs()) {
      if (!(o instanceof SolShip)) continue;
      float dst = o.getPos().dst(pos);
      SolShip s = (SolShip) o;
      // todo if map then icon radius
      if (dst < s.getHull().config.approxRadius) {
        targetShip = s;
        break;
      }
    }
    Action action;
    if (targetShip != null) {
      boolean enemies = g.getFractionMan().areEnemies(targetShip, g.getHero());
      if (enemies) action = Action.ATTACK; else action = Action.FOLLOW;
      // todo remember pilot, relative pos
    } else {
      action = Action.MOVE;
      // todo if planet then bind
    }

    if (clicked) {
      myCurrAction = action;
      myAttackSprite.tint.a = myCurrAction == Action.ATTACK ? 1 : 0;
      myMoveSprite.tint.a = myCurrAction == Action.MOVE ? 1 : 0;
      myFollowSprite.tint.a = myCurrAction == Action.FOLLOW ? 1 : 0;
      if (myD != null) {
        myD.getPos().set(pos);
      } else {
        myFarD.getPos().set(pos);
      }
    }
    return action;
  }

  public Vector2 getPos() {
    return myD == null ? myFarD.getPos() : myD.getPos();
  }

  public Action getCurrAction() {
    return myCurrAction;
  }

  public static enum Action {
    MOVE, ATTACK, FOLLOW
  }
}
