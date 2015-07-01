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
 
 package com.miloshpetrov.sol2.game.screens;

import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class DmgWarnDrawer extends WarnDrawer {

  public DmgWarnDrawer(float r) {
    super(r, "Heavily Damaged");
  }

  @Override
  protected boolean shouldWarn(SolGame game) {
    SolShip hero = game.getHero();
    if (hero == null) return false;
    float l = hero.getLife();
    int ml = hero.getHull().config.maxLife;
    return l < ml * .3f;
  }
}
