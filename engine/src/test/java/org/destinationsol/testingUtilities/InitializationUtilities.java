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
package org.destinationsol.testingUtilities;

import org.destinationsol.entitysystem.EntitySystemManager;
import org.destinationsol.modules.FacadeModuleConfig;
import org.destinationsol.modules.ModuleManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import org.destinationsol.SolApplication;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.SolGame;
import org.destinationsol.testsupport.TestModuleConfig;
import org.terasology.context.Lifetime;
import org.terasology.gestalt.di.DefaultBeanContext;
import org.terasology.gestalt.di.ServiceRegistry;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.gestalt.module.ModuleFactory;
import org.terasology.gestalt.module.ModulePathScanner;
import org.terasology.gestalt.module.TableModuleRegistry;

import java.util.Collections;

public final class InitializationUtilities {

    public static SolGame game;
    private static boolean initialized;

    private InitializationUtilities() { } // empty private constructor for utility class

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        DebugOptions.DEV_ROOT_PATH = "engine/src/main/resources/";
        GL20 mockGL = new MockGL();
        Gdx.gl = mockGL;
        Gdx.gl20 = mockGL;

        ServiceRegistry serviceRegistry = new ServiceRegistry();
        serviceRegistry
                .with(FacadeModuleConfig.class)
                .lifetime(Lifetime.Singleton)
                .use(TestModuleConfig::new);
        serviceRegistry
                .with(ModulePathScanner.class)
                .lifetime(Lifetime.Singleton);

        final HeadlessApplication application = new HeadlessApplication(new SolApplication(100, serviceRegistry), new HeadlessApplicationConfiguration());
        game = ((SolApplication) application.getApplicationListener()).getGame();
    }
}

// Following lies the offering to the great vampire gods of SolUniverse, with great praises for them to keep local magic working.
//
//
//               __             _,-"~^"-.
//             _// )      _,-"~`         `.
//           ." ( /`"-,-"`                 ;
//          / 6                             ;
//         /           ,             ,-"     ;
//        (,__.--.      \           /        ;
//         //'   /`-.\   |          |        `._________
//           _.-'_/`  )  )--...,,,___\     \-----------,)
//         ((("~` _.-'.-'           __`-.   )         //
//               ((("`             (((---~"`         //
//                                                  ((________________
//                                                  `----""""~~~~^^^```



// (Read: the hack is ugly, but working and I don't wanna redo it)
