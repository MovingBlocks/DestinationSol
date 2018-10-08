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
package org.destinationsol.di;

import dagger.Module;
import dagger.Provides;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.SolFileReader;
import org.destinationsol.di.Qualifier.Mobile;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.game.context.Context;

import javax.inject.Singleton;

@Module
public class ApplicationModule {

    private final SolApplication application;
    private final Context context;
    private final SolFileReader reader;


    public ApplicationModule(SolApplication application, Context context, SolFileReader solFileReader){
        this.application = application;
        this.context = context;
        this.reader = solFileReader;
    }
    @Provides
    @Singleton
    public SolApplication provideSolApplication(){
        return application;
    }
    @Provides
    @Singleton
    public Context provideContext(){
        return context;
    }
    @Provides
    @Singleton
    public WorldConfig provideWorldConfig(){
        return new WorldConfig();
    }

    @Provides
    @Singleton
    public GameOptions provideGameOptions(@Mobile boolean mobile){
        return new GameOptions(mobile,reader);
    }


}
