package com.miloshpetrov.sol2;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.DebugAspects;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.menu.MenuScreens;
import com.miloshpetrov.sol2.save.SaveData;
import com.miloshpetrov.sol2.save.SaveMan;
import com.miloshpetrov.sol2.ui.*;

public class SolCmp {

  private final SolInputMan myInputMan;
  private final UiDrawer myUiDrawer;
  private final MenuScreens myMenuScreens;
  private final SaveMan mySaveMan;
  private final TexMan myTexMan;
  private final SolLayouts myLayouts;
  private final boolean myMobile;
  private String myFatalError;

  private float myAccum = 0;
  private SolGame myGame;

  //commented by NoiseDoll
  public SolCmp() {
    myMobile = Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;


    myUiDrawer = new UiDrawer();
    myTexMan = new TexMan();
    myInputMan = new SolInputMan(myTexMan, myUiDrawer.r);
    mySaveMan = new SaveMan();
    myLayouts = new SolLayouts(myUiDrawer.r);
    myMenuScreens = new MenuScreens(myLayouts, mySaveMan, myTexMan, isMobile());

    myInputMan.setScreen(this, myMenuScreens.main);
  }

  public void render() {
    myAccum += Gdx.graphics.getDeltaTime();
    while (myAccum > Const.REAL_TIME_STEP) {
      safeUpdate();
      myAccum -= Const.REAL_TIME_STEP;
    }
    draw();
  }

  private void safeUpdate() {
    if (myFatalError != null) return;
    try {
      update();
    } catch (Throwable t) {
      myFatalError = "A fatal error occurred:\n" + t.getMessage();
      t.printStackTrace();
    }
  }

  private void update() {
    DebugCollector.update();
    DebugCollector.debug("Fps: ", Gdx.graphics.getFramesPerSecond());
    DebugCollector.debug("Version: " + Const.VERSION);
    myInputMan.update(this);
    if (myGame != null) myGame.update();
    SolMath.checkVectorsTaken(null);
  }

  private void draw() {
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    if (myGame != null) {
      myGame.draw();
    }
    myUiDrawer.begin();
    myInputMan.draw(myUiDrawer, this);
    DebugCollector.draw(myUiDrawer);
    if (myFatalError != null) {
      myUiDrawer.draw(myUiDrawer.whiteTex, myUiDrawer.r, .5f, 0, 0, 0, .25f, 0, Col.B75);
      myUiDrawer.drawString(myFatalError, myUiDrawer.r / 2, .5f, FontSize.MENU, true, Col.W);
    }
    myUiDrawer.end();
  }

  public void startNewGame(boolean tut) {
    startGame(false, tut);
  }

  private void startGame(boolean resume, boolean tut) {
    SaveData sd = null;
    if (resume) sd = mySaveMan.getData();
    myGame = new SolGame(this, sd, myTexMan, tut);
    myInputMan.setScreen(this, myGame.getScreens().mainScreen);
  }

  public SolInputMan getInputMan() {
    return myInputMan;
  }

  public MenuScreens getMenuScreens() {
    return myMenuScreens;
  }

  public void dispose() {
    myUiDrawer.dispose();
    if (myGame != null) myGame.dispose();
    myTexMan.dispose();
  }

  public void resumeGame() {
    startGame(true, false);
  }

  public SolGame getGame() {
    return myGame;
  }

  public SolLayouts getLayouts() {
    return myLayouts;
  }

  public void finishGame() {
    myGame.dispose();
    myGame = null;
  }

  public TexMan getTexMan() {
    return myTexMan;
  }

  public boolean isMobile() {
    return DebugAspects.MOBILE || myMobile;
  }

}
