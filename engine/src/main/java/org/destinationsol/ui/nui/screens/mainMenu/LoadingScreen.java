/*
 * Copyright 2021 The Terasology Foundation
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
package org.destinationsol.ui.nui.screens.mainMenu;

import org.destinationsol.SolApplication;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.ui.nui.NUIScreenLayer;

import javax.inject.Inject;

public class LoadingScreen extends NUIScreenLayer {

    private SolApplication solApplication;
    private boolean loadTutorial;
    private boolean isNewGame;
    private String shipName;
    private WorldConfig worldConfig;
    private boolean firstUpdate;

    @Inject
    public LoadingScreen(SolApplication solApplication) {
        this.solApplication = solApplication;
    }

    @Override
    public void onAdded() {
        firstUpdate = false;
    }

    @Override
    public void update(float delta) {
        // Only start the long-running solApplication.play call after the first draw has been made.
        if (!firstUpdate) {
            firstUpdate = true;
        } else {
            solApplication.play(loadTutorial, shipName, isNewGame, worldConfig);
        }
    }

    public void setMode(boolean loadTutorial, String shipName, boolean isNewGame, WorldConfig worldConfig) {
        this.loadTutorial = loadTutorial;
        this.shipName = shipName;
        this.isNewGame = isNewGame;
        this.worldConfig = worldConfig;
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }
}
