package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Faction;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.Guardian;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class HireShips implements InventoryOperations {

  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myBuyCtrl;

  public HireShips(InventoryScreen inventoryScreen, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    myBuyCtrl = new SolUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyHireShip());
    myBuyCtrl.setDisplayName("Hire");
    myControls.add(myBuyCtrl);
  }

  @Override
  public ItemContainer getItems(SolGame game) {
    return game.getScreens().talkScreen.getTarget().getTradeContainer().getMercs();
  }

  @Override
  public boolean isUsing(SolGame game, SolItem item) {
    return false;
  }

  @Override
  public float getPriceMul() {
    return 1;
  }

  @Override
  public String getHeader() {
    return "Mercenaries:";
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    SolGame game = cmp.getGame();
    InventoryScreen is = game.getScreens().inventoryScreen;
    SolShip hero = game.getHero();
    TalkScreen talkScreen = game.getScreens().talkScreen;
    if (talkScreen.isTargetFar(hero)) {
      cmp.getInputMan().setScreen(cmp, game.getScreens().mainScreen);
      return;
    }
    SolItem selItem = is.getSelectedItem();
    boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice();
    myBuyCtrl.setDisplayName(enabled ? "Hire" : "---");
    myBuyCtrl.setEnabled(enabled);
    if (!enabled) return;
    if (myBuyCtrl.isJustOff()) {
      boolean hired = hireShip(game, hero, (MercItem) selItem);
      if (hired) hero.setMoney(hero.getMoney() - selItem.getPrice());
    }
  }

  private boolean hireShip(SolGame game, SolShip hero, MercItem selected) {
    ShipConfig config = selected.getConfig();
    Guardian dp = new Guardian(game, config.hull, hero.getPilot(), hero.getPos(), hero.getHull().config, SolMath.rnd(180));
    AiPilot pilot = new AiPilot(dp, true, Faction.LAANI, false, "Merc", Const.AI_DET_DIST);
    Vector2 pos = getPos(game, hero, config.hull);
    if (pos == null) return false;
    FarShip merc = game.getShipBuilder().buildNewFar(game, pos, new Vector2(), 0, 0, pilot, config.items, config.hull, null, true, config.money, null, true);
    game.getObjMan().addFarObjNow(merc);
    return true;
  }

  private Vector2 getPos(SolGame game, SolShip hero, HullConfig hull) {
    Vector2 pos = new Vector2();
    float dist = hero.getHull().config.getApproxRadius() + Guardian.DIST + hull.getApproxRadius();
    Vector2 heroPos = hero.getPos();
    Planet np = game.getPlanetMan().getNearestPlanet();
    boolean nearGround = np.isNearGround(heroPos);
    float fromPlanet = SolMath.angle(np.getPos(), heroPos);
    for (int i = 0; i < 50; i++) {
      float relAngle;
      if (nearGround) {
        relAngle = fromPlanet;
      } else {
        relAngle = SolMath.rnd(180);
      }
      SolMath.fromAl(pos, relAngle, dist);
      pos.add(heroPos);
      if (game.isPlaceEmpty(pos, false)) return pos;
      dist += Guardian.DIST;
    }
    return null;
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

  @Override
  public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {

  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {
    cmp.getGame();
  }

  @Override
  public void drawText(UiDrawer uiDrawer, SolApplication cmp) {

  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }
}
