package com.miloshpetrov.sol2.game.gun;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.input.Shooter;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.List;

public class GunMount {
  private final Vector2 myRelPos;
  private final boolean myFixed;
  private SolGun myGun;
  private float myRelGunAngle;

  public GunMount(Vector2 relPos, boolean fixed) {
    myRelPos = relPos;
    myFixed = fixed;
  }

  public void update(ItemContainer ic, SolGame game, float shipAngle, SolObj creator, boolean shouldShoot, SolShip nearestEnemy, Fraction fraction) {
    if (myGun == null) return;
    if (!ic.contains(myGun.getItem())) {
      setGun(game, creator, null, false);
      return;
    }

    if (!myFixed && nearestEnemy != null) {
//      myRelGunAngle = SolMath.angle(creator.getPos(), nearestEnemy.getPos()) - shipAngle;
      Vector2 mountPos = SolMath.toWorld(myRelPos, shipAngle, creator.getPos());
      float shootAngle = Shooter.calcShootAngle(mountPos, creator.getSpd(), nearestEnemy.getPos(), nearestEnemy.getSpd(), myGun.getConfig().projConfig.spdLen);
      SolMath.free(mountPos);
      if (shootAngle == shootAngle) myRelGunAngle = shootAngle - shipAngle;
    }
    float gunAngle = shipAngle + myRelGunAngle;
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
}
