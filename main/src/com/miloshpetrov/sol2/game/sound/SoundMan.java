package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.planet.Planet;

import java.util.HashMap;
import java.util.List;

public class SoundMan {
  public static final String DIR = "res/sounds/";
  public final HashMap<String, SolSounds> mySounds;

  public SoundMan() {
    mySounds = new HashMap<String, SolSounds>();
  }

  public SolSounds getSounds(String relPath) {
    SolSounds res = mySounds.get(relPath);
    if (res != null) return res;
    res = new SolSounds();
    mySounds.put(relPath, res);
    FileHandle atmDir = Gdx.files.internal(DIR + relPath + "/atm");
    fillSounds(res.atm, atmDir);
    FileHandle spaceDir = Gdx.files.internal(DIR + relPath + "/space");
    fillSounds(res.space, spaceDir);
    return res;
  }

  private void fillSounds(List<Sound> list, FileHandle dir) {
    if (!dir.isDirectory()) throw new AssertionError("Can't load sound: can't find directory " + dir);
    for (FileHandle soundFile : dir.list()) {
      Sound sound = Gdx.audio.newSound(soundFile);
      list.add(sound);
    }
  }

  public void play(SolGame game, SolSounds sounds, Vector2 pos) {
    Planet np = game.getPlanetMan().getNearestPlanet();
    boolean atm = game.getCam().getPos().dst(np.getPos()) < np.getFullHeight();
    List<Sound> list = atm ? sounds.atm : sounds.space;
    Sound sound = SolMath.elemRnd(list);
    sound.play(/*todo params*/);
  }
}
