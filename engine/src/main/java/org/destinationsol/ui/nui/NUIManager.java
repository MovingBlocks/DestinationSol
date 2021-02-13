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
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.sound.OggSound;
import org.destinationsol.game.context.Context;
import org.destinationsol.util.InjectionHelper;
import org.joml.Rectanglei;
import org.joml.Vector2i;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.input.InputType;
import org.terasology.input.MouseInput;
import org.terasology.input.device.CharKeyboardAction;
import org.terasology.input.device.RawKeyboardAction;
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
import org.terasology.nui.events.NUICharEvent;
import org.terasology.nui.canvas.CanvasRenderer;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.events.NUIMouseButtonEvent;
import org.terasology.nui.events.NUIMouseWheelEvent;
import org.terasology.nui.skin.UISkin;
import org.terasology.nui.util.RectUtility;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIText;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  The NUI Manager is responsible for the initialisation and interaction between the NUI library and the game.
 *  It manages the rendering and update cycles of NUI widgets, which are contained in {@link NUIScreenLayer}
 *  containers. Each NUIScreenLayer manages its own UI-specific logic and rendering.
 */
public class NUIManager {
    /**
     * This LibGDX renderer used for NUI. It shares a SpriteBatch with the main game, although not a ShapeRenderer.
     */
    private LibGDXCanvasRenderer canvasRenderer;
    /**
     * The game's canvas, which is used for all NUI rendering operations. See also {@link NUIManager#canvasRenderer}.
     */
    private SolCanvas canvas;
    /**
     * A blank white texture, used by-default for the text cursor.
     */
    private UITextureRegion whiteTexture;
    /**
     * The NUI mouse device. Receives input directly form LibGDX, independently of the game.
     */
    private MouseDevice mouse;
    /**
     * The NUI keyboard device. Receives input directly from LibHDX, independently of the game.
     */
    private KeyboardDevice keyboard;
    /**
     * This allows NUI to determine which widgets are in-focus at the moment, which is used primarily for tabbing.
     */
    private FocusManager focusManager;
    /**
     * The default UI skin used by all widgets.
     */
    private UISkin skin;
    /**
     * The current game context used to initialise UI screens.
     */
    private Context context;
    /*
     * An instance of the {@link SolApplication} used to access all game resources.
     */
    private SolApplication solApplication;

    /**
     * The UI stack. The elements are rendered from most recently added to least recent, so a stack-like structure
     * was used.
     */
    private Deque<NUIScreenLayer> uiScreens = new LinkedList<>();

    private static final String WHITE_TEXTURE_URN = "engine:uiWhiteTex";
    private static final String DEFAULT_SKIN_URN = "engine:default";
    private static final String BUTTON_CLICK_URN = "engine:uiHover";
    /**
     * The value 0.9 was found from {@link org.destinationsol.ui.SolInputManager#playClick}, so it was copied here to
     * retain the same click sound as the built-in UI.
     */
    private static final float BUTTON_CLICK_PITCH = 0.9f;

    /**
     * Creates and initialises a new NUIManager instance, which involves initialising a canvas and NUI input handlers.
     * @param solApplication the application to use for initialisation
     * @param context the game context to use when displaying UI screens
     * @param commonDrawer used to directly access the game's LibGDX {@link com.badlogic.gdx.graphics.g2d.SpriteBatch}
     * @param options used to initialise the UI scale with its previously-saved value
     */
    public NUIManager(SolApplication solApplication, Context context, CommonDrawer commonDrawer, GameOptions options) {
        NUIInputProcessor.CONSUME_INPUT = true;
        this.context = context;

        mouse = new LibGDXMouseDevice();
        keyboard = new LibGDXKeyboardDevice();
        canvasRenderer = new LibGDXCanvasRenderer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                commonDrawer.getSpriteBatch(), new ShapeRenderer(), false, true);
        focusManager = new FocusManagerImpl();
        whiteTexture = Assets.getDSTexture(WHITE_TEXTURE_URN).getUiTexture();
        skin = Assets.getSkin(DEFAULT_SKIN_URN);
        this.solApplication = solApplication;

        canvas = new SolCanvas(canvasRenderer, focusManager, keyboard, mouse, whiteTexture, skin, 100);
        TabbingManager.setFocusManager(focusManager);

        OggSound sound = Assets.getSound(BUTTON_CLICK_URN);
        sound.setBasePitch(BUTTON_CLICK_PITCH);

        // NUI widgets do not know how to obtain assets directly, so we need to provide defaults values here.
        UIButton.DEFAULT_CLICK_SOUND = sound;
        UIText.DEFAULT_CURSOR_TEXTURE = whiteTexture;

        // NOTE: SolApplication::addResizeSubscriber is not intended to be static, so use the instance form for compatibility
        solApplication.addResizeSubscriber(() -> resize(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight()));

        setUiScale(options.getNuiUiScale());
    }

    /**
     * Processes NUI input events (Keyboard and Mouse) and updates all UI layers.
     * @param solApplication the application to use
     */
    public void update(SolApplication solApplication) {
        canvas.processMousePosition(mouse.getPosition());
        canvas.setGameTime(System.currentTimeMillis());

        for (RawKeyboardAction action : keyboard.getInputQueue()) {
            NUIKeyEvent event = new NUIKeyEvent(mouse, keyboard, action.getInput(), action.getState());

            if (focusManager.getFocus() != null) {
                focusManager.getFocus().onKeyEvent(event);
            }

            for (NUIScreenLayer uiScreen : uiScreens) {
                if (uiScreen.onKeyEvent(event) || uiScreen.isBlockingInput() || event.isConsumed()) {
                    break;
                }
            }
        }

        for (CharKeyboardAction action : keyboard.getCharInputQueue()) {
            NUICharEvent event = new NUICharEvent(mouse, keyboard, action.getCharacter());

            if (focusManager.getFocus() != null) {
                focusManager.getFocus().onCharEvent(event);
            }

            for (NUIScreenLayer uiScreen : uiScreens) {
                if (uiScreen.onCharEvent(event) || uiScreen.isBlockingInput() || event.isConsumed()) {
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

    /**
     * Renders all UI layers.
     * @param gameDrawer used to directly access the game's LibGDX {@link com.badlogic.gdx.graphics.g2d.SpriteBatch}
     */
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

    /**
     * Obtains the topmost screen (rendered on-top of all others)
     * @return the topmost screen
     */
    public NUIScreenLayer getTopScreen() {
        return uiScreens.peek();
    }

    /**
     * Pushes a screen onto the UI stack.
     * @param layer the screen to add
     */
    public void pushScreen(NUIScreenLayer layer) {
        uiScreens.push(layer);

        // Populate all @In annotated fields in the layer class with values from the context.
        InjectionHelper.inject(layer, context);
        layer.setFocusManager(focusManager);
        layer.setNuiManager(this);
        layer.initialise();
    }

    /**
     * Removes the topmost screen from the UI stack and returns it.
     * @return the topmost screen
     */
    public NUIScreenLayer popScreen() {
        if (!uiScreens.isEmpty()) {
            uiScreens.peek().onRemoved();
        }
        return uiScreens.pop();
    }

    /**
     * Removes a screen form the UI stack. It is no longer updated or drawn.
     * @param screen the screen to remove
     */
    public void removeScreen(NUIScreenLayer screen) {
        screen.onRemoved();
        uiScreens.remove(screen);
    }

    /**
     * States if a screen is currently present on the UI stack.
     * @param screen the screen to search for
     * @return true if the screen is currently on the UI stack, otherwise false
     */
    public boolean hasScreen(NUIScreenLayer screen) {
        return uiScreens.contains(screen);
    }

    /**
     * States if a screen of a specified type is present on the UI stack.
     * @param type the type of screen to search for
     * @return true if a screen of the specified type is currently on the UI stack, otherwise false
     */
    public boolean hasScreenOfType(Class<? extends NUIScreenLayer> type) {
        for (NUIScreenLayer screenLayer : uiScreens) {
            if (screenLayer.getClass().isAssignableFrom(type)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all of the UI screens currently on the UI stack.
     * @return the UI stack
     */
    public Deque<NUIScreenLayer> getScreens() {
        return uiScreens;
    }

    /**
     * Returns the default {@link UISkin} for widgets.
     * @return the default {@link UISkin}
     */
    public UISkin getDefaultSkin() {
        return skin;
    }

    /**
     * Sets the game context to be used by all UI screens. Newly-added screens will the use this context.
     * @param context the new context to use
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /*
     * Returns the {@link SolApplication} instance for the game.
     * @return the {@link SolApplication} instance
     */
    public SolApplication getSolApplication() {
        return solApplication;
    }

    public boolean isMouseOnUi() {
        // TODO: Find better way of doing this.
        Vector2i mousePosition = mouse.getMousePosition();
        for (Rectanglei interactionRegion : canvas.getInteractionRegions()) {
            if (RectUtility.contains(interactionRegion, mousePosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Re-sizes the current canvas to a particular width and height. This is not always the same as the game's current
     * rendering resolution.
     * @param width the width to use
     * @param height the height to use
     */
    public void resize(int width, int height) {
        canvasRenderer.resize(width, height);
    }

    /**
     * Sets the UI scale to a specified value. This will internally render the UI at specified scale and up-scale it
     * to the target resolution.
     * @param scale the new UI scale to use
     */
    public void setUiScale(float scale) {
        canvas.setUiScale(scale);
        canvasRenderer.setUiScale(1.0f / scale);
    }

    private class SolCanvas extends CanvasImpl {
        public SolCanvas(CanvasRenderer renderer, FocusManager focusManager, KeyboardDevice keyboard, MouseDevice mouse, UITextureRegion whiteTexture, UISkin defaultSkin, int uiScale) {
            super(renderer, focusManager, keyboard, mouse, whiteTexture, defaultSkin, uiScale);
        }

        public List<Rectanglei> getInteractionRegions() {
            return interactionRegions.stream().map(region -> region.region).collect(Collectors.toList());
        }
    }
}
