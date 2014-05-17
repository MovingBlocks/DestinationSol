package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.ShipAbility;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.menu.GameOptions;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements SolUiScreen {
  public static final float ICON_SZ = .03f;
  public static final float BAR_SZ = ICON_SZ * 5;
  public static final int MAX_ICON_COUNT = 3;
  public static final float CELL_SZ = .2f;
  public static final float H_PAD = .005f;
  public static final float V_PAD = H_PAD * 2;

  private final List<SolUiControl> myControls;
  private final CollisionWarnDrawer myCollisionWarnDrawer;
  private final SunWarnDrawer mySunWarnDrawer;
  private final ZoneNameAnnouncer myZoneNameAnnouncer;
  private final BorderDrawer myBorderDrawer;
  private final TextureAtlas.AtlasRegion myLifeTex;
  private final TextureAtlas.AtlasRegion myInfinityTex;
  private final TextureAtlas.AtlasRegion myWaitTex;
  private final TextureAtlas.AtlasRegion myShieldTex;
  public final ShipUiControl shipControl;

  private final SolUiControl myMenuCtrl;
  public final SolUiControl mapCtrl;
  private final SolUiControl myInvCtrl;
  public final SolUiControl talkCtrl;
  private final SolUiControl myPauseCtrl;


  public MainScreen(float r, RightPaneLayout rightPaneLayout, SolCmp cmp) {
    myControls = new ArrayList<SolUiControl>();

    int ct = cmp.getOptions().controlType;
    if (ct == GameOptions.CONTROL_KB) {
      shipControl = new ShipKbControl(cmp, r, myControls);
    } else if (ct == GameOptions.CONTROL_MIXED) {
      shipControl = new ShipMixedControl(cmp, myControls);
    } else {
      shipControl = new ShipMouseControl(cmp);
    }
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


    myCollisionWarnDrawer = new CollisionWarnDrawer(r);
    mySunWarnDrawer = new SunWarnDrawer(r);
    myZoneNameAnnouncer = new ZoneNameAnnouncer();
    myBorderDrawer = new BorderDrawer(r, cmp);

    TexMan texMan = cmp.getTexMan();
    myLifeTex = texMan.getTex(TexMan.ICONS_DIR + "life", null);
    myShieldTex = texMan.getTex(TexMan.ICONS_DIR + "shield", null);
    myInfinityTex = texMan.getTex(TexMan.ICONS_DIR + "infinity", null);
    myWaitTex = texMan.getTex(TexMan.ICONS_DIR + "wait", null);
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

  public static Rectangle btn(float x, float y) {
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

    myCollisionWarnDrawer.update(game);
    mySunWarnDrawer.update(game);
    myZoneNameAnnouncer.update(game);

    if (myMenuCtrl.isJustOff()) {
      inputMan.setScreen(cmp, screens.menuScreen);
    }

    if (shipControl != null) shipControl.update(cmp);

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
    float har = hero.getHull().config.approxRadius;
    for (SolObj o : game.getObjMan().getObjs()) {
      if (!(o instanceof SolShip)) continue;
      SolShip ship = (SolShip) o;
      if (fracMan.areEnemies(hero, ship)) continue;
      if (ship.getTradeContainer() == null) continue;
      float dst = ship.getPos().dst(hero.getPos());
      float ar = ship.getHull().config.approxRadius;
      if (minDist < dst - har - ar) continue;
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
    myCollisionWarnDrawer.draw(uiDrawer);
    mySunWarnDrawer.draw(uiDrawer);
    myZoneNameAnnouncer.draw(uiDrawer);

    SolShip hero = cmp.getGame().getHero();
    if (hero != null) {
      TexMan texMan = cmp.getTexMan();

      float row = BorderDrawer.TISHCH_SZ + V_PAD;
      float col0 = BorderDrawer.TISHCH_SZ + H_PAD;
      float col1 = col0 + ICON_SZ + H_PAD;
      float col2 = col1 + BAR_SZ + H_PAD;

      Shield shield = hero.getShield();
      if (shield != null) {
        uiDrawer.draw(myShieldTex, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, Col.W);
        float shieldPerc = shield.getLife() / shield.getMaxLife();
        drawBar(uiDrawer, texMan, col1, row, shieldPerc);
        row += ICON_SZ + V_PAD;
      }

      uiDrawer.draw(myLifeTex, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, Col.W);
      float lifePerc = hero.getLife() / hero.getHull().config.maxLife;
      drawBar(uiDrawer, texMan, col1, row, lifePerc);
      int repairKitCount = hero.getItemContainer().count(RepairItem.EXAMPLE);
      ItemMan itemMan = cmp.getGame().getItemMan();
      drawIcons(uiDrawer, col2, row, repairKitCount, itemMan.repairIcon);

      row += ICON_SZ + V_PAD;
      boolean consumed = drawGunStat(uiDrawer, texMan, hero, false, col0, col1, col2, row);
      if (consumed) row += ICON_SZ + V_PAD;
      consumed = drawGunStat(uiDrawer, texMan, hero, true, col0, col1, col2, row);
      if (consumed) row += ICON_SZ + V_PAD;

      ShipAbility ability = hero.getAbility();
      SolItem abilityChargeEx = ability == null ? null : ability.getChargeExample();
      if (abilityChargeEx != null) {
        int abilityChargeCount = hero.getItemContainer().count(abilityChargeEx);
        TextureAtlas.AtlasRegion icon = abilityChargeEx.getIcon(cmp.getGame());
        uiDrawer.draw(icon, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, Col.W);
        float chargePerc = 1 - SolMath.clamp(hero.getAbilityAwait() / ability.getRechargeTime());
        drawBar(uiDrawer, texMan, col1, row, chargePerc);
        drawIcons(uiDrawer, col2, row, abilityChargeCount, icon);
      }
    }
  }

  private boolean drawGunStat(UiDrawer uiDrawer, TexMan texMan, SolShip hero, boolean secondary, float col0, float col1,
    float col2, float y)
  {
    GunItem g = hero.getHull().getGun(secondary);
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
    uiDrawer.draw(texMan.whiteTex, BAR_SZ, ICON_SZ, 0, 0, x, y, 0, Col.UI_DARK);
    uiDrawer.draw(texMan.whiteTex, BAR_SZ * perc, ICON_SZ, 0, 0, x, y, 0, Col.UI_LIGHT);
  }

  private void drawIcons(UiDrawer uiDrawer, float x, float y, int count, TextureAtlas.AtlasRegion tex) {
    int excess = count - MAX_ICON_COUNT;
    int iconCount = excess > 0 ? MAX_ICON_COUNT : count;
    for (int i = 0; i < iconCount; i++) {
      uiDrawer.draw(tex, ICON_SZ, ICON_SZ, 0, 0, x, y, 0, Col.W);
      x += ICON_SZ + H_PAD;
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
    return shipControl.isLeft();
  }

  public boolean isRight() {
    return shipControl.isRight();
  }

  public boolean isUp() {
    return shipControl.isUp();
  }

  public boolean isDown() {
    return shipControl.isDown();
  }

  public boolean isShoot() {
    return shipControl.isShoot();
  }

  public boolean isShoot2() {
    return shipControl.isShoot2();
  }

  public boolean isAbility() {
    return shipControl.isAbility();
  }

}
