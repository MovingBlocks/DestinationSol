package com.miloshpetrov.sol2.game.input;


import com.miloshpetrov.sol2.game.Fraction;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.screens.MainScreen;
import com.miloshpetrov.sol2.game.ship.FarShip;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class PlayerPilot implements Pilot {

  private final MainScreen myScreen;

  public PlayerPilot(MainScreen screen) {
    myScreen = screen;
  }

  @Override
  public void update(SolGame game, SolShip ship, SolShip nearestEnemy) {
  }

  @Override
  public boolean isUp() {
    return myScreen.isUp();
  }

  @Override
  public boolean isLeft() {
    return myScreen.isLeft();
  }

  @Override
  public boolean isRight() {
    return myScreen.isRight();
  }

  @Override
  public boolean isShoot() {
    return myScreen.isShoot();
  }

  @Override
  public boolean isShoot2() {
    return myScreen.isShoot2();
  }

  @Override
  public boolean collectsItems() {
    return true;
  }

  @Override
  public boolean isAbility() {
    return myScreen.isAbility();
  }

  @Override
  public Fraction getFraction() {
    return Fraction.LAANI;
  }

  @Override
  public boolean shootsAtObstacles() {
    return false;
  }

  @Override
  public float getDetectionDist() {
    return -1;
  }

  @Override
  public String getMapHint() {
    return "You";
  }

  @Override
  public void updateFar(SolGame game, FarShip farShip) {
  }
}
