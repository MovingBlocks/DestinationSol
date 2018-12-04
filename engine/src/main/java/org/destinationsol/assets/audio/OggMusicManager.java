/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol.assets.audio;

import com.badlogic.gdx.audio.Music;
import org.destinationsol.GameOptions;
import org.destinationsol.assets.Assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for playing all music throughout the game.
 * <p>
 * This class does not rely on external updates; once a music set is set to be played, it will play, even looping,
 * until another is chosen. By default, music does not play concurrently.
 */
public class OggMusicManager {
    public static final String MENU_MUSIC_SET = "menu";
    public static final String GAME_MUSIC_SET = "game";
    private final Map<String, List<Music>> musicMap;
    private Music currentlyPlaying;

    /**
     * Registers engine music.
     * TODO Make music registerable from modules, and then register this music through the new way.
     */
    public OggMusicManager() {
        musicMap = new HashMap<>();
        registerMusic(MENU_MUSIC_SET, "engine:dreadnaught");
        registerMusic(GAME_MUSIC_SET, "engine:cimmerianDawn");
        registerMusic(GAME_MUSIC_SET, "engine:intoTheDark");
        registerMusic(GAME_MUSIC_SET, "engine:spaceTheatre");
    }

    /**
     *Registers a music track into a music set.
     *
     * Once registered, the track may then be played by {@link #playMusic(String, GameOptions)}. The track will be
     * played even if the {@code musicSet} set is already playing.
     * @param musicSet Name of the set to register to
     * @param music Fully qualified name of the music to register (eg. {@code "engine:dreadnaught"})
     */
    public void registerMusic(String musicSet, String music) {
        registerMusic(musicSet, Assets.getMusic(music).getMusic());
    }

    /**
     * Registers a music track into a music set.
     *
     * Once registered, the track may then be played by {@link #playMusic(String, GameOptions)}. The track will be
     * played even if the {@code musicSet} set is already playing.
     * @param musicSet Name of the set to register to
     * @param music Music to register
     */
    public void registerMusic(String musicSet, Music music) {
        if (!musicMap.containsKey(musicSet)) {
            musicMap.put(musicSet, new ArrayList<>());
        }
        musicMap.get(musicSet).add(music);

    }

    /**
     * Sets a music set to play music from.
     *
     * When end of each of tracks in the music set is reached, a neext song from the set is then played. When there are
     * no more tracks in a set, the first one is played again. When an invalid music set name is passed in, the music
     * just stops playing.
     *
     * @param musicSet Name of the set to play.
     * @param options GameOptions with volume for the music to have.
     */
    public void playMusic(final String musicSet, final GameOptions options) {
        stopMusic();
        if (musicMap.get(musicSet).isEmpty()) {
            return;
        }
        int index = 0;
        if (currentlyPlaying != null && musicMap.get(musicSet).contains(currentlyPlaying)) {
            // skip the track
            index = musicMap.get(musicSet).indexOf(currentlyPlaying);
            if (++index + 1 > musicMap.get(musicSet).size()) { // next track, plus one because indexing is from 0
                index = 0;
            }
        }
        final Music music = musicMap.get(musicSet).get(index);
        music.setOnCompletionListener(a -> playMusic(musicSet, options));
        playMusicTrack(music, options);
    }

    /**
     * Plays a music track and sets it as current.
     *
     * @param music Music track to play
     * @param options Options with volume for the track
     */
    private void playMusicTrack(Music music, GameOptions options) {
        currentlyPlaying = music;
        currentlyPlaying.setVolume(options.musicVolume.getVolume());
        currentlyPlaying.play();
    }

    /**
     * Stop playing current music track.
     */
    private void stopMusic() {
        if (currentlyPlaying != null) {
            currentlyPlaying.stop();
        }
    }

    /**
     * Changes volume of currently playing track.
     *
     * @param options GameOptions containing the requested volume.
     */
    public void changeVolume(GameOptions options) {
        currentlyPlaying.setVolume(options.musicVolume.getVolume());
    }
}
