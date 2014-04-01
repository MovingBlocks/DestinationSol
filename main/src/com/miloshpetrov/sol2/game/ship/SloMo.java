package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.SolItem;

public class SloMo implements ShipAbility {
  private static final float SLO_MO_CHG_SPD = .03f;
  private final float myMaxFactor;

  private float myFactor;

  public SloMo(float maxFactor) {
    myMaxFactor = maxFactor;
    myFactor = 1;
  }

  @Override
  public SolItem getAmmoExample() {
    return Ammo.EXAMPLE;
  }

  @Override
  public float getRechargeTime() {
    return 3;
  }

  @Override
  public boolean update(SolGame game, boolean tryToUse) {
    if (tryToUse) {
      myFactor = myMaxFactor;
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
    private final float myFactor;

    public Config(float factor) {
      myFactor = factor;
    }

    @Override
    public ShipAbility build() {
      return new SloMo(myFactor);
    }

    public static AbilityConfig load(JsonValue abNode) {
      float factor = abNode.getFloat("factor");
      return new Config(factor);
    }
  }


  public static class Ammo implements SolItem {
    public static final Ammo EXAMPLE = new Ammo();

    private Ammo() {
    }

    @Override
    public String getDisplayName() {
      return "Slo Mo";
    }

    @Override
    public float getPrice() {
      return 30;
    }

    @Override
    public String getDesc() {
      return "Use as special to slow the time down";
    }

    @Override
    public SolItem copy() {
      return new Ammo();
    }

    @Override
    public boolean isSame(SolItem item) {
      return item instanceof Ammo;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(SolGame game) {
      return game.getItemMan().sloMoChargeIcon;
    }
  }
}
