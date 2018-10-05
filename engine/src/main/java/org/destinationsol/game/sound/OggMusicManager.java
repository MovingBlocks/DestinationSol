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

import com.badlogic.gdx.audio.Music;
import org.destinationsol.GameOptions;
import org.destinationsol.assets.Assets;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that is responsible for playing all music throughout the game.
 *
 * @author SimonC4
 * @author Rulasmur
 */
public class OggMusicManager {
    private final Music menuMusic;
    private final List<Music> gameMusic;
    private Music currentlyPlaying = null;

    public OggMusicManager() {
        menuMusic = Assets.getMusic("engine:dreadnaught").getMusic();
        menuMusic.setLooping(true);

        gameMusic = new ArrayList<>();
        gameMusic.add(Assets.getMusic("engine:cimmerianDawn").getMusic());
        gameMusic.add(Assets.getMusic("engine:intoTheDark").getMusic());
        gameMusic.add(Assets.getMusic("engine:spaceTheatre").getMusic());
    }

    /**
     * Start playing the music menu from the beginning of the track. The menu music loops continuously.
     */
    public void playMenuMusic(GameOptions options) {
        if (currentlyPlaying != null) {
            if (currentlyPlaying != menuMusic || !currentlyPlaying.isPlaying()) {
                stopMusic();
                playMusic(menuMusic, options);
            }
        } else {
            stopMusic();
            playMusic(menuMusic, options);
        }
    }

    public void playGameMusic(final GameOptions options) {
        stopMusic();
        if (currentlyPlaying != null && gameMusic.contains(currentlyPlaying)) {
            int index = gameMusic.indexOf(currentlyPlaying) + 1;
            if (gameMusic.size() - 1 >= index) {
                playMusic(gameMusic.get(index), options);
                currentlyPlaying.setOnCompletionListener(music -> playGameMusic(options));

            } else {
                playMusic(gameMusic.get(0), options);
            }
        } else {
            playMusic(gameMusic.get(0), options);
        }
    }

    public void playMusic(Music music, GameOptions options) {
        currentlyPlaying = music;
        currentlyPlaying.setVolume(options.musicVolume.getVolume());
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
        currentlyPlaying.setVolume(options.musicVolume.getVolume());
    }
}
