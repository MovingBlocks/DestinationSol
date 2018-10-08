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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.google.common.collect.Sets;
import dagger.Provides;
import org.destinationsol.CommonDrawer;
import org.destinationsol.GameOptions;
import org.destinationsol.ModuleManager;
import org.destinationsol.SolApplication;
import org.destinationsol.SolFileReader;
import org.destinationsol.assets.audio.OggMusicManager;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.di.Qualifier.Mobile;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.game.context.Context;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.UiDrawer;
import org.terasology.module.Module;
import org.terasology.module.ModuleEnvironment;
import org.terasology.module.ModuleFactory;
import org.terasology.module.ModulePathScanner;
import org.terasology.module.ModuleRegistry;
import org.terasology.module.TableModuleRegistry;
import org.terasology.module.sandbox.StandardPermissionProviderFactory;

import javax.inject.Singleton;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@dagger.Module
public class AppModule {
    private final SolApplication application;
    private final Context context;
    private final SolFileReader reader;


    public AppModule(SolApplication application, Context context, SolFileReader solFileReader){
        this.application = application;
        this.context = context;
        this.reader = solFileReader;
    }

    @Provides
    @Mobile
    static  boolean isMobile(){
        return DebugOptions.EMULATE_MOBILE || Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
    }

    @Provides
    @Singleton
    static OggMusicManager proivdeOggMusicManager() {
        return new OggMusicManager();
    }

    @Provides
    @Singleton
    static OggSoundManager proivdeOggSoundManager(GameOptions gameOptions) {
        return new OggSoundManager(gameOptions);
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

    @Singleton
    @Provides
    static CommonDrawer provideCommonDrawer() {
        return new CommonDrawer();
    }

    @Singleton
    @Provides
    static UiDrawer provideUiDrawer(CommonDrawer commonDrawer) {
        return new UiDrawer(commonDrawer);
    }

    @Singleton
    @Provides
    static SolLayouts provideSolLayout() {
        return new SolLayouts();
    }

    @Provides
    @Singleton
    static ModuleRegistry provideTableModuleRegistry() {
        return new TableModuleRegistry();
    }

    @Provides
    @Singleton
    static ModuleManager provideModuleManager(ModuleEnvironment moduleEnvironment, ModuleRegistry moduleRegistry) {
        return new ModuleManager(moduleEnvironment, moduleRegistry);
    }

    @Provides
    @Singleton
    static SolInputManager provideInputManager(OggSoundManager oggSoundManager){
        return new SolInputManager(oggSoundManager);
    }

    @Provides
    @Singleton
    public ModuleEnvironment provideModuleEnviroment(ModuleRegistry registry) {

        try {
            URI engineClasspath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            org.terasology.module.Module engineModule = new ModuleFactory().createModule(Paths.get(engineClasspath));

            Path modulesRoot;
            if (DebugOptions.DEV_ROOT_PATH != null) {
                modulesRoot = Paths.get(".").resolve("modules");
            } else {
                modulesRoot = Paths.get(".").resolve("..").resolve("modules");
            }
            new ModulePathScanner().scan(registry, modulesRoot);

            Set<Module> requiredModules = Sets.newHashSet();
            requiredModules.add(engineModule);
            requiredModules.addAll(registry);
            return new ModuleEnvironment(requiredModules, new StandardPermissionProviderFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
