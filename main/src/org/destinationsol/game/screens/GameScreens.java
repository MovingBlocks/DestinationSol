package org.destinationsol.game.screens;

import org.destinationsol.SolApplication;
import org.destinationsol.ui.SolLayouts;

public class GameScreens {
  public final MainScreen mainScreen;
  public final MapScreen mapScreen;
  public final MenuScreen menuScreen;
  public final InventoryScreen inventoryScreen;
  public final TalkScreen talkScreen;

  public GameScreens(float r, SolApplication cmp) {
    SolLayouts layouts = cmp.getLayouts();
    RightPaneLayout rightPaneLayout = layouts.rightPaneLayout;
    mainScreen = new MainScreen(r, rightPaneLayout, cmp);
    mapScreen = new MapScreen(rightPaneLayout, cmp.isMobile(), r, cmp.getOptions());
    menuScreen = new MenuScreen(layouts.menuLayout, cmp.getOptions());
    inventoryScreen = new InventoryScreen(r, cmp.getOptions());
    talkScreen = new TalkScreen(layouts.menuLayout, cmp.getOptions());
  }

}
