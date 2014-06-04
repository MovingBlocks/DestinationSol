package com.miloshpetrov.sol2.game.gun;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.item.ClipConfig;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.particle.LightSrc;
import com.miloshpetrov.sol2.game.projectile.Projectile;
import com.miloshpetrov.sol2.game.projectile.ProjectileConfig;

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

  public SolGun(SolGame game, GunItem item, Vector2 relPos, boolean underShip) {
    myItem = item;
    if (myItem.config.lightOnShot) {
      Color lightCol = Col.W;
      ProjectileConfig projConfig = myItem.config.clipConf.projConfig;
      if (projConfig.bodyEffect != null) lightCol = projConfig.bodyEffect.tint;
      else if (projConfig.collisionEffect != null) lightCol = projConfig.collisionEffect.tint;
      myLightSrc = new LightSrc(game, .25f, true, 1f, Vector2.Zero, lightCol);
    } else {
      myLightSrc = null;
    }
    myRelPos = new Vector2(relPos);
    DraLevel level = underShip ? DraLevel.U_GUNS : DraLevel.GUNS;
    float texLen = myItem.config.gunLength / myItem.config.texLenPerc * 2;
    mySprite = new RectSprite(myItem.config.tex, texLen, 0, 0, new Vector2(relPos), level, 0, 0, Col.W, false);
    myDras = new ArrayList<Dra>();
    myDras.add(mySprite);
    if (myLightSrc != null) myLightSrc.collectDras(myDras);
  }

  public List<Dra> getDras() {
    return myDras;
  }

  private void shoot(Vector2 gunSpd, SolGame game, float gunAngle, Vector2 muzzlePos, Fraction fraction, SolObj creator) {
    Vector2 baseSpd = gunSpd;
    ClipConfig cc = myItem.config.clipConf;
    if (cc.projConfig.zeroAbsSpd) {
      baseSpd = Vector2.Zero;
    }

    myCurrAngleVar = SolMath.approach(myCurrAngleVar, myItem.config.maxAngleVar, myItem.config.angleVarPerShot);
    boolean multiple = cc.projectilesPerShot > 1;
    for (int i = 0; i < cc.projectilesPerShot; i++) {
      float bulletAngle = gunAngle;
      if(myCurrAngleVar > 0) bulletAngle += SolMath.rnd(myCurrAngleVar);
      Projectile proj = new Projectile(game, bulletAngle, muzzlePos, baseSpd, fraction, cc.projConfig, multiple);
      game.getObjMan().addObjDelayed(proj);
    }
    myCoolDown += myItem.config.timeBetweenShots;
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
    if (myItem.ammo <= 0 && myItem.reloadAwait <= 0) {
      if (myItem.config.clipConf.infinite || ic != null && ic.tryConsumeItem(myItem.config.clipConf.example)) {
        myItem.reloadAwait = myItem.config.reloadTime + .0001f;
        game.getSoundMan().play(game, myItem.config.reloadSound, null, creator);
      }
    } else if (myItem.reloadAwait > 0) {
      myItem.reloadAwait -= ts;
      if (myItem.reloadAwait <= 0) {
        myItem.ammo = myItem.config.clipConf.size;
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
