package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.miloshpetrov.sol2.game.projectile.Projectile;

public class SolContactFilter implements ContactFilter {
  private final FractionMan myFractionMan;

  public SolContactFilter(FractionMan fractionMan) {
    myFractionMan = fractionMan;
  }

  @Override
  public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
    SolObj oA = (SolObj) fixtureA.getBody().getUserData();
    SolObj oB = (SolObj) fixtureB.getBody().getUserData();

    boolean aIsProj = oA instanceof Projectile;
    if (!aIsProj && !(oB instanceof Projectile)) return true;

    Projectile proj = (Projectile)(aIsProj ? oA : oB);
    SolObj o = aIsProj ? oB : oA;
    Fixture f = aIsProj ? fixtureB : fixtureA;
    return proj.shouldCollide(o, f, myFractionMan);
  }
}
