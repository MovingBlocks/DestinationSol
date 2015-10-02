package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.MapDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MapScreen implements SolUiScreen {
  private final List<SolUiControl> myControls;
  public final SolUiControl closeCtrl;
  public final SolUiControl zoomInCtrl;
  public final SolUiControl zoomOutCtrl;

  public MapScreen(RightPaneLayout rightPaneLayout, boolean mobile, float r, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    Rectangle closeArea = mobile ? MainScreen.btn(0, MainScreen.HELPER_ROW_1, true) : rightPaneLayout.buttonRect(1);
    closeCtrl = new SolUiControl(closeArea, true, gameOptions.getKeyMap(), gameOptions.getKeyClose());
    closeCtrl.setDisplayName("Close");
    myControls.add(closeCtrl);
    float row0 = 1 - MainScreen.CELL_SZ;
    float row1 = row0 - MainScreen.CELL_SZ;
    float colN = r - MainScreen.CELL_SZ;
    Rectangle zoomInArea = mobile ? MainScreen.btn(0, row1, false) : rightPaneLayout.buttonRect(2);
    zoomInCtrl = new SolUiControl(zoomInArea, true, gameOptions.getKeyZoomIn());
    zoomInCtrl.setDisplayName("Zoom In");
    myControls.add(zoomInCtrl);
    Rectangle zoomOutArea = mobile ? MainScreen.btn(0, row0, false) : rightPaneLayout.buttonRect(3);
    zoomOutCtrl = new SolUiControl(zoomOutArea, true, gameOptions.getKeyZoomOut());
    zoomOutCtrl.setDisplayName("Zoom Out");
    myControls.add(zoomOutCtrl);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    SolGame g = cmp.getGame();
    GameOptions gameOptions = cmp.getOptions();
    boolean justClosed = closeCtrl.isJustOff();
    MapDrawer mapDrawer = g.getMapDrawer();
    mapDrawer.setToggled(!justClosed);
    SolInputManager im = cmp.getInputMan();
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
        zoomOutCtrl.maybeFlashPressed(gameOptions.getKeyZoomOut());
      } else {
        zoomInCtrl.maybeFlashPressed(gameOptions.getKeyZoomIn());
      }
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
