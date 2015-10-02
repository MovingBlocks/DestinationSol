package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.destinationsol.common.Nullable;
import org.destinationsol.game.dra.Dra;

import java.util.List;

public interface SolObject {
  void update(SolGame game);
  boolean shouldBeRemoved(SolGame game);
  void onRemove(SolGame game);
  void receiveDmg(float dmg, SolGame game, @Nullable Vector2 pos, DmgType dmgType);
  boolean receivesGravity();
  void receiveForce(Vector2 force, SolGame game, boolean acc);
  Vector2 getPos();
  FarObj toFarObj();
  List<Dra> getDras();
  float getAngle();
  Vector2 getSpd();
  void handleContact(SolObject other, ContactImpulse impulse, boolean isA, float absImpulse, SolGame game,
    Vector2 collPos);
  String toDebugString();
  Boolean isMetal();
  boolean hasBody();
}
