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

package org.destinationsol.save;

import org.destinationsol.game.FarObject;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.SolarSystem;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
    public final List<FarObject> farObjects;
    public final List<SolarSystem> systems;
    public final List<Planet> planets;

    public SaveData() {
        farObjects = new ArrayList<>();
        planets = new ArrayList<>();
        systems = new ArrayList<>();
    }
}
