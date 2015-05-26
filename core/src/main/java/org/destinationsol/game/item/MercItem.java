package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolGame;

public class MercItem implements SolItem {
  private final ShipConfig myConfig;
  private final String myDesc;

  public MercItem(ShipConfig config) {
    myConfig = config;
    myDesc = "Has a shield and repairers\n" + ShipItem.makeDesc(myConfig.hull);
  }

  @Override
  public String getDisplayName() {
    return myConfig.hull.displayName;
  }

  @Override
  public float getPrice() {
    return myConfig.hull.hirePrice;
  }

  @Override
  public String getDesc() {
    return myDesc;
  }

  @Override
  public SolItem copy() {
    return new MercItem(myConfig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof MercItem && ((MercItem) item).myConfig == myConfig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return myConfig.hull.icon;
  }

  @Override
  public SolItemType getItemType() {
    return ShipItem.EMPTY;
  }

  @Override
  public String getCode() {
    return null;
  }

  public ShipConfig getConfig() {
    return myConfig;
  }
}
