package com.miloshpetrov.sol2.ui;

import com.badlogic.gdx.utils.TimeUtils;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.DebugAspects;
import com.miloshpetrov.sol2.game.screens.BorderDrawer;

import java.util.*;

public class DebugCollector {
  private static final long WARN_TIME = 6000;
  private static final StringBuilder myDebugStrings = new StringBuilder();
  private static final Map<String, Long> myWarnings = new HashMap<String, Long>();

  private DebugCollector() {
  }

  public static void draw(UiDrawer drawer) {
    drawer.drawString(myDebugStrings.toString(), .5f, BorderDrawer.TISHCH_SZ, FontSize.DEBUG, false, Col.W);
  }

  public static void debug(Object ... objs) {
    if (!DebugAspects.VALS) return;
    for (Object o : objs) {
      myDebugStrings.append(String.valueOf(o)).append(" ");
    }
    myDebugStrings.append("\n");
  }

  public static void warn(Object ... objs) {
    if (!DebugAspects.WARNINGS) return;
    StringBuilder sb = new StringBuilder("WARNING: ");
    for (Object o : objs) {
      sb.append(String.valueOf(o)).append(" ");
    }
    myWarnings.put(sb.toString(), TimeUtils.millis() + WARN_TIME);
  }

  public static void update() {
    myDebugStrings.setLength(0);

    Iterator<Map.Entry<String, Long>> it = myWarnings.entrySet().iterator();
    long now = TimeUtils.millis();
    while (it.hasNext()) {
      Map.Entry<String, Long> e = it.next();
      if (e.getValue() < now) {
        it.remove();
        continue;
      }
      myDebugStrings.append(e.getKey()).append("\n");
    }

  }

}
