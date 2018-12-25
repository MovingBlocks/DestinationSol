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

package org.destinationsol.nui.input.device.nulldevices;

import org.terasology.input.ControllerDevice;
import org.terasology.input.device.ControllerAction;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * A dummy implementation of {@link ControllerDevice}.
 */
public class NullControllerDevice implements ControllerDevice {

    @Override
    public Queue<ControllerAction> getInputQueue() {
        return new ArrayDeque<>();
    }

    @Override
    public List<String> getControllers() {
        return Collections.emptyList();
    }
}
