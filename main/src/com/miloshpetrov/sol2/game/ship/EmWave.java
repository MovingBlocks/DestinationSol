package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.item.SolItem;

public class EmWave implements ShipAbility {
  public static final int MAX_RADIUS = 4;
  private final Config myConfig;

  public EmWave(Config config) {
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
      if (!game.getFractionMan().areEnemies(oShip, owner)) continue;
      Vector2 oPos = o.getPos();
      float dst = oPos.dst(ownerPos);
      float perc = KnockBack.getPerc(dst, MAX_RADIUS);
      if (perc <= 0) continue;
      float duration = perc * myConfig.duration;
      oShip.disableControls(duration);
    }
    return true;
  }


  public static class Config implements AbilityConfig {
    public final float rechargeTime;
    private final SolItem chargeExample;
    public final float duration;

    public Config(float rechargeTime, SolItem chargeExample, float duration) {
      this.rechargeTime = rechargeTime;
      this.chargeExample = chargeExample;
      this.duration = duration;
    }

    @Override
    public ShipAbility build() {
      return new EmWave(this);
    }

    public static AbilityConfig load(JsonValue abNode, ItemMan itemMan) {
      float rechargeTime = abNode.getFloat("rechargeTime");
      float duration = abNode.getFloat("duration");
      SolItem chargeExample = itemMan.getExample("emWaveCharge");
      return new Config(rechargeTime, chargeExample, duration);
    }
  }
}
