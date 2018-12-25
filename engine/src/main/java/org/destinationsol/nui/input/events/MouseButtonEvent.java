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
package org.destinationsol.nui.input.events;


import com.badlogic.gdx.math.Vector2;
import org.destinationsol.nui.input.ButtonState;
import org.destinationsol.nui.input.MouseInput;
public class MouseButtonEvent extends ButtonEvent {

    private MouseInput button;
    private ButtonState state;
    private Vector2 mousePosition = new Vector2();

    public MouseButtonEvent(MouseInput button, ButtonState state, float delta) {
        super(delta);
        this.state = state;
        this.button = button;
    }

    @Override
    public ButtonState getState() {
        return state;
    }

    public MouseInput getButton() {
        return button;
    }

    public String getMouseButtonName() {
        return button.getName();
    }

    public String getButtonName() {
        return "mouse:" + getMouseButtonName();
    }

    public Vector2 getMousePosition() {
        return new Vector2(mousePosition);
    }

    protected void setButton(MouseInput button) {
        this.button = button;
    }

    public void setMousePosition(Vector2 mousePosition) {
        this.mousePosition.set(mousePosition);
    }

    public void reset() {
        reset(0f);
    }
}
