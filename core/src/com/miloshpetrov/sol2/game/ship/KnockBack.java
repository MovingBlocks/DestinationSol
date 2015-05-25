package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.DraLevel;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.item.SolItem;
import com.miloshpetrov.sol2.game.particle.ParticleSrc;

public class KnockBack implements ShipAbility {
  public static final int MAX_RADIUS = 8;
  private final Config myConfig;

  public KnockBack(Config config) {
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
    ParticleSrc src = new ParticleSrc(myConfig.cc.effect, MAX_RADIUS, DraLevel.PART_BG_0, new Vector2(), true, game, ownerPos, Vector2.Zero, 0);
    game.getPartMan().finish(game, src, ownerPos);
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
    public final AbilityCommonConfig cc;

    public Config(float rechargeTime, SolItem chargeExample, float force, AbilityCommonConfig cc) {
      this.rechargeTime = rechargeTime;
      this.chargeExample = chargeExample;
      this.force = force;
      this.cc = cc;
    }

    @Override
    public ShipAbility build() {
      return new KnockBack(this);
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

    public static AbilityConfig load(JsonValue abNode, ItemMan itemMan, AbilityCommonConfig cc) {
      float rechargeTime = abNode.getFloat("rechargeTime");
      float force = abNode.getFloat("force");
      SolItem chargeExample = itemMan.getExample("knockBackCharge");
      return new Config(rechargeTime, chargeExample, force, cc);
    }
  }
}
