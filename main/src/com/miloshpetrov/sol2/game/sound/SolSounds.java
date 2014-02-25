package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.List;

public class SolSounds {
  public final List<Sound> sounds;
  public final String dir;
  public final String definedBy;

  public SolSounds(String dir, String definedBy) {
    this.dir = dir;
    this.definedBy = definedBy;
    this.sounds = new ArrayList<Sound>();
  }

  public String getDebugString() {
    StringBuilder sb = new StringBuilder(dir).append('\n').append(definedBy);
    if (sounds.isEmpty()) sb.append('\n').append("no sounds!");
    return sb.toString();
  }
}
