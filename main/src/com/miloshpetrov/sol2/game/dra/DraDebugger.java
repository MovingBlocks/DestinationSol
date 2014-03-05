package com.miloshpetrov.sol2.game.dra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.ui.DebugCollector;

import java.util.HashSet;
import java.util.Set;

public class DraDebugger {
  private final Set<TextureAtlas.AtlasRegion> myCollector;

  public DraDebugger() {
    myCollector = new HashSet<TextureAtlas.AtlasRegion>();
  }

  public void update(SolGame game) {
    maybeCollectTexs(game);

  }

  private void maybeCollectTexs(SolGame game) {
    if (!Gdx.input.isTouched()) return;
    myCollector.clear();
    Vector2 cursorPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
    game.getCam().screenToWorld(cursorPos);
    game.getDraMan().collectTexs(myCollector, cursorPos);

    for (TextureAtlas.AtlasRegion tex : myCollector) {
      DebugCollector.warn(tex.name);
    }
  }
}
