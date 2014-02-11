package com.miloshpetrov.sol2;

import com.badlogic.gdx.ApplicationListener;

public class SolAppListener implements ApplicationListener {

  private SolCmp myCmp;

  @Override
  public void create() {
    myCmp = new SolCmp();
  }

  @Override
  public void resize(int width, int height) {
    // ignore nagluho
  }

  @Override
  public void render() {
    myCmp.render();
  }

  @Override
  public void pause() {
    // ignore for now
  }

  @Override
  public void resume() {
    // ignore for now
  }

  @Override
  public void dispose() {
    myCmp.dispose();
  }
}
