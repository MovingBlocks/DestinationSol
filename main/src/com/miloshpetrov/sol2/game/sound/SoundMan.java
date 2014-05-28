package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.common.Nullable;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.menu.IniReader;

import java.util.*;

public class SoundMan {
  public static final String DIR = "res/sounds/";

  private final HashMap<String, SolSound> mySounds;
  private final DebugHintDrawer myHintDrawer;
  private final Map<SolObj, Map<SolSound, Float>> myLoopedSounds;

  public SoundMan() {
    mySounds = new HashMap<String, SolSound>();
    myHintDrawer = new DebugHintDrawer();
    myLoopedSounds = new HashMap<SolObj, Map<SolSound, Float>>();
  }

  public SolSound getLoopedSound(String relPath, @Nullable FileHandle configFile) {
    return getSound0(relPath, configFile, true);
  }

  public SolSound getSound(String relPath, @Nullable FileHandle configFile) {
    return getSound0(relPath, configFile, false);
  }

  private SolSound getSound0(String relPath, @Nullable FileHandle configFile, boolean looped) {
    if (relPath.isEmpty()) return null;
    SolSound res = mySounds.get(relPath);
    if (res != null) return res;

    String definedBy = configFile == null ? "hardcoded" : configFile.path();
    String dirPath = DIR + relPath;
    String paramsPath = dirPath + "/params.txt";
    FileHandle dir = SolFiles.readOnly(dirPath);
    float[] params = loadSoundParams(paramsPath);
    float loopTime = params[1];
    float volume = params[0];
    res = new SolSound(dir.toString(), definedBy, loopTime, volume);
    mySounds.put(relPath, res);
    fillSounds(res.sounds, dir);
    boolean empty = res.sounds.isEmpty();
    if (!empty && looped && loopTime == 0) throw new AssertionError("please specify loopTime value in " + paramsPath);
    if (empty) {
      String warnMsg = "found no sounds in " + dir;
      if (configFile != null) {
        warnMsg += " (defined in " + configFile.path() + ")";
      }
      DebugOptions.MISSING_SOUND_ACTION.handle(warnMsg);
    }
    return res;
  }

  private float[] loadSoundParams(String paramsPath) {
    float[] r = {0, 0};
    IniReader reader = new IniReader(paramsPath);
    r[0] = reader.f("volume", 1);
    r[1] = reader.f("loopTime", 0);
    return r;
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
   * @param volMul multiplier for sound volume
   */
  public void play(SolGame game, SolSound sound, @Nullable Vector2 pos, @Nullable SolObj source, float volMul) {
    if (source == null && pos == null) throw new AssertionError("pass either pos or source");
    if (source == null && sound.loopTime > 0) throw new AssertionError("looped sound without source object: " + sound.dir);
    if (sound == null) return;

    if (pos == null) pos = source.getPos();

    // vol
    Vector2 camPos = game.getCam().getPos();
    float airPerc = 0;
    Planet np = game.getPlanetMan().getNearestPlanet();
    if (np.getConfig().skyConfig != null) {
      float camToAtmDst = camPos.dst(np.getPos()) - np.getGroundHeight() - Const.ATM_HEIGHT/2;
      airPerc = SolMath.clamp(1 - camToAtmDst / (Const.ATM_HEIGHT / 2));
    }
    if (DebugOptions.SOUND_IN_SPACE) airPerc = 1;
    float maxSoundDist = 1 + 1.5f * airPerc * Const.CAM_VIEW_DIST_GROUND;
    float dst = pos.dst(camPos);
    float distMul = SolMath.clamp(1 - dst / maxSoundDist);
    float vol = sound.volume * volMul * distMul;
    if (vol <= 0) return;

    //pitch
    float pitch = SolMath.rnd(.95f, 1.05f) * game.getTimeFactor();

    if (skipLooped(source, sound, game.getTime())) return;
    if (DebugOptions.SOUND_INFO) {
      myHintDrawer.add(source, pos, sound.getDebugString());
    }
    if (sound.sounds.isEmpty()) return;
    if (DebugOptions.NO_SOUND) return;
    Sound sound0 = SolMath.elemRnd(sound.sounds);
    sound0.play(vol, pitch, 0);
  }

/**
 * Plays a sound. Either pos or source must not be null.
 * @param pos position of a sound. If null, source.getPos() will be used
 * @param source bearer of a sound. Must not be null for looped sounds
 */
  public void play(SolGame game, SolSound sound, @Nullable Vector2 pos, @Nullable SolObj source){
    this.play(game, sound, pos, source, 1f);
  }

  private boolean skipLooped(SolObj source, SolSound sound, float time) {
    if (sound.loopTime == 0) return false;
    boolean playing = true;
    Map<SolSound, Float> looped = myLoopedSounds.get(source);
    if (looped == null) {
      looped = new HashMap<SolSound, Float>();
      myLoopedSounds.put(source, looped);
      playing = false;
    } else {
      Float endTime = looped.get(sound);
      if (endTime == null || endTime <= time) {
        looped.put(sound, time + sound.loopTime); // argh, performance loss
        playing = false;
      } else {
        playing = time < endTime;
      }
    }
    return playing;
  }

  public void drawDebug(GameDrawer drawer, SolGame game) {
    if (DebugOptions.SOUND_INFO) myHintDrawer.draw(drawer, game);
  }

  public void update(SolGame game) {
    if (DebugOptions.SOUND_INFO) myHintDrawer.update(game);
    cleanLooped(game);
  }

  private void cleanLooped(SolGame game) {
    Iterator<SolObj> it = myLoopedSounds.keySet().iterator();
    while (it.hasNext()) {
      SolObj o = it.next();
      if (o.shouldBeRemoved(game)) it.remove();
    }
  }

}
