package org.destinationsol.ui;

import com.badlogic.gdx.InputProcessor;

public class SolInputProcessor implements InputProcessor {

  private final SolInputManager myInputMan;

  public SolInputProcessor(SolInputManager inputMan) {
    myInputMan = inputMan;
  }

  @Override
  public boolean keyDown(int keyCode) {
    myInputMan.maybeFlashPressed(keyCode);
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    myInputMan.maybeFlashPressed(screenX, screenY);
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    myInputMan.scrolled(amount > 0);
    return false;
  }
}
