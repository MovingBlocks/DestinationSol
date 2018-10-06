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

import dagger.Module;
import dagger.Provides;

@Module
public class GameOptionsProvider {
    private boolean mobile;
    private SolFileReader solFileReader;
    public GameOptionsProvider(boolean mobile, SolFileReader solFileReader){
        this.mobile = mobile;
        this.solFileReader = solFileReader;
    }

    @Provides
    public GameOptions provideGameOptions(){
        return new GameOptions(mobile,solFileReader);
    }
}
