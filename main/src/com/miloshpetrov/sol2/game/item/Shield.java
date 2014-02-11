package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.ShipHull;

public class Shield implements SolItem {
  public static final float SIZE_PERC = .7f;
  private final Config myConfig;
  private float myLife;
  private float myIdleTime;

  private Shield(Config config) {
    myConfig = config;
    myLife = myConfig.maxLife;
  }

  public void update(SolGame game) {
    float ts = game.getTimeStep();
    if (myIdleTime >= myConfig.myMaxIdleTime) {
      if (myLife < myConfig.maxLife) {
        float regen = myConfig.regenSpd * ts;
        myLife = SolMath.approach(myLife, myConfig.maxLife, regen);
      }
    } else {
      myIdleTime += ts;
    }
  }

  @Override
  public String getTexName() {
    return "shield";
  }

  @Override
  public String getDisplayName() {
    return myConfig.displayName;
  }

  @Override
  public float getPrice() {
    return myConfig.price;
  }

  @Override
  public String getDesc() {
    return myConfig.desc;
  }

  @Override
  public SolItem copy() {
    return new Shield(myConfig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof Shield && ((Shield) item).myConfig == myConfig;
  }

  public float getLife() {
    return myLife;
  }

  public float getMaxLife() {
    return myConfig.maxLife;
  }

  public float absorb(SolGame game, float dmg, Vector2 pos, ShipHull hull) {
    if (dmg <= 0) return 0;
    myIdleTime = 0f;
    if (myLife > 0) {
      game.getPartMan().shieldSpark(game, pos, hull);
    }
    if (myLife >= dmg) {
      myLife -= dmg;
      return 0;
    }
    dmg -= myLife;
    myLife = 0;
    return dmg;
  }
  
  public static class Configs {
    public final Config std;
    public final Config med;
    public final Config big;

    public Configs() {
      std = new Config(10, "Small Shield", 40, "Poorly rotects from projectiles");
      med = new Config(20, "Shield", 100, "Protects from projectiles");
      big = new Config(30, "Big Shield", 150, "Greatly protects from projectiles");
    }
  }

  public static class Config {
    public final String displayName;
    public final int price;
    public final String desc;
    public final Shield example;
    public float maxLife;
    public float myMaxIdleTime = 2;
    public float regenSpd;

    private Config(int maxLife, String displayName, int price, String desc) {
      this.maxLife = maxLife;
      this.displayName = displayName;
      this.price = price;
      this.desc = desc;
      regenSpd = this.maxLife / 3;
      example = new Shield(this);
    }

  }
}
