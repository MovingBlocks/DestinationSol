package org.destinationsol.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.dra.Dra;
import org.destinationsol.game.dra.DraLevel;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.item.EngineItem;
import org.destinationsol.game.particle.PartMan;
import org.destinationsol.game.sound.SolSound;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.LightSrc;
import org.destinationsol.game.particle.ParticleSrc;

import java.util.ArrayList;
import java.util.List;

public class ShipEngine {
  public static final float MAX_RECOVER_ROT_SPD = 5f;
  public static final float RECOVER_MUL = 15f;
  public static final float RECOVER_AWAIT = 2f;

  private final ParticleSrc myFlameSrc1;
  private final ParticleSrc myFlameSrc2;
  private final LightSrc myLightSrc1;
  private final LightSrc myLightSrc2;
  private final EngineItem myItem;
  private final List<Dra> myDras;
  private float myRecoverAwait;

  public ShipEngine(SolGame game, EngineItem ei, Vector2 e1RelPos, Vector2 e2RelPos, SolShip ship) {
    myItem = ei;
    myDras = new ArrayList<Dra>();
    EffectConfig ec = myItem.getEffectConfig();
    Vector2 shipPos = ship.getPos();
    Vector2 shipSpd = ship.getSpd();
    myFlameSrc1 = new ParticleSrc(ec, -1, DraLevel.PART_BG_0, e1RelPos, true, game, shipPos, shipSpd, 0);
    myDras.add(myFlameSrc1);
    myFlameSrc2 = new ParticleSrc(ec, -1, DraLevel.PART_BG_0, e2RelPos, true, game, shipPos, shipSpd, 0);
    myDras.add(myFlameSrc2);
    float lightSz = ec.sz * 2.5f;
    myLightSrc1 = new LightSrc(game, lightSz, true, .7f, new Vector2(e1RelPos), ec.tint);
    myLightSrc1.collectDras(myDras);
    myLightSrc2 = new LightSrc(game, lightSz, true, .7f, new Vector2(e2RelPos), ec.tint);
    myLightSrc2.collectDras(myDras);
  }

  public List<Dra> getDras() {
    return myDras;
  }

  public void update(float angle, SolGame game, Pilot provider, Body body, Vector2 spd, SolObject owner,
    boolean controlsEnabled, float mass)
  {
    boolean working = applyInput(game, angle, provider, body, spd, controlsEnabled, mass);

    myFlameSrc1.setWorking(working);
    myFlameSrc2.setWorking(working);

    myLightSrc1.update(working, angle, game);
    myLightSrc2.update(working, angle, game);
    if (working) {
      SolSound sound = myItem.getWorkSound();
      game.getSoundMan().play(game, sound, myFlameSrc1.getPos(), owner); // hack with pos
    }
  }

  private boolean applyInput(SolGame cmp, float shipAngle, Pilot provider, Body body, Vector2 spd,
    boolean controlsEnabled, float mass)
  {
    boolean spdOk = SolMath.canAccelerate(shipAngle, spd);
    boolean working = controlsEnabled && provider.isUp() && spdOk;

    EngineItem e = myItem;
    if (working) {
      Vector2 v = SolMath.fromAl(shipAngle, mass * e.getAcc());
      body.applyForceToCenter(v, true);
      SolMath.free(v);
    }

    float ts = cmp.getTimeStep();
    float rotSpd = body.getAngularVelocity() * SolMath.radDeg;
    float desiredRotSpd = 0;
    float rotAcc = e.getRotAcc();
    boolean l = controlsEnabled && provider.isLeft();
    boolean r = controlsEnabled && provider.isRight();
    float absRotSpd = SolMath.abs(rotSpd);
    if (absRotSpd < e.getMaxRotSpd() && l != r) {
      desiredRotSpd = SolMath.toInt(r) * e.getMaxRotSpd();
      if (absRotSpd < MAX_RECOVER_ROT_SPD) {
        if (myRecoverAwait > 0) myRecoverAwait -= ts;
        if (myRecoverAwait <= 0) rotAcc *= RECOVER_MUL;
      }
    } else {
      myRecoverAwait = RECOVER_AWAIT;
    }
    body.setAngularVelocity(SolMath.degRad * SolMath.approach(rotSpd, desiredRotSpd, rotAcc * ts));
    return working;
  }

  public void onRemove(SolGame game, Vector2 basePos) {
    PartMan pm = game.getPartMan();
    pm.finish(game, myFlameSrc1, basePos);
    pm.finish(game, myFlameSrc2, basePos);
  }

  public EngineItem getItem() {
    return myItem;
  }
}
