package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.item.SolItem;

public class Teleport implements ShipAbility {
  private final Vector2 myNewPos;
  private boolean myShouldTeleport;

  public Teleport() {
    myNewPos = new Vector2();
  }

  @Override
  public boolean update(SolGame game, SolShip owner, boolean tryToUse) {
    myShouldTeleport = false;
    if (!tryToUse) return false;
    Vector2 pos = owner.getPos();
    Fraction frac = owner.getPilot().getFraction();
    SolShip ne = game.getFractionMan().getNearestEnemy(game, 4, frac, pos);
    if (ne == null) return false;
    Vector2 nePos = ne.getPos();
    for (int i = 0; i < 5; i++) {
      myNewPos.set(pos);
      myNewPos.sub(nePos);
      float angle = SolMath.rnd(30, 45) * SolMath.toInt(SolMath.test(.5f));
      SolMath.rotate(myNewPos, angle);
      myNewPos.add(nePos);
      if (game.isPlaceEmpty(myNewPos)) {
        myShouldTeleport = true;
        return true;
      }
    }
    return false;
  }

  @Override
  public SolItem getAmmoExample() {
    return null;
  }

  @Override
  public float getRechargeTime() {
    return 3;
  }

  public void maybeTeleport(SolGame game, SolShip owner) {
    if (!myShouldTeleport) return;
    FarShip ship = owner.toFarObj();
    game.getObjMan().removeObjDelayed(owner);
    ship.setPos(myNewPos);
    SolObj newOwner = ship.toObj(game);
    game.getObjMan().addObjDelayed(newOwner);
  }

  public static class Config implements AbilityConfig {
    public ShipAbility build() {
      return new Teleport();
    }

    public static AbilityConfig load(JsonValue abNode) {
      return new Config();
    }
  }
}
