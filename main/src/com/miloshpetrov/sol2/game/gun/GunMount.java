package com.miloshpetrov.sol2.game.gun;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.input.Shooter;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.List;

public class GunMount {
  private final Vector2 myRelPos;
  private final boolean myCanFix;
  private SolGun myGun;
  private boolean myDetected;

  public GunMount(Vector2 relPos, boolean canFix) {
    myRelPos = relPos;
    myCanFix = canFix;
  }

  public void update(ItemContainer ic, SolGame game, float shipAngle, SolShip creator, boolean shouldShoot, SolShip nearestEnemy, Fraction fraction) {
    if (myGun == null) return;
    if (!ic.contains(myGun.getItem())) {
      setGun(game, creator, null, false);
      return;
    }

    float relGunAngle = 0;
    myDetected = false;
    if (!myGun.getConfig().fixed && nearestEnemy != null) {
      Vector2 creatorPos = creator.getPos();
      float dst = creatorPos.dst(nearestEnemy.getPos()) - creator.getHull().config.approxRadius - nearestEnemy.getHull().config.approxRadius;
      float detDst = game.getPlanetMan().getNearestPlanet().isNearGround(creatorPos) ? Const.AUTO_SHOOT_GROUND : Const.AUTO_SHOOT_SPACE;
      if (dst < detDst) {
        Vector2 mountPos = SolMath.toWorld(myRelPos, shipAngle, creatorPos);
        float shootAngle = Shooter.calcShootAngle(mountPos, creator.getSpd(), nearestEnemy.getPos(), nearestEnemy.getSpd(), myGun.getConfig().clipConf.projConfig.spdLen);
        if (shootAngle == shootAngle) {
          relGunAngle = shootAngle - shipAngle;
          myDetected = true;
        }
        SolMath.free(mountPos);
      }
    }

    float gunAngle = shipAngle + relGunAngle;
    myGun.update(ic, game, gunAngle, creator, shouldShoot, fraction);
  }

  public GunItem getGun() {
    return myGun == null ? null : myGun.getItem();
  }

  public void setGun(SolGame game, SolObj o, GunItem gunItem, boolean underShip) {
    List<Dra> dras = o.getDras();
    if (myGun != null) {
      List<Dra> dras1 = myGun.getDras();
      dras.removeAll(dras1);
      game.getDraMan().removeAll(dras1);
      myGun = null;
    }
    if (gunItem != null) {
      if (gunItem.config.fixed && !myCanFix) throw new AssertionError();
      myGun = new SolGun(game, gunItem, myRelPos, underShip);
      List<Dra> dras1 = myGun.getDras();
      dras.addAll(dras1);
      game.getDraMan().addAll(dras1);
    }
  }

  public boolean canFix() {
    return myCanFix;
  }

  public Vector2 getRelPos() {
    return myRelPos;
  }

  public boolean isDetected() {
    return myDetected;
  }
}
