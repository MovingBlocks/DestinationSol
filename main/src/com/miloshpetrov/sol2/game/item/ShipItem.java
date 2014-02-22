package com.miloshpetrov.sol2.game.item;

import com.miloshpetrov.sol2.game.ship.HullConfig;

public class ShipItem implements SolItem {

  private final String myDisplayName;
  private final float myPrice;
  private final String myDesc;
  private final HullConfig myConfig;

  public ShipItem(HullConfig config, String displayName, String desc, float price) {
    myDisplayName = displayName;
    myConfig = config;
    myPrice = price;
    myDesc = desc;
  }

  @Override
  public String getTexName() {
    return myConfig.texName;
  }

  @Override
  public String getDisplayName() {
    return myDisplayName;
  }

  @Override
  public float getPrice() {
    return myPrice;
  }

  @Override
  public String getDesc() {
    return myDesc;
  }

  @Override
  public SolItem copy() {
    return new ShipItem(myConfig, myDisplayName, myDesc, myPrice);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof ShipItem && ((ShipItem) item).myConfig == myConfig;
  }

  public HullConfig getConfig() {
    return myConfig;
  }
}
