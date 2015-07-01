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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.game.ShipConfig;
import com.miloshpetrov.sol2.game.SolGame;

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
