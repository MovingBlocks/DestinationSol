package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.item.SolItem;

public class SloMo implements ShipAbility {
  private static final float SLO_MO_CHG_SPD = .03f;
  private final Config myConfig;

  private float myFactor;

  public SloMo(Config config) {
    myConfig = config;
    myFactor = 1;
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
    if (tryToUse) {
      myFactor = myConfig.factor;
      return true;
    }
    float ts = game.getTimeStep();
    myFactor = SolMath.approach(myFactor, 1, SLO_MO_CHG_SPD * ts);
    return false;
  }

  public float getFactor() {
    return myFactor;
  }


  public static class Config implements AbilityConfig {
    public final float factor;
    public final float rechargeTime;
    private final SolItem chargeExample;

    public Config(float factor, float rechargeTime, SolItem chargeExample) {
      this.factor = factor;
      this.rechargeTime = rechargeTime;
      this.chargeExample = chargeExample;
    }

    @Override
    public ShipAbility build() {
      return new SloMo(this);
    }

    public static AbilityConfig load(JsonValue abNode, ItemMan itemMan) {
      float factor = abNode.getFloat("factor");
      float rechargeTime = abNode.getFloat("rechargeTime");
      SolItem chargeExample = itemMan.getExample("sloMoCharge");
      return new Config(factor, rechargeTime, chargeExample);
    }
  }
}
