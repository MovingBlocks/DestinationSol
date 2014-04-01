package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.SolGame;

public class MoneyItem implements SolItem {
  public static final int AMT = 10;
  public static final int BIG_AMT = 10 * AMT;
  public static final MoneyItem EXAMPLE = new MoneyItem(false);
  public static final MoneyItem BIG_EXAMPLE = new MoneyItem(true);

  private final boolean myBig;

  private MoneyItem(boolean big) {
    myBig = big;
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
    return new MoneyItem(myBig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof MoneyItem && ((MoneyItem) item).myBig == myBig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return myBig ? game.getItemMan().bigMoneyIcon : game.getItemMan().moneyIcon;
  }
}
