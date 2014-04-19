package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.item.*;

public class UnShield implements ShipAbility {
  public static final int MAX_RADIUS = 4;
  private final Config myConfig;

  public UnShield(Config config) {
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
      if (!(o instanceof SolShip) || o == owner) continue;
      SolShip oShip = (SolShip) o;
      Shield shield = oShip.getShield();
      if (shield == null) continue;
      float shieldLife = shield.getLife();
      if (shieldLife <= 0) continue;
      if (!game.getFractionMan().areEnemies(oShip, owner)) continue;
      Vector2 oPos = o.getPos();
      float dst = oPos.dst(ownerPos);
      float perc = KnockBack.getPerc(dst, MAX_RADIUS);
      if (perc <= 0) continue;
      float amount = perc * myConfig.amount;
      float newLife = shieldLife < amount ? 0 : shieldLife - amount;
      shield.setLife(newLife);
    }
    return true;
  }


  public static class Config implements AbilityConfig {
    public final float rechargeTime;
    private final SolItem chargeExample;
    public final float amount;

    public Config(float rechargeTime, SolItem chargeExample, float amount) {
      this.rechargeTime = rechargeTime;
      this.chargeExample = chargeExample;
      this.amount = amount;
    }

    @Override
    public ShipAbility build() {
      return new UnShield(this);
    }

    public static AbilityConfig load(JsonValue abNode, ItemMan itemMan, AbilityCommonConfig unShield) {
      float rechargeTime = abNode.getFloat("rechargeTime");
      float amount = abNode.getFloat("amount");
      SolItem chargeExample = itemMan.getExample("unShieldCharge");
      return new Config(rechargeTime, chargeExample, amount);
    }
  }
}
