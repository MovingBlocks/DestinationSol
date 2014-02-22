package com.miloshpetrov.sol2.game.item;

public class ClipItem implements SolItem {
  private final ClipConfig myConfig;

  public ClipItem(ClipConfig config) {
    myConfig = config;
  }

  @Override
  public String getTexName() {
    return myConfig.iconName;
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

  public ClipConfig getConfig() {
    return myConfig;
  }

  @Override
  public SolItem copy() {
    return new com.miloshpetrov.sol2.game.item.ClipItem(myConfig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof com.miloshpetrov.sol2.game.item.ClipItem && ((com.miloshpetrov.sol2.game.item.ClipItem) item).myConfig == myConfig;
  }
}
