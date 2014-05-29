package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
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
  public static final float V_PAD = H_PAD;

  private final List<SolUiControl> myControls;
  private final CollisionWarnDrawer myCollisionWarnDrawer;
  private final SunWarnDrawer mySunWarnDrawer;
  private final ZoneNameAnnouncer myZoneNameAnnouncer;
  private final BorderDrawer myBorderDrawer;
  private final TextureAtlas.AtlasRegion myLifeTex;
  private final TextureAtlas.AtlasRegion myInfinityTex;
  private final TextureAtlas.AtlasRegion myWaitTex;
  private final TextureAtlas.AtlasRegion myCompassTex;

  public final ShipUiControl shipControl;
  private final SolUiControl myMenuCtrl;
  public final SolUiControl mapCtrl;
  private final SolUiControl myInvCtrl;
  public final SolUiControl talkCtrl;
  private final SolUiControl myPauseCtrl;
  private final Color myCompassTint;
  private final TextPlace myRepairsText;
  private final TextPlace myG1AmmoText;
  private final TextPlace myG2AmmoText;
  private final TextPlace myChargesText;
  private final TextPlace myMoneyText;


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
    myMenuCtrl = new SolUiControl(rightPaneLayout.buttonRect(0), true, Input.Keys.ESCAPE);
    myMenuCtrl.setDisplayName("Menu");
    myControls.add(myMenuCtrl);
    mapCtrl = new SolUiControl(rightPaneLayout.buttonRect(1), true, Input.Keys.TAB);
    mapCtrl.setDisplayName("Map");
    myControls.add(mapCtrl);
    myInvCtrl = new SolUiControl(rightPaneLayout.buttonRect(2), true, Input.Keys.I);
    myInvCtrl.setDisplayName("Items");
    myControls.add(myInvCtrl);
    talkCtrl = new SolUiControl(rightPaneLayout.buttonRect(3), true, Input.Keys.T);
    talkCtrl.setDisplayName("Talk");
    myControls.add(talkCtrl);
    myPauseCtrl = new SolUiControl(null, true, Input.Keys.P);
    myControls.add(myPauseCtrl);


    myCollisionWarnDrawer = new CollisionWarnDrawer(r);
    mySunWarnDrawer = new SunWarnDrawer(r);
    myZoneNameAnnouncer = new ZoneNameAnnouncer();
    myBorderDrawer = new BorderDrawer(r, cmp);

    TexMan texMan = cmp.getTexMan();
    myLifeTex = texMan.getTex(TexMan.ICONS_DIR + "life", null);
    myInfinityTex = texMan.getTex(TexMan.ICONS_DIR + "infinity", null);
    myWaitTex = texMan.getTex(TexMan.ICONS_DIR + "wait", null);
    myCompassTex = texMan.getTex("ui/compass", null);
    myCompassTint = Col.col(1, 0);
    myRepairsText = new TextPlace();
    myG1AmmoText = new TextPlace();
    myG2AmmoText = new TextPlace();
    myChargesText = new TextPlace();
    myMoneyText = new TextPlace();
  }

  public void maybeDrawHeight(UiDrawer drawer, SolCmp cmp) {
    SolGame game = cmp.getGame();
    Planet np = game.getPlanetMan().getNearestPlanet();
    SolCam cam = game.getCam();
    Vector2 camPos = cam.getPos();
    if (np != null && np.getPos().dst(camPos) < np.getFullHeight()) {
      drawHeight(drawer, np, camPos, cam.getAngle());
    }
  }

  private void drawHeight(UiDrawer drawer, Planet np, Vector2 camPos, float camAngle) {
    float toPlanet = camPos.dst(np.getPos());
    toPlanet -= np.getGroundHeight();
    if (Const.ATM_HEIGHT < toPlanet) return;
    float perc = toPlanet / Const.ATM_HEIGHT;
    float sz = .08f;
    float maxY = 1 - sz/2;
    float y = 1 - perc;
    myCompassTint.a = SolMath.clamp(1.5f * y);
    if (maxY < y) y = maxY;
    float angle = np.getAngle() - camAngle;
    drawer.draw(myCompassTex, sz, sz, sz/2, sz/2, sz/2, y, angle, myCompassTint);
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
    if (hero != null && !inputMan.isScreenOn(screens.inventoryScreen)) {
      if (hero.getItemContainer().hasNew()) myInvCtrl.enableWarn();
    }
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

  private boolean drawGunStat(UiDrawer uiDrawer, TexMan texMan, SolShip hero, boolean secondary, float col0, float col1,
    float col2, float y)
  {
    GunItem g = hero.getHull().getGun(secondary);
    if (g == null) return false;
    TextureAtlas.AtlasRegion tex = g.config.icon;

    uiDrawer.draw(tex, ICON_SZ, ICON_SZ, 0, 0, col0, y, 0, Col.W);
    if (g.reloadAwait <= 0) {
      int maxAmmo = g.config.clipConf.size;
      float ammoPerc = g.ammo * 1f / maxAmmo;
      drawBar(uiDrawer, texMan, col1, y, ammoPerc);
    } else {
      drawWait(uiDrawer, col1, y);
    }
    if (!g.config.clipConf.infinite) {
      int clipCount = hero.getItemContainer().count(g.config.clipConf.example);
      drawIcons(uiDrawer, col2, y, clipCount, g.config.clipConf.icon, secondary ? myG2AmmoText : myG1AmmoText);
    } else {
      uiDrawer.draw(myInfinityTex, ICON_SZ, ICON_SZ, 0, 0, col2, y, 0, Col.W);
    }
    return true;
  }

  private void drawWait(UiDrawer uiDrawer, float x, float y) {
    uiDrawer.draw(myWaitTex, ICON_SZ, ICON_SZ, ICON_SZ/2, ICON_SZ/2, x + BAR_SZ/2, y + ICON_SZ/2, 0, Col.W);
  }

  private void drawBar(UiDrawer uiDrawer, TexMan texMan, float x, float y, float perc) {
    uiDrawer.draw(uiDrawer.whiteTex, BAR_SZ, ICON_SZ, 0, 0, x, y, 0, Col.UI_DARK);
    uiDrawer.draw(uiDrawer.whiteTex, BAR_SZ * perc, ICON_SZ, 0, 0, x, y, 0, Col.UI_LIGHT);
  }

  private void drawIcons(UiDrawer uiDrawer, float x, float y, int count, TextureAtlas.AtlasRegion tex,
    TextPlace textPlace) {
    int excess = count - MAX_ICON_COUNT;
    int iconCount = excess > 0 ? MAX_ICON_COUNT : count;
    for (int i = 0; i < iconCount; i++) {
      uiDrawer.draw(tex, ICON_SZ, ICON_SZ, 0, 0, x, y, 0, Col.W);
      x += ICON_SZ + H_PAD;
    }
    if (excess > 0) {
      updateTextPlace(x, y, "+" + excess, textPlace);
    }
  }

  private void updateTextPlace(float x, float y, String text, TextPlace textPlace) {
    textPlace.text = text;
    textPlace.pos.set(x, y + .25f * ICON_SZ);
  }

  @Override
  public boolean isCursorOnBg(SolInputMan.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolCmp cmp) {

  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolCmp cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolCmp cmp) {
    myRepairsText.text = null;
    myG1AmmoText.text = null;
    myG2AmmoText.text = null;
    myChargesText.text = null;
    myMoneyText.text = null;

    maybeDrawHeight(uiDrawer, cmp);
    myBorderDrawer.draw(uiDrawer, cmp);

    SolGame game = cmp.getGame();
    SolShip hero = game.getHero();
    if (hero != null) {
      TexMan texMan = cmp.getTexMan();

      float row = BorderDrawer.TISHCH_SZ + V_PAD;
      float col0 = BorderDrawer.TISHCH_SZ + H_PAD;
      float col1 = col0 + ICON_SZ + H_PAD;
      float col2 = col1 + BAR_SZ + H_PAD;

      Shield shield = hero.getShield();
      if (shield != null) {
        uiDrawer.draw(shield.getIcon(game), ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, Col.W);
        float shieldPerc = shield.getLife() / shield.getMaxLife();
        drawBar(uiDrawer, texMan, col1, row, shieldPerc);
        row += ICON_SZ + V_PAD;
      }

      uiDrawer.draw(myLifeTex, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, Col.W);
      float lifePerc = hero.getLife() / hero.getHull().config.maxLife;
      drawBar(uiDrawer, texMan, col1, row, lifePerc);
      int repairKitCount = hero.getItemContainer().count(game.getItemMan().getRepairExample());
      ItemMan itemMan = game.getItemMan();
      drawIcons(uiDrawer, col2, row, repairKitCount, itemMan.repairIcon, myRepairsText);

      row += ICON_SZ + V_PAD;
      boolean consumed = drawGunStat(uiDrawer, texMan, hero, false, col0, col1, col2, row);
      if (consumed) row += ICON_SZ + V_PAD;
      consumed = drawGunStat(uiDrawer, texMan, hero, true, col0, col1, col2, row);
      if (consumed) row += ICON_SZ + V_PAD;

      ShipAbility ability = hero.getAbility();
      SolItem abilityChargeEx = ability == null ? null : ability.getConfig().getChargeExample();
      if (abilityChargeEx != null) {
        int abilityChargeCount = hero.getItemContainer().count(abilityChargeEx);
        TextureAtlas.AtlasRegion icon = abilityChargeEx.getIcon(game);
        uiDrawer.draw(icon, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, Col.W);
        float chargePerc = 1 - SolMath.clamp(hero.getAbilityAwait() / ability.getConfig().getRechargeTime());
        drawBar(uiDrawer, texMan, col1, row, chargePerc);
        drawIcons(uiDrawer, col2, row, abilityChargeCount, icon, myChargesText);
        row += ICON_SZ + V_PAD;
      }
      uiDrawer.draw(game.getItemMan().moneyIcon, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, Col.W);
      updateTextPlace(col1, row, (int) hero.getMoney() + "", myMoneyText);
    }

    myCollisionWarnDrawer.draw(uiDrawer);
    mySunWarnDrawer.draw(uiDrawer);
  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolCmp cmp) {
    myRepairsText.draw(uiDrawer);
    myG1AmmoText.draw(uiDrawer);
    myG2AmmoText.draw(uiDrawer);
    myChargesText.draw(uiDrawer);
    myMoneyText.draw(uiDrawer);

    myCollisionWarnDrawer.drawText(uiDrawer);
    mySunWarnDrawer.drawText(uiDrawer);
    myZoneNameAnnouncer.drawText(uiDrawer);
  }

  @Override
  public void blurCustom(SolCmp cmp) {
    shipControl.blur();
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

  public static class TextPlace {
    public String text;
    public Vector2 pos = new Vector2();

    public void draw(UiDrawer uiDrawer) {
      uiDrawer.drawString(text, pos.x, pos.y + .25f * ICON_SZ, FontSize.HUD, false, Col.W); // hack!
    }
  }
}
