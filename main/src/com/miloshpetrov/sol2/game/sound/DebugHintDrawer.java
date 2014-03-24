package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.Nullable;
import com.miloshpetrov.sol2.game.*;

import java.util.*;

public class DebugHintDrawer {
  private final Map<SolObj, DebugHint> myTracedNotes;
  private final Map<Vector2, DebugHint> myFreeNotes;

  public DebugHintDrawer() {
    myTracedNotes = new HashMap<SolObj, DebugHint>();
    myFreeNotes = new HashMap<Vector2, DebugHint>();
  }

  public void add(@Nullable SolObj owner, Vector2 pos, String value) {
    DebugHint dh;
    if (owner == null) {
      dh = myFreeNotes.get(pos);
      if (dh == null) {
        dh = new DebugHint(null, pos);
        myFreeNotes.put(pos, dh);
      }
    } else {
      dh = myTracedNotes.get(owner);
      if (dh == null) {
        dh = new DebugHint(owner, pos);
        myTracedNotes.put(owner, dh);
      }
    }
    dh.add(value);
  }

  public void update(SolGame game) {
    updateEach(game, myTracedNotes.values().iterator());
    updateEach(game, myFreeNotes.values().iterator());
  }

  private void updateEach(SolGame game, Iterator<DebugHint> it) {
    while (it.hasNext()) {
      DebugHint n = it.next();
      n.update(game);
      if (n.shouldRemove()) it.remove();
    }
  }

  public void draw(Drawer drawer, SolGame game) {
    for (DebugHint n : myTracedNotes.values()) n.draw(drawer, game);
    for (DebugHint n : myFreeNotes.values()) n.draw(drawer, game);
  }

}
