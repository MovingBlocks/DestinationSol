package com.miloshpetrov.sol2.game.item;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.SolGame;

public class RepairItem implements SolItem {
  public static final int LIFE_AMT = 20;
  private final SolItemType myItemType;

  public RepairItem(SolItemType itemType) {
    myItemType = itemType;
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
    return "Stay idle to fix " + LIFE_AMT + " dmg";
  }

  @Override
  public SolItem copy() {
    return new RepairItem(myItemType);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof RepairItem;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return game.getItemMan().repairIcon;
  }

  @Override
  public SolItemType getItemType() {
    return myItemType;
  }
}
