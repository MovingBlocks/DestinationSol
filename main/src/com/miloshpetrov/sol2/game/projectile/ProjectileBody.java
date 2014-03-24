package com.miloshpetrov.sol2.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;

public interface ProjectileBody {
  void update(SolGame game);
  Vector2 getPos();
  Vector2 getSpd();
  void receiveAcc(Vector2 acc, SolGame game);
  void onRemove(SolGame game);
  Object getObstacle();
  float getAngle();
  void handleContact(SolObj other, ContactImpulse impulse, boolean isA, float absImpulse, SolGame game, Vector2 collPos);
}
