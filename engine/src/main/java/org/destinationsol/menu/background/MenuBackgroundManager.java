/*
 * Copyright 2020 MovingBlocks
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

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.Const;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.UiDrawer;

import javax.inject.Inject;

/**
 * <h1>General Menu background manager</h1>
 * Manages menu viewport, world, and the menu world's objects.
 */
public class MenuBackgroundManager {
    public static final float VIEWPORT_HEIGHT = 5f;

    private World world;

    private MenuBackgroundAsteroidManager asteroidManager;
    private MenuBackgroundShipManager shipManager;

    OrthographicCamera backgroundCamera;

    @Inject
    public MenuBackgroundManager(DisplayDimensions displayDimensions) {
        world = new World(new Vector2(0, 0), true);

        asteroidManager = new MenuBackgroundAsteroidManager(displayDimensions, world);
        shipManager = new MenuBackgroundShipManager(displayDimensions, world);

        backgroundCamera = new OrthographicCamera(VIEWPORT_HEIGHT * displayDimensions.getRatio(), -VIEWPORT_HEIGHT);
    }

    public void update() {
        asteroidManager.update();
        shipManager.update();
        world.step(Const.REAL_TIME_STEP, 6, 2);
    }

    public void draw(UiDrawer drawer) {
        drawer.setMatrix(backgroundCamera.combined);
        asteroidManager.draw(drawer);
        shipManager.draw(drawer);
        drawer.updateMtx();
    }

}
