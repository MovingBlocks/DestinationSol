package org.destinationsol.menu;

import com.badlogic.gdx.Gdx;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.*;

import java.util.ArrayList;
import java.util.List;

public class ResolutionScreen implements SolUiScreen {

  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myCloseCtrl;
  private final SolUiControl myResoCtrl;
  private final SolUiControl myFsCtrl;

  public ResolutionScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    myResoCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
    myResoCtrl.setDisplayName("Resolution");
    myControls.add(myResoCtrl);

    myFsCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), true);
    myFsCtrl.setDisplayName("Fullscreen");
    myControls.add(myFsCtrl);

    myCloseCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myCloseCtrl.setDisplayName("Back");
    myControls.add(myCloseCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    SolInputManager im = cmp.getInputMan();
    if (myCloseCtrl.isJustOff()) {
      GameOptions options = cmp.getOptions();
      Gdx.graphics.setDisplayMode(options.x, options.y, options.fullscreen);
      im.setScreen(cmp, cmp.getMenuScreens().options);
      return;
    }

    GameOptions options = cmp.getOptions();
    myResoCtrl.setDisplayName(options.x + "x" + options.y);
    if (myResoCtrl.isJustOff()) {
      options.advanceReso();
    }
    myFsCtrl.setDisplayName(options.fullscreen ? "Fullscreen" : "Windowed");
    if (myFsCtrl.isJustOff()) {
      options.advanceFullscreen();
    }
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
    uiDrawer.drawString("Click 'Back' to apply changes", .5f * uiDrawer.r, .3f, FontSize.MENU, true, SolColor.W);
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolApplication cmp) {
  }

  @Override
  public void blurCustom(SolApplication cmp) {

  }

}
