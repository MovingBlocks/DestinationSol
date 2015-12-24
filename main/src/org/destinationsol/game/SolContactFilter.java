package org.destinationsol.game;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.destinationsol.game.projectile.Projectile;

public class SolContactFilter implements ContactFilter {
  private final FactionMan myFactionMan;

  public SolContactFilter(FactionMan factionMan) {
    myFactionMan = factionMan;
  }

  @Override
  public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
    SolObject oA = (SolObject) fixtureA.getBody().getUserData();
    SolObject oB = (SolObject) fixtureB.getBody().getUserData();

    boolean aIsProj = oA instanceof Projectile;
    if (!aIsProj && !(oB instanceof Projectile)) return true;

    Projectile proj = (Projectile)(aIsProj ? oA : oB);
    SolObject o = aIsProj ? oB : oA;
    Fixture f = aIsProj ? fixtureB : fixtureA;
    return proj.shouldCollide(o, f, myFactionMan);
  }
}
