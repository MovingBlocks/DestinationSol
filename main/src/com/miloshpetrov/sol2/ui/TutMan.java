package com.miloshpetrov.sol2.ui;

import com.badlogic.gdx.math.Rectangle;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.screens.*;

import java.util.ArrayList;

public class TutMan {
  private final Rectangle myBg;
  private final ArrayList<Step> mySteps;

  private int myStepIdx;

  public TutMan(float r, GameScreens screens, boolean mobile) {
    myBg = new Rectangle(0, .95f, r, .05f);
    mySteps = new ArrayList<Step>();
    myStepIdx = 0;

    MainScreen main = screens.mainScreen;
    boolean mouseCtrl = main.shipControl instanceof ShipMixedControl;
    SolUiControl shootCtrl;
    String shootKey;
    String shootKey2;
    SolUiControl upCtrl;
    SolUiControl leftCtrl;
    if (mouseCtrl) {
      ShipMixedControl mixedControl = (ShipMixedControl) main.shipControl;
      shootCtrl = mixedControl.shootCtrl;
      shootKey = "(LEFT mouse button)";
      shootKey2 = "(Click LEFT mouse button)";
      upCtrl = mixedControl.upCtrl;
      leftCtrl = null;
    } else {
      ShipKbControl kbControl = (ShipKbControl) main.shipControl;
      shootCtrl = kbControl.shootCtrl;
      upCtrl = kbControl.upCtrl;
      leftCtrl = kbControl.leftCtrl;
      if (mobile) {
        shootKey = "(PRIMARY button)";
        shootKey2 = "(Press PRIMARY button)";
      } else {
        shootKey = "(SPACE key)";
        shootKey2 = "(Press SPACE key)";
      }
    }

    s("Hi! Shoot your main gun " + shootKey, shootCtrl);

    if (leftCtrl != null) {
      if (mobile) {
        s("Great! Turn left. Don't fly away yet!", leftCtrl);
      } else {
        s("Great! Turn left (LEFT key). Don't fly away yet!", leftCtrl);
      }
    }

    if (mobile) {
      s("Have a look at the map", main.mapCtrl, true);
    } else {
      s("Have a look at the map (TAB key)", main.mapCtrl, true);
    }

    if (mouseCtrl) {
      s("Zoom in the map (W key)", screens.mapScreen.zoomInCtrl);
    } else if (mobile) {
      s("Zoom in the map", screens.mapScreen.zoomInCtrl);
    } else {
      s("Zoom in the map (UP key)", screens.mapScreen.zoomInCtrl);
    }

    if (mobile) {
      s("Close the map", screens.mapScreen.closeCtrl, true);
    } else {
      s("Close the map (TAB or ESCAPE keys)", screens.mapScreen.closeCtrl, true);
    }

    if (mouseCtrl || mobile) {
      s("Have a look at your inventory", main.invCtrl, true);
    } else {
      s("Have a at your inventory (I key)", main.invCtrl, true);
    }

    if (mouseCtrl || mobile) {
      s("In the inventory, select the second row", screens.inventoryScreen.itemCtrls[1]);
    } else {
      s("In the inventory, select the next item (DOWN key)", screens.inventoryScreen.downCtrl);
    }

    if (mouseCtrl || mobile) {
      s("Go to the next page", screens.inventoryScreen.nextCtrl, true);
    } else {
      s("Go to the next page (LEFT key)", screens.inventoryScreen.nextCtrl, true);
    }

    if (mouseCtrl || mobile) {
      s("Throw away some item you don't use", screens.inventoryScreen.showInventory.dropCtrl);
    } else {
      s("Throw away some item you don't use (D key)", screens.inventoryScreen.showInventory.dropCtrl);
    }

    if (mouseCtrl || mobile) {
      s("Unequip some item that is used now", screens.inventoryScreen.showInventory.eq1Ctrl);
    } else {
      s("Unequip some item that is used now (SPACE key)", screens.inventoryScreen.showInventory.eq1Ctrl);
    }

    if (mouseCtrl || mobile) {
      s("Now equip it again", screens.inventoryScreen.showInventory.eq1Ctrl);
    } else {
      s("Now equip it again (SPACE key)", screens.inventoryScreen.showInventory.eq1Ctrl);
    }

    if (mobile) {
      s("Close the inventory", screens.inventoryScreen.closeCtrl, true);
    } else {
      s("Close the inventory (ESCAPE key)", screens.inventoryScreen.closeCtrl, true);
    }

    if (mouseCtrl) {
      s("Move forward (W key). There's no stop!", upCtrl);
    } else if (mobile) {
      s("Move forward. There's no stop!", upCtrl);
    } else {
      s("Move forward (UP key). There's no stop!", upCtrl);
    }

    if (mouseCtrl || mobile) {
      s("Fly closer to the station and talk with it", main.talkCtrl, true);
    } else {
      s("Fly closer to the station and talk with it (T key)", main.talkCtrl, true);
    }

    if (mouseCtrl || mobile) {
      s("See what there is to buy", screens.talkScreen.buyCtrl, true);
    } else {
      s("See what there is to buy (B key)", screens.talkScreen.buyCtrl, true);
    }

    if (mouseCtrl || mobile) {
      s("Buy some item", screens.inventoryScreen.buyItems.buyCtrl);
    } else {
      s("Buy some item (SPACE key)", screens.inventoryScreen.buyItems.buyCtrl);
    }

    if (mobile) {
      s("Close the Buy screen", screens.inventoryScreen.closeCtrl, true);
    } else {
      s("Close the Buy screen (ESCAPE key)", screens.inventoryScreen.closeCtrl, true);
    }

    if (mobile) {
      s("Close the Talk screen", screens.talkScreen.closeCtrl, true);
    } else {
      s("Close the Talk screen (ESCAPE key)", screens.talkScreen.closeCtrl, true);
    }

    s("Here's a couple of hints... " + shootKey2, shootCtrl);
    s("Enemies are orange icons, allies are blue", shootCtrl);
    s("Avoid enemies with skull icon", shootCtrl);
    s("Find or buy shields, armor, weapons; equip them", shootCtrl);
    s("To repair, have repair kits and just stay idle", shootCtrl);
    s("Buy new ships at stations", shootCtrl);
    s("Use Slo Mo charges by pressing SHIFT", shootCtrl);
    s("Tutorial complete! " + shootKey2, shootCtrl);
  }

  private void s(String text, SolUiControl ctrl) {
    s(text, ctrl, false);
  }
  private void s(String text, SolUiControl ctrl, boolean checkOn) {
    mySteps.add(new Step(text, ctrl, checkOn));
  }

  public void update() {
    Step step = mySteps.get(myStepIdx);
    step.ctrl.enableWarn();
    if (step.checkOn ? step.ctrl.isOn() : step.ctrl.isJustOff()) {
      myStepIdx++;
    }
  }

  public void draw(UiDrawer uiDrawer) {
    if (isFinished()) return;
    Step step = mySteps.get(myStepIdx);
    uiDrawer.draw(myBg, Col.B75);
    uiDrawer.drawString(step.text, uiDrawer.r/2, .975f, FontSize.MENU, true, Col.W);
  }

  public boolean isFinished() {
    return myStepIdx == mySteps.size();
  }

  public static class Step {
    public final String text;
    public final SolUiControl ctrl;
    public final boolean checkOn;

    public Step(String text, SolUiControl ctrl, boolean checkOn) {
      this.text = text;
      this.ctrl = ctrl;
      this.checkOn = checkOn;
    }
  }
}
