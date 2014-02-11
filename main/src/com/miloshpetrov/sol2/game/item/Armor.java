package com.miloshpetrov.sol2.game.item;

public class Armor implements SolItem {
  private final Config myConfig;

  private Armor(Config config) {
    myConfig = config;
  }

  @Override
  public String getTexName() {
    return "armor";
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
    return new Armor(myConfig);
  }

  @Override
  public boolean isSame(SolItem item) {
    return item instanceof Armor && ((Armor) item).myConfig == myConfig;
  }

  public float getPerc() {
    return myConfig.perc;
  }

  public static class Configs {
    public final Config std;
    public final Config med;
    public final Config big;

    public Configs() {
      std = new Config(.1f, "Light Armor", 30);
      med = new Config(.2f, "Medium Armor", 60);
      big = new Config(.3f, "Heavy Armor", 100);
    }
  }

  public static class Config {
    public final String displayName;
    public final float price;
    public final float perc;
    public final String desc;
    public final Armor example;

    private Config(float perc, String displayName, float price) {
      this.displayName = displayName;
      this.price = price;
      this.perc = perc;
      desc = "Reduces all damage by " + (int)(perc * 100) + "%";
      example = new Armor(this);
    }
  }
}
