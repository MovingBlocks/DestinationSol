package org.destinationsol.soundtest;

import com.badlogic.gdx.ApplicationListener;

public class SoundTestListener implements ApplicationListener {

  private SoundTestCmp myCmp;

  @Override
  public void create() {
    myCmp = new SoundTestCmp();
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void render() {
    myCmp.render();
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {

  }

}
