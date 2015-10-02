package org.destinationsol.game.dra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.DevTextureProvider;
import org.destinationsol.common.DebugCol;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.UiDrawer;

import java.util.HashSet;
import java.util.Set;

public class DraDebugger {
  public static final float TEX_SZ = .1f;
  public static final float GAP = .01f;
  private final Set<TextureAtlas.AtlasRegion> myCollector;

  public DraDebugger() {
    myCollector = new HashSet<TextureAtlas.AtlasRegion>();
  }

  public void update(SolGame game) {
    if (!DebugOptions.TEX_INFO) return;
    maybeCollectTexs(game);
  }

  private void maybeCollectTexs(SolGame game) {
    if (!Gdx.input.isTouched()) return;
    myCollector.clear();
    Vector2 cursorPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
    game.getCam().screenToWorld(cursorPos);
    game.getDraMan().collectTexs(myCollector, cursorPos);
  }

  public void draw(UiDrawer uiDrawer, SolGame game) {
    if (!DebugOptions.TEX_INFO) return;
    float y = GAP;
    for (TextureAtlas.AtlasRegion tex : myCollector) {
      float x = GAP;
      uiDrawer.draw(uiDrawer.whiteTex, 5 * TEX_SZ, TEX_SZ + 2 * GAP, 0, 0, x, y, 0, SolColor.DG);
      y += GAP;
      x += GAP;
      float r = 1f * tex.getTexture().getWidth() / tex.getTexture().getHeight();
      float w = r > 1 ? TEX_SZ : TEX_SZ/r;
      float h = r > 1 ? TEX_SZ/r : TEX_SZ;
      uiDrawer.draw(tex, w, h, w/2, h/2, x + .5f * TEX_SZ, y + .5f * TEX_SZ, 0, SolColor.W);
      x += TEX_SZ + GAP;
      uiDrawer.drawString(tex.name, x, y, FontSize.DEBUG, false, DebugCol.TEX_INFO);
      y += .5f * TEX_SZ;
      String definedBy = ((DevTextureProvider.SolTex) tex).definedBy;
      uiDrawer.drawString(definedBy, x, y, FontSize.DEBUG, false, DebugCol.TEX_INFO);
      y += .5f * TEX_SZ + 2 * GAP;
    }
  }
}
