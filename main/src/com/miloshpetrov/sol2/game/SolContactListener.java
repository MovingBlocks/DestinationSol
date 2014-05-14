package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.projectile.Projectile;

public class SolContactListener implements ContactListener {
  private final SolGame myGame;

  public SolContactListener(SolGame game) {
    myGame = game;
  }

  @Override
  public void beginContact(Contact contact) {
    SolObj oA = (SolObj) contact.getFixtureA().getBody().getUserData();
    SolObj oB = (SolObj) contact.getFixtureB().getBody().getUserData();

    boolean aIsProj = oA instanceof Projectile;
    if (!aIsProj && !(oB instanceof Projectile)) return;

    Projectile proj = (Projectile)(aIsProj ? oA : oB);
    SolObj o = aIsProj ? oB : oA;
    proj.setObstacle(o, myGame);
  }

  @Override
  public void endContact(Contact contact) {
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
    SolObj soa = (SolObj) contact.getFixtureA().getBody().getUserData();
    SolObj sob = (SolObj) contact.getFixtureB().getBody().getUserData();
    if (soa instanceof Projectile && ((Projectile) soa).getConfig().density <= 0) return;
    if (sob instanceof Projectile && ((Projectile) sob).getConfig().density <= 0) return;

    float absImpulse = calcAbsImpulse(impulse);
    Vector2 collPos = contact.getWorldManifold().getPoints()[0];
    soa.handleContact(sob, impulse, true, absImpulse, myGame, collPos);
    sob.handleContact(soa, impulse, false, absImpulse, myGame, collPos);
    myGame.getSpecialSounds().playColl(myGame, absImpulse, soa, collPos);
    myGame.getSpecialSounds().playColl(myGame, absImpulse, sob, collPos);
  }

  private float calcAbsImpulse(ContactImpulse impulse) {
    float absImpulse = 0;
    int pointCount = impulse.getCount();
    float[] normImpulses = impulse.getNormalImpulses();
    for (int i = 0; i < pointCount; i++) {
      float normImpulse = normImpulses[i];
      normImpulse = SolMath.abs(normImpulse);
      if (absImpulse < normImpulse) absImpulse = normImpulse;
    }
    return absImpulse;
  }
}
