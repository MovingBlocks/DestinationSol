package com.miloshpetrov.sol2.game.gun;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.particle.LightSrc;
import com.miloshpetrov.sol2.game.projectile.Projectile;

import java.util.ArrayList;
import java.util.List;

public class SolGun {
  private final LightSrc myLightSrc;
  private final Vector2 myRelPos;
  private final RectSprite mySprite;
  private final GunItem myItem;
  private final List<Dra> myDras;
  private float myCoolDown;
  private float myCurrAngleVar;

  public SolGun(SolGame game, GunItem item, Vector2 relPos) {
    myItem = item;
    myLightSrc = myItem.config.lightOnShot ? new LightSrc(game, .25f, true, 1f, Vector2.Zero) : null;
    myRelPos = new Vector2(relPos);
    mySprite = new RectSprite(myItem.config.tex, myItem.config.gunLength * 2, 0, 0, new Vector2(relPos), DraLevel.GUNS, 0, 0, Col.W);
    myDras = new ArrayList<Dra>();
    myDras.add(mySprite);
    if (myLightSrc != null) myLightSrc.collectDras(myDras);
  }

  public List<Dra> getDras() {
    return myDras;
  }

  private void shoot(Vector2 gunSpd, SolGame game, float gunAngle, Vector2 muzzlePos, Fraction fraction, SolObj creator) {
    myCurrAngleVar = SolMath.approach(myCurrAngleVar, myItem.config.maxAngleVar, myItem.config.angleVarPerShot);
    float bulletAngle = gunAngle;
    bulletAngle += SolMath.rnd(myCurrAngleVar);
    myCoolDown += myItem.config.timeBetweenShots;
    Projectile proj = new Projectile(game, bulletAngle, muzzlePos, gunSpd, fraction, myItem.config.dmg, myItem.config.projConfig);
    game.getObjMan().addObjDelayed(proj);
    myItem.ammo--;
    game.getSoundMan().play(game, myItem.config.shootSound, muzzlePos, creator);
  }

  public void update(ItemContainer ic, SolGame game, float gunAngle, SolObj creator, boolean shouldShoot, Fraction fraction) {
    float baseAngle = creator.getAngle();
    Vector2 basePos = creator.getPos();
    float gunRelAngle = gunAngle - baseAngle;
    mySprite.relAngle = gunRelAngle;
    Vector2 muzzleRelPos = SolMath.fromAl(gunRelAngle, myItem.config.gunLength);
    muzzleRelPos.add(myRelPos);
    if (myLightSrc != null) myLightSrc.setRelPos(muzzleRelPos);
    Vector2 muzzlePos = SolMath.toWorld(muzzleRelPos, baseAngle, basePos);
    SolMath.free(muzzleRelPos);

    float ts = game.getTimeStep();
    int ics = myItem.config.infiniteClipSize;
    if (myItem.ammo <= 0 && myItem.reloadAwait <= 0) {
      if (ics != 0 || ic != null && ic.tryConsumeItem(myItem.config.clipConf.example)) {
        myItem.reloadAwait = myItem.config.maxReloadTime;
        game.getSoundMan().play(game, myItem.config.reloadSound, muzzlePos, creator);
      }
    } else if (myItem.reloadAwait > 0) {
      myItem.reloadAwait -= ts;
      if (myItem.reloadAwait <= 0) {
        myItem.ammo = ics == 0 ? myItem.config.clipConf.size : ics;
      }
    }

    if (myCoolDown > 0) myCoolDown -= ts;

    boolean shot = shouldShoot && myCoolDown <= 0 && myItem.ammo > 0;
    if (shot) {
      Vector2 gunSpd = creator.getSpd();
      shoot(gunSpd, game, gunAngle, muzzlePos, fraction, creator);
    } else {
      myCurrAngleVar = SolMath.approach(myCurrAngleVar, myItem.config.minAngleVar, myItem.config.angleVarDamp * ts);
    }
    if (myLightSrc != null) myLightSrc.update(shot, baseAngle, game);
    SolMath.free(muzzlePos);
  }


  public GunConfig getConfig() {
    return myItem.config;
  }

  public GunItem getItem() {
    return myItem;
  }
}
