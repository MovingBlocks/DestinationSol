package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;

public interface FarObj {
  boolean shouldBeRemoved(SolGame game);
  SolObject toObj(SolGame game);
  void update(SolGame game);
  float getRadius();
  Vector2 getPos();
  String toDebugString();
  boolean hasBody();
}
