package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.SolGame;
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

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return myConfig.icon;
  }

  public HullConfig getConfig() {
    return myConfig;
  }
}
