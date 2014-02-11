package com.miloshpetrov.sol2.game.item;


public class RepairItem implements SolItem {
  public static final int LIFE_AMT = 20;
  public static final String TEX_NAME = "repairItem";
  public static final SolItem EXAMPLE = new RepairItem();

  private RepairItem() {
  }

  @Override
  public String getTexName() {
    return TEX_NAME;
  }

  @Override
  public String getDisplayName() {
    return "Repair Kit";
  }

  @Override
  public float getPrice() {
    return 25;
  }

  @Override
  public String getDesc() {
    return "Stay idle to repair " + LIFE_AMT + " life";
  }

  @Override
  public SolItem copy() {
    return new RepairItem();
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof RepairItem;
  }
}
