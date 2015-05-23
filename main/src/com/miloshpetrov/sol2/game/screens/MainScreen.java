package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.SolColor;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.ShipAbility;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.GameOptions;
import com.miloshpetrov.sol2.ui.*;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements SolUiScreen {
  public static final float ICON_SZ = .03f;
  public static final float BAR_SZ = ICON_SZ * 5;
  public static final int MAX_ICON_COUNT = 3;
  public static final float CELL_SZ = .17f;
  public static final float H_PAD = .005f;
  public static final float V_PAD = H_PAD;

  private final List<SolUiControl> myControls;
  private final ZoneNameAnnouncer myZoneNameAnnouncer;
  private final BorderDrawer myBorderDrawer;
  private final TextureAtlas.AtlasRegion myLifeTex;
  private final TextureAtlas.AtlasRegion myInfinityTex;
  private final TextureAtlas.AtlasRegion myWaitTex;
  private final TextureAtlas.AtlasRegion myCompassTex;
  private final List<WarnDrawer> myWarnDrawers;

  public final ShipUiControl shipControl;
  private final SolUiControl myMenuCtrl;
  public final SolUiControl mapCtrl;
  public final SolUiControl invCtrl;
  public final SolUiControl talkCtrl;
  private final SolUiControl myPauseCtrl;
  private final Color myCompassTint;
  private final TextPlace myLifeTp;
  private final TextPlace myRepairsExcessTp;
  private final TextPlace myShieldLifeTp;
  private final TextPlace myG1AmmoTp;
  private final TextPlace myG1AmmoExcessTp;
  private final TextPlace myG2AmmoTp;
  private final TextPlace myG2AmmoExcessTp;
  private final TextPlace myChargesExcessTp;
  private final TextPlace myMoneyExcessTp;
  public static final float HELPER_ROW_1 = 1 - 3f * MainScreen.CELL_SZ;
  public static final float HELPER_ROW_2 = HELPER_ROW_1 - .5f * MainScreen.CELL_SZ;


  public MainScreen(float r, RightPaneLayout rightPaneLayout, SolApplication cmp) {
    myControls = new ArrayList<SolUiControl>();
    GameOptions gameOptions = cmp.getOptions();

    int ct = cmp.getOptions().controlType;
    if (ct == GameOptions.CONTROL_KB) {
      shipControl = new ShipKbControl(cmp, r, myControls);
    } else if (ct == GameOptions.CONTROL_MIXED) {
      shipControl = new ShipMixedControl(cmp, myControls);
    } else {
      shipControl = new ShipMouseControl(cmp);
    }
    boolean mobile = cmp.isMobile();
    float lastCol = r - MainScreen.CELL_SZ;
    Rectangle menuArea = mobile ? btn(0, HELPER_ROW_2, true) : rightPaneLayout.buttonRect(0);
    myMenuCtrl = new SolUiControl(menuArea, true, gameOptions.getKeyMenu());
    myMenuCtrl.setDisplayName("Menu");
    myControls.add(myMenuCtrl);
    Rectangle mapArea = mobile ? btn(0, HELPER_ROW_1, true) : rightPaneLayout.buttonRect(1);
    mapCtrl = new SolUiControl(mapArea, true, gameOptions.getKeyMap());
    mapCtrl.setDisplayName("Map");
    myControls.add(mapCtrl);
    Rectangle invArea = mobile ? btn(lastCol, HELPER_ROW_1, true) : rightPaneLayout.buttonRect(2);
    invCtrl = new SolUiControl(invArea, true, Input.Keys.I);
    invCtrl.setDisplayName("Items");
    myControls.add(invCtrl);
    Rectangle talkArea = mobile ? btn(lastCol, HELPER_ROW_2, true) : rightPaneLayout.buttonRect(3);
    talkCtrl = new SolUiControl(talkArea, true, Input.Keys.T);
    talkCtrl.setDisplayName("Talk");
    myControls.add(talkCtrl);
    myPauseCtrl = new SolUiControl(null, true, Input.Keys.P);
    myControls.add(myPauseCtrl);


    myWarnDrawers = new ArrayList<WarnDrawer>();
    myWarnDrawers.add(new CollisionWarnDrawer(r));
    myWarnDrawers.add(new SunWarnDrawer(r));
    myWarnDrawers.add(new EnemyWarn(r));
    myWarnDrawers.add(new DmgWarnDrawer(r));
    myWarnDrawers.add(new NoShieldWarn(r));
    myWarnDrawers.add(new NoArmorWarn(r));

    myZoneNameAnnouncer = new ZoneNameAnnouncer();
    myBorderDrawer = new BorderDrawer(r, cmp);

    TextureManager textureManager = cmp.getTexMan();
    myLifeTex = textureManager.getTex(TextureManager.ICONS_DIR + "life", null);
    myInfinityTex = textureManager.getTex(TextureManager.ICONS_DIR + "infinity", null);
    myWaitTex = textureManager.getTex(TextureManager.ICONS_DIR + "wait", null);
    myCompassTex = textureManager.getTex("ui/compass", null);
    myCompassTint = SolColor.col(1, 0);

    myLifeTp = new TextPlace(SolColor.W50);
    myRepairsExcessTp = new TextPlace(SolColor.W);
    myShieldLifeTp = new TextPlace(SolColor.W50);
    myG1AmmoTp = new TextPlace(SolColor.W50);
    myG1AmmoExcessTp = new TextPlace(SolColor.W);
    myG2AmmoTp = new TextPlace(SolColor.W50);
    myG2AmmoExcessTp = new TextPlace(SolColor.W);
    myChargesExcessTp = new TextPlace(SolColor.W);
    myMoneyExcessTp = new TextPlace(SolColor.W);
  }

  public void maybeDrawHeight(UiDrawer drawer, SolApplication cmp) {
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

  public static Rectangle btn(float x, float y, boolean halfHeight) {
    float gap = .01f;
    float cellH = CELL_SZ;
    if (halfHeight) cellH /= 2;
    return new Rectangle(x + gap, y + gap, CELL_SZ - gap * 2, cellH - gap * 2);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (DebugOptions.PRINT_BALANCE) {
      cmp.finishGame();
      return;
    }
    SolGame game = cmp.getGame();
    SolInputManager inputMan = cmp.getInputMan();
    GameScreens screens = game.getScreens();
    SolShip hero = game.getHero();

    for (int i = 0, sz = myWarnDrawers.size(); i < sz; i++) {
      WarnDrawer wd = myWarnDrawers.get(i);
      wd.update(game);
    }

    myZoneNameAnnouncer.update(game);

    if (myMenuCtrl.isJustOff()) {
      inputMan.setScreen(cmp, screens.menuScreen);
    }

    boolean controlsEnabled = inputMan.getTopScreen() == this;
    shipControl.update(cmp, controlsEnabled);

    if (mapCtrl.isJustOff()) {
      inputMan.setScreen(cmp, screens.mapScreen);
    }

    invCtrl.setEnabled(hero != null);
    if (hero != null && !inputMan.isScreenOn(screens.inventoryScreen)) {
      if (hero.getItemContainer().hasNew()) invCtrl.enableWarn();
    }
    if (invCtrl.isJustOff()) {
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
    List<SolObject> objs = game.getObjMan().getObjs();
    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      SolObject o = objs.get(i);
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
      SolApplication cmp = game.getCmp();
      SolInputManager inputMan = cmp.getInputMan();
      boolean isOn = inputMan.isScreenOn(talkScreen);
      inputMan.setScreen(cmp, this);
      if (!isOn) {
        talkScreen.setTarget(target);
        inputMan.addScreen(cmp, talkScreen);
      }
    }
  }

  private boolean drawGunStat(UiDrawer uiDrawer, SolShip hero, boolean secondary, float col0, float col1,
    float col2, float y)
  {
    GunItem g = hero.getHull().getGun(secondary);
    if (g == null) return false;
    TextureAtlas.AtlasRegion tex = g.config.icon;

    uiDrawer.draw(tex, ICON_SZ, ICON_SZ, 0, 0, col0, y, 0, SolColor.W);
    float curr;
    float max;
    if (g.reloadAwait > 0) {
      max = g.config.reloadTime;
      curr = max - g.reloadAwait;
    } else {
      curr = g.ammo;
      max = g.config.clipConf.size;
    }
    TextPlace ammoTp = g.reloadAwait > 0 ? null : secondary ? myG2AmmoTp : myG1AmmoTp;
    drawBar(uiDrawer, col1, y, curr, max, ammoTp);
    if (g.reloadAwait > 0) drawWait(uiDrawer, col1, y);
    if (!g.config.clipConf.infinite) {
      int clipCount = hero.getItemContainer().count(g.config.clipConf.example);
      drawIcons(uiDrawer, col2, y, clipCount, g.config.clipConf.icon, secondary ? myG2AmmoExcessTp : myG1AmmoExcessTp);
    } else {
      uiDrawer.draw(myInfinityTex, ICON_SZ, ICON_SZ, 0, 0, col2, y, 0, SolColor.W);
    }
    return true;
  }

  private void drawWait(UiDrawer uiDrawer, float x, float y) {
    uiDrawer.draw(myWaitTex, ICON_SZ, ICON_SZ, ICON_SZ/2, ICON_SZ/2, x + BAR_SZ/2, y + ICON_SZ/2, 0, SolColor.W);
  }

  private void drawBar(UiDrawer uiDrawer, float x, float y, float curr, float max, TextPlace tp) {
    float perc = curr / max;
    uiDrawer.draw(uiDrawer.whiteTex, BAR_SZ, ICON_SZ, 0, 0, x, y, 0, SolColor.UI_DARK);
    uiDrawer.draw(uiDrawer.whiteTex, BAR_SZ * perc, ICON_SZ, 0, 0, x, y, 0, SolColor.UI_LIGHT);
    if (tp != null && max > 1 && curr > 0) {
      tp.text = (int) curr + "/" + (int) max;
      tp.pos.set(x + BAR_SZ/2, y + ICON_SZ/2);
    }
  }

  private void drawIcons(UiDrawer uiDrawer, float x, float y, int count, TextureAtlas.AtlasRegion tex,
    TextPlace textPlace) {
    int excess = count - MAX_ICON_COUNT;
    int iconCount = excess > 0 ? MAX_ICON_COUNT : count;
    for (int i = 0; i < iconCount; i++) {
      uiDrawer.draw(tex, ICON_SZ, ICON_SZ, 0, 0, x, y, 0, SolColor.W);
      x += ICON_SZ + H_PAD;
    }
    if (excess > 0) {
      updateTextPlace(x, y, "+" + excess, textPlace);
    }
  }

  private void updateTextPlace(float x, float y, String text, TextPlace textPlace) {
    textPlace.text = text;
    textPlace.pos.set(x + ICON_SZ/2, y + ICON_SZ/2);
  }

  @Override
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(SolApplication cmp) {

  }

  @Override
  public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {
    myLifeTp.text = null;
    myRepairsExcessTp.text = null;
    myShieldLifeTp.text = null;
    myG1AmmoTp.text = null;
    myG1AmmoExcessTp.text = null;
    myG2AmmoTp.text = null;
    myG2AmmoExcessTp.text = null;
    myChargesExcessTp.text = null;
    myMoneyExcessTp.text = null;

    maybeDrawHeight(uiDrawer, cmp);
    myBorderDrawer.draw(uiDrawer, cmp);

    SolGame game = cmp.getGame();
    SolShip hero = game.getHero();
    if (hero != null) {
      float row = BorderDrawer.TISHCH_SZ + V_PAD;
      float col0 = BorderDrawer.TISHCH_SZ + H_PAD;
      float col1 = col0 + ICON_SZ + H_PAD;
      float col2 = col1 + BAR_SZ + H_PAD;

      Shield shield = hero.getShield();
      if (shield != null) {
        uiDrawer.draw(shield.getIcon(game), ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, SolColor.W);
        drawBar(uiDrawer, col1, row, shield.getLife(), shield.getMaxLife(), myShieldLifeTp);
        row += ICON_SZ + V_PAD;
      }

      uiDrawer.draw(myLifeTex, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, SolColor.W);
      drawBar(uiDrawer, col1, row, hero.getLife(), hero.getHull().config.maxLife, myLifeTp);
      int repairKitCount = hero.getItemContainer().count(game.getItemMan().getRepairExample());
      ItemMan itemMan = game.getItemMan();
      drawIcons(uiDrawer, col2, row, repairKitCount, itemMan.repairIcon, myRepairsExcessTp);

      row += ICON_SZ + V_PAD;
      boolean consumed = drawGunStat(uiDrawer, hero, false, col0, col1, col2, row);
      if (consumed) row += ICON_SZ + V_PAD;
      consumed = drawGunStat(uiDrawer, hero, true, col0, col1, col2, row);
      if (consumed) row += ICON_SZ + V_PAD;

      ShipAbility ability = hero.getAbility();
      SolItem abilityChargeEx = ability == null ? null : ability.getConfig().getChargeExample();
      if (abilityChargeEx != null) {
        int abilityChargeCount = hero.getItemContainer().count(abilityChargeEx);
        TextureAtlas.AtlasRegion icon = abilityChargeEx.getIcon(game);
        uiDrawer.draw(icon, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, SolColor.W);
        float chargePerc = 1 - SolMath.clamp(hero.getAbilityAwait() / ability.getConfig().getRechargeTime());
        drawBar(uiDrawer, col1, row, chargePerc, 1, null);
        if (chargePerc < 1) drawWait(uiDrawer, col1, row);
        drawIcons(uiDrawer, col2, row, abilityChargeCount, icon, myChargesExcessTp);
        row += ICON_SZ + V_PAD;
      }
      uiDrawer.draw(game.getItemMan().moneyIcon, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, SolColor.W);
      updateTextPlace(col1, row, (int) hero.getMoney() + "", myMoneyExcessTp);
    }

    for (int i = 0, sz = myWarnDrawers.size(); i < sz; i++) {
      WarnDrawer wd = myWarnDrawers.get(i);
      if (wd.drawPerc > 0) {
        wd.draw(uiDrawer);
        break;
      }
    }
  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
    myLifeTp.draw(uiDrawer);
    myRepairsExcessTp.draw(uiDrawer);
    myShieldLifeTp.draw(uiDrawer);
    myG1AmmoTp.draw(uiDrawer);
    myG1AmmoExcessTp.draw(uiDrawer);
    myG2AmmoTp.draw(uiDrawer);
    myG2AmmoExcessTp.draw(uiDrawer);
    myChargesExcessTp.draw(uiDrawer);
    myMoneyExcessTp.draw(uiDrawer);

    for (int i = 0, sz = myWarnDrawers.size(); i < sz; i++) {
      WarnDrawer wd = myWarnDrawers.get(i);
      if (wd.drawPerc > 0) {
        wd.drawText(uiDrawer);
        break;
      }
    }

    myZoneNameAnnouncer.drawText(uiDrawer);
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public void blurCustom(SolApplication cmp) {
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
    public final Color color;

    public TextPlace(Color col) {
      color = new Color(col);
    }

    public void draw(UiDrawer uiDrawer) {
      uiDrawer.drawString(text, pos.x, pos.y, FontSize.HUD, true, color);
    }
  }

  private static class NoShieldWarn extends WarnDrawer {
    public NoShieldWarn(float r) {
      super(r, "No Shield");
    }
    protected boolean shouldWarn(SolGame game) {
      SolShip h = game.getHero();
      if (h == null) return false;
      return h.getShield() == null;
    }
  }

  private static class NoArmorWarn extends WarnDrawer {
    public NoArmorWarn(float r) {
      super(r, "No Armor");
    }
    protected boolean shouldWarn(SolGame game) {
      SolShip h = game.getHero();
      if (h == null) return false;
      return h.getArmor() == null;
    }
  }

  private static class EnemyWarn extends WarnDrawer {
    public EnemyWarn(float r) {
      super(r, "Dangerous\nEnemy");
    }
    protected boolean shouldWarn(SolGame game) {
      SolShip h = game.getHero();
      if (h == null) return false;
      float heroCap = HardnessCalc.getShipDmgCap(h);
      List<SolObject> objs = game.getObjMan().getObjs();
      FractionMan fm = game.getFractionMan();
      SolCam cam = game.getCam();
      float viewDist = cam.getViewDist();
      float dps = 0;
      for (int i = 0, sz = objs.size(); i < sz; i++) {
        SolObject o = objs.get(i);
        if (!(o instanceof SolShip)) continue;
        SolShip ship = (SolShip) o;
        if (viewDist < ship.getPos().dst(h.getPos())) continue;
        if (!fm.areEnemies(h, ship)) continue;
        dps += HardnessCalc.getShipDps(ship);
        if (HardnessCalc.isDangerous(heroCap, dps)) return true;
      }
      return false;
    }
  }
}
