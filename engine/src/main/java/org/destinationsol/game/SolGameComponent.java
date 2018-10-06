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
package org.destinationsol.game;

import dagger.Component;
import dagger.Subcomponent;
import org.destinationsol.SolApplicationComponent;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.game.chunk.ChunkManager;
import org.destinationsol.game.chunk.ChunkProvider;

import javax.inject.Scope;

@Component(dependencies = {SolApplicationComponent.class},modules = {ChunkProvider.class})
@GameScope
public interface SolGameComponent {

//    Assets assets();
    OggSoundManager soundManager();
    OggMusicManager musicManager();
    ChunkManager chunkManager();
}