package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.DebugAspects;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.ui.DebugCollector;

import java.util.HashMap;
import java.util.List;

public class SoundMan {
  public static final String DIR = "res/sounds/";
  public static final float MAX_SPACE_DIST = 1f;
  public final HashMap<String, SolSounds> mySounds;

  public SoundMan() {
    mySounds = new HashMap<String, SolSounds>();
  }

  public SolSounds getSounds(String relPath) {
    SolSounds res = mySounds.get(relPath);
    if (res != null) return res;
    res = new SolSounds();
    mySounds.put(relPath, res);
    FileHandle dir = SolFiles.readOnly(DIR + relPath);
    fillSounds(res.atm, dir);
    return res;
  }

  private void fillSounds(List<Sound> list, FileHandle dir) {
    if (!dir.isDirectory()) throw new AssertionError("Can't load sound: can't find directory " + dir);
    for (FileHandle soundFile : dir.list()) {
      String ext = soundFile.extension();
      if (ext.equals("wav") || ext.equals("mp3") || ext.equals("ogg")) //filter by supported audio files
      {
        Sound sound = Gdx.audio.newSound(soundFile);
        list.add(sound);
      }
    }
    if (list.isEmpty()) DebugCollector.warn("found no sounds in " + dir);
  }

  public void play(SolGame game, SolSounds sounds, Vector2 pos) {
    if (DebugAspects.NO_SOUND) return;
    Planet np = game.getPlanetMan().getNearestPlanet();
    Vector2 camPos = game.getCam().getPos();
    boolean atm = camPos.dst(np.getPos()) < np.getFullHeight();
    if (sounds.atm.isEmpty()) return;
    Sound sound = SolMath.elemRnd(sounds.atm);
    float vol;
    float pitch = SolMath.rnd(.95f, 1.05f);
    if (atm) {
      vol = 1; // todo
    } else {
      float dst = pos.dst(camPos);
      vol = 1 - SolMath.clamp(dst / MAX_SPACE_DIST, 0, 1);
      pitch = .75f * vol + .25f;
    }
    sound.play(vol, pitch, 0);
  }
}
