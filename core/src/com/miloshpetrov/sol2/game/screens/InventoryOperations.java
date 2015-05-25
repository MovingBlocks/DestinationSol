package com.miloshpetrov.sol2.game.screens;

import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.item.ItemContainer;
import com.miloshpetrov.sol2.game.item.SolItem;
import com.miloshpetrov.sol2.ui.SolUiScreen;

public interface InventoryOperations extends SolUiScreen {
  ItemContainer getItems(SolGame game);
  boolean isUsing(SolGame game, SolItem item);
  float getPriceMul();
  String getHeader();
}
