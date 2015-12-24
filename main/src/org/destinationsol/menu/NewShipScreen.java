package org.destinationsol.menu;

import com.badlogic.gdx.Input;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.*;

import java.util.ArrayList;
import java.util.List;

public class NewShipScreen implements SolUiScreen {
  private final List<SolUiControl> myControls;
  private final SolUiControl myOkCtrl;
  public final SolUiControl myCancelCtrl;

  public NewShipScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();
    myOkCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.H);
    myOkCtrl.setDisplayName("OK");
    myControls.add(myOkCtrl);

    myCancelCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myCancelCtrl.setDisplayName("Cancel");
    myControls.add(myCancelCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void onAdd(SolApplication cmp) {
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (myCancelCtrl.isJustOff()) {
      cmp.getInputMan().setScreen(cmp, cmp.getMenuScreens().newGame);
      return;
    }
    if (myOkCtrl.isJustOff()) {
      cmp.loadNewGame(false, false);
    }
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void blurCustom(SolApplication cmp) {

  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {

  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
    uiDrawer.drawString("This will erase your previous ship", .5f * uiDrawer.r, .3f, FontSize.MENU, true, SolColor.W);
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }
}
