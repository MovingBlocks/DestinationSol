package com.miloshpetrov.sol2.game.item;

public class MoneyItem implements SolItem {

  public static final int AMT = 10;
  public static final MoneyItem EXAMPLE = new MoneyItem();

  private MoneyItem() {
  }

  @Override
  public String getTexName() {
    return "money";
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
}
