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
package org.destinationsol;

import dagger.Component;
import org.destinationsol.assets.AssetProvider;
import org.destinationsol.assets.audio.AudioProvider;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.game.SolGameComponent;
import org.destinationsol.ui.InputProvider;
import org.destinationsol.ui.SolInputManager;
import org.terasology.module.ModuleEnvironment;

import javax.inject.Singleton;


@Singleton
@Component(modules = {InputProvider.class,ModuleManagerProvider.class, AudioProvider.class,GameOptionsProvider.class, AssetProvider.class})
public interface SolApplicationComponent {
    void inject(SolApplication solApplication);

    ModuleEnvironment moduleEnviroment();
    SolInputManager inputModule();
    OggSoundManager soundManager();
    OggMusicManager musicManager();

}