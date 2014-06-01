package com.miloshpetrov.sol2.game.input;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.Fraction;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.item.EngineItem;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.planet.PlanetBind;
import com.miloshpetrov.sol2.game.ship.*;

public class AiPilot implements Pilot {

  public static final float MIN_IDLE_DIST = .8f;
  public static final float MAX_GROUND_BATTLE_SPD = .7f;
  public static final float MAX_BATTLE_SPD_BIG = 1f;
  public static final float MAX_BATTLE_SPD = 2f;
  public static final float MAX_BIND_AWAIT = .25f;

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
        boolean big = hullConfig.type == HullConfig.Type.BIG;
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
    float enemyApproxRad = nearestEnemy == null ? 0 : nearestEnemy.getHull().config.approxRadius;
    myShooter.update(ship, enemyPos, moverActive, canShoot, enemySpd, enemyApproxRad);
    if (hasEngine && !moverActive && !isShooterRotated()) {
      myMover.rotateOnIdle(ship, np, dest, shouldStopNearDest, maxIdleDist);
    }
  }

  private float getMaxIdleDist(HullConfig hullConfig) {
    float maxIdleDist = hullConfig.approxRadius;
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
    float maxIdleDist = .05f;
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
      if (myDestProvider.shouldStopNearDest() && toDestLen < maxIdleDist) {
        spd.set(myDestProvider.getDestSpd());
        desiredAngle = angle; // can be improved
      } else {
        desiredAngle = SolMath.angle(shipPos, dest);
        if (myDestProvider.shouldAvoidBigObjs()) {
          desiredAngle = myMover.getBigObjAvoider().avoid(game, shipPos, dest, desiredAngle);
        }
        float spdLen = myDestProvider.getDesiredSpdLen();
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
