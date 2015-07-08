package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.Fraction;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.gun.GunMount;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.planet.PlanetBind;
import com.miloshpetrov.sol2.game.ship.*;

public class AiPilot implements Pilot {

  public static final float MIN_IDLE_DIST = .8f;
  public static final float MAX_GROUND_BATTLE_SPD = .7f;
  public static final float MAX_BATTLE_SPD_BIG = 1f;
  public static final float MAX_BATTLE_SPD = 2f;
  public static final float MAX_BIND_AWAIT = .25f;
  public static final float MAX_RE_EQUIP_AWAIT = 3f;

  private final MoveDestProvider myDestProvider;
  private final boolean myCollectsItems;
  private final Mover myMover;
  private final Shooter myShooter;
  private final Fraction myFraction;
  private final boolean myShootAtObstacles;
  private final String myMapHint;
  private final BattleDestProvider myBattleDestProvider;
  private final float myDetectionDist;
  private final AbilityUpdater myAbilityUpdater;

  private float myBindAwait;
  private PlanetBind myPlanetBind;
  private float myReEquipAwait;

  public AiPilot(MoveDestProvider destProvider, boolean collectsItems, Fraction fraction,
    boolean shootAtObstacles, String mapHint, float detectionDist)
  {
    myDestProvider = destProvider;
    myDetectionDist = detectionDist;
    myMover = new Mover();
    myShooter = new Shooter();
    myBattleDestProvider = new BattleDestProvider();
    myCollectsItems = collectsItems;
    myFraction = fraction;
    myShootAtObstacles = shootAtObstacles;
    myMapHint = mapHint;
    myAbilityUpdater = new AbilityUpdater();
  }

  @Override
  public void update(SolGame game, SolShip ship, SolShip nearestEnemy) {
    myAbilityUpdater.update(ship, nearestEnemy);
    myPlanetBind = null;
    Vector2 shipPos = ship.getPos();
    HullConfig hullConfig = ship.getHull().config;
    float maxIdleDist = getMaxIdleDist(hullConfig);
    myDestProvider.update(game, shipPos, maxIdleDist, hullConfig, nearestEnemy);

    Boolean canShoot = canShoot0(ship);
    boolean canShootUnfixed = canShoot == null;
    if (canShootUnfixed) canShoot = true;
    Planet np = game.getPlanetMan().getNearestPlanet();
    boolean nearGround = np.isNearGround(shipPos);

    Vector2 dest = null;
    Vector2 destSpd = null;
    boolean shouldStopNearDest = false;
    boolean avoidBigObjs = false;
    float desiredSpdLen = myDestProvider.getDesiredSpdLen();
    boolean hasEngine = ship.getHull().getEngine() != null;
    if (hasEngine) {
      Boolean battle = null;
      if (nearestEnemy != null) battle = myDestProvider.shouldManeuver(canShoot, nearestEnemy, nearGround);
      if (battle != null) {
        dest = myBattleDestProvider.getDest(ship, nearestEnemy, np, battle, game.getTimeStep(), canShootUnfixed, nearGround);
        shouldStopNearDest = myBattleDestProvider.shouldStopNearDest();
        destSpd = nearestEnemy.getSpd();
        boolean big = hullConfig.getType() == HullConfig.Type.BIG;
        float maxBattleSpd = nearGround ? MAX_GROUND_BATTLE_SPD : big ? MAX_BATTLE_SPD_BIG : MAX_BATTLE_SPD;
        if (maxBattleSpd < desiredSpdLen) desiredSpdLen = maxBattleSpd;
        if (!big) desiredSpdLen += destSpd.len();
      } else {
        dest = myDestProvider.getDest();
        destSpd = myDestProvider.getDestSpd();
        shouldStopNearDest = myDestProvider.shouldStopNearDest();
        avoidBigObjs = myDestProvider.shouldAvoidBigObjs();
      }
    }

    myMover.update(game, ship, dest, np, maxIdleDist, hasEngine, avoidBigObjs, desiredSpdLen, shouldStopNearDest, destSpd);
    boolean moverActive = myMover.isActive();

    Vector2 enemyPos = nearestEnemy == null ? null : nearestEnemy.getPos();
    Vector2 enemySpd = nearestEnemy == null ? null : nearestEnemy.getSpd();
    float enemyApproxRad = nearestEnemy == null ? 0 : nearestEnemy.getHull().config.getApproxRadius();
    myShooter.update(ship, enemyPos, moverActive, canShoot, enemySpd, enemyApproxRad);
    if (hasEngine && !moverActive && !isShooterRotated()) {
      myMover.rotateOnIdle(ship, np, dest, shouldStopNearDest, maxIdleDist);
    }

    if (myReEquipAwait <= 0) {
      reEquip(game, ship);
      myReEquipAwait = MAX_RE_EQUIP_AWAIT;
    } else {
      myReEquipAwait -= game.getTimeStep();
    }
  }

  public static void reEquip(SolGame game, SolShip ship) {
    ShipHull hull = ship.getHull();
    GunItem g1 = hull.getGun(false);
    GunItem g2 = hull.getGun(true);
    Shield s = ship.getShield();
    Armor a = ship.getArmor();
    GunMount m1 = hull.getGunMount(false);
    GunMount m2 = hull.getGunMount(true);
    ItemContainer ic = ship.getItemContainer();
    for (int idx = 0, sz = ic.groupCount(); idx < sz; idx++) {
      SolItem i = ic.getGroup(idx).get(0);
      if (i == g1 || i == g2 || i == s || i == a) continue;
      if (i instanceof GunItem) {
        GunItem gNew = (GunItem) i;
        if (gunIsBetter(gNew, g1, ic, m1)) {
          ship.maybeEquip(game, gNew, false, true);
          g1 = gNew;
        } else if (m2 != null && gunIsBetter(gNew, g2, ic, m2)) {
          ship.maybeEquip(game, gNew, true, true);
          g2 = gNew;
        }
      } else if (i instanceof Armor) {
        Armor aNew = (Armor) i;
        if (a == null || a.getPerc() < aNew.getPerc()) {
          ship.maybeEquip(game, aNew, true);
          a = aNew;
        }
      } else if (i instanceof Shield) {
        Shield sNew = (Shield) i;
        if (s == null || s.getLife() < sNew.getLife()) {
          ship.maybeEquip(game, sNew, true);
          s = sNew;
        }
      }
    }
  }

  private static boolean gunIsBetter(GunItem gNew, GunItem g, ItemContainer ic, GunMount m) {
    if (m.isFixed() != gNew.config.fixed) return false;
    ClipConfig newCc = gNew.config.clipConf;
    boolean newAmmoOk = newCc.infinite || ic.count(newCc.example) > 0;
    if (!newAmmoOk) return false;
    if (g == null) return true;
    ClipConfig cc = g.config.clipConf;
    boolean ammoOk = cc.infinite || ic.count(cc.example) > 0;
    if (!ammoOk) return true;
    return g.config.meanDps < gNew.config.meanDps;
  }

  private float getMaxIdleDist(HullConfig hullConfig) {
    float maxIdleDist = hullConfig.getApproxRadius();
    if (maxIdleDist < MIN_IDLE_DIST) maxIdleDist = MIN_IDLE_DIST;
    return maxIdleDist;
  }

  private Boolean canShoot0(SolShip ship) {
    GunItem g1 = ship.getHull().getGun(false);
    if (g1 != null && g1.canShoot()) return !g1.config.fixed ? null : true;
    GunItem g2 = ship.getHull().getGun(true);
    if (g2 != null && (g2.canShoot())) return !g2.config.fixed ? null : true;
    return false;
  }

  private boolean isShooterRotated() {
    return myShooter.isLeft() || myShooter.isRight();
  }

  @Override
  public boolean isUp() {
    return myMover.isUp();
  }

  @Override
  public boolean isLeft() {
    return myMover.isLeft() || myShooter.isLeft();
  }

  @Override
  public boolean isRight() {
    return myMover.isRight() || myShooter.isRight();
  }

  @Override
  public boolean isShoot() {
    return myShooter.isShoot();
  }

  @Override
  public boolean isShoot2() {
    return myShooter.isShoot2();
  }

  @Override
  public boolean collectsItems() {
    return myCollectsItems;
  }

  @Override
  public boolean isAbility() {
    return myAbilityUpdater.isAbility();
  }

  @Override
  public Fraction getFraction() {
    return myFraction;
  }

  @Override
  public boolean shootsAtObstacles() {
    return myShootAtObstacles;
  }

  @Override
  public float getDetectionDist() {
    return myDetectionDist;
  }

  @Override
  public String getMapHint() {
    return myMapHint;
  }

  @Override
  public void updateFar(SolGame game, FarShip farShip) {
    Vector2 shipPos = farShip.getPos();
    HullConfig hullConfig = farShip.getHullConfig();
    float maxIdleDist = getMaxIdleDist(hullConfig);
    myDestProvider.update(game, shipPos, maxIdleDist, hullConfig, null);
    Vector2 dest = myDestProvider.getDest();

    Vector2 spd = farShip.getSpd();
    float angle = farShip.getAngle();
    EngineItem engine = farShip.getEngine();
    float ts = game.getTimeStep();
    if (dest == null || engine == null) {
      if (myPlanetBind == null) {
        if (myBindAwait > 0) {
          myBindAwait -= ts;
        } else {
          myPlanetBind = PlanetBind.tryBind(game, shipPos, angle);
          myBindAwait = MAX_BIND_AWAIT;
        }
      }
      if (myPlanetBind != null) {
        myPlanetBind.setDiff(spd, shipPos, false);
        spd.scl(1/ ts);
        angle = myPlanetBind.getDesiredAngle();
      }
    } else {
      float toDestLen = shipPos.dst(dest);
      float desiredAngle;
      float maxIdleDistHack = .05f; // to avoid StillGuards from getting stuck inside ground
      if (myDestProvider.shouldStopNearDest() && toDestLen < maxIdleDistHack) {
        spd.set(myDestProvider.getDestSpd());
        desiredAngle = angle; // can be improved
      } else {
        desiredAngle = SolMath.angle(shipPos, dest);
        if (myDestProvider.shouldAvoidBigObjs()) {
          desiredAngle = myMover.getBigObjAvoider().avoid(game, shipPos, dest, desiredAngle);
        }
        float desiredSpdLen = myDestProvider.getDesiredSpdLen();
        float spdLenDiff = engine.getAcc() * ts;
        float spdLen = SolMath.approach(spd.len(), desiredSpdLen, spdLenDiff);
        if (toDestLen < spdLen) spdLen = toDestLen;
        SolMath.fromAl(spd, desiredAngle, spdLen);
      }
      angle = SolMath.approachAngle(angle, desiredAngle, engine.getMaxRotSpd() * ts);
    }

    farShip.setSpd(spd);
    farShip.setAngle(angle);

    Vector2 newPos = SolMath.getVec(spd);
    newPos.scl(ts);
    newPos.add(shipPos);
    farShip.setPos(newPos);
    SolMath.free(newPos);
  }

  @Override
  public String toDebugString() {
    return "moverActive: " + myMover.isActive();
  }

  @Override
  public boolean isPlayer() {
    return myDestProvider instanceof BeaconDestProvider;
  }

}
