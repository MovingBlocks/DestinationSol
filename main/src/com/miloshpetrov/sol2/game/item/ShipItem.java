package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.HullConfig;

public class ShipItem implements SolItem {

  public static final SolItemType EMPTY = new SolItemType(new Color(), null, 1);
  private final HullConfig myConfig;
  private final String myDesc;

  public ShipItem(HullConfig config) {
    myConfig = config;
    myDesc = makeDesc(myConfig);
  }

  public static String makeDesc(HullConfig hull) {
    StringBuilder sb = new StringBuilder();
    sb.append("Takes ").append(hull.getMaxLife()).append(" dmg\n");
    boolean noG2 = hull.getG2Pos() == null;
    if (noG2 || hull.m1IsFixed() != hull.m2IsFixed()) {
      if (noG2) {
        sb.append(hull.m1IsFixed() ? "1 heavy gun slot\n" : "1 light gun slot\n");
      } else {
        sb.append("1 heavy + 1 light gun slots\n");
      }
    } else {
      sb.append(hull.m1IsFixed() ? "2 heavy gun slots\n" : "2 light gun slots\n");
    }
    if (hull.getAbility() != null) {
      sb.append("Ability:\n");
      hull.getAbility().appendDesc(sb);
    }
    return sb.toString();
  }

  @Override
  public String getDisplayName() {
    return myConfig.getDisplayName();
  }

  @Override
  public float getPrice() {
    return myConfig.getPrice();
  }

  @Override
  public String getDesc() {
    return myDesc;
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
    return myConfig.getIcon();
  }

  @Override
  public SolItemType getItemType() {
    return EMPTY;
  }

  @Override
  public String getCode() {
    return null;
  }

  public HullConfig getConfig() {
    return myConfig;
  }
}
