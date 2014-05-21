package com.miloshpetrov.sol2.game;

import com.miloshpetrov.sol2.ui.DebugCollector;

public enum MissingResourceAction {
  IGNORE("ignore"), WARN("warn"), FAIL("fail");
  public final String name;

  MissingResourceAction(String name) {
    this.name = name;
  }

  public static MissingResourceAction forName(String name) {
    for (MissingResourceAction dt : MissingResourceAction.values()) {
      if (dt.name.equals(name)) return dt;
    }
    throw new AssertionError("no missing resource action for name " + name);
  }

  public void handle(String msg) {
    if (this == WARN) DebugCollector.warn(msg);
    if (this == FAIL) throw new AssertionError(msg);
  }
}
