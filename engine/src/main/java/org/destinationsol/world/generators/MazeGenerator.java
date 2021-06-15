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
package org.destinationsol.world.generators;

/**
 * This class defines the general behavior for Maze generators (such as having a layout, or a radius). Any Maze will be
 * created from a concrete implementation of this class, with behavior specific to that Maze defined there.
 * TODO: Implement behavior common to all Mazes as concrete methods in this class
 */
public abstract class MazeGenerator extends FeatureGenerator {

}
