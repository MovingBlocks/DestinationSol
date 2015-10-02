package org.destinationsol.game.dra;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;

//TODO Dra?
public interface Dra {
  Texture getTex0();
  TextureAtlas.AtlasRegion getTex();
  DraLevel getLevel();
  // called on every update from manager
  void update(SolGame game, SolObject o);
  // called on every draw from manager. after that, this dra should be able to return correct pos & radius
  void prepare(SolObject o);
  Vector2 getPos();
  Vector2 getRelPos();
  float getRadius();
  void draw(GameDrawer drawer, SolGame game);
  boolean isEnabled();
  boolean okToRemove();
}
