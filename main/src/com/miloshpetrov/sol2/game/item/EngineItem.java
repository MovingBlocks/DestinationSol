package com.miloshpetrov.sol2.game.item;

public class EngineItem implements SolItem {
  public static final String TEX_NAME = "engine";
  public final Config config;

  private EngineItem(Config config) {
    this.config = config;
  }

  public String getTexName() {
    return TEX_NAME;
  }

  @Override
  public String getDisplayName() {
    return config.big ? "Big Engine" : "Engine";
  }

  @Override
  public float getPrice() {
    return config.big ? 50 : 10;
  }

  @Override
  public String getDesc() {
    return config.big ? "Suitable for big ships only" : "A standard engine";
  }

  @Override
  public SolItem copy() {
    return new EngineItem(config);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof EngineItem && ((EngineItem) item).config == config;
  }

  public static class Configs {
    public final Config std;
    public final Config big;

    public Configs() {
      std = new Config(2f, 230f, 515f, false);
      big = new Config(2f, 40f, 100f, true);
    }
  }

  public static class Config {
    public final float rotAcc;
    public final float acc;
    public final float maxRotSpd;
    public final boolean big;
    public final EngineItem example;

    private Config(float acc, float maxRotSpd, float rotAcc, boolean big) {
      this.acc = acc;
      this.maxRotSpd = maxRotSpd;
      this.rotAcc = rotAcc;
      this.big = big;
      example = new EngineItem(this);
    }
  }
}
