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
import org.destinationsol.assets.json.Json;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private String currentlyRegisteredModule;
    private Logger logger = LoggerFactory.getLogger(OggMusicManager.class);

    /**
     * Registers engine music.
     * TODO Make music registerable from modules, and then register this music through the new way.
     */
    public OggMusicManager(GameOptions options) {
        musicMap = new HashMap<>();
        registerMusic(GAME_MUSIC_SET, "engine:cimmerianDawn");
        registerMusic(GAME_MUSIC_SET, "engine:intoTheDark");
        registerMusic(GAME_MUSIC_SET, "engine:spaceTheatre");

        registerAllMenuMusic(options);
    }

    /**
     * Registers a music track into a music set.
     * <p>
     * Once registered, the track may then be played by {@link #playMusic(String, GameOptions)}. The track will be
     * played even if the {@code musicSet} set is already playing.
     *
     * @param musicSet Name of the set to register to
     * @param music    Fully qualified name of the music to register (eg. {@code "engine:dreadnaught"})
     */
    public void registerMusic(String musicSet, String music) {
        registerMusic(musicSet, Assets.getMusic(music).getMusic());
    }

    /**
     * Registers a music track into a music set.
     * <p>
     * Once registered, the track may then be played by {@link #playMusic(String, GameOptions)}. The track will be
     * played even if the {@code musicSet} set is already playing.
     *
     * @param musicSet Name of the set to register to
     * @param music    Music to register
     */
    public void registerMusic(String musicSet, Music music) {
        if (!musicMap.containsKey(musicSet)) {
            musicMap.put(musicSet, new ArrayList<>());
        }
        musicMap.get(musicSet).add(music);

    }

    /**
     * Sets a music set to play music from.
     * <p>
     * When end of each of tracks in the music set is reached, a neext song from the set is then played. When there are
     * no more tracks in a set, the first one is played again. When an invalid music set name is passed in, the music
     * just stops playing.
     *
     * @param musicSet Name of the set to play.
     * @param options  GameOptions with volume for the music to have.
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
     * @param music   Music track to play
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

    /**
     * Registers all music from the module of shipName
     *
     * @param moduleName Name of the module of ship that is loaded when creating a new game
     */
    public void registerModuleMusic(String moduleName, final GameOptions options) {
        if (currentlyRegisteredModule != null) {
            if (moduleName.equals(currentlyRegisteredModule)) {
                //Skips directly to module music
                stopMusic();
                playMusicTrack(this.musicMap.get(GAME_MUSIC_SET).get(2), options);
                return;
            } else {
                unregisterModuleMusic();
                currentlyRegisteredModule = null;
            }
        }

        if (Assets.getAssetHelper().list(Json.class, moduleName + ":musicConfig").isEmpty()) {
            return;
        } else {
            logger.info("Music Config found for module " + moduleName);
        }

        Json musicJson = Assets.getJson(moduleName + ":musicConfig");

        MusicConfig musicConfig = MusicConfig.load(moduleName, musicJson.getJsonValue());
        Map<String, List<String>> musicSets = musicConfig.getMusicMap();

        for (String music : musicSets.get(GAME_MUSIC_SET)) {
            registerMusic(GAME_MUSIC_SET, music);
            logger.info("Registered " + music);
        }

        currentlyRegisteredModule = moduleName;

        //Skips directly to module music
        stopMusic();
        playMusicTrack(this.musicMap.get(GAME_MUSIC_SET).get(2), options);
    }

    /**
     * Registers all module menu music
     */
    public void registerAllMenuMusic(GameOptions options) {
        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().list(Json.class, "[a-zA-Z]*:musicConfig");

        for (ResourceUrn configUrn : configUrnList) {
            String urnString = configUrn.toString();
            Json musicJson = Assets.getJson(urnString);
            JSONObject musicNode = musicJson.getJsonValue();

            for (Object musicFileName : musicNode.getJSONArray("menuMusic")) {
                if (!(musicFileName instanceof String)) {
                    break;
                }
                String music = (String) musicFileName;
                registerMusic(MENU_MUSIC_SET, urnString.split(":")[0] + ":" + music);
            }
        }

        registerMusic(MENU_MUSIC_SET, "engine:dreadnaught");
        //Skips to module music
        playMusicTrack(musicMap.get(MENU_MUSIC_SET).get(0), options);
    }

    /**
     * Unregisters all Module Music
     */
    public void unregisterModuleMusic() {
        musicMap.get(GAME_MUSIC_SET).clear();
        registerMusic(GAME_MUSIC_SET, "engine:cimmerianDawn");
        registerMusic(GAME_MUSIC_SET, "engine:intoTheDark");
        registerMusic(GAME_MUSIC_SET, "engine:spaceTheatre");

    }

    public void resetMusic() {
        musicMap.put(GAME_MUSIC_SET, new ArrayList<>());
    }
}
