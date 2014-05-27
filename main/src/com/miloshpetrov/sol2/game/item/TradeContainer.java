package com.miloshpetrov.sol2.game.item;

import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.HullConfig;

public class TradeContainer {
  private static final float MAX_AWAIT = 60f;

  private final TradeConfig myConfig;
  private final ItemContainer myItems;

  private float myAwait;

  public TradeContainer(TradeConfig config) {
    myConfig = config;
    myItems = new ItemContainer();
  }

  public void update(SolGame game, HullConfig hullConfig) {
    if (0 < myAwait) {
      myAwait -= game.getTimeStep();
      return;
    }

    myAwait = MAX_AWAIT;
    int amt = 8;
    if (hullConfig.type != HullConfig.Type.STATION) amt /= 2;
    int excess = myItems.size() - amt;
    for (int i = 0; i < excess; i++) {
      myItems.remove(myItems.getGroup(SolMath.intRnd(myItems.size())).get(0));
    }
    for (int i = 0; i < amt; i++) {
      SolItem itemOrig = myConfig.items.getRandom();
      if (itemOrig != null) myItems.add(itemOrig.copy());
    }
  }

  public ItemContainer getItems() {
    return myItems;
  }

  public ItemContainer getShips() {
    return myConfig.hulls;
  }

  public ItemContainer getMercs() {
    return myConfig.mercs;
  }
}
