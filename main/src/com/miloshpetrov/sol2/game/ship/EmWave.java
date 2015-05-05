package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.DraLevel;
import com.miloshpetrov.sol2.game.item.ItemManager;
import com.miloshpetrov.sol2.game.item.SolItem;
import com.miloshpetrov.sol2.game.particle.ParticleSrc;

public class EmWave implements ShipAbility {
  public static final int MAX_RADIUS = 4;
  private final Config myConfig;

  public EmWave(Config config) {
    myConfig = config;
  }

  @Override
  public AbilityConfig getConfig() {
    return myConfig;
  }

  @Override
  public AbilityCommonConfig getCommonConfig() {
    return myConfig.cc;
  }

  @Override
  public float getRadius() {
    return MAX_RADIUS;
  }

  @Override
  public boolean update(SolGame game, SolShip owner, boolean tryToUse) {
    if (!tryToUse) return false;
    Vector2 ownerPos = owner.getPos();
    for (SolObject o : game.getObjMan().getObjs()) {
      if (!(o instanceof SolShip) || o == owner) continue;
      SolShip oShip = (SolShip) o;
      if (!game.getFractionMan().areEnemies(oShip, owner)) continue;
      Vector2 oPos = o.getPos();
      float dst = oPos.dst(ownerPos);
      float perc = KnockBack.getPerc(dst, MAX_RADIUS);
      if (perc <= 0) continue;
      float duration = perc * myConfig.duration;
      oShip.disableControls(duration, game);
    }
    ParticleSrc src = new ParticleSrc(myConfig.cc.effect, MAX_RADIUS, DraLevel.PART_BG_0, new Vector2(), true, game, ownerPos, Vector2.Zero, 0);
    game.getPartMan().finish(game, src, ownerPos);
    return true;
  }


  public static class Config implements AbilityConfig {
    public final float rechargeTime;
    private final SolItem chargeExample;
    public final float duration;
    private final AbilityCommonConfig cc;

    public Config(float rechargeTime, SolItem chargeExample, float duration, AbilityCommonConfig cc) {
      this.rechargeTime = rechargeTime;
      this.chargeExample = chargeExample;
      this.duration = duration;
      this.cc = cc;
    }

    @Override
    public ShipAbility build() {
      return new EmWave(this);
    }

    @Override
    public SolItem getChargeExample() {
      return chargeExample;
    }

    @Override
    public float getRechargeTime() {
      return rechargeTime;
    }

    @Override
    public void appendDesc(StringBuilder sb) {
      sb.append("?\n");
    }

    public static AbilityConfig load(JsonValue abNode, ItemManager itemManager, AbilityCommonConfig cc) {
      float rechargeTime = abNode.getFloat("rechargeTime");
      float duration = abNode.getFloat("duration");
      SolItem chargeExample = itemManager.getExample("emWaveCharge");
      return new Config(rechargeTime, chargeExample, duration, cc);
    }
  }
}
