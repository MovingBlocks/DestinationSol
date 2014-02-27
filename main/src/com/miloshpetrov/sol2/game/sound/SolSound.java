package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.List;

public class SolSound {
  public final List<Sound> sounds;
  public final String dir;
  public final String definedBy;
  public final long loopTime;

  public SolSound(String dir, String definedBy, long loopTime) {
    this.dir = dir;
    this.definedBy = definedBy;
    this.loopTime = loopTime;
    this.sounds = new ArrayList<Sound>();
  }

  public String getDebugString() {
    StringBuilder sb = new StringBuilder();
    if (sounds.isEmpty()) sb.append('\n').append("EMPTY ");
    sb.append(dir).append(" ( from ").append(definedBy).append(')');
    return sb.toString();
  }
}
