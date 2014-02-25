package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.Nullable;
import com.miloshpetrov.sol2.game.*;

import java.util.*;

public class DebugHintDrawer {
  private final Map<SolObj, DebugHint> myTracedNotes;
  private final List<DebugHint> myFreeNotes;

  public DebugHintDrawer() {
    myTracedNotes = new HashMap<SolObj, DebugHint>();
    myFreeNotes = new ArrayList<DebugHint>();
  }

  public void add(@Nullable SolObj owner, Vector2 pos, String value) {
    DebugHint note = myTracedNotes.get(owner);
    if (note == null) {
      note = new DebugHint(owner, pos);
      if (owner != null) myTracedNotes.put(owner, note);
      else myFreeNotes.add(note);
    }
    note.add(value);
  }

  public void update(SolGame game) {
    updateEach(game, myTracedNotes.values().iterator());
    updateEach(game, myFreeNotes.iterator());
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
    for (DebugHint n : myFreeNotes) n.draw(drawer, game);
  }

}
