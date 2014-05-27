package com.miloshpetrov.sol2.ui;

import com.badlogic.gdx.math.Rectangle;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.screens.InventoryScreen;

import java.util.HashMap;

public class TutMan {
  private final HashMap<Integer,String> myMsgs;
  private final Rectangle myBg;
  private int myStep;

  public TutMan(float r) {
    myMsgs = new HashMap<Integer, String>();
    myMsgs.put(0, "Hi! Press SPACE to shoot your main weapon");
    myMsgs.put(1, "Press CTRL to shoot your secondary weapon");
    myMsgs.put(2, "Press TAB to open the map");
    myMsgs.put(3, "In the map, press UP to zoom in");
    myMsgs.put(4, "In the map, press DOWN to zoom out");
    myMsgs.put(5, "Press TAB or ESCAPE to close the map");
    myMsgs.put(6, "Press I to see your inventory");
    myMsgs.put(7, "In the inventory, press DOWN to select next item");
    myMsgs.put(8, "In the inventory, press LEFT to see next page");
    myMsgs.put(9, "In the inventory, press D to drop the item");
    myMsgs.put(10, "In the inventory, select \"Engine\" and press SPACE to start using it");
    myMsgs.put(11, "In the inventory, select \"Slow Gun\" and press CTRL to start using it as a secondary weapon");
    myMsgs.put(12, "Press I or ESC to close the inventory");
    myMsgs.put(13, "Press LEFT to rotate left");
    myMsgs.put(14, "Press RIGHT to rotate right");
    myMsgs.put(15, "Press UP to move forward. There's no stop!");
    myMsgs.put(16, "Fly closer to the station");
    myMsgs.put(17, "While near the station, press T to talk");
    myMsgs.put(18, "In the talk dialog, press S to sell things or B to buy");
    myMsgs.put(19, "In the buy/sell dialog, press SPACE to buy or sell");
    myMsgs.put(20, "Press ESC to close the buy/sell dialog");
    myMsgs.put(21, "Here's a couple of hints... (Press SPACE to continue)");
    myMsgs.put(22, "1. Enemies are black icons, allies are white");
    myMsgs.put(23, "2. Avoid enemies with skull icon");
    myMsgs.put(24, "3. Find or buy shields, armor, weapons; equip them");
    myMsgs.put(25, "4. To repair, have repair kits and just stay idle");
    myMsgs.put(26, "5. Buy new ships at stations");
    myMsgs.put(27, "6. Use slo mo charges by pressing SHIFT");
    myMsgs.put(28, "Tutorial complete! Press SPACE to restart in normal mode!");

    myBg = new Rectangle(0, .9f, r, .1f);
  }

  public void update(SolCmp cmp) {
    SolGame g = cmp.getGame();
    if (g == null) {
      myStep = 0;
      return;
    }
    if (!g.isTut() || !myMsgs.containsKey(myStep)) return;
    if (ah(g)) myStep++;
    if (myStep == 29) {
      cmp.finishGame();
      cmp.startNewGame(false);
    }
  }

  public boolean ah(SolGame g) {

    switch (myStep) {
    case 0: return shoot(g);
    case 1: return shoot2(g);
    case 2: return mapOpen(g);
    case 3: return mapZoomIn(g);
    case 4: return mapZoomOut(g);
    case 5: return mapZoomClose(g);
    case 6: return itemsOpen(g);
    case 7: return itemsDown(g);
    case 8: return itemsLeft(g);
    case 9: return itemsDrop(g);
    case 10: return itemsUse(g);
    case 11: return itemsUse2(g);
    case 12: return itemsClose(g);
    case 13: return left(g);
    case 14: return right(g);
    case 15: return up(g);
    case 16: return toStation(g);
    case 17: return talk(g);
    case 18: return buySell(g);
    case 19: return doBuySell(g);
    case 20: return buySellClose(g);
    default: return spaceJustOff(g);
    }
  }

  private boolean spaceJustOff(SolGame g) {
    return true /*g.getScreens().mainScreen.myShootCtrl.isJustOff()*/;
  }

  private boolean buySellClose(SolGame g) {
    return !g.getCmp().getInputMan().isScreenOn(g.getScreens().inventoryScreen);
  }

  private boolean doBuySell(SolGame g) {
    InventoryScreen is = g.getScreens().inventoryScreen;
    return is.buyItems.buyCtrl.isJustOff() || is.sellItems.sellCtrl.isJustOff();
  }

  private boolean buySell(SolGame g) {
    InventoryScreen is = g.getScreens().inventoryScreen;
    return is.getOperations() == is.buyItems || is.getOperations() == is.sellItems;
  }

  private boolean talk(SolGame g) {
    return g.getCmp().getInputMan().isScreenOn(g.getScreens().talkScreen);
  }

  private boolean toStation(SolGame g) {
    return g.getScreens().mainScreen.talkCtrl.isEnabled();
  }

  private boolean up(SolGame g) {
    return g.getScreens().mainScreen.isUp();
  }

  private boolean right(SolGame g) {
    return g.getScreens().mainScreen.isRight();
  }

  private boolean left(SolGame g) {
    return g.getScreens().mainScreen.isLeft();
  }

  private boolean itemsClose(SolGame g) {
    return !g.getCmp().getInputMan().isScreenOn(g.getScreens().inventoryScreen);
  }

  private boolean itemsUse2(SolGame g) {
    boolean slowGunSelected = g.getItemMan().getExample("sg").isSame(g.getScreens().inventoryScreen.getSelectedItem());
    return slowGunSelected && g.getScreens().inventoryScreen.showInventory.eq2Ctrl.isJustOff();
  }

  private boolean itemsUse(SolGame g) {
    boolean engineSelected = g.getItemMan().getExample("e").isSame(g.getScreens().inventoryScreen.getSelectedItem());
    return engineSelected && g.getScreens().inventoryScreen.showInventory.eq1Ctrl.isJustOff();
  }

  private boolean itemsDrop(SolGame g) {
    return g.getScreens().inventoryScreen.showInventory.dropCtrl.isJustOff();
  }

  private boolean itemsDown(SolGame g) {
    return g.getScreens().inventoryScreen.downCtrl.isJustOff();
  }

  private boolean itemsLeft(SolGame g) {
    return g.getScreens().inventoryScreen.getPage() > 0;
  }

  private boolean itemsOpen(SolGame g) {
    return g.getCmp().getInputMan().isScreenOn(g.getScreens().inventoryScreen);
  }

  private boolean mapZoomClose(SolGame g) {
    return !g.getCmp().getInputMan().isScreenOn(g.getScreens().mapScreen);
  }

  private boolean mapZoomOut(SolGame g) {
    return g.getScreens().mapScreen.zoomOutCtrl.isJustOff();
  }

  private boolean mapZoomIn(SolGame g) {
    return g.getScreens().mapScreen.zoomInCtrl.isJustOff();
  }

  private boolean mapOpen(SolGame g) {
    return g.getCmp().getInputMan().isScreenOn(g.getScreens().mapScreen);
  }

  private boolean shoot(SolGame g) {
    return g.getScreens().mainScreen.isShoot();
  }

  private boolean shoot2(SolGame g) {
    return g.getScreens().mainScreen.isShoot2();
  }

  public void draw(UiDrawer uiDrawer, SolCmp cmp) {
    SolGame g = cmp.getGame();
    if (g == null || !g.isTut() || !myMsgs.containsKey(myStep)) return;
    uiDrawer.draw(myBg, Col.B75);
    uiDrawer.drawString(myMsgs.get(myStep), uiDrawer.r/2, .95f, FontSize.MENU, true, Col.W);
  }
}
