package com.miloshpetrov.sol2.ui;

import com.miloshpetrov.sol2.game.screens.RightPaneLayout;
import com.miloshpetrov.sol2.menu.MenuLayout;

public class SolLayouts {
  public final RightPaneLayout rightPaneLayout;
  public final MenuLayout menuLayout;

  public SolLayouts(float r) {
    rightPaneLayout = new RightPaneLayout(r);
    menuLayout = new MenuLayout(r);
  }
}
