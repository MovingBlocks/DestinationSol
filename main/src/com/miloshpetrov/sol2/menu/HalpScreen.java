package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.Input;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class HalpScreen implements SolUiScreen {
  private final List<SolUiControl> myControls;
  private final SolUiControl myCloseCtrl;

  public HalpScreen(MenuLayout menuLayout) {
    myControls = new ArrayList<SolUiControl>();

    myCloseCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), Input.Keys.ESCAPE);
    myCloseCtrl.setDisplayName("Close");
    myControls.add(myCloseCtrl);
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
    }
  }

  @Override
  public void drawPre(UiDrawer uiDrawer, SolCmp cmp) {
    uiDrawer.drawString(getText(), .5f * uiDrawer.r, .3f, FontSize.MENU, true, Col.W);
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

  private String getText() {
    return "Keyboard Controls:\n" +
      "Move forward: up arrow\n" +
      "Rotate left/right: left/right arrow\n" +
      "Shoot primary: space\n" +
      "Shoot secondary: left ctrl\n" +
      "Special: left shift\n" +
      "Map: tab\n" +
      "Inventory: i";
  }
}
