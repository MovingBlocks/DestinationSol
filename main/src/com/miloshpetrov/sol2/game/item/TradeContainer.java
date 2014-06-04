package com.miloshpetrov.sol2.game.item;

import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.ship.HullConfig;

import java.util.ArrayList;
import java.util.List;

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
    int groupsToLeave = Const.ITEM_GROUPS_PER_PAGE;
    if (hullConfig.type != HullConfig.Type.STATION) groupsToLeave /= 2;
    int excess = myItems.groupCount() - groupsToLeave;
    for (int i = 0; i < excess; i++) {
      List<SolItem> group = myItems.getGroup(SolMath.intRnd(myItems.groupCount()));
      ArrayList<SolItem> groupCopy = new ArrayList<SolItem>(group);
      for (SolItem it : groupCopy) {
        myItems.remove(it);
      }
    }
    List<ItemConfig> items = myConfig.items;
    for (int i1 = 0, sz = items.size(); i1 < sz; i1++) {
      ItemConfig i = items.get(i1);
      SolItem ex = i.examples.get(0);
      int amt = ex.isSame(ex) ? 8 : 1;
      for (int j = 0; j < amt; j++) {
        if (myItems.canAdd(ex)) myItems.add(ex.copy());
      }
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
