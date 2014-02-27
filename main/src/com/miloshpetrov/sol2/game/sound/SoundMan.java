package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.common.Nullable;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.ui.DebugCollector;

import java.util.HashMap;
import java.util.List;

public class SoundMan {
  public static final String DIR = "res/sounds/";
  public static final float MAX_SPACE_DIST = 1f;
  public final HashMap<String, SolSound> mySounds;
  private final DebugHintDrawer myHintDrawer;

  public SoundMan() {
    mySounds = new HashMap<String, SolSound>();
    myHintDrawer = new DebugHintDrawer();
  }

  public SolSound getSound(String relPath, @Nullable FileHandle configFile) {
    return getSound(relPath, configFile, -1);
  }

  public SolSound getSound(String relPath, @Nullable FileHandle configFile, long loopTime) {
    SolSound res = mySounds.get(relPath);
    if (res != null) return res;

    String definedBy = configFile == null ? "hardcoded" : configFile.path();
    FileHandle dir = SolFiles.readOnly(DIR + relPath);
    res = new SolSound(dir.toString(), definedBy, loopTime);
    mySounds.put(relPath, res);
    fillSounds(res.sounds, dir);

    if (res.sounds.isEmpty()) {
      String warnMsg = "found no sounds in " + dir;
      if (configFile != null) {
        warnMsg += " (defined in " + configFile.path() + ")";
      }
      DebugCollector.warn(warnMsg);
    }
    return res;
  }

  private void fillSounds(List<Sound> list, FileHandle dir) {
    //try empty dirs
    //if (!dir.isDirectory()) throw new AssertionError("Can't load sound: can't find directory " + dir);
    for (FileHandle soundFile : dir.list()) {
      String ext = soundFile.extension();
      if (ext.equals("wav") || ext.equals("mp3") || ext.equals("ogg")) //filter by supported audio files
      {
        Sound sound = Gdx.audio.newSound(soundFile);
        list.add(sound);
      }
    }
  }

  /**
   * Plays a sound. Either pos or source must not be null.
   * @param pos position of a sound. If null, source.getPos() will be used
   * @param source bearer of a sound. Must not be null for looped sounds
   */
  public void play(SolGame game, SolSound sounds, @Nullable Vector2 pos, @Nullable SolObj source) {
    if (DebugAspects.NO_SOUND) return;
    if (pos == null) {
      if (source == null) return;
      pos = source.getPos();
    }
    if (source == null && sounds.loopTime > 0) throw new AssertionError("looped sound without source object: " + sounds.dir);

    Planet np = game.getPlanetMan().getNearestPlanet();
    Vector2 camPos = game.getCam().getPos();
    boolean atm = camPos.dst(np.getPos()) < np.getFullHeight();
    float vol;
    float pitch = SolMath.rnd(.95f, 1.05f);
    if (atm || DebugAspects.SOUND_IN_SPACE) {
      vol = 1; // todo
    } else {
      float dst = pos.dst(camPos);
      vol = 1 - SolMath.clamp(dst / MAX_SPACE_DIST, 0, 1);
      pitch = .75f * vol + .25f;
    }
    if (vol <= 0) return;

    if (DebugAspects.SOUND_DEBUG) {
      myHintDrawer.add(source, pos, sounds.getDebugString());
    }
    if (sounds.sounds.isEmpty()) return;
    Sound sound = SolMath.elemRnd(sounds.sounds);
    sound.play(vol, pitch, 0);
  }

  public void drawDebug(Drawer drawer, SolGame game) {
    if (DebugAspects.SOUND_DEBUG) myHintDrawer.draw(drawer, game);
  }

  public void update(SolGame game) {
    if (DebugAspects.SOUND_DEBUG) myHintDrawer.update(game);
  }

}
