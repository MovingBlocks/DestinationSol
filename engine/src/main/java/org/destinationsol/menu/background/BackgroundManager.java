/*
 * Copyright 2019 MovingBlocks
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
package org.destinationsol.menu.background;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.Const;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.UiDrawer;

public class BackgroundManager {
    private World world;

    private BackgroundAsteroidManager asteroidManager;
    private BackgroundShipManager shipManager;

    public BackgroundManager(DisplayDimensions displayDimensions) {
        world = new World(new Vector2(0, 0), true);
        MenuContactListener contactListener = new MenuContactListener();
        world.setContactListener(contactListener);

        asteroidManager = new BackgroundAsteroidManager(displayDimensions, world);
        shipManager = new BackgroundShipManager(displayDimensions, world);
    }

    public void update() {
        asteroidManager.update();
        shipManager.update();
        world.step(Const.REAL_TIME_STEP, 6, 2);
    }

    public void draw(UiDrawer uiDrawer) {
        asteroidManager.draw(uiDrawer);
        shipManager.draw(uiDrawer);
    }
}
