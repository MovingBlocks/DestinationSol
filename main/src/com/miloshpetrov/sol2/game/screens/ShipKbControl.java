package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.SolUiControl;

import java.util.List;

public class ShipKbControl implements ShipUiControl {
  public final SolUiControl leftCtrl;
  public final SolUiControl rightCtrl;
  public final SolUiControl upCtrl;
  public final SolUiControl myDownCtrl;
  public final SolUiControl shootCtrl;
  public final SolUiControl shoot2Ctrl;
  public final SolUiControl abilityCtrl;

  public ShipKbControl(SolCmp cmp, float r, List<SolUiControl> controls) {
    boolean showButtons = cmp.isMobile();
    float col0 = 0;
    float col1 = col0 + MainScreen.CELL_SZ;
    float colN0 = r - MainScreen.CELL_SZ;
    float colN1 = colN0 - MainScreen.CELL_SZ;
    float rowN0 = 1 - MainScreen.CELL_SZ;
    float rowN1 = rowN0 - MainScreen.CELL_SZ;

    leftCtrl = new SolUiControl(showButtons ? MainScreen.btn(colN1, rowN0, false) : null, false, Input.Keys.LEFT);
    leftCtrl.setDisplayName("Left");
    controls.add(leftCtrl);
    rightCtrl = new SolUiControl(showButtons ? MainScreen.btn(colN0, rowN0, false) : null, false, Input.Keys.RIGHT);
    rightCtrl.setDisplayName("Right");
    controls.add(rightCtrl);
    upCtrl = new SolUiControl(showButtons ? MainScreen.btn(col0, rowN0, false) : null, false, Input.Keys.UP);
    upCtrl.setDisplayName("Fwd");
    controls.add(upCtrl);
    myDownCtrl = new SolUiControl(null, true, Input.Keys.DOWN);
    controls.add(myDownCtrl);
    shootCtrl = new SolUiControl(showButtons ? MainScreen.btn(col0, rowN1, false) : null, false, Input.Keys.SPACE);
    shootCtrl.setDisplayName("Gun 1");
    controls.add(shootCtrl);
    shoot2Ctrl = new SolUiControl(showButtons ? MainScreen.btn(col1, rowN0, false) : null, false, Input.Keys.CONTROL_LEFT);
    shoot2Ctrl.setDisplayName("Gun 2");
    controls.add(shoot2Ctrl);
    abilityCtrl = new SolUiControl(showButtons ? MainScreen.btn(colN0, rowN1, false) : null, false, Input.Keys.SHIFT_LEFT);
    abilityCtrl.setDisplayName("Ability");
    controls.add(abilityCtrl);
  }

  @Override
  public void update(SolCmp cmp, boolean enabled) {
    if (!enabled) {
      upCtrl.setEnabled(false);
      leftCtrl.setEnabled(false);
      rightCtrl.setEnabled(false);
      shootCtrl.setEnabled(false);
      shoot2Ctrl.setEnabled(false);
      abilityCtrl.setEnabled(false);
      return;
    }
    SolShip hero = cmp.getGame().getHero();
    boolean hasEngine = hero != null && hero.getHull().getEngine() != null;
    upCtrl.setEnabled(hasEngine);
    leftCtrl.setEnabled(hasEngine);
    rightCtrl.setEnabled(hasEngine);

    GunItem g1 = hero == null ? null : hero.getHull().getGun(false);
    shootCtrl.setEnabled(g1 != null && g1.ammo > 0);
    GunItem g2 = hero != null ? hero.getHull().getGun(true) : null;
    shoot2Ctrl.setEnabled(g2 != null && g2.ammo > 0);
    abilityCtrl.setEnabled(hero != null && hero.canUseAbility());
  }

  @Override
  public boolean isLeft() {
    return leftCtrl.isOn();
  }

  @Override
  public boolean isRight() {
    return rightCtrl.isOn();
  }

  @Override
  public boolean isUp() {
    return upCtrl.isOn();
  }

  @Override
  public boolean isDown() {
    return myDownCtrl.isOn();
  }

  @Override
  public boolean isShoot() {
    return shootCtrl.isOn();
  }

  @Override
  public boolean isShoot2() {
    return shoot2Ctrl.isOn();
  }

  @Override
  public boolean isAbility() {
    return abilityCtrl.isOn();
  }

  @Override
  public TextureAtlas.AtlasRegion getInGameTex() {
    return null;
  }

  @Override
  public void blur() {

  }
}
