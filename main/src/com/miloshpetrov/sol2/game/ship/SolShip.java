package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraMan;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.gun.GunMount;
import com.miloshpetrov.sol2.game.input.Pilot;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.particle.ParticleSrc;
import com.miloshpetrov.sol2.game.sound.SolSound;

import java.util.List;

public class SolShip implements SolObj {
  public static final float BASE_DUR_MOD = .3f;
  public static final float PULL_DIST = 2f;
  public static final float SMOKE_PERC = .6f;
  public static final float FIRE_PERC = .3f;
  private static final int TRADE_AFTER = 3;
  public static final float MAX_FIRE_AWAIT = 1f;

  private final Pilot myPilot;
  private final ItemContainer myItemContainer;
  private final ItemContainer myTradeContainer;
  private final ShipHull myHull;
  private final ParticleSrc mySmokeSrc;
  private final ParticleSrc myFireSrc;
  private final RemoveController myRemoveController;
  private final List<Dra> myDras;
  private final float myRadius;
  private final ShipRepairer myRepairer;
  private final ShipAbility myAbility;

  private Shield myShield;
  private float myMoney;
  private float myIdleTime;
  private Armor myArmor;
  private float myFireAwait;
  private float myAbilityAwait;
  private float myControlEnableAwait;

  public SolShip(SolGame game, Pilot pilot, ShipHull hull, RemoveController removeController, List<Dra> dras,
    ItemContainer container, ShipRepairer repairer, float money, ItemContainer tradeContainer, Shield shield,
    Armor armor)
  {
    myRemoveController = removeController;
    myDras = dras;
    myPilot = pilot;
    myHull = hull;
    myItemContainer = container;
    myTradeContainer = tradeContainer;
    List<ParticleSrc> effs = game.getSpecialEffects().buildFireSmoke(myHull.config.size, game, myHull.getPos(), myHull.getSpd());
    mySmokeSrc = effs.get(0);
    myFireSrc = effs.get(1);
    myDras.add(mySmokeSrc);
    myDras.add(myFireSrc);
    myRadius = DraMan.radiusFromDras(myDras);
    myRepairer = repairer;
    myMoney = money;
    myShield = shield;
    myArmor = armor;
    AbilityConfig ac = myHull.config.ability;
    myAbility = ac == null ? null : ac.build();
    if (myAbility != null) myAbilityAwait = myAbility.getRechargeTime();
  }

  @Override
  public Vector2 getPos() {
    return myHull.getPos();
  }

  @Override
  public FarShip toFarObj() {
    GunMount m1 = myHull.getGunMount(false);
    GunMount m2 = myHull.getGunMount(true);
    return new FarShip(myHull.getPos(), myHull.getSpd(), myHull.getAngle(), myHull.getBody().getAngularVelocity(), myPilot, myItemContainer, myHull.config, myHull.life,
      m1.isFixed(), m2.isFixed(), m1.getGun(), m2.getGun(), myRadius, myRemoveController, myHull.getEngine(), myRepairer, myMoney, myTradeContainer, myShield, myArmor);
  }

  @Override
  public List<Dra> getDras() {
    return myDras;
  }

  @Override
  public void handleContact(SolObj other, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game, Vector2 collPos)
  {
    if (tryCollectLoot(other)) return;
    if (myHull.config.type != HullConfig.Type.STATION) {
      Fixture f = null; // restore?
      float dmg = absImpulse / myHull.getBody().getMass() / myHull.config.durability;
      if (f == myHull.getBase()) dmg *= BASE_DUR_MOD;
      receiveDmg((int) dmg, game, collPos, DmgType.CRASH);
    }
  }

  @Override
  public String toDebugString() {
    return "";
  }

  @Override
  public Boolean isMetal() {
    return true;
  }

  @Override
  public boolean hasBody() {
    return true;
  }

  private boolean tryCollectLoot(SolObj obj) {
    //todo: play pickup sound
    if (!(obj instanceof Loot)) return false;
    if (!myPilot.collectsItems()) return false;
    Loot loot = (Loot) obj;
    if (loot.getOwner() == this) return false;
    SolItem i = loot.getItem();
    if (i == null) return false;
    if (i instanceof MoneyItem) {
      myMoney += i.getPrice();
      loot.setLife(0);
      return true;
    }
    ItemContainer c = shouldTrade(i) ? myTradeContainer : myItemContainer;
    boolean canAdd = c.canAdd();
    if (canAdd) {
      loot.setLife(0);
      c.add(i);
    }
    return canAdd;
  }

  private boolean shouldTrade(SolItem i) {
    if (myTradeContainer == null) return false;
    if (i instanceof RepairItem) {
      return myItemContainer.count(RepairItem.EXAMPLE) >= TRADE_AFTER;
    }
    GunItem g1 = myHull.getGunMount(false).getGun();
    if (g1 != null && g1.config.infiniteClipSize == 0 && g1.config.clipConf.example.isSame(i)) {
      return myItemContainer.count(g1.config.clipConf.example) >= TRADE_AFTER;
    }
    GunItem g2 = myHull.getGunMount(true).getGun();
    if (g2 != null && g2.config.infiniteClipSize == 0 && g2.config.clipConf.example.isSame(i)) {
      return myItemContainer.count(g2.config.clipConf.example) >= TRADE_AFTER;
    }
    return true;
  }

  public Vector2 getSpd() {
    return myHull.getSpd();
  }

  public float getAngle() {
    return myHull.getAngle();
  }

  public float getAcc() {
    EngineItem e = myHull.getEngine();
    return e == null ? 0 : e.getAac();
  }

  @Override
  public void update(SolGame game) {
    SolShip nearestEnemy = game.getFractionMan().getNearestEnemy(game, this);
    myPilot.update(game, this, nearestEnemy);
    pullLoot(game);
    myHull.update(game, myItemContainer, myPilot, this, nearestEnemy);

    updateAbility(game);
    updateIdleTime(game);
    updateShield(game);
    if (myArmor != null && !myItemContainer.contains(myArmor)) myArmor = null;
    game.getTradeMan().manage(game, myTradeContainer, myHull.config);

    if (isControlsEnabled() && myRepairer != null && myIdleTime > ShipRepairer.REPAIR_AWAIT) {
      myHull.life += myRepairer.tryRepair(game, myItemContainer, myHull.life, myHull.config);
    }

    float ts = game.getTimeStep();
    if (myFireAwait > 0) myFireAwait -= ts;
    mySmokeSrc.setWorking(myFireAwait > 0 || myHull.life < SMOKE_PERC * myHull.config.maxLife);
    myFireSrc.setWorking(myFireAwait > 0 || myHull.life < FIRE_PERC * myHull.config.maxLife);

    if (!isControlsEnabled()) myControlEnableAwait -= ts;

    if (myAbility instanceof Teleport) {
      ((Teleport) myAbility).maybeTeleport(game, this);
    }
  }

  private void updateAbility(SolGame game) {
    if (myAbility == null) return;
    if (myAbilityAwait > 0) {
      myAbilityAwait -= game.getTimeStep();
    }
    boolean tryToUse = isControlsEnabled() && myPilot.isAbility() && canUseAbility();
    boolean used = myAbility.update(game, this, tryToUse);
    if (used) {
      SolItem example = myAbility.getChargeExample();
      if (example != null) myItemContainer.tryConsumeItem(example);
      myAbilityAwait = myAbility.getRechargeTime();
    }
  }

  private void updateShield(SolGame game) {
    if (myShield != null) {
      if (myItemContainer.contains(myShield)) {
        myShield.update(game, this);
      } else {
        myShield = null;
      }
    }
  }

  private void updateIdleTime(SolGame game) {
    float ts = game.getTimeStep();
    if (Pilot.Utils.isIdle(myPilot)) {
      myIdleTime += ts;
    } else {
      myIdleTime = 0;
    }
  }

  public boolean canUseAbility() {
    if (myAbility == null || myAbilityAwait > 0) return false;
    SolItem example = myAbility.getChargeExample();
    if (example == null) return true;
    return myItemContainer.count(example) > 0;
  }

  private void pullLoot(SolGame game) {
    if (!myPilot.collectsItems() || !myItemContainer.canAdd()) return;
    Vector2 pos = getPos();
    for (SolObj obj : game.getObjMan().getObjs()) {
      if (!(obj instanceof Loot)) continue;
      ((Loot) obj).maybePulled(this, pos, PULL_DIST + myHull.config.approxRadius);
    }
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return myHull.life <= 0 || myRemoveController != null && myRemoveController.shouldRemove(myHull.getPos());
  }

  @Override
  public void onRemove(SolGame game) {
    if (myHull.life <= 0) {
      game.getShardBuilder().buildExplosionShards(game, myHull.getPos(), myHull.getSpd(), myHull.config.size);
      throwAllLoot(game);
    }
    myHull.onRemove(game);
    game.getPartMan().finish(game, mySmokeSrc, myHull.getPos());
    game.getPartMan().finish(game, myFireSrc, myHull.getPos());
  }

  private void throwAllLoot(SolGame game) {
    for (SolItem item : myItemContainer) {
      float dropChance = maybeUnequip(game, item, false) ? .2f : .8f;
      if (SolMath.test(1 - dropChance)) continue;
      throwLoot(game, item, true);
    }
    if (myTradeContainer != null) for (SolItem item : myTradeContainer) {
      float dropChance = .8f;
      if (SolMath.test(1 - dropChance)) continue;
      throwLoot(game, item, true);
    }
    float thrMoney = myMoney * SolMath.rnd(.3f, .7f);
    while (thrMoney > MoneyItem.AMT) {
      MoneyItem example;
      if (thrMoney > MoneyItem.BIG_AMT) {
        example = MoneyItem.BIG_EXAMPLE;
        thrMoney -= MoneyItem.BIG_AMT;
      } else {
        example = MoneyItem.EXAMPLE;
        thrMoney -= MoneyItem.AMT;
      }
      throwLoot(game, example.copy(), true);
    }
  }

  private void throwLoot(SolGame game, SolItem item, boolean onDeath) {
    Vector2 lootSpd = new Vector2();
    float spdAngle;
    float spdLen;
    Vector2 pos = new Vector2();
    if (onDeath) {
      spdAngle = SolMath.rnd(180);
      spdLen = SolMath.rnd(0, Loot.MAX_SPD);
    } else {
      spdAngle = getAngle();
      spdLen = Loot.MAX_SPD * 1.5f;
      SolMath.fromAl(pos, spdAngle, myHull.config.approxRadius);
    }
    SolMath.fromAl(lootSpd, spdAngle, spdLen);
    lootSpd.add(myHull.getSpd());
    pos.add(myHull.getPos());
    Loot l = game.getLootBuilder().build(game, pos, item, lootSpd, Loot.MAX_LIFE, SolMath.rnd(Loot.MAX_ROT_SPD), this);
    game.getObjMan().addObjDelayed(l);
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
    if (myShield != null) {
      dmg = myShield.absorb(game, dmg, pos, this, dmgType);
    }
    if (dmg <= 0) return;
    if (myArmor != null) {
      dmg *= (1 - myArmor.getPerc());
    }
    playDmgSound(game, pos, dmgType);

    boolean wasAlive = myHull.life > 0;
    myHull.life -= dmg;
    if (wasAlive && myHull.life <= 0) {
      Vector2 shipPos = getPos();
      game.getSpecialEffects().explodeShip(game, shipPos, myHull.config.size);
      game.getSoundMan().play(game, game.getSpecialSounds().shipExplosion, null, this);
    }
    if (dmgType == DmgType.FIRE) myFireAwait = MAX_FIRE_AWAIT;
  }

  private void playDmgSound(SolGame game, Vector2 pos, DmgType dmgType) {
    if (myArmor != null) {
      SolSound sound = myArmor.getDmgSound(dmgType);
      game.getSoundMan().play(game, sound, null, this);
    } else {
      game.getSpecialSounds().playDmg(game, this, pos, dmgType);
    }
  }

  @Override
  public boolean receivesGravity() {
    return true;
  }

  @Override
  public void receiveForce(Vector2 force, SolGame game, boolean acc) {
    Body body = myHull.getBody();
    if (acc) force.scl(body.getMass());
    body.applyForceToCenter(force, true);
  }

  public ItemContainer getItemContainer() {
    return myItemContainer;
  }

  public GunItem getGun(boolean gun2) {
    GunMount m = myHull.getGunMount(gun2);
    return m.getGun();
  }

  public float getLife() {
    return myHull.life;
  }

  public Pilot getPilot() {
    return myPilot;
  }

  public float getRotSpd() { return myHull.getRotSpd(); }

  public float getRotAcc() {
    EngineItem e = myHull.getEngine();
    return e == null ? 0 : e.getRotAcc();
  }

  public ShipHull getHull() {
    return myHull;
  }

  public float calcTimeToTurn(float destAngle) {
    float angle = myHull.getAngle();
    EngineItem e = myHull.getEngine();
    float ad = SolMath.angleDiff(angle, destAngle);
    return ad/e.getMaxRotSpd();
  }

  public boolean maybeEquip(SolGame game, SolItem item, boolean equip) {
    return maybeEquip(game, item, false, equip) || maybeEquip(game, item, true, equip);
  }

  public boolean maybeEquip(SolGame game, SolItem item, boolean secondarySlot, boolean equip) {
    if (!secondarySlot) {
      if (item instanceof EngineItem) {
        if (true) throw new AssertionError("no engine item support for now");
        EngineItem ei = (EngineItem) item;
        boolean ok = ei.isBig() == (myHull.config.type == HullConfig.Type.BIG);
        if (ok && equip) myHull.setEngine(game, this, ei);
        return ok;
      }
      if (item instanceof Shield) {
        Shield shield = (Shield) item;
        if (equip) myShield = shield;
        return true;
      }
      if (item instanceof Armor) {
        Armor armor = (Armor) item;
        if (equip) myArmor = armor;
        return true;
      }
    }
    if (item instanceof GunItem) {
      if (equip) {
        if (myHull.getGunMount(!secondarySlot).getGun() == item) myHull.getGunMount(!secondarySlot).setGun(game, this, null);
        myHull.getGunMount(secondarySlot).setGun(game, this, (GunItem) item);
      }
      return true;
    }
    return false;
  }

  public boolean maybeUnequip(SolGame game, SolItem item, boolean unequip) {
    return maybeUnequip(game, item, false, unequip) || maybeUnequip(game, item, true, unequip);
  }

  public boolean maybeUnequip(SolGame game, SolItem item, boolean secondarySlot, boolean unequip) {
    if (!secondarySlot) {
      if (myHull.getEngine() == item) {
        if (true) throw new AssertionError("engine items not supported");
        if (unequip) myHull.setEngine(game, this, null);
        return true;
      }
      if (myShield == item) {
        if (unequip) myShield = null;
        return true;
      }
      if (myArmor == item) {
        if (unequip) myArmor = null;
        return true;
      }
    }
    GunMount m = myHull.getGunMount(secondarySlot);
    if (m.getGun() == item) {
      if (unequip) m.setGun(game, this, null);
      return true;
    }
    return false;
  }

  public float getRepairPoints() {
    return myRepairer == null ? 0 : myRepairer.getRepairPoints();
  }

  public float getMoney() {
    return myMoney;
  }

  public ItemContainer getTradeContainer() {
    return myTradeContainer;
  }

  public void setMoney(float money) {
    myMoney = money;
  }

  public Shield getShield() {
    return myShield;
  }

  public Armor getArmor() {
    return myArmor;
  }

  public ShipAbility getAbility() {
    return myAbility;
  }

  public void disableControls(float duration) {
    myControlEnableAwait = duration;
  }

  public boolean isControlsEnabled() {
    return myControlEnableAwait <= 0;
  }

  public void dropItem(SolGame game, SolItem item) {
    myItemContainer.remove(item);
    throwLoot(game, item, false);
  }
}
