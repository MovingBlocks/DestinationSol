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
    float bgW = r * .5f;
    float bgH = .2f;
    myBg = new Rectangle(r/2 - bgW/2, 1 - bgH, bgW, bgH);
    mySteps = new ArrayList<Step>();
    myStepIdx = 0;

    MainScreen main = screens.mainScreen;
    boolean mouseCtrl = main.shipControl instanceof ShipMixedControl;
    SolUiControl shootCtrl;
    String shootKey;
    String shootKey2;
    SolUiControl upCtrl;
    SolUiControl leftCtrl;
    SolUiControl abilityCtrl;
    if (mouseCtrl) {
      ShipMixedControl mixedControl = (ShipMixedControl) main.shipControl;
      shootCtrl = mixedControl.shootCtrl;
      shootKey = "(LEFT mouse button)";
      shootKey2 = "(Click LEFT mouse button)";
      upCtrl = mixedControl.upCtrl;
      leftCtrl = null;
      abilityCtrl = mixedControl.abilityCtrl;
    } else {
      ShipKbControl kbControl = (ShipKbControl) main.shipControl;
      shootCtrl = kbControl.shootCtrl;
      upCtrl = kbControl.upCtrl;
      leftCtrl = kbControl.leftCtrl;
      abilityCtrl = kbControl.abilityCtrl;
      if (mobile) {
        shootKey = "(PRIMARY button)";
        shootKey2 = "(Press PRIMARY button)";
      } else {
        shootKey = "(SPACE key)";
        shootKey2 = "(Press SPACE key)";
      }
    }

    s("Hi! Shoot your main gun\n" + shootKey, shootCtrl);

    if (leftCtrl != null) {
      if (mobile) {
        s("Great! Turn left.\nDon't fly away yet!", leftCtrl);
      } else {
        s("Great! Turn left (LEFT key). \nDon't fly away yet!", leftCtrl);
      }
    }

    if (mobile) {
      s("Have a look at the map", main.mapCtrl, true);
    } else {
      s("Have a look at the map\n(TAB key)", main.mapCtrl, true);
    }

    if (mouseCtrl) {
      s("Zoom in the map\n(mouse wheel UP)", screens.mapScreen.zoomInCtrl);
    } else if (mobile) {
      s("Zoom in the map", screens.mapScreen.zoomInCtrl);
    } else {
      s("Zoom in the map\n(UP key)", screens.mapScreen.zoomInCtrl);
    }

    if (mobile) {
      s("Close the map", screens.mapScreen.closeCtrl, true);
    } else {
      s("Close the map\n(TAB or ESCAPE keys)", screens.mapScreen.closeCtrl, true);
    }

    if (mouseCtrl || mobile) {
      s("Have a look\nat your inventory", main.invCtrl, true);
    } else {
      s("Have a look\nat your inventory (I key)", main.invCtrl, true);
    }

    if (mouseCtrl || mobile) {
      s("In the inventory,\nselect the second row", screens.inventoryScreen.itemCtrls[1]);
    } else {
      s("In the inventory,\nselect the next item (DOWN key)", screens.inventoryScreen.downCtrl);
    }

    if (mouseCtrl || mobile) {
      s("Go to the next page", screens.inventoryScreen.nextCtrl, true);
    } else {
      s("Go to the next page\n(LEFT key)", screens.inventoryScreen.nextCtrl, true);
    }

    if (mouseCtrl || mobile) {
      s("Throw away some item\nyou don't use", screens.inventoryScreen.showInventory.dropCtrl);
    } else {
      s("Throw away some item\nyou don't use (D key)", screens.inventoryScreen.showInventory.dropCtrl);
    }

    if (mobile) {
      s("Unequip some item\nthat is used now", screens.inventoryScreen.showInventory.eq1Ctrl);
    } else {
      s("Unequip some item\nthat is used now (SPACE key)", screens.inventoryScreen.showInventory.eq1Ctrl);
    }

    if (mobile) {
      s("Now equip it again", screens.inventoryScreen.showInventory.eq1Ctrl);
    } else {
      s("Now equip it again\n(SPACE key)", screens.inventoryScreen.showInventory.eq1Ctrl);
    }

    if (mobile) {
      s("Close the inventory\n(Touch the screen outside inventory)", screens.inventoryScreen.closeCtrl, true);
    } else {
      s("Close the inventory (ESCAPE key)", screens.inventoryScreen.closeCtrl, true);
    }

    if (mouseCtrl) {
      s("Move forward (W key).\nThere's no stop!", upCtrl);
    } else if (mobile) {
      s("Move forward.\nThere's no stop!", upCtrl);
    } else {
      s("Move forward (UP key).\nThere's no stop!", upCtrl);
    }

    if (mobile) {
      s("Fly closer to the station\nand talk with it", main.talkCtrl, true);
    } else {
      s("Fly closer to the station\nand talk with it (T key)", main.talkCtrl, true);
    }

    if (mouseCtrl || mobile) {
      s("See what there is to buy", screens.talkScreen.buyCtrl, true);
    } else {
      s("See what there is to buy\n(B key)", screens.talkScreen.buyCtrl, true);
    }

    if (mobile) {
      s("Buy some item", screens.inventoryScreen.buyItems.buyCtrl);
    } else {
      s("Buy some item\n(SPACE key)", screens.inventoryScreen.buyItems.buyCtrl);
    }

    if (mobile) {
      s("Close the Buy screen\n(Touch the screen outside inventory)", screens.inventoryScreen.closeCtrl, true);
    } else {
      s("Close the Buy screen\n(ESCAPE key)", screens.inventoryScreen.closeCtrl, true);
    }

    if (mobile) {
      s("Close the Talk screen\n(Touch the screen outside inventory)", screens.talkScreen.closeCtrl, true);
    } else {
      s("Close the Talk screen\n(ESCAPE key)", screens.talkScreen.closeCtrl, true);
    }

    if (mouseCtrl) {
      s("Use the ability of your ship\n(MIDDLE mouse button or SHIFT key)", abilityCtrl, true);
    } else if (mobile) {
      s("Use the ability of your ship", abilityCtrl, true);
    } else {
      s("Use the ability of your ship\n(SHIFT key)", abilityCtrl, true);
    }

    s("Here's a couple of hints...\n" + shootKey2, shootCtrl);
    s("Enemies are orange icons, allies are blue\n" + shootKey2, shootCtrl);
    s("Avoid enemies with skull icon\n" + shootKey2, shootCtrl);
    s("To repair, have repair kits and just stay idle\n" + shootKey2, shootCtrl);
    s("Destroy asteroids to find money\n" + shootKey2, shootCtrl);
    s("Find or buy shields, armor, guns; equip them\n" + shootKey2, shootCtrl);
    s("Buy new ships, hire mercenaries\n" + shootKey2, shootCtrl);
    s("Tutorial is complete and will exit now!\n" + shootKey2, shootCtrl);
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
    uiDrawer.draw(myBg, Col.UI_BG);
    uiDrawer.drawString(step.text, uiDrawer.r/2, myBg.y + myBg.height/2, FontSize.TUT, true, Col.W);
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
