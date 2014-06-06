package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.game.MapDrawer;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class MapScreen implements SolUiScreen {
  public static final int ZOOM_IN_KEY = Input.Keys.UP;
  public static final int ZOOM_OUT_KEY = Input.Keys.DOWN;
  private final List<SolUiControl> myControls;
  public final SolUiControl closeCtrl;
  public final SolUiControl zoomInCtrl;
  public final SolUiControl zoomOutCtrl;

  public MapScreen(RightPaneLayout rightPaneLayout) {
    myControls = new ArrayList<SolUiControl>();

    closeCtrl = new SolUiControl(rightPaneLayout.buttonRect(1), true, Input.Keys.TAB, Input.Keys.ESCAPE);
    closeCtrl.setDisplayName("Close");
    myControls.add(closeCtrl);
    zoomInCtrl = new SolUiControl(rightPaneLayout.buttonRect(2), true, ZOOM_IN_KEY);
    zoomInCtrl.setDisplayName("Zoom In");
    myControls.add(zoomInCtrl);
    zoomOutCtrl = new SolUiControl(rightPaneLayout.buttonRect(3), true, ZOOM_OUT_KEY);
    zoomOutCtrl.setDisplayName("Zoom Out");
    myControls.add(zoomOutCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs) {
    SolGame g = cmp.getGame();
    boolean justClosed = closeCtrl.isJustOff();
    MapDrawer mapDrawer = g.getMapDrawer();
    mapDrawer.setToggled(!justClosed);
    SolInputMan im = cmp.getInputMan();
    if (justClosed) {
      im.setScreen(cmp, g.getScreens().mainScreen);
    }
    boolean zoomIn = zoomInCtrl.isJustOff();
    if (zoomIn || zoomOutCtrl.isJustOff()) {
      mapDrawer.changeZoom(zoomIn);
    }
    float mapZoom = mapDrawer.getZoom();
    zoomInCtrl.setEnabled(mapZoom != MapDrawer.MIN_ZOOM);
    zoomOutCtrl.setEnabled(mapZoom != MapDrawer.MAX_ZOOM);
    ShipUiControl sc = g.getScreens().mainScreen.shipControl;
    if (sc instanceof ShipMouseControl) sc.update(cmp, true);
    Boolean scrolledUp = im.getScrolledUp();
    if (scrolledUp != null) {
      if (scrolledUp) {
        zoomOutCtrl.maybeFlashPressed(ZOOM_OUT_KEY);
      } else {
        zoomInCtrl.maybeFlashPressed(ZOOM_IN_KEY);
      }
    }
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolCmp cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolCmp cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolCmp cmp) {
  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolCmp cmp) {

  }

  @Override
  public void blurCustom(SolCmp cmp) {

  }
}
