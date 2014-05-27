package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.SolCmp;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.input.Mover;
import com.miloshpetrov.sol2.game.input.Shooter;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.ui.SolInputMan;
import com.miloshpetrov.sol2.ui.SolUiControl;

import java.util.List;

class ShipMixedControl implements ShipUiControl {
  private final SolUiControl myUpCtrl;
  private final SolUiControl myDownCtrl;
  private final Vector2 myMouseWorldPos;
  private final TextureAtlas.AtlasRegion myCursor;

  private boolean myRight;
  private boolean myLeft;
  private boolean myShoot;
  private boolean myShoot2;
  private boolean myAbility;

  public ShipMixedControl(SolCmp cmp, List<SolUiControl> controls) {
    myCursor = cmp.getTexMan().getTex("ui/cursorTarget", null);
    myMouseWorldPos = new Vector2();
    myUpCtrl = new SolUiControl(null, false, Input.Keys.W);
    controls.add(myUpCtrl);
    myDownCtrl = new SolUiControl(null, false, Input.Keys.S);
    controls.add(myDownCtrl);
  }

  @Override
  public void update(SolCmp cmp) {
    blur();
    SolInputMan im = cmp.getInputMan();
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
        myShoot = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        myShoot2 = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
        myAbility = Gdx.input.isButtonPressed(Input.Buttons.MIDDLE);
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
    return myUpCtrl.isOn();
  }

  @Override
  public boolean isDown() {
    return myDownCtrl.isOn();
  }

  @Override
  public boolean isShoot() {
    return myShoot;
  }

  @Override
  public boolean isShoot2() {
    return myShoot2;
  }

  @Override
  public boolean isAbility() {
    return myAbility;
  }

  @Override
  public TextureAtlas.AtlasRegion getInGameTex() {
    return myCursor;
  }

  @Override
  public void blur() {
    myLeft = false;
    myRight = false;
    myShoot = false;
    myShoot2 = false;
    myAbility = false;
  }
}
