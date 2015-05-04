package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Nullable;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.files.FileManager;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.SolShip;
import com.miloshpetrov.sol2.IniReader;

import java.util.*;

public class SoundManager {
  public static final String DIR = "res/sounds/";

  private final HashMap<String, SolSound> mySounds;
  private final DebugHintDrawer myHintDrawer;
  private final Map<SolObject, Map<SolSound, Float>> myLoopedSounds;

  private float myLoopAwait;

  public SoundManager() {
    mySounds = new HashMap<String, SolSound>();
    myHintDrawer = new DebugHintDrawer();
    myLoopedSounds = new HashMap<SolObject, Map<SolSound, Float>>();
  }

  public SolSound getLoopedSound(String relPath, @Nullable FileHandle configFile) {
    return getSound0(relPath, configFile, true, 1);
  }

  public SolSound getSound(String relPath, @Nullable FileHandle configFile) {
    return getPitchedSound(relPath, configFile, 1);
  }

  public SolSound getPitchedSound(String relPath, @Nullable FileHandle configFile, float basePitch) {
    return getSound0(relPath, configFile, false, basePitch);
  }

  private SolSound getSound0(String relPath, @Nullable FileHandle configFile, boolean looped, float basePitch) {
    if (relPath.isEmpty()) return null;
    String key = relPath + "#" + basePitch;
    SolSound res = mySounds.get(key);
    if (res != null) return res;

    String definedBy = configFile == null ? "hardcoded" : configFile.path();
    String dirPath = DIR + relPath;
    String paramsPath = dirPath + "/params.txt";
    FileHandle dir = FileManager.getInstance().getStaticFiles(dirPath);
    float[] params = loadSoundParams(paramsPath);
    float loopTime = params[1];
    float baseVolume = params[0];
    ArrayList<Sound> sounds = new ArrayList<Sound>();
    boolean[] emptyDirArr = {false};
    fillSounds(sounds, dir, emptyDirArr);
    boolean emptyDir = emptyDirArr[0];
    res = new SolSound(dir.toString(), definedBy, loopTime, baseVolume, basePitch, sounds, emptyDir);
    mySounds.put(key, res);
    if (!emptyDir && looped && loopTime == 0) throw new AssertionError("please specify loopTime value in " + paramsPath);
    if (emptyDir) {
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
    IniReader reader = new IniReader(paramsPath, null, true);
    r[0] = reader.f("volume", 1);
    r[1] = reader.f("loopTime", 0);
    return r;
  }

  private void fillSounds(List<Sound> list, FileHandle dir, boolean[] emptyDir) {
    emptyDir[0] = true;
    //try empty dirs
    //if (!dir.isDirectory()) throw new AssertionError("Can't load sound: can't find directory " + dir);
    for (FileHandle soundFile : dir.list()) {
      String ext = soundFile.extension();
      if (ext.equals("wav") || ext.equals("mp3") || ext.equals("ogg")) //filter by supported audio files
      {
        emptyDir[0] = false;
        if (DebugOptions.NO_SOUND) return;
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
  public void play(SolGame game, SolSound sound, @Nullable Vector2 pos, @Nullable SolObject source, float volMul) {
    if (source == null && pos == null) throw new AssertionError("pass either pos or source");
    if (source == null && sound.loopTime > 0) throw new AssertionError("looped sound without source object: " + sound.dir);
    if (sound == null) return;
    float globalVolMul = game.getCmp().getOptions().volMul;
    if (globalVolMul == 0) return;

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
    float maxSoundDist = 1 + 1.5f * Const.CAM_VIEW_DIST_GROUND * airPerc;
    SolShip hero = game.getHero();
    float fullSoundRad = hero == null ? 0 : hero.getHull().config.approxRadius;
    float dst = pos.dst(camPos) - fullSoundRad;
    float distMul = SolMath.clamp(1 - dst / maxSoundDist);
    float vol = sound.baseVolume * volMul * distMul * globalVolMul;
    if (vol <= 0) return;

    //pitch
    float pitch = SolMath.rnd(.97f, 1.03f) * game.getTimeFactor() * sound.basePitch;

    if (skipLooped(source, sound, game.getTime())) return;
    if (DebugOptions.SOUND_INFO) {
      myHintDrawer.add(source, pos, sound.getDebugString());
    }
    if (sound.sounds.isEmpty()) return;
    Sound sound0 = SolMath.elemRnd(sound.sounds);
    sound0.play(vol, pitch, 0);
  }

/**
 * Plays a sound. Either pos or source must not be null.
 * @param pos position of a sound. If null, source.getPos() will be used
 * @param source bearer of a sound. Must not be null for looped sounds
 */
  public void play(SolGame game, SolSound sound, @Nullable Vector2 pos, @Nullable SolObject source){
    this.play(game, sound, pos, source, 1f);
  }

  private boolean skipLooped(SolObject source, SolSound sound, float time) {
    if (sound.loopTime == 0) return false;
    boolean playing;
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
    myLoopAwait -= game.getTimeStep();
    if (myLoopAwait <= 0) {
      myLoopAwait = 30;
      cleanLooped(game);
    }
  }

  private void cleanLooped(SolGame game) {
    Iterator<SolObject> it = myLoopedSounds.keySet().iterator();
    while (it.hasNext()) {
      SolObject o = it.next();
      if (o.shouldBeRemoved(game)) it.remove();
    }
  }

  public void dispose() {
    for (SolSound ss : mySounds.values()) {
      for (Sound s : ss.sounds) {
        s.dispose();
      }
    }
  }
}
