package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.SolGame;

public class MercItem implements SolItem {
  private final ShipConfig myConfig;
  private final String myDesc;

  public MercItem(ShipConfig config) {
    myConfig = config;
    myDesc = ShipItem.makeDesc(myConfig.hull);
  }

  @Override
  public String getDisplayName() {
    return myConfig.hull.displayName;
  }

  @Override
  public float getPrice() {
    return myConfig.hull.hirePrice;
  }

  @Override
  public String getDesc() {
    return myDesc;
  }

  @Override
  public SolItem copy() {
    return new MercItem(myConfig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof MercItem && ((MercItem) item).myConfig == myConfig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return myConfig.hull.icon;
  }

  @Override
  public SolItemType getItemType() {
    return ShipItem.EMPTY;
  }

  public ShipConfig getConfig() {
    return myConfig;
  }
}
