package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.item.SolItem;
import com.miloshpetrov.sol2.game.planet.Planet;

public class Teleport implements ShipAbility {
  public static final int MAX_RADIUS = 4;
  public static final String TEX_PATH = "smallGameObjs/teleportBlip";
  private final Vector2 myNewPos;
  private final Config myConfig;
  private boolean myShouldTeleport;
  private float myAngle;

  public Teleport(Config config) {
    myConfig = config;
    myNewPos = new Vector2();
  }

  @Override
  public boolean update(SolGame game, SolShip owner, boolean tryToUse) {
    myShouldTeleport = false;
    if (!tryToUse) return false;
    Vector2 pos = owner.getPos();
    Fraction frac = owner.getPilot().getFraction();
    SolShip ne = game.getFractionMan().getNearestEnemy(game, MAX_RADIUS, frac, pos);
    if (ne == null) return false;
    Vector2 nePos = ne.getPos();
    Planet np = game.getPlanetMan().getNearestPlanet();
    if (np.isNearGround(nePos)) return false;
    for (int i = 0; i < 5; i++) {
      myNewPos.set(pos);
      myNewPos.sub(nePos);
      myAngle = myConfig.angle * SolMath.rnd(.5f, 1) * SolMath.toInt(SolMath.test(.5f));
      SolMath.rotate(myNewPos, myAngle);
      myNewPos.add(nePos);
      if (game.isPlaceEmpty(myNewPos, false)) {
        myShouldTeleport = true;
        return true;
      }
    }
    return false;
  }

  @Override
  public AbilityConfig getConfig() {
    return myConfig;
  }

  @Override
  public AbilityCommonConfig getCommonConfig() {
    return myConfig.cc;
  }

  @Override
  public float getRadius() {
    return MAX_RADIUS;
  }

  // can be performed in update
  public void maybeTeleport(SolGame game, SolShip owner) {
    if (!myShouldTeleport) return;

    TextureAtlas.AtlasRegion tex = game.getTexMan().getTex(TEX_PATH, null);
    float blipSz = owner.getHull().config.getApproxRadius() * 3;
    game.getPartMan().blip(game, owner.getPos(), SolMath.rnd(180), blipSz, 1, Vector2.Zero, tex);
    game.getPartMan().blip(game, myNewPos, SolMath.rnd(180), blipSz, 1, Vector2.Zero, tex);

    float newAngle = owner.getAngle() + myAngle;
    Vector2 newSpd = SolMath.getVec(owner.getSpd());
    SolMath.rotate(newSpd, myAngle);

    Body body = owner.getHull().getBody();
    body.setTransform(myNewPos, newAngle * SolMath.degRad);
    body.setLinearVelocity(newSpd);

    SolMath.free(newSpd);
  }

  public static class Config implements AbilityConfig {
    private final float angle;
    private final SolItem chargeExample;
    private final float rechargeTime;
    private final AbilityCommonConfig cc;

    public Config(float angle, SolItem chargeExample, float rechargeTime, AbilityCommonConfig cc) {
      this.angle = angle;
      this.chargeExample = chargeExample;
      this.rechargeTime = rechargeTime;
      this.cc = cc;
    }

    public ShipAbility build() {
      return new Teleport(this);
    }

    @Override
    public SolItem getChargeExample() {
      return chargeExample;
    }

    @Override
    public float getRechargeTime() {
      return rechargeTime;
    }

    @Override
    public void appendDesc(StringBuilder sb) {
      sb.append("Teleport around enemy");
    }

    public static AbilityConfig load(JsonValue abNode, ItemMan itemMan, AbilityCommonConfig cc) {
      float angle = abNode.getFloat("angle");
      SolItem chargeExample = itemMan.getExample("teleportCharge");
      float rechargeTime = abNode.getFloat("rechargeTime");
      return new Config(angle, chargeExample, rechargeTime, cc);
    }
  }
}
