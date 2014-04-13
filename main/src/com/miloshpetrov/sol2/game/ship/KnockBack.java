package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.item.SolItem;

public class KnockBack implements ShipAbility {
  public static final int MAX_RADIUS = 4;
  private final Config myConfig;

  public KnockBack(Config config) {
    myConfig = config;
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
  public boolean update(SolGame game, SolShip owner, boolean tryToUse) {
    if (!tryToUse) return false;
    Vector2 ownerPos = owner.getPos();
    for (SolObj o : game.getObjMan().getObjs()) {
      if (o == owner || !o.receivesGravity()) continue;
      Vector2 oPos = o.getPos();
      float dst = oPos.dst(ownerPos);
      if (dst == 0) continue; // O__o
      float perc = getPerc(dst, MAX_RADIUS);
      if (perc <= 0) continue;
      Vector2 toO = SolMath.distVec(ownerPos, oPos);
      float accLen = myConfig.force * perc;
      toO.scl(accLen / dst);
      o.receiveForce(toO, game, false);
      SolMath.free(toO);
    }
    return true;
  }

  public static float getPerc(float dst, float radius) {
    if (radius < dst) return 0;
    float rHalf = radius / 2;
    if (dst < rHalf) return 1;
    return 1 - (dst - rHalf) / rHalf;
  }


  public static class Config implements AbilityConfig {
    public final float rechargeTime;
    private final SolItem chargeExample;
    public final float force;

    public Config(float rechargeTime, SolItem chargeExample, float force) {
      this.rechargeTime = rechargeTime;
      this.chargeExample = chargeExample;
      this.force = force;
    }

    @Override
    public ShipAbility build() {
      return new KnockBack(this);
    }

    public static AbilityConfig load(JsonValue abNode, ItemMan itemMan) {
      float rechargeTime = abNode.getFloat("rechargeTime");
      float force = abNode.getFloat("force");
      SolItem chargeExample = itemMan.getExample("knockBackCharge");
      return new Config(rechargeTime, chargeExample, force);
    }
  }
}
