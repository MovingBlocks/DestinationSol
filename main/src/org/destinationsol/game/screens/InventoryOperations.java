package org.destinationsol.game.screens;

import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.ui.SolUiScreen;

public interface InventoryOperations extends SolUiScreen {
  ItemContainer getItems(SolGame game);
  boolean isUsing(SolGame game, SolItem item);
  float getPriceMul();
  String getHeader();
}
