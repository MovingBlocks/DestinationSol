package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.SolGame;

public class MoneyItem implements SolItem {
  public static final int AMT = 10;
  public static final int MED_AMT = 3 * AMT;
  public static final int BIG_AMT = 10 * AMT;

  private final float myAmt;
  private final SolItemType myItemType;

  public MoneyItem(float amt, SolItemType itemType) {
    myAmt = amt;
    myItemType = itemType;
  }

  @Override
  public String getDisplayName() {
    return "money";
  }

  @Override
  public float getPrice() {
    return myAmt;
  }

  @Override
  public String getDesc() {
    return "money";
  }

  @Override
  public MoneyItem copy() {
    return new MoneyItem(myAmt, myItemType);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof MoneyItem && ((MoneyItem) item).myAmt == myAmt;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    ItemMan im = game.getItemMan();
    if (myAmt == BIG_AMT) return im.bigMoneyIcon;
    if (myAmt == MED_AMT) return im.medMoneyIcon;
    return im.moneyIcon;
  }

  @Override
  public SolItemType getItemType() {
    return myItemType;
  }
}
