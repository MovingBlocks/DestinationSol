package org.destinationsol.ui.nui.screens;

import org.destinationsol.SolApplication;
import org.destinationsol.game.FactionManager;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.screens.TalkScreen;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.terasology.nui.AbstractWidget;
import org.terasology.nui.widgets.UIButton;

import java.util.List;

public class MainGameScreen extends NUIScreenLayer {
    private SolShip talkTarget;

    @Override
    public void initialise() {
        SolApplication solApplication = nuiManager.getSolApplication();
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();

        UIButton menuButton = find("menuButton", UIButton.class);
        menuButton.subscribe(widget -> {
            solInputManager.setScreen(solApplication, gameScreens.menuScreen);
        });

        UIButton mapButton = find("mapButton", UIButton.class);
        mapButton.subscribe(widget -> {
            solInputManager.setScreen(solApplication, gameScreens.mapScreen);
        });

        UIButton inventoryButton = find("itemsButton", UIButton.class);
        inventoryButton.subscribe(widget -> {
            if (!solInputManager.isScreenOn(gameScreens.inventoryScreen)) {
                gameScreens.inventoryScreen.showInventory.setTarget(solApplication.getGame().getHero().getShip());
                gameScreens.inventoryScreen.setOperations(gameScreens.inventoryScreen.showInventory);
                solInputManager.addScreen(solApplication, gameScreens.inventoryScreen);
            }
        });

        UIButton talkButton = find("talkButton", UIButton.class);
        talkButton.subscribe(widget -> {
            if (!solInputManager.isScreenOn(gameScreens.talkScreen)) {
                gameScreens.talkScreen.setTarget(talkTarget);
                solInputManager.addScreen(solApplication, gameScreens.talkScreen);
            }
        });

        UIButton mercsButton = find("mercsButton", UIButton.class);
        mercsButton.subscribe(widget -> {
            if (!solInputManager.isScreenOn(gameScreens.inventoryScreen)) {
                gameScreens.inventoryScreen.setOperations(gameScreens.inventoryScreen.chooseMercenaryScreen);
                solInputManager.addScreen(solApplication, gameScreens.inventoryScreen);
                solApplication.getGame().getHero().getMercs().markAllAsSeen();
            }
        });
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
        UIButton talkButton = find("talkButton", UIButton.class);
        UIButton mercsButton = find("mercsButton", UIButton.class);
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
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }

    @Override
    public boolean isBlockingInput() {
        return true;
    }
}
