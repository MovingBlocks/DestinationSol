package org.destinationsol;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

public class TextureChecker {
  private final ArrayList<String> myCollected = new ArrayList<String>();
  private Texture myCurr = null;
  private int myAwait;

  public void onString(Texture texture) {
    evt(texture, "text");
  }

  private void evt(Texture texture, String name) {
    if (texture == null || name == null || name.isEmpty()) throw new AssertionError("null texture or no texture name");
    if (myAwait > 0) return;
    if (texture.equals(myCurr)) return;
    myCollected.add(name);
    myCurr = texture;
  }

  public void onReg(TextureAtlas.AtlasRegion tr) {
    evt(tr.getTexture(), tr.name);
  }

  public void onSprite(Texture texture, TextureAtlas.AtlasRegion tex) {
    evt(texture, tex.name);
  }

  public void onEnd() {
    if (myAwait == 0) {
//      System.out.println("\n\n\n" + myCollected);
      myCollected.clear();
      myCurr = null;
      myAwait = 120;
      return;
    }
    if (myAwait > 0) {
      myAwait--;
    }
  }
}
