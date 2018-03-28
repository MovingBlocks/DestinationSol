/*
 * Copyright 2017 MovingBlocks
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

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.destinationsol.SolApplication;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.SolGame;

public class InitializationUtilities {

    public static SolGame game;
    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        DebugOptions.DEV_ROOT_PATH = "engine/src/main/resources/";
        final LwjglApplication application = new LwjglApplication(new SolApplication());
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        game = ((SolApplication) application.getApplicationListener()).getGame();
    }
}
