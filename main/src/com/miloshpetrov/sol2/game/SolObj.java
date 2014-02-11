package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.miloshpetrov.sol2.game.dra.Dra;

import java.util.List;

public interface SolObj {
  void update(SolGame game);
  boolean shouldBeRemoved(SolGame game);
  void onRemove(SolGame game);
  float getRadius();
  void receiveDmg(float dmg, SolGame game, Vector2 pos);
  boolean receivesGravity();
  void receiveAcc(Vector2 acc, SolGame game);
  Vector2 getPos();
  FarObj toFarObj();
  List<Dra> getDras();
  float getAngle();
  Vector2 getSpd();
  void handleContact(SolObj other, Contact contact, ContactImpulse impulse, boolean isA, float absImpulse, SolGame game);
  String toDebugString();
}
