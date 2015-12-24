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

package org.destinationsol.game.screens;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.SolApplication;

public interface ShipUiControl {
  void update(SolApplication cmp, boolean enabled);
  boolean isLeft();
  boolean isRight();
  boolean isUp();
  boolean isDown();
  boolean isShoot();
  boolean isShoot2();
  boolean isAbility();
  TextureAtlas.AtlasRegion getInGameTex();
  void blur();
}
