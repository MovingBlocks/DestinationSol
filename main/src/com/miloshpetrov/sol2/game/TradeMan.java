package com.miloshpetrov.sol2.game;

import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.item.ItemMan;
import com.miloshpetrov.sol2.game.ship.HullConfig;

public class TradeMan {
  private static final float MAX_AWAIT = 60f;
  private float myAwait;

  public void update(SolGame game) {
    myAwait += game.getTimeStep();
    if (MAX_AWAIT < myAwait) myAwait = 0;
  }

  public void manage(SolGame game, ItemContainer c, HullConfig hullConfig) {
    if (c == null) return;
    if (myAwait != 0) return;
    int amt = Const.ITEMS_PER_PAGE;
    if (hullConfig.type != HullConfig.Type.STATION) amt /= 2;
    int excess = c.size() - amt;
    for (int i = 0; i < excess; i++) {
      c.remove(c.get(SolMath.intRnd(c.size())));
    }
    ItemMan itemMan = game.getItemMan();
    for (int i = 0; i < amt; i++) {
      c.add(itemMan.random());
    }
  }
}
