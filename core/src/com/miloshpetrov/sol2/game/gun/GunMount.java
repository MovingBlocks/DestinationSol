package com.miloshpetrov.sol2.game.gun;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.input.Shooter;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.List;

public class GunMount {
  private final Vector2 myRelPos;
  private final boolean myFixed;
  private SolGun myGun;
  private boolean myDetected;
  private float myRelGunAngle;

  public GunMount(Vector2 relPos, boolean fixed) {
    myRelPos = relPos;
    myFixed = fixed;
  }

  public void update(ItemContainer ic, SolGame game, float shipAngle, SolShip creator, boolean shouldShoot, SolShip nearestEnemy, Fraction fraction) {
    if (myGun == null) return;
    if (!ic.contains(myGun.getItem())) {
      setGun(game, creator, null, false);
      return;
    }

    if (creator.getHull().config.type != HullConfig.Type.STATION) myRelGunAngle = 0;
    myDetected = false;
    if (!myFixed && nearestEnemy != null) {
      Vector2 creatorPos = creator.getPos();
      Vector2 nePos = nearestEnemy.getPos();
      float dst = creatorPos.dst(nePos) - creator.getHull().config.approxRadius - nearestEnemy.getHull().config.approxRadius;
      float detDst = game.getPlanetMan().getNearestPlanet().isNearGround(creatorPos) ? Const.AUTO_SHOOT_GROUND : Const.AUTO_SHOOT_SPACE;
      if (dst < detDst) {
        Vector2 mountPos = SolMath.toWorld(myRelPos, shipAngle, creatorPos);
        boolean player = creator.getPilot().isPlayer();
        float shootAngle = Shooter.calcShootAngle(mountPos, creator.getSpd(), nePos, nearestEnemy.getSpd(), myGun.getConfig().clipConf.projConfig.spdLen, player);
        if (shootAngle == shootAngle) {
          myRelGunAngle = shootAngle - shipAngle;
          myDetected = true;
          if (player) game.getMountDetectDrawer().setNe(nearestEnemy);
        }
        SolMath.free(mountPos);
      }
    }

    float gunAngle = shipAngle + myRelGunAngle;
    myGun.update(ic, game, gunAngle, creator, shouldShoot, fraction);
  }

  public GunItem getGun() {
    return myGun == null ? null : myGun.getItem();
  }

  public void setGun(SolGame game, SolObject o, GunItem gunItem, boolean underShip) {
    List<Dra> dras = o.getDras();
    if (myGun != null) {
      List<Dra> dras1 = myGun.getDras();
      dras.removeAll(dras1);
      game.getDraMan().removeAll(dras1);
      myGun = null;
    }
    if (gunItem != null) {
      if (gunItem.config.fixed != myFixed) throw new AssertionError("tried to set gun to incompatible mount");
      myGun = new SolGun(game, gunItem, myRelPos, underShip);
      List<Dra> dras1 = myGun.getDras();
      dras.addAll(dras1);
      game.getDraMan().addAll(dras1);
    }
  }

  public boolean isFixed() {
    return myFixed;
  }

  public Vector2 getRelPos() {
    return myRelPos;
  }

  public boolean isDetected() {
    return myDetected;
  }
}
