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
import org.destinationsol.game.planet.Planet;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the sound manager used in  DestinationSol. It is responsible for handling and playing all
 * sounds in the game. It loads the sound as required from {@link Assets}, all you need to do for your sound to be
 * registered is to place the sound in {@code .ogg} format in one of the asset-scanned directories.
 */
public class OggSoundManager {
    private final Map<String, OggSound> soundMap;
    private final Map<SolObject, Map<OggSound, Float>> loopedSoundMap;
    private final DebugHintDrawer debugHintDrawer;

    private float myLoopAwait;

    public OggSoundManager() {
        soundMap = new HashMap<>();
        loopedSoundMap = new HashMap<>();
        debugHintDrawer = new DebugHintDrawer();
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

        if (DebugOptions.SOUND_INFO) {
            debugHintDrawer.add(source, position, sound.toString());
        }

        Sound gdxSound = sound.getSound();
        gdxSound.play(volume, pitch, 0);
    }

    private float getVolume(SolGame game, @Nullable Vector2 position, float volumeMultiplier, OggSound sound) {
        // Calculate the volume multiplier for the sound
        float globalVolumeMultiplier = game.getCmp().getOptions().sfxVolumeMultiplier;

        Vector2 cameraPosition = game.getCam().getPosition();
        Planet nearestPlanet = game.getPlanetManager().getNearestPlanet();

        float airPercentage = 0;
        if (nearestPlanet.getConfig().skyConfig != null) {
            float distanceToAtmosphere = cameraPosition.dst(nearestPlanet.getPosition()) - nearestPlanet.getGroundHeight() - Const.ATM_HEIGHT / 2;
            airPercentage = SolMath.clamp(1 - distanceToAtmosphere / (Const.ATM_HEIGHT / 2));
        }
        if (DebugOptions.SOUND_IN_SPACE) {
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
     * Plays a sound at specified position, or coming from specific source.
     * <p>
     * {@code source} must not be null if the sound is specified to loop, and at least one of {@code source} or
     * {@code position} must be specified.
     *
     * @param game             Game to play the sound in.
     * @param sound            The sound to play
     * @param position         Position to play the sound at. If null, source.getPosition() will be used.
     * @param source           Bearer of a sound. Must not be null for looped sounds or when {@code position} is null.
     */
    public void play(SolGame game, PlayableSound sound, @Nullable Vector2 position, @Nullable SolObject source) {
        play(game, sound, position, source, 1f);
    }

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
     * Draws info about recently played sounds in player proximity when {@link DebugOptions#SOUND_INFO} flag is set.
     *
     * @param drawer {@code GameDrawer} to use for drawing
     * @param game   {@code Game} to draw to.
     */
    public void drawDebug(GameDrawer drawer, SolGame game) {
        if (DebugOptions.SOUND_INFO) {
            debugHintDrawer.draw(drawer, game);
        }
    }

    public void update(SolGame game) {
        if (DebugOptions.SOUND_INFO) {
            debugHintDrawer.update(game);
        }

        myLoopAwait -= game.getTimeStep();
        if (myLoopAwait <= 0) {
            myLoopAwait = 30;
            cleanLooped(game);
        }
    }

    private void cleanLooped(SolGame game) {
        loopedSoundMap.keySet().removeIf(o -> o.shouldBeRemoved(game));
    }

    public void dispose() {
        for (OggSound sound : soundMap.values()) {
            sound.doDispose();
        }
    }
}
