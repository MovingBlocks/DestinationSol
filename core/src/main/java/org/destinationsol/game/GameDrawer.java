package org.destinationsol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.CommonDrawer;
import org.destinationsol.TextureManager;

public class GameDrawer {

  public final float r;
  public final TextureAtlas.AtlasRegion debugWhiteTex;
  private final CommonDrawer myDrawer;

  private boolean myCurrAdditive;

  public GameDrawer(TextureManager textureManager, CommonDrawer commonDrawer) {
    myDrawer = commonDrawer;
    r = myDrawer.r;
    debugWhiteTex = textureManager.getTex("ui/whiteTex", null);
  }

  public void begin() {
    myDrawer.begin();
  }

  public void updateMtx(SolGame game) {
    myDrawer.setMtx(game.getCam().getMtx());
  }

  public void end() {
    myDrawer.end();
  }

  public void drawString(String s, float x, float y, float size, boolean centered, Color col) {
    maybeChangeAdditive(false);
    myDrawer.drawString(s, x, y, size, centered, col);
  }

  public void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y,
    float rot, Color tint)
  {
    maybeChangeAdditive(false);
    myDrawer.draw(tr, width, height, origX, origY, x, y, rot, tint);
  }

  public void drawAdditive(TextureRegion tr, float width, float height, float origX, float origY, float x, float y,
    float rot, Color tint)
  {
    maybeChangeAdditive(true);
    myDrawer.draw(tr, width, height, origX, origY, x, y, rot, tint);
  }

  public void drawLine(TextureRegion tex, float x, float y, float angle, float len, Color col, float width) {
    maybeChangeAdditive(false);
    myDrawer.drawLine(tex, x, y, angle, len, col, width);
  }

  public void drawLine(TextureRegion tex, Vector2 p1, Vector2 p2, Color col, float width, boolean precise) {
    maybeChangeAdditive(false);
    myDrawer.drawLine(tex, p1, p2, col, width, precise);
  }

  public void draw(ParticleEmitter emitter, TextureAtlas.AtlasRegion tex, boolean additive) {
    maybeChangeAdditive(additive);
    emitter.draw(myDrawer.getBatch(emitter.getSprite().getTexture(), tex));
  }

  public void drawCircle(TextureRegion tex, Vector2 center, float radius, Color col, float width, float vh) {
    maybeChangeAdditive(false);
    myDrawer.drawCircle(tex, center, radius, col, width, vh);
  }

  public void maybeChangeAdditive(boolean additive) {
    if (myCurrAdditive == additive) return;
    myCurrAdditive = additive;
    myDrawer.setAdditive(additive);
  }
}