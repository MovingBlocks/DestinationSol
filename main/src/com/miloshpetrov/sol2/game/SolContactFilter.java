package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.miloshpetrov.sol2.game.projectile.Bullet;

public class SolContactFilter implements ContactFilter {
  @Override
  public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
    Object oA = fixtureA.getBody().getUserData();
    Object oB = fixtureB.getBody().getUserData();

    boolean aIsRocket = oA instanceof Bullet;
    if (!aIsRocket && !(oB instanceof Bullet)) return true;

    Bullet m = (Bullet)(aIsRocket ? oA : oB);
    Object o = aIsRocket ? oB : oA;
    return m.shouldCollide(o);
  }
}
