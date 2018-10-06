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
import org.destinationsol.CommonDrawer;
import org.destinationsol.di.Qualifier.ResolutionQualifier;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.UiDrawer;

import javax.inject.Singleton;

@Module
public class DrawerModule {
    @Singleton
    @Provides
    public CommonDrawer provideCommonDrawer(){
        return new CommonDrawer();
    }

    @Singleton
    @Provides
    public UiDrawer provideUiDrawer(CommonDrawer commonDrawer){
        return new UiDrawer(commonDrawer);
    }

    @Singleton
    @Provides
    public SolLayouts provideSolLayout(UiDrawer uiDrawer){
        return new SolLayouts(uiDrawer.ratio);
    }



}

