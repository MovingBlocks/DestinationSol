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
package org.destinationsol.menu;

import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.ui.DisplayDimensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackgroundShip implements MenuObject {
    private static final Logger logger = LoggerFactory.getLogger(BackgroundShip.class);

    private final DisplayDimensions displayDimensions;

    public BackgroundShip(DisplayDimensions displayDimensions, World world) {
        MenuMeshLoader shipMesh = new MenuMeshLoader("engine:menuships", world);
        this.displayDimensions = displayDimensions;
    }

    @Override
    public void update() {

    }

}
