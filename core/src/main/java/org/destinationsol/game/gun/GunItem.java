package org.destinationsol.game.gun;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.item.SolItemType;

public class GunItem implements SolItem {

  public final GunConfig config;
  public int ammo;
  public float reloadAwait;

  public GunItem(GunConfig config, int ammo, float reloadAwait) {
    this.config = config;
    this.ammo = ammo;
    this.reloadAwait = reloadAwait;
  }

  @Override
  public String getDisplayName() {
    return config.displayName;
  }

  @Override
  public float getPrice() {
    return config.price;
  }

  @Override
  public String getDesc() {
    return config.desc;
  }

  @Override
  public GunItem copy() {
    return new GunItem(config, ammo, reloadAwait);
  }

  @Override
  public boolean isSame(SolItem item) {
    return false;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(SolGame game) {
    return config.icon;
  }

  @Override
  public SolItemType getItemType() {
    return config.itemType;
  }

  @Override
  public String getCode() {
    return config.code;
  }

  public boolean canShoot() {
    return ammo > 0 || reloadAwait > 0;
  }
}
