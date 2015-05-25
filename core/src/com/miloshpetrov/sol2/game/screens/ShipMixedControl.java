package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.SolApplication;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.input.Mover;
import com.miloshpetrov.sol2.game.input.Shooter;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.SolInputManager;
import com.miloshpetrov.sol2.ui.SolUiControl;

import java.util.List;

public class ShipMixedControl implements ShipUiControl {
  public static final int SHOOT_HACK = Input.Keys.NUM_5;
  public static final int SHOOT_2_HACK = Input.Keys.NUM_6;
  public static final int ABILITY_HACK = Input.Keys.NUM_7;
  public final SolUiControl upCtrl;
  private final SolUiControl myDownCtrl;
  private final Vector2 myMouseWorldPos;
  private final TextureAtlas.AtlasRegion myCursor;
  public final SolUiControl shootCtrl;
  public final SolUiControl shoot2Ctrl;
  public final SolUiControl abilityCtrl;

  private boolean myRight;
  private boolean myLeft;

  public ShipMixedControl(SolApplication cmp, List<SolUiControl> controls) {
    myCursor = cmp.getTexMan().getTex("ui/cursorTarget", null);
    myMouseWorldPos = new Vector2();
    upCtrl = new SolUiControl(null, false, Input.Keys.W);
    controls.add(upCtrl);
    myDownCtrl = new SolUiControl(null, false, Input.Keys.S);
    controls.add(myDownCtrl);
    shootCtrl = new SolUiControl(null, false, SHOOT_HACK);
    controls.add(shootCtrl);
    shoot2Ctrl = new SolUiControl(null, false, SHOOT_2_HACK);
    controls.add(shoot2Ctrl);
    abilityCtrl = new SolUiControl(null, false, ABILITY_HACK, Input.Keys.SHIFT_LEFT);
    controls.add(abilityCtrl);
  }

  @Override
  public void update(SolApplication cmp, boolean enabled) {
    blur();
    if (!enabled) return;
    SolInputManager im = cmp.getInputMan();
    SolGame g = cmp.getGame();
    SolShip h = g.getHero();
    if (h != null) {
      myMouseWorldPos.set(Gdx.input.getX(), Gdx.input.getY());
      g.getCam().screenToWorld(myMouseWorldPos);
      float desiredAngle = SolMath.angle(h.getPos(), myMouseWorldPos);
      Boolean ntt = Mover.needsToTurn(h.getAngle(), desiredAngle, h.getRotSpd(), h.getRotAcc(), Shooter.MIN_SHOOT_AAD);
      if (ntt != null) {
        if (ntt) myRight = true; else myLeft = true;
      }
      if (!im.isMouseOnUi()) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) shootCtrl.maybeFlashPressed(SHOOT_HACK);
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) shoot2Ctrl.maybeFlashPressed(SHOOT_2_HACK);
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) abilityCtrl.maybeFlashPressed(ABILITY_HACK);
      }
    }
  }

  @Override
  public boolean isLeft() {
    return myLeft;
  }

  @Override
  public boolean isRight() {
    return myRight;
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
    return myCursor;
  }

  @Override
  public void blur() {
    myLeft = false;
    myRight = false;
  }
}
