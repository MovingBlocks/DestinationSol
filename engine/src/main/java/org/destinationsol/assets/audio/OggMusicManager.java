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
    private final Music menuMusic;
    private final List<Music> gameMusic;
    private Music currentlyPlaying = null;

    public OggMusicManager() {
        musicMap = new HashMap<>();
        menuMusic = Assets.getMusic("engine:dreadnaught").getMusic();
        menuMusic.setLooping(true);
        registerMusic(MENU_MUSIC_SET, "engine:dreadnaught");

        gameMusic = new ArrayList<>();
        gameMusic.add(Assets.getMusic("engine:cimmerianDawn").getMusic());
        gameMusic.add(Assets.getMusic("engine:intoTheDark").getMusic());
        gameMusic.add(Assets.getMusic("engine:spaceTheatre").getMusic());
        registerMusic(GAME_MUSIC_SET, "engine:cimmerianDawn");
        registerMusic(GAME_MUSIC_SET, "engine:intoTheDark");
        registerMusic(GAME_MUSIC_SET, "engine:spaceTheatre");
    }

    public void registerMusic(String musicSet, String music) {
        registerMusic(musicSet, Assets.getMusic(music).getMusic());
    }

    public void registerMusic(String musicSet, Music music) {
        if (!musicMap.containsKey(musicSet)) {
            musicMap.put(musicSet, new ArrayList<>());
        }
        musicMap.get(musicSet).add(music);

    }

    public void playMusic(final String musicSet, final GameOptions options) {
        stopMusic();
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
     * Start playing the music menu from the beginning of the track. The menu music loops continuously.
     */
    public void playMenuMusic(GameOptions options) {
        playMusic(MENU_MUSIC_SET, options);
    }

    public void playGameMusic(final GameOptions options) {
        playMusic(GAME_MUSIC_SET, options);
    }

    public void playMusicTrack(Music music, GameOptions options) {
        currentlyPlaying = music;
        currentlyPlaying.setVolume(options.musicVolumeMultiplier);
        currentlyPlaying.play();
    }

    /**
     * Stop playing all music.
     */
    public void stopMusic() {
        if (currentlyPlaying != null) {
            currentlyPlaying.stop();
        }
    }

    public void resetVolume(GameOptions options) {
        currentlyPlaying.setVolume(options.musicVolumeMultiplier);
    }
}
