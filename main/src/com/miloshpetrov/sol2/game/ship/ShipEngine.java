package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.SolObj;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraLevel;
import com.miloshpetrov.sol2.game.input.Pilot;
import com.miloshpetrov.sol2.game.item.EngineItem;
import com.miloshpetrov.sol2.game.particle.*;
import com.miloshpetrov.sol2.game.sound.SolSound;

import java.util.ArrayList;
import java.util.List;

public class ShipEngine {
  private final ParticleSrc myFlameSrc1;
  private final ParticleSrc myFlameSrc2;
  private final LightSrc myLightSrc1;
  private final LightSrc myLightSrc2;
  private final EngineItem myItem;
  private final List<Dra> myDras;

  public ShipEngine(SolGame game, EngineItem ei, Vector2 e1RelPos, Vector2 e2RelPos, SolShip ship) {
    myItem = ei;
    myDras = new ArrayList<Dra>();
    EffectConfig ec = myItem.getEffectConfig();
    Vector2 shipPos = ship.getPos();
    Vector2 shipSpd = ship.getSpd();
    myFlameSrc1 = new ParticleSrc(ec, -1, DraLevel.PART_BG_0, e1RelPos, true, game, shipPos, shipSpd);
    myDras.add(myFlameSrc1);
    myFlameSrc2 = new ParticleSrc(ec, -1, DraLevel.PART_BG_0, e2RelPos, true, game, shipPos, shipSpd);
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

  public void update(float angle, SolGame game, Pilot provider, Body body, Vector2 spd, SolObj owner,
    boolean controlsEnabled)
  {
    boolean working = applyInput(game, angle, provider, body, spd, controlsEnabled);

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
    boolean controlsEnabled)
  {
    boolean spdOk = spd.len() < Const.MAX_MOVE_SPD || SolMath.angleDiff(SolMath.angle(spd), shipAngle) > 90;
    boolean working = controlsEnabled && provider.isUp() && spdOk;

    EngineItem e = myItem;
    if (working) {
      Vector2 v = SolMath.fromAl(shipAngle, body.getMass() * e.getAac());
      body.applyForceToCenter(v, true);
      SolMath.free(v);
    }
    float rotSpd = body.getAngularVelocity() * SolMath.radDeg;
    float desiredRotSpd = 0;
    boolean l = controlsEnabled && provider.isLeft();
    boolean r = controlsEnabled && provider.isRight();
    if (-e.getMaxRotSpd() < rotSpd && rotSpd < e.getMaxRotSpd() && l != r) {
      desiredRotSpd = SolMath.toInt(r) * e.getMaxRotSpd();
    }
    body.setAngularVelocity(SolMath.degRad * SolMath.approach(rotSpd, desiredRotSpd, e.getRotAcc() * cmp.getTimeStep()));
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
