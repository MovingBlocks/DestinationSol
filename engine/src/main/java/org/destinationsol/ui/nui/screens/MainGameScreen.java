package org.destinationsol.ui.nui.screens;

import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.game.FactionManager;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.screens.TalkScreen;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.input.ButtonState;
import org.terasology.input.Keyboard;
import org.terasology.nui.AbstractWidget;
import org.terasology.nui.UIWidget;
import org.terasology.nui.asset.UIElement;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.widgets.UIButton;

import java.util.List;

public class MainGameScreen extends NUIScreenLayer {
    private SolShip talkTarget;
    private UIButton menuButton;
    private UIButton mapButton;
    private UIButton inventoryButton;
    private UIButton talkButton;
    private UIButton mercsButton;
    private ConsoleScreen consoleScreen;

    @Override
    public void initialise() {
        consoleScreen = (ConsoleScreen) Assets.getAssetHelper().get(new ResourceUrn("engine:console"), UIElement.class).get().getRootWidget();

        SolApplication solApplication = nuiManager.getSolApplication();
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        menuButton = find("menuButton", UIButton.class);
        menuButton.subscribe(this::onMenuButtonClicked);

        mapButton = find("mapButton", UIButton.class);
        mapButton.subscribe(this::onMapButtonClicked);

        inventoryButton = find("itemsButton", UIButton.class);
        inventoryButton.subscribe(this::onItemsButtonClicked);

        talkButton = find("talkButton", UIButton.class);
        talkButton.subscribe(this::onTalkButtonClicked);

        mercsButton = find("mercsButton", UIButton.class);
        mercsButton.subscribe(this::onMercsButtonClicked);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        SolApplication solApplication = nuiManager.getSolApplication();
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();
        if (!solInputManager.isScreenOn(gameScreens.menuScreen) &&
                !solInputManager.isScreenOn(gameScreens.mapScreen)) {
            ((AbstractWidget) contents).setVisible(true);
        } else {
            ((AbstractWidget) contents).setVisible(false);
        }

        // NOTE: Copied directly from the original MainGameScreen class. The logic hasn't been changed
        //       but some variables have been re-named to be more descriptive.
        SolGame game = solApplication.getGame();
        Hero hero = game.getHero();

        mercsButton.setEnabled(hero.isNonTranscendent());
        if (hero.isTranscendent()) {
            talkButton.setEnabled(false);
            return;
        }

        FactionManager factionManager = game.getFactionMan();

        talkTarget = null;
        float minDist = TalkScreen.MAX_TALK_DIST;
        float heroApproxRadius = hero.getHull().config.getApproxRadius();
        List<SolObject> objs = game.getObjectManager().getObjects();
        for (SolObject obj : objs) {
            if (!(obj instanceof SolShip)) {
                continue;
            }
            SolShip ship = (SolShip) obj;
            if (factionManager.areEnemies(hero.getShip(), ship)) {
                continue;
            }
            if (ship.getTradeContainer() == null) {
                continue;
            }
            float distance = ship.getPosition().dst(hero.getPosition());
            float shipApproxRadius = ship.getHull().config.getApproxRadius();
            if (minDist < distance - heroApproxRadius - shipApproxRadius) {
                continue;
            }
            talkTarget = ship;
            minDist = distance;
        }
        talkButton.setEnabled(talkTarget != null);

        if (consoleScreen.isConsoleJustClosed()) {
            game.setPaused(false);
        }
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }

    @Override
    public boolean isBlockingInput() {
        return true;
    }

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (event.getState() == ButtonState.UP &&
                (event.getKey() == Keyboard.Key.GRAVE || event.getKey() == Keyboard.Key.F1) &&
                nuiManager.hasScreen(consoleScreen)) {
            nuiManager.removeScreen(consoleScreen);
        }

        if (!contents.isVisible()) {
            return super.onKeyEvent(event);
        }

        if (event.getState() == ButtonState.UP && event.getKey() == Keyboard.Key.ESCAPE) {
            onMenuButtonClicked(menuButton);
            return true;
        }

        if (event.getState() == ButtonState.UP && event.getKey() == Keyboard.Key.TAB) {
            onMapButtonClicked(mapButton);
            return true;
        }

        if (event.getState() == ButtonState.UP && event.getKey() == Keyboard.Key.I) {
            onItemsButtonClicked(inventoryButton);
            return true;
        }

        if (event.getState() == ButtonState.UP && event.getKey() == Keyboard.Key.T) {
            if (talkButton.isEnabled()) {
                onTalkButtonClicked(talkButton);
            }
            return true;
        }

        if (event.getState() == ButtonState.UP && event.getKey() == Keyboard.Key.M) {
            if (mercsButton.isEnabled()) {
                onMercsButtonClicked(mercsButton);
            }
            return true;
        }

        if (event.getState() == ButtonState.UP &&
                (event.getKey() == Keyboard.Key.GRAVE || event.getKey() == Keyboard.Key.F1)) {
            if (!nuiManager.hasScreen(consoleScreen)) {
                nuiManager.pushScreen(consoleScreen);
                nuiManager.getSolApplication().getGame().setPaused(true);
            }
        }

        return super.onKeyEvent(event);
    }

    private void onMenuButtonClicked(UIWidget widget) {
        SolApplication solApplication = nuiManager.getSolApplication();
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        solInputManager.setScreen(solApplication, gameScreens.menuScreen);
    }

    private void onMapButtonClicked(UIWidget widget) {
        SolApplication solApplication = nuiManager.getSolApplication();
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        solInputManager.setScreen(solApplication, gameScreens.mapScreen);
    }

    private void onItemsButtonClicked(UIWidget widget) {
        SolApplication solApplication = nuiManager.getSolApplication();
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        solInputManager.setScreen(solApplication, gameScreens.mainGameScreen);
        if (!solInputManager.isScreenOn(gameScreens.inventoryScreen)) {
            gameScreens.inventoryScreen.showInventory.setTarget(solApplication.getGame().getHero().getShip());
            gameScreens.inventoryScreen.setOperations(gameScreens.inventoryScreen.showInventory);
            solInputManager.addScreen(solApplication, gameScreens.inventoryScreen);
        }
    }

    private void onTalkButtonClicked(UIWidget widget) {
        SolApplication solApplication = nuiManager.getSolApplication();
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        solInputManager.setScreen(solApplication, gameScreens.mainGameScreen);
        if (!solInputManager.isScreenOn(gameScreens.talkScreen)) {
            gameScreens.talkScreen.setTarget(talkTarget);
            solInputManager.addScreen(solApplication, gameScreens.talkScreen);
        }
    }

    private void onMercsButtonClicked(UIWidget widget) {
        SolApplication solApplication = nuiManager.getSolApplication();
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        solInputManager.setScreen(solApplication, gameScreens.mainGameScreen);
        if (!solInputManager.isScreenOn(gameScreens.inventoryScreen)) {
            gameScreens.inventoryScreen.setOperations(gameScreens.inventoryScreen.chooseMercenaryScreen);
            solInputManager.addScreen(solApplication, gameScreens.inventoryScreen);
            solApplication.getGame().getHero().getMercs().markAllAsSeen();
        }
    }
}
