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
package org.destinationsol.ui.nui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.destinationsol.CommonDrawer;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.sound.OggSound;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.input.InputType;
import org.terasology.input.MouseInput;
import org.terasology.input.device.KeyboardAction;
import org.terasology.input.device.KeyboardDevice;
import org.terasology.input.device.MouseAction;
import org.terasology.input.device.MouseDevice;
import org.terasology.nui.FocusManager;
import org.terasology.nui.FocusManagerImpl;
import org.terasology.nui.TabbingManager;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.backends.libgdx.LibGDXCanvasRenderer;
import org.terasology.nui.backends.libgdx.LibGDXKeyboardDevice;
import org.terasology.nui.backends.libgdx.LibGDXMouseDevice;
import org.terasology.nui.backends.libgdx.NUIInputProcessor;
import org.terasology.nui.canvas.CanvasImpl;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.events.NUIMouseButtonEvent;
import org.terasology.nui.events.NUIMouseWheelEvent;
import org.terasology.nui.skin.UISkin;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIText;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class NUIManager {
    private LibGDXCanvasRenderer canvasRenderer;
    private CanvasImpl canvas;
    private UITextureRegion whiteTexture;
    private MouseDevice mouse;
    private KeyboardDevice keyboard;
    private FocusManager focusManager;
    private UISkin skin;

    private Deque<NUIScreenLayer> uiScreens = new LinkedList<>();

    private static final String WHITE_TEXTURE_URN = "engine:uiWhiteTex";
    private static final String DEFAULT_SKIN_URN = "engine:default";
    private static final String BUTTON_CLICK_URN = "engine:uiHover";
    private static final float BUTTON_CLICK_PITCH = 0.9f;

    public NUIManager(SolApplication solApplication, CommonDrawer commonDrawer) {
        NUIInputProcessor.CONSUME_INPUT = true;

        mouse = new LibGDXMouseDevice();
        keyboard = new LibGDXKeyboardDevice();
        canvasRenderer = new LibGDXCanvasRenderer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                commonDrawer.getSpriteBatch(), new ShapeRenderer(), false, true);
        focusManager = new FocusManagerImpl();
        whiteTexture = Assets.getDSTexture(WHITE_TEXTURE_URN).getUiTexture();
        skin = Assets.getAssetHelper().get(new ResourceUrn(DEFAULT_SKIN_URN), UISkin.class).get();

        canvas = new CanvasImpl(canvasRenderer, focusManager, keyboard, mouse, whiteTexture, skin, 100);
        TabbingManager.setFocusManager(focusManager);

        OggSound sound = Assets.getSound(BUTTON_CLICK_URN);
        sound.setBasePitch(BUTTON_CLICK_PITCH);

        UIButton.DEFAULT_CLICK_SOUND = sound;
        UIText.DEFAULT_CURSOR_TEXTURE = whiteTexture;

        // NOTE: SolApplication::addResizeSubscriber is not intended to be static, so use the instance form for compatibility
        solApplication.addResizeSubscriber(() -> resize(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight()));
    }

    public void update(SolApplication solApplication) {
        canvas.processMousePosition(mouse.getMousePosition());
        canvas.setGameTime(System.currentTimeMillis());

        for (KeyboardAction action : keyboard.getInputQueue()) {
            NUIKeyEvent event = new NUIKeyEvent(mouse, keyboard, action.getInput(), action.getInputChar(), action.getState());

            if (focusManager.getFocus() != null) {
                focusManager.getFocus().onKeyEvent(event);
            }

            for (NUIScreenLayer uiScreen : uiScreens) {
                if (uiScreen.onKeyEvent(event) || uiScreen.isBlockingInput() || event.isConsumed()) {
                    break;
                }
            }
        }

        for (MouseAction action : mouse.getInputQueue()) {
            if (action.getInput().getType() == InputType.MOUSE_BUTTON) {
                if (action.getState().isDown()) {
                    canvas.processMouseClick((MouseInput) action.getInput(), action.getMousePosition());
                } else {
                    canvas.processMouseRelease((MouseInput) action.getInput(), action.getMousePosition());
                }

                NUIMouseButtonEvent event = new NUIMouseButtonEvent((MouseInput) action.getInput(), action.getState(), action.getMousePosition());

                if (focusManager.getFocus() != null) {
                    focusManager.getFocus().onMouseButtonEvent(event);
                }

                for (NUIScreenLayer uiScreen : uiScreens) {
                    uiScreen.onMouseButtonEvent(event);
                    if (event.isConsumed()) {
                        break;
                    }
                }
            } else if (action.getInput().getType() == InputType.MOUSE_WHEEL) {
                canvas.processMouseWheel(action.getTurns(), action.getMousePosition());

                NUIMouseWheelEvent event = new NUIMouseWheelEvent(mouse, keyboard, action.getMousePosition(), action.getTurns());

                if (focusManager.getFocus() != null) {
                    focusManager.getFocus().onMouseWheelEvent(event);
                }

                for (NUIScreenLayer uiScreen : uiScreens) {
                    uiScreen.onMouseWheelEvent(event);
                    if (event.isConsumed()) {
                        break;
                    }
                }
            }
        }

        for (NUIScreenLayer uiScreen : uiScreens) {
            uiScreen.update(Gdx.graphics.getDeltaTime());
        }
    }

    public void draw(CommonDrawer gameDrawer) {
        gameDrawer.getSpriteBatch().flush();

        canvas.preRender();

        // NOTE: Need to render in the inverse to how they are updated, so the top screen is drawn last
        Iterator<NUIScreenLayer> screenIterator = uiScreens.descendingIterator();
        while (screenIterator.hasNext()) {
            NUIScreenLayer screenLayer = screenIterator.next();
            canvas.setSkin(screenLayer.getSkin());
            canvas.drawWidget(screenLayer);
        }

        canvas.postRender();

        gameDrawer.getSpriteBatch().flush();
    }

    public NUIScreenLayer getTopScreen() {
        return uiScreens.peek();
    }

    public void pushScreen(NUIScreenLayer layer) {
        uiScreens.push(layer);

        layer.setFocusManager(focusManager);
        layer.setNuiManager(this);
        layer.initialise();
    }

    public NUIScreenLayer popScreen() {
        if (!uiScreens.isEmpty()) {
            uiScreens.peek().onRemoved();
        }
        return uiScreens.pop();
    }

    public void removeScreen(NUIScreenLayer screen) {
        screen.onRemoved();
        uiScreens.remove(screen);
    }

    public boolean hasScreen(NUIScreenLayer screen) {
        return uiScreens.contains(screen);
    }

    public boolean hasScreenOfType(Class<? extends NUIScreenLayer> type) {
        for (NUIScreenLayer screenLayer : uiScreens) {
            if (screenLayer.getClass().isAssignableFrom(type)) {
                return true;
            }
        }

        return false;
    }

    public Deque<NUIScreenLayer> getScreens() {
        return uiScreens;
    }

    public UISkin getDefaultSkin() {
        return skin;
    }

    public void resize(int width, int height) {
        canvasRenderer.resize(width, height);
    }
}
