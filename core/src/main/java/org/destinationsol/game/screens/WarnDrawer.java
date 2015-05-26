package org.destinationsol.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.Const;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.UiDrawer;

public abstract class WarnDrawer {
  public static final float FADE_TIME = 1f;
  private final Rectangle myWarn;
  private final Color myBgCol;
  private final Color myTextCol;
  private final float myBgOrigA;
  private final String myText;

  public boolean show;
  public float drawPerc;

  public WarnDrawer(float r, String text) {
    myWarn = rect(r);
    myText = text;
    myBgCol = new Color(SolColor.UI_WARN);
    myBgOrigA = myBgCol.a;
    myTextCol = new Color(SolColor.W);
  }

  public static Rectangle rect(float r) {
    return new Rectangle(.4f * r, 0, .2f * r, .1f);
  }

  public void update(SolGame game) {
    show = shouldWarn(game);
    if (show) drawPerc = 1;
    else drawPerc = SolMath.approach(drawPerc, 0, Const.REAL_TIME_STEP / FADE_TIME);
    myBgCol.a = myBgOrigA * drawPerc;
    myTextCol.a = drawPerc;
  }

  protected abstract boolean shouldWarn(SolGame game);

  public void draw(UiDrawer uiDrawer) {
    uiDrawer.draw(myWarn, myBgCol);
  }

  public void drawText(UiDrawer uiDrawer) {
    uiDrawer.drawString(myText, myWarn.x + myWarn.width/2, myWarn.y + myWarn.height/2, FontSize.MENU, true, myTextCol);
  }
}
