package com.miloshpetrov.sol2.game.item;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.SolGame;

public class SloMoCharge implements SolItem {
  public static final SloMoCharge EXAMPLE = new SloMoCharge();

  private SloMoCharge() {
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
    return new SloMoCharge();
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof SloMoCharge;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return game.getItemMan().sloMoChargeIcon;
  }
}
