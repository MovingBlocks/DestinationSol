package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.HullConfig;

public class ShipItem implements SolItem {

  public static final SolItemType EMPTY = new SolItemType(new Color(), null);
  private final HullConfig myConfig;

  public ShipItem(HullConfig config) {
    myConfig = config;
  }

  @Override
  public String getDisplayName() {
    return myConfig.displayName;
  }

  @Override
  public float getPrice() {
    return myConfig.price;
  }

  @Override
  public String getDesc() {
    return myConfig.desc;
  }

  @Override
  public SolItem copy() {
    return new ShipItem(myConfig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof ShipItem && ((ShipItem) item).myConfig == myConfig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return myConfig.icon;
  }

  @Override
  public SolItemType getItemType() {
    return EMPTY;
  }

  public HullConfig getConfig() {
    return myConfig;
  }
}
