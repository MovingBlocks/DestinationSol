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
import org.destinationsol.ui.UiDrawer;
import org.joml.Vector2i;
import org.terasology.input.InputType;
import org.terasology.input.Keyboard;
import org.terasology.input.MouseInput;
import org.terasology.input.device.CharKeyboardAction;
import org.terasology.input.device.KeyboardDevice;
import org.terasology.input.device.MouseAction;
import org.terasology.input.device.MouseDevice;
import org.terasology.input.device.RawKeyboardAction;
import org.terasology.joml.geom.Rectanglei;
import org.terasology.nui.Canvas;
import org.terasology.nui.FocusManager;
import org.terasology.nui.TabbingManager;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.UIWidget;
import org.terasology.nui.asset.UIElement;
import org.terasology.nui.backends.libgdx.LibGDXCanvasRenderer;
import org.terasology.nui.backends.libgdx.LibGDXKeyboardDevice;
import org.terasology.nui.backends.libgdx.LibGDXMouseDevice;
import org.terasology.nui.backends.libgdx.NUIInputProcessor;
import org.terasology.nui.canvas.CanvasImpl;
import org.terasology.nui.canvas.CanvasRenderer;
import org.terasology.nui.events.NUICharEvent;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.events.NUIMouseButtonEvent;
import org.terasology.nui.events.NUIMouseWheelEvent;
import org.terasology.nui.skin.UISkin;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIText;

import javax.inject.Inject;
import java.io.Closeable;
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
    /**
     * The baseline UI scale used on Android.
     */
    private final float baseUIScale;

    /**
     * The UI stack. The elements are rendered from most recently added to least recent, so a stack-like structure
     * was used.
     */
    private Deque<NUIScreenLayer> uiScreens = new LinkedList<>();

    /**
     * An {@link UiDrawer} instance that can be used to interact with legacy UI screens that require it.
     */
    private final UiDrawer uiDrawer;

    private static final String WHITE_TEXTURE_URN = "engine:uiWhiteTex";
    private static final String DEFAULT_SKIN_URN = "engine:default";
    private static final String BUTTON_CLICK_URN = "engine:uiHover";
    private static final float MOBILE_UI_DENSITY = 2.0f;
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
    @Inject
    public NUIManager(SolApplication solApplication,
                      Context context,
                      CommonDrawer commonDrawer,
                      GameOptions options,
                      UiDrawer uiDrawer,
                      FocusManager focusManager) {
        NUIInputProcessor.CONSUME_INPUT = false;
        this.context = context;
        this.uiDrawer = uiDrawer;
        this.focusManager = focusManager;

        // TODO: Re-enable tabbing when it works
        TabbingManager.tabForwardInput = Keyboard.Key.NONE;
        TabbingManager.tabBackInputModifier = Keyboard.Key.NONE;
        TabbingManager.activateInput = Keyboard.Key.NONE;

        mouse = new LibGDXMouseDevice();
        keyboard = new LibGDXKeyboardDevice();
        canvasRenderer = new LibGDXCanvasRenderer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                commonDrawer.getSpriteBatch(), new ShapeRenderer(), false, true);
        whiteTexture = Assets.getDSTexture(WHITE_TEXTURE_URN).getUiTexture();
        skin = Assets.getSkin(DEFAULT_SKIN_URN);

        canvas = new SolCanvas(canvasRenderer, focusManager, keyboard, mouse, whiteTexture, skin, 100);
        TabbingManager.setFocusManager(focusManager);

        OggSound sound = Assets.getSound(BUTTON_CLICK_URN);
        sound.setBasePitch(BUTTON_CLICK_PITCH);

        // NUI widgets do not know how to obtain assets directly, so we need to provide defaults values here.
        UIButton.DEFAULT_CLICK_SOUND = sound;
        UIText.DEFAULT_CURSOR_TEXTURE = whiteTexture;

        // NOTE: SolApplication::addResizeSubscriber is not intended to be static, so use the instance form for compatibility
        solApplication.addResizeSubscriber(() -> resize(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight()));

        // Mobile screen densities can vary considerably, so a large digital resolution can be displayed
        // on a very small screen. Due to this, it makes sense to scale the UI roughly proportionally
        // to form a more sensible default. Otherwise, the UI may appear to be too big/small.
        // On very large mobile screens, such as tablets, you may need to adjust the UI scale further in the game options.
        if (solApplication.isMobile()) {
            baseUIScale = Gdx.graphics.getDensity() / MOBILE_UI_DENSITY;
        } else {
            baseUIScale = 1.0f;
        }

        setUiScale(options.getNuiUiScale());
    }

    /**
     * Processes NUI input events (Keyboard and Mouse) and updates all UI layers.
     * @param solApplication the application to use
     */
    public void update(SolApplication solApplication) {
        mouse.update();

        for (int pointer = 0; pointer < mouse.getMaxPointers(); pointer++) {
            canvas.processMousePosition(mouse.getPosition(pointer), pointer);
        }

        canvas.setGameTime(System.currentTimeMillis());

        for (RawKeyboardAction action : keyboard.getInputQueue()) {
            NUIKeyEvent event = new NUIKeyEvent(mouse, keyboard, action.getInput(), action.getState());

            if (focusManager.getFocus() != null) {
                focusManager.getFocus().onKeyEvent(event);
            }

            // Create a copy of the list, as screens may be removed in response to key events.
            // This does not happen for most key events, however it may occur with KeyActivatedButton instances,
            // which already have special handling in the NUIScreenLayer class.
            for (NUIScreenLayer uiScreen : new LinkedList<>(uiScreens)) {
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
                    if (canvas.processMouseClick((MouseInput) action.getInput(), action.getMousePosition(), action.getPointer())) {
                        continue;
                    }
                } else {
                    if (canvas.processMouseRelease((MouseInput) action.getInput(), action.getMousePosition(), action.getPointer())) {
                        continue;
                    }
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

        // Create a copy of the list, as screens may be removed during update.
        for (NUIScreenLayer uiScreen : new LinkedList<>(uiScreens)) {
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
     * Loads a UI screen from the specified asset and returns it.
     * If the screen has not been previously loaded, then it is initialised.
     *
     * If the screen cannot be loaded, then the method may throw a RuntimeException.
     * If the UI element loaded is not a UI screen itself, then the method may throw an IllegalArgumentException.
     * @param uri the screen to load
     * @return the loaded screen
     */
    public NUIScreenLayer createScreen(String uri) {
        boolean alreadyLoaded = Assets.isLoaded(uri, UIElement.class);
        UIWidget rootWidget = Assets.getUIElement(uri).getRootWidget();
        if (rootWidget instanceof NUIScreenLayer) {
            NUIScreenLayer screen = (NUIScreenLayer) rootWidget;
            if (!alreadyLoaded) {
                screen.initialise();
            }
            return screen;
        } else {
            throw new IllegalArgumentException("Asset " + uri + " is not a UI screen!");
        }
    }

    /**
     * Pushes a screen onto the UI stack.
     * @param layer the screen to add
     */
    public void pushScreen(NUIScreenLayer layer) {
        uiScreens.push(layer);
        layer.onAdded();
    }

    /**
     * Replaces the entire UI stack with the specified screen.
     *
     * This is generally not desirable behaviour, however, it can be useful for flows
     * where only one screen is shown at a time, such as in the main menu.
     * @param layer the screen to add
     */
    public void setScreen(NUIScreenLayer layer) {
        Iterator<NUIScreenLayer> screenIterator = uiScreens.descendingIterator();
        while (screenIterator.hasNext()) {
            NUIScreenLayer uiScreen = screenIterator.next();
            screenIterator.remove();
            uiScreen.onRemoved();
        }

        pushScreen(layer);
    }

    /**
     * Removes the topmost screen from the UI stack and returns it.
     * @return the topmost screen
     */
    public NUIScreenLayer popScreen() {
        NUIScreenLayer uiScreen = uiScreens.pop();
        uiScreen.onRemoved();
        return uiScreen;
    }

    /**
     * Removes a screen form the UI stack. It is no longer updated or drawn.
     * @param screen the screen to remove
     */
    public void removeScreen(NUIScreenLayer screen) {
        uiScreens.remove(screen);
        screen.onRemoved();
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
     * Removes all of the UI screens currently on UI stack.
     */
    public void clearScreens() {
        Iterator<NUIScreenLayer> screenIterator = uiScreens.descendingIterator();
        while (screenIterator.hasNext()) {
            NUIScreenLayer uiScreen = screenIterator.next();
            screenIterator.remove();
            uiScreen.onRemoved();
        }
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

    /**
     * Returns if the mouse is currently over an interactive UI element.
     * @return true, if the mouse is currently over an interactive UI element, otherwise false
     */
    public boolean isMouseOnUi() {
        Vector2i mousePosition = mouse.getPosition();
        for (Rectanglei interactionRegion : canvas.getInteractionRegions()) {
            if (interactionRegion.containsPoint(mousePosition)) {
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
        float actualScale = scale * baseUIScale;
        canvas.setUiScale(actualScale);
        canvasRenderer.setUiScale(1.0f / actualScale);
    }

    /**
     * Returns the current UI scale. This can be useful when converting from UI co-ordinates to screen co-ordinates.
     * @return the current UI scale
     */
    public float getUiScale() {
        return canvas.getUiScale();
    }

    /**
     * Returns a wrapper that allows safe usage of {@link UiDrawer} code with widget draw calls.
     * @return a {@link UiDrawer} wrapper for accessing legacy UI screens
     */
    public LegacyUiDrawerWrapper getLegacyUiDrawer() {
        return new LegacyUiDrawerWrapper(uiDrawer, canvasRenderer);
    }

    /**
     * This class acts as a wrapper to safely allow {@link UiDrawer} code to be called in {@link UIWidget#onDraw(Canvas)} methods.
     * The wrapper must be closed after use and no NUI canvas methods can be used before closing the wrapper.
     * Example usage:
     * <code>
     * try (NUIManager.LegacyUiDrawerWrapper wrapper = nuiManager.getLegacyUiDrawer()) {
     *     legacyUiScreen.draw(wrapper.getUiDrawer());
     * }
     * </code>
     */
    public static class LegacyUiDrawerWrapper implements Closeable {
        private final UiDrawer uiDrawer;
        private final CanvasRenderer canvasRenderer;

        public LegacyUiDrawerWrapper(UiDrawer uiDrawer, CanvasRenderer canvasRenderer) {
            this.uiDrawer = uiDrawer;
            this.canvasRenderer = canvasRenderer;
            canvasRenderer.postRender();
        }

        public UiDrawer getUiDrawer() {
            return uiDrawer;
        }

        @Override
        public void close() {
            canvasRenderer.preRender();
        }
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
