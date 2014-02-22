package com.miloshpetrov.sol2.game.item;

public class ClipConfig {
  public final String iconName;
  public final int price;
  public final String displayName;
  public final String desc;
  public final int size;
  public final ClipItem example;

  public ClipConfig(String iconName, int price, String displayName, int size, String descSuff) {
    this.iconName = iconName;
    this.price = price;
    this.displayName = displayName;
    this.size = size;
    this.desc = "A clip of " + size + " " + descSuff;
    this.example = new ClipItem(this);
  }

  public static void load(ItemMan itemMan) {
    //load here
    itemMan.registerItem("b", new ClipConfig("bulletClip", 30, "Bullets", 60, "bullets").example);
    itemMan.registerItem("r", new ClipConfig("rocketClip", 70, "Rockets", 6, "rockets").example);
  }
}
