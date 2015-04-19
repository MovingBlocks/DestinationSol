package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.SolColor;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.ui.*;

import java.util.*;

public class CreditsScreen implements SolUiScreen {
  public static final float MAX_AWAIT = 6f;
  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myCloseCtrl;
  private final ArrayList<String> myPages;
  private final Color myColor;

  private int myIdx;
  private float myPerc;

  public CreditsScreen(float r) {
    myControls = new ArrayList<SolUiControl>();
    myCloseCtrl = new SolUiControl(MainScreen.creditsBtnRect(r), true, Input.Keys.ESCAPE);
    myCloseCtrl.setDisplayName("Close");
    myControls.add(myCloseCtrl);
    myColor = SolColor.col(1, 1);

    myPages = new ArrayList<String>();
    String[][] sss = {
      {
        "Idea, coding, team lead:",
        "Milosh Petrov",
        "",
        "Drawing:",
        "Kent C. Jensen",
        "",
        "Additional coding:",
        "Nika \"NoiseDoll\" Burimenko",
        "",
        "Additional drawing:",
        "Julia Nikolaeva"
      },
      {
        "Game engine:",
        "LibGDX",
        "",
        "Platform:",
        "OpenJDK",
        "",
        "Windows wrapper:",
        "Launch4J"
      },
      {
        "Font:",
        "\"Jet Set\" by Captain Falcon",
        "",
        "Sounds by FreeSound.org users:",
        "Smokum, Mattpavone",
        "Hanstimm, Sonidor,",
        "Isaac200000, TheHadnot, Garzul",
        "Dpoggioli, Raremess, Giddykipper,",
        "Steveygos93",
      },
    };
    for (String[] ss : sss) {
      StringBuilder page = new StringBuilder();
      for (String s : ss) {
        page.append(s).append("\n");
      }
      myPages.add(page.toString());
    }

  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void onAdd(SolCmp cmp) {
    myIdx = 0;
    myPerc = 0;
    myColor.a = 0;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (myCloseCtrl.isJustOff()) {
      cmp.getInputMan().setScreen(cmp, cmp.getMenuScreens().main);
      return;
    }
    myPerc += Const.REAL_TIME_STEP / MAX_AWAIT;
    if (myPerc > 1) {
      myPerc = 0;
      myIdx++;
      if (myIdx >= myPages.size()) myIdx = 0;
    }
    float a = myPerc * 2;
    if (a > 1) a = 2 - a;
    a *= 3;
    myColor.a = SolMath.clamp(a);
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void blurCustom(SolCmp cmp) {
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolCmp cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolCmp cmp) {
  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolCmp cmp) {
    uiDrawer.drawString(myPages.get(myIdx), uiDrawer.r/2, .5f, FontSize.MENU, true, myColor);
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }
}
