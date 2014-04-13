package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;

public interface ProjectileBody {
  void update(SolGame game);
  Vector2 getPos();
  Vector2 getSpd();
  void receiveForce(Vector2 force, SolGame game, boolean acc);
  void onRemove(SolGame game);
  float getAngle();
  void setSpd(Vector2 spd);
}
