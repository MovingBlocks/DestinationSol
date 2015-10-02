package org.destinationsol.game.item;

import java.util.List;

public class ItemConfig {
  public final List<SolItem> examples;
  public final int amt;
  public final float chance;

  public ItemConfig(List<SolItem> examples, int amt, float chance) {
    this.examples = examples;
    this.amt = amt;
    this.chance = chance;
  }
}
