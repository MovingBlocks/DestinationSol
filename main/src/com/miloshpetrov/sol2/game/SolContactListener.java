package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.miloshpetrov.sol2.common.SolMath;

public class SolContactListener implements ContactListener {

  private final SolGame myGame;

  public SolContactListener(SolGame game) {
    myGame = game;
  }

  @Override
  public void beginContact(Contact contact) {
  }

  @Override
  public void endContact(Contact contact) {
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
    SolObj soa = ObjMan.asSolObj(contact.getFixtureA().getBody().getUserData());
    SolObj sob = ObjMan.asSolObj(contact.getFixtureB().getBody().getUserData());
    if (soa == null && sob == null) return;

    float absImpulse = 0;
    int pointCount = impulse.getCount();
    float[] normImpulses = impulse.getNormalImpulses();
    for (int i = 0; i < pointCount; i++) {
      float normImpulse = normImpulses[i];
      normImpulse = SolMath.abs(normImpulse);
      if (absImpulse < normImpulse) absImpulse = normImpulse;
    }
    if (soa != null) soa.handleContact(sob, contact, impulse, true, absImpulse, myGame);
    if (sob != null) sob.handleContact(soa, contact, impulse, false, absImpulse, myGame);
  }
}
