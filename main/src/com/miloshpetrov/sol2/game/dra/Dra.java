package com.miloshpetrov.sol2.game.dra;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.Drawer;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;

public interface Dra {
  Texture getTex();
  DraLevel getLevel();
  // called on every update from manager
  void update(SolGame game, SolObj o);
  // called on every draw from manager. after that, this dra should be able to return correct pos & radius
  void prepare(SolObj o);
  Vector2 getPos();
  Vector2 getRelPos();
  float getRadius();
  void draw(Drawer drawer, SolGame game);
  boolean isEnabled();
  boolean okToRemove();
}
