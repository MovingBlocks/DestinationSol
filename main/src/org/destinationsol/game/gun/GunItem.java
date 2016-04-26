/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.gun;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.item.SolItemType;

public class GunItem implements SolItem {

  public final GunConfig config;
  public int ammo;
  public float reloadAwait;
  private int equipped;

  public GunItem(GunConfig config, int ammo, float reloadAwait) {
    this.config = config;
    this.ammo = ammo;
    this.reloadAwait = reloadAwait;
  }

  public GunItem(GunConfig config, int ammo, float reloadAwait, int equipped) {
    this(config, ammo, reloadAwait);
    this.equipped = equipped;
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
    return new GunItem(config, ammo, reloadAwait, equipped);
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

  public int isEquipped() { return equipped; }

  public void setEquipped(int equipped) { this.equipped = equipped; }
}
