package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.item.SolItem;

public class Teleport implements ShipAbility {
  public static final int MAX_RADIUS = 4;
  private final Vector2 myNewPos;
  private final Config myConfig;
  private boolean myShouldTeleport;
  private float myAngle;

  public Teleport(Config config) {
    myConfig = config;
    myNewPos = new Vector2();
  }

  @Override
  public boolean update(SolGame game, SolShip owner, boolean tryToUse) {
    myShouldTeleport = false;
    if (!tryToUse) return false;
    Vector2 pos = owner.getPos();
    Fraction frac = owner.getPilot().getFraction();
    SolShip ne = game.getFractionMan().getNearestEnemy(game, MAX_RADIUS, frac, pos);
    if (ne == null) return false;
    Vector2 nePos = ne.getPos();
    for (int i = 0; i < 5; i++) {
      myNewPos.set(pos);
      myNewPos.sub(nePos);
      myAngle = myConfig.angle * SolMath.rnd(.5f, 1) * SolMath.toInt(SolMath.test(.5f));
      SolMath.rotate(myNewPos, myAngle);
      myNewPos.add(nePos);
      if (game.isPlaceEmpty(myNewPos)) {
        myShouldTeleport = true;
        return true;
      }
    }
    return false;
  }

  @Override
  public SolItem getChargeExample() {
    return myConfig.chargeExample;
  }

  @Override
  public float getRechargeTime() {
    return myConfig.rechargeTime;
  }

  @Override
  public AbilityCommonConfig getCommonConfig() {
    return myConfig.cc;
  }

  public void maybeTeleport(SolGame game, SolShip owner) {
    if (!myShouldTeleport) return;

    TextureAtlas.AtlasRegion tex = game.getTexMan().getTex("misc/teleportBlip", false, null);
    float blipSz = owner.getHull().config.approxRadius * 3;
    game.getPartMan().blip(game, owner.getPos(), SolMath.rnd(180), blipSz, 1, Vector2.Zero, tex);
    game.getPartMan().blip(game, myNewPos, SolMath.rnd(180), blipSz, 1, Vector2.Zero, tex);

    FarShip ship = owner.toFarObj();
    game.getObjMan().removeObjDelayed(owner);
    ship.setPos(myNewPos);
    ship.setAngle(ship.getAngle() + myAngle);
    Vector2 newSpd = SolMath.getVec(ship.getSpd());
    SolMath.rotate(newSpd, myAngle);
    ship.setSpd(newSpd);
    SolMath.free(newSpd);
    SolObj newOwner = ship.toObj(game);
    game.getObjMan().addObjDelayed(newOwner);
  }

  public static class Config implements AbilityConfig {
    private final float angle;
    private final SolItem chargeExample;
    private final float rechargeTime;
    private final AbilityCommonConfig cc;

    public Config(float angle, SolItem chargeExample, float rechargeTime, AbilityCommonConfig cc) {
      this.angle = angle;
      this.chargeExample = chargeExample;
      this.rechargeTime = rechargeTime;
      this.cc = cc;
    }

    public ShipAbility build() {
      return new Teleport(this);
    }

    public static AbilityConfig load(JsonValue abNode, ItemMan itemMan, AbilityCommonConfig cc) {
      float angle = abNode.getFloat("angle");
      SolItem chargeExample = itemMan.getExample("teleportCharge");
      float rechargeTime = abNode.getFloat("rechargeTime");
      return new Config(angle, chargeExample, rechargeTime, cc);
    }
  }
}
