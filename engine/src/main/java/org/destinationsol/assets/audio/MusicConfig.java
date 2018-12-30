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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extracts music files from the musicConfig.json and stores them in a music map.
 * 
 * A music map contains 2 lists, "gameMusic" and "menuMusic" with the respective music files, it is then passed on to OggMusicManager to be played.
 */
public class MusicConfig {
    public final String moduleName;
    public final Map<String, List<String>> musicMap;

    public MusicConfig(String moduleName, Map<String, List<String>> musicMap) {
        this.moduleName = moduleName;
        this.musicMap = musicMap;
    }

    static MusicConfig load(String moduleName, JSONObject config) {
        List<String> menuMusicSet = new ArrayList<>();
        List<String> gameMusicSet = new ArrayList<>();

        JSONArray menuMusicArray = config.getJSONArray("menuMusic");
        JSONArray gameMusicArray = config.getJSONArray("gameMusic");

        for (Object musicFileName : menuMusicArray) {
            if (musicFileName instanceof String) {
                menuMusicSet.add(moduleName + ":" + musicFileName);
            }
        }

        for (Object musicFileName : gameMusicArray) {
            if (musicFileName instanceof String) {
                gameMusicSet.add(moduleName + ":" + musicFileName);
            }
        }

        Map<String, List<String>> musicMap = new HashMap<>();
        musicMap.put(OggMusicManager.MENU_MUSIC_SET, menuMusicSet);
        musicMap.put(OggMusicManager.GAME_MUSIC_SET, gameMusicSet);

        return new MusicConfig(moduleName, musicMap);
    }

    public Map<String, List<String>> getMusicMap() {
        return musicMap;
    }
}
