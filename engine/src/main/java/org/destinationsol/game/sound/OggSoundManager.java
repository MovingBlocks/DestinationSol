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
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.ship.SolShip;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OggSoundManager {
    // private static Logger logger = LoggerFactory.getLogger(OggSoundManager.class);
    private final Map<String, OggSound> soundMap;
    private final Map<SolObject, Map<OggSound, Float>> loopedSoundMap;
    private final DebugHintDrawer debugHintDrawer;

    private float myLoopAwait;

    public OggSoundManager() {
        this.soundMap = new HashMap<>();
        this.loopedSoundMap = new HashMap<>();
        this.debugHintDrawer = new DebugHintDrawer();
    }

    public OggSound getSound(String path) {
        return getSound(path, 1.0f);
    }

    public OggSound getSound(String path, float basePitch) {
        if (soundMap.containsKey(path)) {
            return soundMap.get(path);
        }

        OggSound sound = Assets.getSound(path);
        sound.setBasePitch(basePitch);
        soundMap.put(path, sound);
        return sound;
    }

    /**
     * Plays a sound. Source must not be null.
     *
     * @param position         position of a sound. If null, source.getPosition() will be used
     * @param source           bearer of a sound. Must not be null for looped sounds
     * @param volumeMultiplier multiplier for sound volume
     */
    public void play(SolGame game, PlayableSound playableSound, @Nullable Vector2 position, @Nullable SolObject source, float volumeMultiplier) {
        if (playableSound == null) {
            return;
        }

        OggSound sound = playableSound.getOggSound();
        // logger.debug("Playing sound: {}", sound.getUrn().toString());

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

        // Calculate the volume multiplier for the sound
        float globalVolumeMultiplier = game.getCmp().getOptions().sfxVolumeMultiplier;
        if (globalVolumeMultiplier == 0) {
            return;
        }

        Vector2 cameraPosition = game.getCam().getPos();
        Planet nearestPlanet = game.getPlanetMan().getNearestPlanet();

        float airPerc = 0;
        if (nearestPlanet.getConfig().skyConfig != null) {
            float distanceToAtmosphere = cameraPosition.dst(nearestPlanet.getPos()) - nearestPlanet.getGroundHeight() - Const.ATM_HEIGHT / 2;
            airPerc = SolMath.clamp(1 - distanceToAtmosphere / (Const.ATM_HEIGHT / 2));
        }
        if (DebugOptions.SOUND_IN_SPACE) {
            airPerc = 1;
        }

        float maxSoundDist = 1 + 1.5f * Const.CAM_VIEW_DIST_GROUND * airPerc;

        SolShip hero = game.getHero();
        float soundRadius = hero == null ? 0 : hero.getHull().config.getApproxRadius();
        float distance = position.dst(cameraPosition) - soundRadius;
        float distanceMultiplier = SolMath.clamp(1 - distance / maxSoundDist);

        float volume = sound.getBaseVolume() * volumeMultiplier * distanceMultiplier * globalVolumeMultiplier;

        if (volume <= 0) {
            return;
        }

        // Calculate the pitch for the sound
        float pitch = SolMath.rnd(.97f, 1.03f) * game.getTimeFactor() * playableSound.getBasePitch();

        if (skipLooped(source, sound, game.getTime())) {
            return;
        }

        if (DebugOptions.SOUND_INFO) {
            debugHintDrawer.add(source, position, sound.toString());
        }

        Sound gdxSound = sound.getSound();
        gdxSound.play(volume, pitch, 0);
    }

    /**
     * Plays a sound. Source must not be null.
     *
     * @param position position of a sound. If null, source.getPosition() will be used
     * @param source   bearer of a sound. Must not be null for looped sounds
     */
    public void play(SolGame game, PlayableSound sound, @Nullable Vector2 position, @Nullable SolObject source) {
        this.play(game, sound, position, source, 1f);
    }

    private boolean skipLooped(SolObject source, OggSound sound, float time) {
        if (sound.getLoopTime() == 0) {
            return false;
        }

        boolean playing;
        Map<OggSound, Float> looped = loopedSoundMap.get(source);
        if (looped == null) {
            looped = new HashMap<>();
            loopedSoundMap.put(source, looped);
            playing = false;
        } else {
            Float endTime = looped.get(sound);
            if (endTime == null || endTime <= time) {
                looped.put(sound, time + sound.getLoopTime()); // argh, performance loss
                playing = false;
            } else {
                playing = time < endTime;
            }
        }
        return playing;
    }

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
