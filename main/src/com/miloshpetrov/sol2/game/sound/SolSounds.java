package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.List;

public class SolSounds {
  public final List<Sound> atm;
  public final List<Sound> space;

  public SolSounds() {
    this.atm = new ArrayList<Sound>();
    this.space = new ArrayList<Sound>();
  }
}
