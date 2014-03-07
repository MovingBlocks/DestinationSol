package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements SolUiScreen {
  public static final float ICON_SZ = .03f;
  public static final float BAR_SZ = ICON_SZ * 5;
  public static final int MAX_ICON_COUNT = 3;
  private final List<SolUiControl> myControls;
  public static final float CELL_SZ = .2f;
  private final SolUiControl myLeftCtrl;
  private final SolUiControl myRightCtrl;
  private final SolUiControl myUpCtrl;
  private final SolUiControl myDownCtrl;
  public final SolUiControl myShootCtrl;
  private final SolUiControl myShoot2Ctrl;
  private final WarnDrawer myWarnDrawer;
  private final SolUiControl mySpecCtrl;
  private final SolUiControl myMenuCtrl;
  public final SolUiControl mapCtrl;
  private final BorderDrawer myBorderDrawer;
  private final SolUiControl myInvCtrl;
  private final TextureAtlas.AtlasRegion myLifeTex;
  private final TextureAtlas.AtlasRegion myInfinityTex;
  private final TextureAtlas.AtlasRegion myWaitTex;
  public final SolUiControl talkCtrl;
  private final TextureAtlas.AtlasRegion myShieldTex;
  private final SolUiControl myPauseCtrl;

  public MainScreen(float r, RightPaneLayout rightPaneLayout, SolCmp cmp) {
    myControls = new ArrayList<SolUiControl>();

    float col0 = 0;
    float col1 = col0 + CELL_SZ;
    float colN0 = r - CELL_SZ;
    float colN1 = colN0 - CELL_SZ;
    float rowN0 = 1 - CELL_SZ;
    float rowN1 = rowN0 - CELL_SZ;
    boolean showButtons = cmp.isMobile();
    myLeftCtrl = new SolUiControl(showButtons ? btn(colN1, rowN0) : null, Input.Keys.LEFT);
    myLeftCtrl.setDisplayName("Left");
    myControls.add(myLeftCtrl);
    myRightCtrl = new SolUiControl(showButtons ? btn(colN0, rowN0) : null, Input.Keys.RIGHT);
    myRightCtrl.setDisplayName("Right");
    myControls.add(myRightCtrl);
    myUpCtrl = new SolUiControl(showButtons ? btn(col0, rowN0) : null, Input.Keys.UP);
    myUpCtrl.setDisplayName("Up");
    myControls.add(myUpCtrl);
    myDownCtrl = new SolUiControl(null, Input.Keys.DOWN);
    myControls.add(myDownCtrl);
    myShootCtrl = new SolUiControl(showButtons ? btn(col0, rowN1) : null, Input.Keys.SPACE);
    myShootCtrl.setDisplayName("Primary");
    myControls.add(myShootCtrl);
    myShoot2Ctrl = new SolUiControl(showButtons ? btn(col1, rowN0) : null, Input.Keys.CONTROL_LEFT);
    myShoot2Ctrl.setDisplayName("Secondary");
    myControls.add(myShoot2Ctrl);
    mySpecCtrl = new SolUiControl(showButtons ? btn(colN0, rowN1) : null, Input.Keys.SHIFT_LEFT);
    mySpecCtrl.setDisplayName("Special");
    myControls.add(mySpecCtrl);


    myMenuCtrl = new SolUiControl(rightPaneLayout.buttonRect(0), Input.Keys.ESCAPE);
    myMenuCtrl.setDisplayName("Menu");
    myControls.add(myMenuCtrl);
    mapCtrl = new SolUiControl(rightPaneLayout.buttonRect(1), Input.Keys.TAB);
    mapCtrl.setDisplayName("Map");
    myControls.add(mapCtrl);
    myInvCtrl = new SolUiControl(rightPaneLayout.buttonRect(2), Input.Keys.I);
    myInvCtrl.setDisplayName("Items");
    myControls.add(myInvCtrl);
    talkCtrl = new SolUiControl(rightPaneLayout.buttonRect(3), Input.Keys.T);
    talkCtrl.setDisplayName("Talk");
    myControls.add(talkCtrl);
    myPauseCtrl = new SolUiControl(null, Input.Keys.P);
    myControls.add(myPauseCtrl);


    myWarnDrawer = new WarnDrawer(r);
    myBorderDrawer = new BorderDrawer(r, cmp);

    TexMan texMan = cmp.getTexMan();
    myLifeTex = texMan.getTex(TexMan.ICONS_DIR + "life");
    myShieldTex = texMan.getTex(TexMan.ICONS_DIR + "shield");
    myInfinityTex = texMan.getTex(TexMan.ICONS_DIR + "infinity");
    myWaitTex = texMan.getTex(TexMan.ICONS_DIR + "wait");
  }

  public static void maybeDrawHeight(UiDrawer drawer, SolCmp cmp) {
    SolGame game = cmp.getGame();
    Planet np = game.getPlanetMan().getNearestPlanet();
    Vector2 camPos = game.getCam().getPos();
    if (np != null && np.getPos().dst(camPos) < np.getFullHeight()) {
      drawHeight(drawer, np, camPos);
    }
  }

  private static void drawHeight(UiDrawer drawer, Planet np, Vector2 camPos) {
    float toPlanet = camPos.dst(np.getPos());
    toPlanet -= np.getGroundHeight();
    if (Const.ATM_HEIGHT < toPlanet) return;
    float perc = toPlanet / Const.ATM_HEIGHT;
    float y = (1 - perc);
    float size = 1f / 60;
    drawer.draw(drawer.whiteTex, size * 3, size, (float) 0, (float) 0, (float) 0, y, (float) 0, Col.W);
  }

  private Rectangle btn(float x, float y) {
    float gap = .02f;
    return new Rectangle(x + gap, y + gap, CELL_SZ - gap * 2, CELL_SZ - gap*2);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs) {
    SolGame game = cmp.getGame();
    SolInputMan inputMan = cmp.getInputMan();
    GameScreens screens = game.getScreens();
    SolShip hero = game.getHero();

    myWarnDrawer.update(game);

    if (myMenuCtrl.isJustOff()) {
      inputMan.setScreen(cmp, screens.menuScreen);
    }

    boolean hasEngine = hero != null && hero.getHull().getEngine() != null;
    myUpCtrl.setEnabled(hasEngine);
    myLeftCtrl.setEnabled(hasEngine);
    myRightCtrl.setEnabled(hasEngine);

    GunItem g1 = hero == null ? null : hero.getHull().getGunMount(false).getGun();
    myShootCtrl.setEnabled(g1 != null && g1.ammo > 0);
    GunItem g2 = hero == null ? null : hero.getHull().getGunMount(true).getGun();
    myShoot2Ctrl.setEnabled(g2 != null && g2.ammo > 0);
    mySpecCtrl.setEnabled(hero != null && hero.canUseSpec());


    if (mapCtrl.isJustOff()) {
      inputMan.setScreen(cmp, screens.mapScreen);
    }

    myInvCtrl.setEnabled(hero != null);
    if (myInvCtrl.isJustOff()) {
      InventoryScreen is = screens.inventoryScreen;
      boolean isOn = inputMan.isScreenOn(is);
      inputMan.setScreen(cmp, screens.mainScreen);
      if (!isOn) {
        is.setOperations(is.showInventory);
        inputMan.addScreen(cmp, is);
      }
    }

    updateTalk(game);

    if (myPauseCtrl.isJustOff()) {
      game.setPaused(!game.isPaused());
    }
  }

  private void updateTalk(SolGame game) {
    SolShip hero = game.getHero();
    if (hero == null) {
      talkCtrl.setEnabled(false);
      return;
    }
    FractionMan fracMan = game.getFractionMan();

    SolShip target = null;
    float minDist = TalkScreen.MAX_TALK_DIST;
    for (SolObj o : game.getObjMan().getObjs()) {
      if (!(o instanceof SolShip)) continue;
      SolShip ship = (SolShip) o;
      if (fracMan.areEnemies(hero, ship)) continue;
      ItemContainer tc = ship.getTradeContainer();
      if (tc == null) continue;
      float dst = ship.getPos().dst(hero.getPos());
      if (minDist < dst) continue;
      target = ship;
      minDist = dst;
    }
    talkCtrl.setEnabled(target != null);
    if (talkCtrl.isJustOff()) {
      TalkScreen talkScreen = game.getScreens().talkScreen;
      SolCmp cmp = game.getCmp();
      SolInputMan inputMan = cmp.getInputMan();
      boolean isOn = inputMan.isScreenOn(talkScreen);
      inputMan.setScreen(cmp, this);
      if (!isOn) {
        talkScreen.setTarget(target);
        inputMan.addScreen(cmp, talkScreen);
      }
    }
  }

  @Override
  public void drawPre(UiDrawer uiDrawer, SolCmp cmp) {
    maybeDrawHeight(uiDrawer, cmp);
    myBorderDrawer.draw(uiDrawer, cmp);
    myWarnDrawer.draw(uiDrawer);

    SolShip hero = cmp.getGame().getHero();
    if (hero != null) {
      TexMan texMan = cmp.getTexMan();

      float row = BorderDrawer.TISHCH_SZ;
      float col0 = BorderDrawer.TISHCH_SZ;
      float col1 = BorderDrawer.TISHCH_SZ + ICON_SZ;
      float col2 = col1 + BAR_SZ;

      Shield shield = hero.getShield();
      if (shield != null) {
        uiDrawer.draw(myShieldTex, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, Col.W);
        float shieldPerc = shield.getLife() / shield.getMaxLife();
        drawBar(uiDrawer, texMan, col1, row, shieldPerc);
        row += ICON_SZ;
      }

      uiDrawer.draw(myLifeTex, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, Col.W);
      float lifePerc = hero.getLife() / hero.getHull().config.maxLife;
      drawBar(uiDrawer, texMan, col1, row, lifePerc);
      int repairKitCount = hero.getItemContainer().count(RepairItem.EXAMPLE);
      ItemMan itemMan = cmp.getGame().getItemMan();
      drawIcons(uiDrawer, col2, row, repairKitCount, itemMan.repairIcon);

      row += ICON_SZ;
      boolean consumed = drawGunStat(uiDrawer, texMan, hero, false, col0, col1, col2, row);
      if (consumed) row += ICON_SZ;
      consumed = drawGunStat(uiDrawer, texMan, hero, true, col0, col1, col2, row);
      if (consumed) row += ICON_SZ;
      int sloMoCount = hero.getItemContainer().count(SloMoCharge.EXAMPLE);
      drawIcons(uiDrawer, col0, row, sloMoCount, itemMan.sloMoChargeIcon);
    }
  }

  private boolean drawGunStat(UiDrawer uiDrawer, TexMan texMan, SolShip hero, boolean secondary, float col0, float col1,
    float col2, float y)
  {
    GunItem g = hero.getHull().getGunMount(secondary).getGun();
    if (g == null) return false;
    TextureAtlas.AtlasRegion tex = g.config.icon;

    uiDrawer.draw(tex, ICON_SZ, ICON_SZ, 0, 0, col0, y, 0, Col.W);
    int ics = g.config.infiniteClipSize;
    if (g.reloadAwait <= 0) {
      int maxAmmo = ics == 0 ? g.config.clipConf.size : ics;
      float ammoPerc = g.ammo * 1f / maxAmmo;
      drawBar(uiDrawer, texMan, col1, y, ammoPerc);
    } else {
      drawWait(uiDrawer, col1, y);
    }
    if (ics == 0) {
      int clipCount = hero.getItemContainer().count(g.config.clipConf.example);
      drawIcons(uiDrawer, col2, y, clipCount, g.config.clipConf.icon);
    } else {
      uiDrawer.draw(myInfinityTex, ICON_SZ, ICON_SZ, 0, 0, col2, y, 0, Col.W);
    }
    return true;
  }

  private void drawWait(UiDrawer uiDrawer, float x, float y) {
    uiDrawer.draw(myWaitTex, ICON_SZ, ICON_SZ, ICON_SZ/2, ICON_SZ/2, x + BAR_SZ/2, y + ICON_SZ/2, 0, Col.W);
  }

  private void drawBar(UiDrawer uiDrawer, TexMan texMan, float x, float y, float perc) {
    float h = ICON_SZ * .6f;
    y += ICON_SZ * .2f;
    uiDrawer.draw(texMan.whiteTex, BAR_SZ, h, 0, 0, x, y, 0, Col.G);
    uiDrawer.draw(texMan.whiteTex, BAR_SZ * perc, h, 0, 0, x, y, 0, Col.LG);
  }

  private void drawIcons(UiDrawer uiDrawer, float x, float y, int count, TextureAtlas.AtlasRegion tex) {
    int excess = count - MAX_ICON_COUNT;
    int iconCount = excess > 0 ? MAX_ICON_COUNT : count;
    for (int i = 0; i < iconCount; i++) {
      uiDrawer.draw(tex, ICON_SZ, ICON_SZ, 0, 0, x, y, 0, Col.W);
      x += ICON_SZ;
    }
    if (excess > 0) uiDrawer.drawString("+" + excess, x, y + .25f * ICON_SZ, FontSize.HUD, false, Col.W); // hack!
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

  public boolean isLeft() {
    return myLeftCtrl.isOn();
  }

  public boolean isRight() {
    return myRightCtrl.isOn();
  }

  public boolean isUp() {
    return myUpCtrl.isOn();
  }

  public boolean isDown() {
    return myDownCtrl.isOn();
  }

  public boolean isShoot() {
    return myShootCtrl.isOn();
  }

  public boolean isShoot2() {
    return myShoot2Ctrl.isOn();
  }

  public boolean isSpec() {
    return mySpecCtrl.isOn();
  }

}
