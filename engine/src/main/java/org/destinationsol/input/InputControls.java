/*
 * Copyright 2022 The Terasology Foundation
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

package org.destinationsol.input;

import org.destinationsol.GameOptions;

import java.util.EnumSet;

/**
 * The base interface implementing new controls that conform to certain control types.
 *
 * It is recommended that you create an enum implementing this interface per-module to contain all the new controls you add to the game.
 * <br>
 * Example Implementation:
 * <pre>
 * public enum ModuleControls implements InputControls {
 *     CONTROL_1("module_control1", "Control 1", EnumSet.of(GameOptions.ControlType.KEYBOARD), Input.Keys.W),
 *     CONTROL_2("module_control2", "Control 2", EnumSet.of(GameOptions.ControlType.MIXED), Input.Keys.S),
 *     CONTROL_2("module_control3", "Control 3", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.PAGE_DOWN);
 *
 *     private final String controlName;
 *     private final String displayName;
 *     private final EnumSet<GameOptions.ControlType> controlTypes;
 *     private final int[] defaultInputs;
 *
 *     ModuleControls(String controlName, String displayName, EnumSet<GameOptions.ControlType> controlTypes, int... defaultInputs) {
 *         this.controlName = controlName;
 *         this.displayName = displayName;
 *         this.controlTypes = controlTypes;
 *         this.defaultInputs = defaultInputs;
 *     }
 *
 *     public String getControlName() {
 *         return controlName;
 *     }
 *
 *     public String getDisplayName() {
 *         return displayName;
 *     }
 *
 *     public EnumSet<GameOptions.ControlType> getControlTypes() {
 *         return controlTypes;
 *     }
 *
 *     public int[] getDefaultInputs() {
 *         return defaultInputs;
 *     }
 * }
 * </pre>
 * @see DefaultControls the built-in game controls
 */
public interface InputControls {
    /**
     * Returns the control name. This is the name used internally to serialise and de-serialise the control values.
     * @return the control name
     */
    String getControlName();

    /**
     * Returns the control's display name. This name is used when referencing the control in user-facing text.
     * @return the control's display name
     */
    String getDisplayName();

    /**
     * Returns the supported control schemes for this control.
     * @return the supported control schemes
     */
    EnumSet<GameOptions.ControlType> getControlTypes();

    /**
     * Returns the default assigned input values for this control. Any one of these values can trigger the control.
     * @return the default assigned input values
     */
    int[] getDefaultInputs();
}
