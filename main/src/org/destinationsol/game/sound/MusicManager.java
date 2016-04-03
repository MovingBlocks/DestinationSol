/*
 * Copyright 2015-2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game.sound;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import org.destinationsol.GameOptions;
import org.destinationsol.files.FileManager;

import java.util.ArrayList;

/**
 * Singleton class that is responsible for playing all music throughout the game.
 */
public final class MusicManager {
    private static MusicManager instance = null;
    private static final String DIR = "res/sounds/";
    private final Music menuMusic;
    private ArrayList<Music> gameMusic = new ArrayList<Music>();;
    private Music currentlyPlaying = null;

    /**
     * Returns the singleton instance of this class.
     * @return The instance.
     */
    public static MusicManager getInstance() {
        if(instance == null) {
            instance = new MusicManager();
        }

        return instance;
    }

    /**
     * Initalise the MusicManager class.
     */
    private MusicManager() {
        menuMusic = Gdx.audio.newMusic(FileManager.getInstance().getStaticFile("res/sounds/music/dreadnaught.ogg"));
        gameMusic.add(Gdx.audio.newMusic(FileManager.getInstance().getStaticFile("res/sounds/music/cimmerian dawn.ogg")));
        gameMusic.add(Gdx.audio.newMusic(FileManager.getInstance().getStaticFile("res/sounds/music/into the dark.ogg")));
        gameMusic.add(Gdx.audio.newMusic(FileManager.getInstance().getStaticFile("res/sounds/music/space theatre.ogg")));
        menuMusic.setLooping(true);
    }

    /**
     * Start playing the music menu from the beginning of the track. The menu music loops continuously.
     */
    public void PlayMenuMusic(GameOptions options) {
        if(currentlyPlaying != null )
        {
            if(currentlyPlaying != menuMusic || (currentlyPlaying == menuMusic && !currentlyPlaying.isPlaying()))
            {
                    StopMusic();
                    playMusic(menuMusic, options);
            }
        }else
        {
            StopMusic();
            playMusic(menuMusic, options);
        }

    }

    public void PlayGameMusic(final GameOptions options) {
        StopMusic();
        if(currentlyPlaying != null && gameMusic.contains(currentlyPlaying))
        {
            int index = gameMusic.indexOf(currentlyPlaying) +1;
            if(gameMusic.size()-1 >= index)
            {
                playMusic(gameMusic.get(index), options);
                currentlyPlaying.setOnCompletionListener(new Music.OnCompletionListener() {
                    @Override
                    public void onCompletion(Music music) {
                        PlayGameMusic(options);
                    }
                });

            }else
            {
                playMusic(gameMusic.get(0), options);
            }
        }else
        {
           playMusic(gameMusic.get(0), options);
        }
    }

    public void playMusic(Music music, GameOptions options)
    {
        currentlyPlaying = music;
        currentlyPlaying.setVolume(options.volMul);
        currentlyPlaying.play();
    }
    /**
     * Stop playing all music.
     */
    public void StopMusic() {
        if(currentlyPlaying != null)
        {
            currentlyPlaying.stop();
        }
    }

    public void resetVolume(GameOptions options)
    {
        currentlyPlaying.setVolume(options.volMul);
    }
}