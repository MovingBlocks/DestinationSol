/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
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
    sb.append("Takes ").append(hull.maxLife).append(" dmg\n");
    boolean noG2 = hull.g2Pos == null;
    if (noG2 || hull.m1Fixed != hull.m2Fixed) {
      if (noG2) {
        sb.append(hull.m1Fixed ? "1 heavy gun slot\n" : "1 light gun slot\n");
      } else {
        sb.append("1 heavy + 1 light gun slots\n");
      }
    } else {
      sb.append(hull.m1Fixed ? "2 heavy gun slots\n" : "2 light gun slots\n");
    }
    if (hull.ability != null) {
      sb.append("Ability:\n");
      hull.ability.appendDesc(sb);
    }
    return sb.toString();
  }

  @Override
  public String getDisplayName() {
    return myConfig.displayName;
  }

  @Override
  public float getPrice() {
    return myConfig.price;
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
    return myConfig.icon;
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
