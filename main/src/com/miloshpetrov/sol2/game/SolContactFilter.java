package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.miloshpetrov.sol2.game.projectile.Rocket;

public class SolContactFilter implements ContactFilter {
  @Override
  public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
    Object oA = fixtureA.getBody().getUserData();
    Object oB = fixtureB.getBody().getUserData();

    boolean aIsRocket = oA instanceof Rocket;
    if (!aIsRocket && !(oB instanceof Rocket)) return true;

    Rocket m = (Rocket)(aIsRocket ? oA : oB);
    Object o = aIsRocket ? oB : oA;
    return m.shouldCollide(o);
  }
}
