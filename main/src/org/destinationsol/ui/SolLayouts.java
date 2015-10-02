package org.destinationsol.ui;

import org.destinationsol.game.screens.RightPaneLayout;
import org.destinationsol.menu.MenuLayout;

public class SolLayouts {
  public final RightPaneLayout rightPaneLayout;
  public final MenuLayout menuLayout;

  public SolLayouts(float r) {
    rightPaneLayout = new RightPaneLayout(r);
    menuLayout = new MenuLayout(r);
  }
}
