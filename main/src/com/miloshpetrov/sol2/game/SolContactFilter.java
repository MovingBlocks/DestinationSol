package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.miloshpetrov.sol2.game.projectile.Projectile;

public class SolContactFilter implements ContactFilter {
  @Override
  public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
    SolObj oA = (SolObj) fixtureA.getBody().getUserData();
    SolObj oB = (SolObj) fixtureB.getBody().getUserData();

    boolean aIsRocket = oA instanceof Projectile;
    if (!aIsRocket && !(oB instanceof Projectile)) return true;

    Projectile m = (Projectile)(aIsRocket ? oA : oB);
    SolObj o = aIsRocket ? oB : oA;
    return m.shouldCollide(o);
  }
}
