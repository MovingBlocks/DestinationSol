package com.miloshpetrov.sol2.ui;

import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.DebugAspects;
import com.miloshpetrov.sol2.game.screens.BorderDrawer;

public class DebugCollector {
  private final StringBuilder myDebugStrings = new StringBuilder();

  public DebugCollector() {
  }

  public void draw(UiDrawer drawer) {
    drawer.drawString(myDebugStrings.toString(), .5f, BorderDrawer.TISHCH_SZ, FontSize.DEBUG, false, Col.W);
  }

  public void debug(Object ... objs) {
    if (DebugAspects.VALS) {
      for (Object o : objs) {
        myDebugStrings.append(String.valueOf(o)).append(" ");
      }
      myDebugStrings.append("\n");
    }
  }

  public void update() {
    myDebugStrings.setLength(0);
  }

}
