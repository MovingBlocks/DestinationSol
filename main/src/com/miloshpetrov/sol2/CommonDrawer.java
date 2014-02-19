package com.miloshpetrov.sol2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;

public class CommonDrawer {
  public final TextureRegion whiteTex;
  public final float w;
  public final float h;
  public final float r;

  private final SpriteBatch mySpriteBatch;
  private final BitmapFont myFont;
  private final float myOrigFontHeight;

  public CommonDrawer() {
    w = Gdx.graphics.getWidth();
    h = Gdx.graphics.getHeight();
    r = w / h;
    mySpriteBatch = new SpriteBatch();
    whiteTex = getWhiteTex();

    myFont = new BitmapFont(SolFiles.readOnly("res/fonts/main.fnt"), true);
    myFont.setUseIntegerPositions(false);
    myOrigFontHeight = myFont.getXHeight();
  }

  private TextureRegion getWhiteTex() {
    Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
    pm.setColor(Col.W);
    pm.fill();
    Texture tex = new Texture(pm);
    pm.dispose();
    return new TextureRegion(tex);
  }

  public void setMtx(Matrix4 mtx) {
    mySpriteBatch.setProjectionMatrix(mtx);
  }

  public void begin() {
    mySpriteBatch.begin();
  }

  public void end() {
    mySpriteBatch.end();
  }

  public void drawString(String s, float x, float y, float fontSize, boolean centered, Color col) {
    if (s == null) return;
    myFont.setColor(col);
    myFont.setScale(fontSize / myOrigFontHeight);
    if (!centered) {
      myFont.drawMultiLine(mySpriteBatch, s, x, y);
      return;
    }
    BitmapFont.TextBounds b = myFont.getMultiLineBounds(s);
    x -= b.width / 2;
    y -= b.height / 2;
    myFont.drawMultiLine(mySpriteBatch, s, x, y, b.width, BitmapFont.HAlignment.CENTER);
  }


  public void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y,
    float rot, Color tint)
  {
    setTint(tint);
    mySpriteBatch.draw(tr, x - origX, y - origY, origX, origY, width, height, 1, 1, rot);
  }

  private void setTint(Color tint) {
    mySpriteBatch.setColor(tint);
  }

  public void draw(Rectangle rect, Color tint) {
    draw(whiteTex, rect.width, rect.height, (float) 0, (float) 0, rect.x, rect.y, (float) 0, tint);
  }

  public void drawCircle(Vector2 center, float radius, Color col, float width, int pointCount) {
    Vector2 pos = SolMath.getVec();
    if (pointCount < 8) pointCount = 8;
    float lineLen = radius * SolMath.PI * 2 / pointCount;
    float angleStep = 360f / pointCount;
    float angleStepH = angleStep / 2;
    for (int i = 0; i < pointCount; i++) {
      float angle = angleStep * i;
      SolMath.fromAl(pos, angle, radius);
      pos.add(center);
      draw(whiteTex, width, lineLen, (float) 0, (float) 0, pos.x, pos.y, angle + angleStepH, col);
    }
    SolMath.free(pos);
  }

  public void drawLine(float x, float y, float angle, float len, Color col, float width) {
    draw(whiteTex, len, width, 0, width/2, x, y, angle, col);
  }

  public void drawLine(Vector2 p1, Vector2 p2, Color col, float width) {
    Vector2 v = SolMath.getVec(p2);
    v.sub(p1);
    drawLine(p1.x, p1.y, v.angle(), v.len(), col, width);
    SolMath.free(v);
  }

  public void dispose() {
    whiteTex.getTexture().dispose();
    mySpriteBatch.dispose();
    myFont.dispose();
  }

  public SpriteBatch getBatch() {
    return mySpriteBatch;
  }

}

