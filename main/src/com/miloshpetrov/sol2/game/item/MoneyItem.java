package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.SolGame;

public class MoneyItem implements SolItem {
  public static final int AMT = 10;
  public static final int BIG_AMT = 10 * AMT;

  private final boolean myBig;
  private final SolItemType myItemType;

  public MoneyItem(boolean big, SolItemType itemType) {
    myBig = big;
    myItemType = itemType;
  }

  @Override
  public String getDisplayName() {
    return "money";
  }

  @Override
  public float getPrice() {
    return myBig ? BIG_AMT : AMT;
  }

  @Override
  public String getDesc() {
    return "money";
  }

  @Override
  public SolItem copy() {
    return new MoneyItem(myBig, myItemType);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof MoneyItem && ((MoneyItem) item).myBig == myBig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return myBig ? game.getItemMan().bigMoneyIcon : game.getItemMan().moneyIcon;
  }

  @Override
  public SolItemType getItemType() {
    return myItemType;
  }
}
