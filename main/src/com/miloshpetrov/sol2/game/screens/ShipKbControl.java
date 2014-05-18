package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.SolUiControl;

import java.util.List;

public class ShipKbControl implements ShipUiControl {
  private final SolUiControl myLeftCtrl;
  private final SolUiControl myRightCtrl;
  private final SolUiControl myUpCtrl;
  private final SolUiControl myDownCtrl;
  public final SolUiControl myShootCtrl;
  private final SolUiControl myShoot2Ctrl;
  private final SolUiControl myAbilityCtrl;

  public ShipKbControl(SolCmp cmp, float r, List<SolUiControl> controls) {
    boolean showButtons = cmp.isMobile();
    float col0 = 0;
    float col1 = col0 + MainScreen.CELL_SZ;
    float colN0 = r - MainScreen.CELL_SZ;
    float colN1 = colN0 - MainScreen.CELL_SZ;
    float rowN0 = 1 - MainScreen.CELL_SZ;
    float rowN1 = rowN0 - MainScreen.CELL_SZ;

    myLeftCtrl = new SolUiControl(showButtons ? MainScreen.btn(colN1, rowN0) : null, false, Input.Keys.LEFT);
    myLeftCtrl.setDisplayName("Left");
    controls.add(myLeftCtrl);
    myRightCtrl = new SolUiControl(showButtons ? MainScreen.btn(colN0, rowN0) : null, false, Input.Keys.RIGHT);
    myRightCtrl.setDisplayName("Right");
    controls.add(myRightCtrl);
    myUpCtrl = new SolUiControl(showButtons ? MainScreen.btn(col0, rowN0) : null, false, Input.Keys.UP);
    myUpCtrl.setDisplayName("Up");
    controls.add(myUpCtrl);
    myDownCtrl = new SolUiControl(null, true, Input.Keys.DOWN);
    controls.add(myDownCtrl);
    myShootCtrl = new SolUiControl(showButtons ? MainScreen.btn(col0, rowN1) : null, false, Input.Keys.SPACE);
    myShootCtrl.setDisplayName("Primary");
    controls.add(myShootCtrl);
    myShoot2Ctrl = new SolUiControl(showButtons ? MainScreen.btn(col1, rowN0) : null, false, Input.Keys.CONTROL_LEFT);
    myShoot2Ctrl.setDisplayName("Secondary");
    controls.add(myShoot2Ctrl);
    myAbilityCtrl = new SolUiControl(showButtons ? MainScreen.btn(colN0, rowN1) : null, false, Input.Keys.SHIFT_LEFT);
    myAbilityCtrl.setDisplayName("Special");
    controls.add(myAbilityCtrl);
  }

  @Override
  public void update(SolCmp cmp) {
    SolShip hero = cmp.getGame().getHero();
    boolean hasEngine = hero != null && hero.getHull().getEngine() != null;
    myUpCtrl.setEnabled(hasEngine);
    myLeftCtrl.setEnabled(hasEngine);
    myRightCtrl.setEnabled(hasEngine);

    GunItem g1 = hero == null ? null : hero.getHull().getGun(false);
    myShootCtrl.setEnabled(g1 != null && g1.ammo > 0);
    GunItem g2 = hero != null ? hero.getHull().getGun(true) : null;
    myShoot2Ctrl.setEnabled(g2 != null && g2.ammo > 0);
    myAbilityCtrl.setEnabled(hero != null && hero.canUseAbility());
  }

  @Override
  public boolean isLeft() {
    return myLeftCtrl.isOn();
  }

  @Override
  public boolean isRight() {
    return myRightCtrl.isOn();
  }

  @Override
  public boolean isUp() {
    return myUpCtrl.isOn();
  }

  @Override
  public boolean isDown() {
    return myDownCtrl.isOn();
  }

  @Override
  public boolean isShoot() {
    return myShootCtrl.isOn();
  }

  @Override
  public boolean isShoot2() {
    return myShoot2Ctrl.isOn();
  }

  @Override
  public boolean isAbility() {
    return myAbilityCtrl.isOn();
  }

  @Override
  public TextureAtlas.AtlasRegion getInGameTex() {
    return null;
  }
}
