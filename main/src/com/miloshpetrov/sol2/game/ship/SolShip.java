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
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.sound.SolSound;

import java.util.List;

public class SolShip implements SolObj {
  public static final float BASE_DUR_MOD = .3f;
  public static final float PULL_DIST = 2f;
  public static final float SMOKE_PERC = .5f;
  private static final float SLO_MO_CHG_SPD = .03f;
  private static final int TRADE_AFTER = 3;

  private final Pilot myPilot;
  private final ItemContainer myItemContainer;
  private final ItemContainer myTradeContainer;
  private final ShipHull myHull;
  private final ParticleSrc mySmokeSrc;
  private final RemoveController myRemoveController;
  private final List<Dra> myDras;
  private final float myRadius;
  private final ShipRepairer myRepairer;
  private Shield myShield;
  private float myMoney;

  private float mySloMoFactor;
  private float myIdleTime;
  private Armor myArmor;


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
    mySmokeSrc = game.getPartMan().buildSmokeSrc(game, new Vector2());
    myDras.add(mySmokeSrc);
    myRadius = DraMan.radiusFromDras(myDras);
    mySloMoFactor = 1f;
    myRepairer = repairer;
    myMoney = money;
    myShield = shield;
    myArmor = armor;
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
      receiveDmg((int) dmg, game, null, DmgType.CRASH);
    }
  }

  @Override
  public String toDebugString() {
    String r;
    if (myShield == null) r = "no shield\n"; else r = myShield.getLife() + "\n";
    r += myHull.getShieldFixture().getFilterData().categoryBits;
    return r;
  }

  @Override
  public Boolean isMetal() {
    return true;
  }

  private boolean tryCollectLoot(SolObj obj) {
    //todo: play pickup sound
    if (!(obj instanceof Loot)) return false;
    if (!myPilot.collectsItems()) return false;
    Loot loot = (Loot) obj;
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
    pullDroppedItems(game);
    myHull.update(game, myItemContainer, myPilot, this, nearestEnemy);
    updateSmokeSrc(game);

    updateSloMo(game);
    updateIdleTime(game);
    updateShield(game);
    if (myArmor != null && !myItemContainer.contains(myArmor)) myArmor = null;
    game.getTradeMan().manage(game, myTradeContainer, myHull.config);

    if (myRepairer != null && myIdleTime > ShipRepairer.REPAIR_AWAIT) {
      myHull.life += myRepairer.tryRepair(game, myItemContainer, myHull.life, myHull.config);
    }

    mySmokeSrc.setWorking(myHull.life < SMOKE_PERC * myHull.config.maxLife);
  }

  private void updateShield(SolGame game) {
    if (myShield != null) {
      if (myItemContainer.contains(myShield)) {
        myShield.update(game, this);
      } else {
        myShield = null;
      }
    }
    boolean active = myShield != null && myShield.getLife() > 0;
    Fixture sf = myHull.getShieldFixture();
    boolean wasActive = sf.getFilterData().categoryBits != 0;
    if (active != wasActive) {
      Filter f = new Filter();
      f.categoryBits = active ? (short)1 : 0;
      sf.setFilterData(f);
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

  private void updateSloMo(SolGame game) {
    float ts = game.getTimeStep();
    if (myPilot.isSpec() && canUseSpec()) {
      myItemContainer.tryConsumeItem(SloMoCharge.EXAMPLE);
      mySloMoFactor = .4f;
    } else {
      mySloMoFactor = SolMath.approach(mySloMoFactor, 1, SLO_MO_CHG_SPD * ts);
    }
  }

  public boolean canUseSpec() {
    return mySloMoFactor == 1f && myItemContainer != null && myItemContainer.count(SloMoCharge.EXAMPLE) > 0;
  }

  private void updateSmokeSrc(SolGame game) {
    Planet np = game.getPlanetMan().getNearestPlanet();
    if (np != null) {
      Vector2 smokeSpd = np.getSmokeSpd(myHull.getPos());
      if (smokeSpd != null) {
        mySmokeSrc.setSpd(smokeSpd);
        SolMath.free(smokeSpd);
        return;
      }
    }
    mySmokeSrc.setSpd(myHull.getSpd());
  }

  private void pullDroppedItems(SolGame game) {
    if (!myPilot.collectsItems() || !myItemContainer.canAdd()) return;
    Vector2 pos = getPos();
    for (SolObj obj : game.getObjMan().getObjs()) {
      if (!(obj instanceof Loot)) continue;
      ((Loot) obj).maybePulled(pos, PULL_DIST + myHull.config.size/2);
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
      throwLoot(game);
    }
    myHull.onRemove(game);
    game.getPartMan().finish(game, mySmokeSrc, myHull.getPos());
  }

  private void throwLoot(SolGame game) {
    for (SolItem item : myItemContainer) {
      float dropChance = maybeUnequip(game, item, false) ? .2f : .8f;
      if (SolMath.test(1 - dropChance)) continue;
      throwLoot0(game, item);
    }
    if (myTradeContainer != null) for (SolItem item : myTradeContainer) {
      float dropChance = .8f;
      if (SolMath.test(1 - dropChance)) continue;
      throwLoot0(game, item);
    }
    for (int i = 0; i < myMoney; i += MoneyItem.AMT) {
      if (SolMath.test(.5f)) continue;
      throwLoot0(game, MoneyItem.EXAMPLE.copy());
    }
  }

  private void throwLoot0(SolGame game, SolItem item) {
    Vector2 lootSpd = new Vector2();
    SolMath.fromAl(lootSpd, SolMath.rnd(180), SolMath.rnd(0, Loot.MAX_SPD));
    lootSpd.add(myHull.getSpd());
    Loot l = game.getLootBuilder().build(game, myHull.getPos(), item, lootSpd, Loot.MAX_LIFE, SolMath.rnd(Loot.MAX_ROT_SPD));
    game.getObjMan().addObjDelayed(l);
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
    if (pos != null && myShield != null) {
      dmg = myShield.absorb(game, dmg, pos, this);
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
      game.getPartMan().explode(shipPos, game, true);
      game.getPartMan().explode(shipPos, game, true);
      game.getSoundMan().play(game, game.getSpecialSounds().shipExplosion, null, this);
    }
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
  public void receiveAcc(Vector2 acc, SolGame game) {
    Body body = myHull.getBody();
    acc.scl(body.getMass());
    body.applyForceToCenter(acc, true);
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

  public float getSloMoFactor() {
    return mySloMoFactor;
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
}
