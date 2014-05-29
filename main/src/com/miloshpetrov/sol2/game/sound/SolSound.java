package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.List;

public class SolSound {
  public final List<Sound> sounds;
  public final String dir;
  public final String definedBy;
  public final float loopTime;
  public final float baseVolume;
  public float basePitch;

  public SolSound(String dir, String definedBy, float loopTime, float baseVolume, float basePitch) {
    this.dir = dir;
    this.definedBy = definedBy;
    this.loopTime = loopTime;
    this.baseVolume = baseVolume;
    this.sounds = new ArrayList<Sound>();
    this.basePitch = basePitch;
  }

  public String getDebugString() {
    StringBuilder sb = new StringBuilder();
    if (sounds.isEmpty()) sb.append("EMPTY ");
    sb.append(dir).append(" (from ").append(definedBy).append(')');
    return sb.toString();
  }
}
