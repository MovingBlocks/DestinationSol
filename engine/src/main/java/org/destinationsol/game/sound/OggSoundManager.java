/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game.sound;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.common.Nullable;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.planet.Planet;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the sound manager used in  DestinationSol. It is responsible for handling and playing all
 * sounds in the game.
 * <p>
 * This class loads the sound as required from {@link Assets}, all you need to do for your sound to be registered is to
 * place the sound in {@code .ogg} format in one of the asset-scanned directories.
 * <p>
 * You can also optionally place a text file with the same name as the ogg file and {@code .soundinfo} extension, that
 * might contain the following: {@code
 * volume=xxx
 * loopTime=xxx
 * }
 * In there, the {@code xxx} stands for a floating point number.<br>
 * {@code volume} specifies the default volume multiplier for the sound, with {@code 1.0} being the value unchanged.<br>
 * {@code loopTime} works as follows: if the loopTime is not specified or set to 0, any new request to play the sound
 * will play it again, even concurrently with itself. If the loopTime is set greater than 0, new request to play the
 * sound will be accepted only when loopTime time units has passed since the beginning of the sound's prior playback, or
 * the request is from different object.
 */
public class OggSoundManager {
    /**
     * A container for all the sounds that have been so far loaded in the game. Sounds are loaded on as needed basis,
     * and once loaded, they persist here till the end of game. String is the fully qualified name of the sound
     * ("module:sound_name").
     */
    private final Map<String, OggSound> soundMap;
    /**
     * A container for working with looping sounds. Looped sounds are stored here per-object, and this map is every
     * while cleared, on basis provided by calling each object's {@link SolObject#shouldBeRemoved(SolGame)} method.
     * {@code SolObject} is the object the sound belongs to, inner map's {@code OggSound} is the sound in question,
     * {@code Float} is an absolute time the sound will stop playing. (Absolute as in not relative to the current time)
     */
    private final Map<SolObject, Map<OggSound, Float>> loopedSoundMap;
    /**
     * Used for drawing debug hints when {@link DebugOptions#soundInfo} flag is set. See
     * {@link #drawDebug(GameDrawer, SolGame)} for more info.
     */
    private final DebugHintDrawer debugHintDrawer;

    /**
     * This is used only in {@link #update(SolGame)}, and is used for ensuring some more resource expensive operations
     * happen only once in a while. This variable functions as millisecond countdown, with {@code <= 0} values meaning
     * "Do the operations now".
     */
    private float myLoopAwait;
    private final SolApplication solApplication;


    public OggSoundManager(Context context) {
        soundMap = new HashMap<>();
        loopedSoundMap = new HashMap<>();
        debugHintDrawer = new DebugHintDrawer();
        solApplication = context.get(SolApplication.class);

    }

    /**
     * Returns an {@link OggSound} specified by name.
     *
     * @param path Name of the sound in th form "module:soundName"
     * @return The specified sound object.
     */
    public OggSound getSound(String path) {
        return getSound(path, 1.0f);
    }

    /**
     * Returns an {@link OggSound} specified by name and highered/lowered by {@code basePitch}.
     *
     * @param path      Name of the sound in th form "module:soundName"
     * @param basePitch Multiplier to higher/lower sound by.
     * @return The specified sound object.
     */
    public OggSound getSound(String path, float basePitch) {
        if (soundMap.containsKey(path)) {
            final OggSound sound = soundMap.get(path);
            sound.setBasePitch(basePitch);
            return sound;
        }

        OggSound sound = Assets.getSound(path);
        sound.setBasePitch(basePitch);
        soundMap.put(path, sound);
        return sound;
    }

    /**
     * Plays a sound at specified position, or coming from specific source.
     * <p>
     * {@code source} must not be null if the sound is specified to loop, and at least one of {@code source} or
     * {@code position} must be specified.
     *
     * @param game     Game to play the sound in.
     * @param sound    The sound to play
     * @param position Position to play the sound at. If null, source.getPosition() will be used.
     * @param source   Bearer of a sound. Must not be null for looped sounds or when {@code position} is null.
     */
    public void play(SolGame game, PlayableSound sound, @Nullable Vector2 position, @Nullable SolObject source) {
        play(game, sound, position, source, 1f);
    }

    /**
     * Plays a sound at specified position, or coming from specific source.
     * <p>
     * {@code source} must not be null if the sound is specified to loop, and at least one of {@code source} or
     * {@code position} must be specified.
     *
     * @param game             Game to play the sound in.
     * @param playableSound    The sound to play
     * @param position         Position to play the sound at. If null, source.getPosition() will be used.
     * @param source           Bearer of a sound. Must not be null for looped sounds or when {@code position} is null.
     * @param volumeMultiplier Multiplier for sound volume.
     */
    public void play(SolGame game, PlayableSound playableSound, @Nullable Vector2 position, @Nullable SolObject source, float volumeMultiplier) {
        if (playableSound == null) {
            return;
        }

        OggSound sound = playableSound.getOggSound();

        // Perform some initial argument validation
        if (source == null && position == null) {
            throw new AssertionError("Either position or source must be non-null");
        }
        if (source == null && sound.getLoopTime() > 0) {
            throw new AssertionError("Attempted to loop a sound without a parent object: " + sound.getUrn());
        }
        if (position == null) {
            position = source.getPosition();
        }

        float volume = getVolume(game, position, volumeMultiplier, sound);

        if (volume <= 0) {
            return;
        }

        // Calculate the pitch for the sound
        float pitch = SolRandom.randomFloat(.97f, 1.03f) * game.getTimeFactor() * playableSound.getBasePitch();

        if (skipLooped(source, sound, game.getTime())) {
            return;
        }

        if (DebugOptions.soundInfo) {
            debugHintDrawer.add(source, position, sound.toString());
        }

        Sound gdxSound = sound.getSound();
        gdxSound.play(volume, pitch, 0);
    }

    /**
     * Calculates the volume a sound should be played at.
     * This method takes several factors in account, more exactly: global game's volume, spreading of sound in vacuum
     * (aka distance to atmosphere of a planet), distance to player, sound's volume multiplier, and volume multiplier
     * passed in as an argument to the method.
     *
     * @param game             Game to play this sound in.
     * @param position         Position to play the sound at.
     * @param volumeMultiplier Special multiplier to multiply the resulting volume by.
     * @param sound            Sound to be played with the calculated volume.
     * @return Volume the sound should play at.
     */
    private float getVolume(SolGame game, Vector2 position, float volumeMultiplier, OggSound sound) {
        float globalVolumeMultiplier = solApplication.getOptions().sfxVolumeMultiplier;

        Vector2 cameraPosition = game.getCam().getPosition();
        Planet nearestPlanet = game.getPlanetManager().getNearestPlanet();

        float airPercentage = 0;
        if (nearestPlanet.getConfig().skyConfig != null) {
            float distanceToAtmosphere = cameraPosition.dst(nearestPlanet.getPosition()) - nearestPlanet.getGroundHeight() - Const.ATM_HEIGHT / 2;
            airPercentage = SolMath.clamp(1 - distanceToAtmosphere / (Const.ATM_HEIGHT / 2));
        }
        if (DebugOptions.soundInSpace) {
            airPercentage = 1;
        }

        float maxSoundDist = 1 + 1.5f * Const.CAM_VIEW_DIST_GROUND * airPercentage;

        Hero hero = game.getHero();
        float soundRadius = hero.isTranscendent() ? 0 : hero.getHull().config.getApproxRadius();
        float distance = position.dst(cameraPosition) - soundRadius;
        float distanceMultiplier = SolMath.clamp(1 - distance / maxSoundDist);

        return sound.getBaseVolume() * volumeMultiplier * distanceMultiplier * globalVolumeMultiplier;
    }

    /**
     * Returns true when sound should not be played because of loop, false otherwise.
     * <p>
     * Sound should not be played when its {@code loopTime > 0} and {@code loopTime} time units have not yet passed
     * since it was last played on the object.
     * TODO: now handles even adding the sound to the list of looping sounds. Possibly extract that?
     *
     * @param source Object playing this sound.
     * @param sound  Sound to be played.
     * @param time   Game's current time.
     * @return true when sound should not be played because of loop, false otherwise.
     */
    private boolean skipLooped(SolObject source, OggSound sound, float time) {
        if (sound.getLoopTime() == 0) {
            return false;
        }

        Map<OggSound, Float> looped = loopedSoundMap.get(source);
        if (looped == null) {
            looped = new HashMap<>();
            loopedSoundMap.put(source, looped);
            return false;
        } else {
            Float endTime = looped.get(sound);
            if (endTime == null || endTime <= time) {
                looped.put(sound, time + sound.getLoopTime()); // argh, performance loss
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Draws info about recently played sounds in player proximity when {@link DebugOptions#soundInfo} flag is set.
     *
     * @param drawer GameDrawer to use for drawing
     * @param game   Game to draw to.
     */
    public void drawDebug(GameDrawer drawer, SolGame game) {
        if (DebugOptions.soundInfo) {
            debugHintDrawer.draw(drawer, game);
        }
    }

    /**
     * Updates drawer used in {@link #drawDebug(GameDrawer, SolGame)} and removes unnecessary objects.
     *
     * @param game Game currently in progress.
     */
    public void update(SolGame game) {
        if (DebugOptions.soundInfo) {
            debugHintDrawer.update(game);
        }

        myLoopAwait -= game.getTimeStep();
        if (myLoopAwait <= 0) {
            myLoopAwait = 30;
            cleanLooped(game);
        }
    }

    /**
     * Iterates {@link #loopedSoundMap} and removes any entries that are no longer in the game.
     * <p>
     * (See {@link SolObject#shouldBeRemoved(SolGame)})
     *
     * @param game Game currently in progress.
     */
    private void cleanLooped(SolGame game) {
        loopedSoundMap.keySet().removeIf(o -> o.shouldBeRemoved(game));
    }

    /**
     * Handles deallocation of resources by the libGdx backend.
     */
    public void dispose() {
        for (OggSound sound : soundMap.values()) {
            sound.doDispose();
        }
    }
}
