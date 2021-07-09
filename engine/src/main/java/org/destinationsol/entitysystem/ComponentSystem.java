/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.entitysystem;

/**
 * This is System part of ECS pattern.
 * <p>Don't forget make constructor and mark it with `@Inject`(empty contrutor too) - this is enable class to discover it via DI.</p>
 */
public class ComponentSystem {

    //TODO: Use this stuff

    /**
     * Called to initialise the system. This occurs after injection, but before other systems are necessarily initialised, so they should not be interacted with.
     */
    public void initialise() {
    }

    /**
     * Called after all systems are initialised, but before the game is loaded.
     */
    public void preBegin() {
    }

    /**
     * Called after the game is loaded, right before first frame.
     */
    public void postBegin() {
    }

    /**
     * Called before the game is auto-saved.
     */
    public void preAutoSave() {
    }

    /**
     * Called after the game is auto-saved.
     */
    public void postAutoSave() {
    }

    /**
     * Called before the game is saved (this may be after shutdown).
     */
    public void preSave() {
    }

    /**
     * Called after the game is saved.
     */
    public void postSave() {
    }

    /**
     * Called right before the game is shut down.
     */
    public void shutdown() {
    }
}

