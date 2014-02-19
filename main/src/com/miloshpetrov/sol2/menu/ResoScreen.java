package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.*;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class ResoScreen implements SolUiScreen {

  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myCloseCtrl;
  private final SolUiControl myResoCtrl;
  private final SolUiControl myFsCtrl;
  private final SettingsReader.Data myD;

  public ResoScreen(MenuLayout menuLayout) {
    myControls = new ArrayList<SolUiControl>();

    myResoCtrl = new SolUiControl(menuLayout.buttonRect(-1, 0), Input.Keys.ESCAPE);
    myResoCtrl.setDisplayName("Resolution");
    myControls.add(myResoCtrl);

    myFsCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), Input.Keys.ESCAPE);
    myFsCtrl.setDisplayName("Fullscreen");
    myControls.add(myFsCtrl);

    myCloseCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), Input.Keys.ESCAPE);
    myCloseCtrl.setDisplayName("Back");
    myControls.add(myCloseCtrl);

    myD = Gdx.app.getType() == Application.ApplicationType.Desktop ? SettingsReader.read() : null;
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs) {
    SolInputMan im = cmp.getInputMan();
    if (myCloseCtrl.isJustOff()) {
      im.setScreen(cmp, cmp.getMenuScreens().options);
      return;
    }

    myResoCtrl.setDisplayName(myD.x + "x" + myD.y);
    if (myResoCtrl.isJustOff()) {
      myD.advance();
      SettingsReader.write(myD);
    }
    myFsCtrl.setDisplayName(myD.fs ? "Fullscreen" : "Windowed");
    if (myFsCtrl.isJustOff()) {
      myD.fs = !myD.fs;
      SettingsReader.write(myD);
    }
  }

  @Override
  public void drawPre(UiDrawer uiDrawer, SolCmp cmp) {
    uiDrawer.drawString("Please restart to apply changes", .5f * uiDrawer.r, .3f, FontSize.MENU, true, Col.W);
  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolCmp cmp) {
  }

  @Override
  public void drawPost(UiDrawer uiDrawer, SolCmp cmp) {

  }

}
