package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.SolApplication;

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
