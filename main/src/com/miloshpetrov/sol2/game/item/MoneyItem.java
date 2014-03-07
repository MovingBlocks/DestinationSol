package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.SolGame;

public class MoneyItem implements SolItem {

  public static final int AMT = 10;
  public static final MoneyItem EXAMPLE = new MoneyItem();

  private MoneyItem() {
  }

  @Override
  public String getDisplayName() {
    return "money";
  }

  @Override
  public float getPrice() {
    return AMT;
  }

  @Override
  public String getDesc() {
    return "money";
  }

  @Override
  public SolItem copy() {
    return new MoneyItem();
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof MoneyItem;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return game.getItemMan().moneyIcon;
  }
}
