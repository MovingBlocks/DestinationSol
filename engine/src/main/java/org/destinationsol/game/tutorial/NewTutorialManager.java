/*
 * Copyright 2023 The Terasology Foundation
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

package org.destinationsol.game.tutorial;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.UpdateAwareSystem;
import org.destinationsol.game.tutorial.steps.ButtonPressStep;
import org.destinationsol.game.tutorial.steps.BuyItemStep;
import org.destinationsol.game.tutorial.steps.BuyMercenaryStep;
import org.destinationsol.game.tutorial.steps.CheckGunReloadStep;
import org.destinationsol.game.tutorial.steps.CheckItemEquippedStep;
import org.destinationsol.game.tutorial.steps.CloseScreenStep;
import org.destinationsol.game.tutorial.steps.CreateWaypointStep;
import org.destinationsol.game.tutorial.steps.DestroySpawnedAsteroidAroundHeroStep;
import org.destinationsol.game.tutorial.steps.DestroySpawnedShipsStep;
import org.destinationsol.game.tutorial.steps.FireGunStep;
import org.destinationsol.game.tutorial.steps.FlyToHeroFirstWaypointStep;
import org.destinationsol.game.tutorial.steps.FlyToNearestStarPortStep;
import org.destinationsol.game.tutorial.steps.FlyToNearestStationStep;
import org.destinationsol.game.tutorial.steps.FlyToPlanetSellingMercenariesStep;
import org.destinationsol.game.tutorial.steps.FlyToRandomWaypointAroundHeroStep;
import org.destinationsol.game.tutorial.steps.ManageMercenariesGuidanceStep;
import org.destinationsol.game.tutorial.steps.MapDragStep;
import org.destinationsol.game.tutorial.steps.MessageStep;
import org.destinationsol.game.tutorial.steps.OpenScreenStep;
import org.destinationsol.game.tutorial.steps.SelectEquippedWeaponStep;
import org.destinationsol.game.tutorial.steps.SlowVelocityStep;
import org.destinationsol.game.tutorial.steps.ThrustForwardsStep;
import org.destinationsol.game.tutorial.steps.TurnLeftRightStep;
import org.destinationsol.game.tutorial.steps.UseAbilityStep;
import org.destinationsol.game.tutorial.steps.WaitUntilFullyRepairedStep;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.screens.MainGameScreen;
import org.destinationsol.ui.nui.screens.TutorialScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

public class NewTutorialManager implements UpdateAwareSystem {
    private static final Logger logger = LoggerFactory.getLogger(NewTutorialManager.class);
    private final NUIManager nuiManager;
    private final TutorialScreen tutorialScreen;
    private final SolApplication solApplication;
    private final Provider<SolGame> solGame;
    private TutorialStep[] steps;
    private int stepNo;

    @Inject
    public NewTutorialManager(NUIManager nuiManager, SolApplication solApplication, Provider<SolGame> game) {
        this.nuiManager = nuiManager;
        this.tutorialScreen = (TutorialScreen) nuiManager.createScreen("engine:tutorialScreen");
        this.solApplication = solApplication;
        this.solGame = game;
    }

    public void start() {
        MainGameScreen mainGameScreen = solGame.get().getMainGameScreen();
        mainGameScreen.getTalkButton().setVisible(false);
        mainGameScreen.getMapButton().setVisible(false);
        mainGameScreen.getInventoryButton().setVisible(false);
        mainGameScreen.getMercsButton().setVisible(false);

        tutorialScreen.clearAllTutorialBoxes();

        GameOptions gameOptions = solApplication.getOptions();
        GameOptions.ControlType controlType = gameOptions.controlType;
        boolean isMobile = solApplication.isMobile();
        boolean usesKeyboard = !isMobile &&
                (controlType != GameOptions.ControlType.MOUSE);

        String turnControlHint = "";
        String thrustForwardControlHint = "";
        String shootControlHint = "";
        String abilityControlHint = "";
        switch (controlType) {
            case KEYBOARD:
                turnControlHint = gameOptions.getKeyLeftName() + " and " + gameOptions.getKeyRightName();
                thrustForwardControlHint = gameOptions.getKeyUpName();
                shootControlHint = gameOptions.getKeyShootName();
                abilityControlHint = gameOptions.getKeyAbilityName();
                break;
            case MIXED:
            case MOUSE:
                turnControlHint = "Move Mouse";
                thrustForwardControlHint = gameOptions.getKeyUpMouseName();
                shootControlHint = "Left Mouse Button";
                abilityControlHint = "Middle Mouse Button";
                break;
            case CONTROLLER:
                if (gameOptions.getControllerAxisLeftRight() > 0) {
                    turnControlHint = "Axis " + gameOptions.getControllerAxisLeftRight();
                } else {
                    turnControlHint = "Button " + gameOptions.getControllerButtonLeft() + " and Button " + gameOptions.getControllerButtonRight();
                }
                if (gameOptions.getControllerAxisUpDown() > 0) {
                    thrustForwardControlHint = "Axis " + gameOptions.getControllerAxisUpDown();
                } else {
                    thrustForwardControlHint = "Button " + gameOptions.getControllerButtonUp();
                }
                if (gameOptions.getControllerAxisShoot() > 0) {
                    shootControlHint = "Axis " + gameOptions.getControllerAxisShoot();
                } else {
                    shootControlHint = "Button " + gameOptions.getControllerButtonShoot();
                }
                if (gameOptions.getControllerAxisAbility() > 0) {
                    abilityControlHint = "Axis " + gameOptions.getControllerAxisAbility();
                } else {
                    abilityControlHint = "Button " + gameOptions.getControllerButtonAbility();
                }
        }

        steps = new TutorialStep[] {
                new MessageStep(tutorialScreen, solGame.get(), "Section 1 - Movement"),
                new TurnLeftRightStep(tutorialScreen, solGame.get(), isMobile ? "Turn left and right." : "Turn left and right (" + turnControlHint + ")."),
                new ThrustForwardsStep(tutorialScreen, solGame.get(), isMobile ? "Thrust forwards." : "Thrust forwards (" + thrustForwardControlHint + ")."),
                new SlowVelocityStep(tutorialScreen, solGame.get(), 0.05f, "Turn around and slow to a stop."),
                new FlyToRandomWaypointAroundHeroStep(tutorialScreen, solGame.get(), 1.0f, 4.0f, "Fly to the waypoint."),
                new MessageStep(tutorialScreen, solGame.get(), "Section 2 - Weapons"),
                new FireGunStep(tutorialScreen, solGame.get(), isMobile ? "Fire your gun." : "Fire your gun (" + shootControlHint + ")."),
                new CheckGunReloadStep(tutorialScreen, solGame.get(), false, true, "Firing weapons drains your ammunition. Keep on firing."),
                new CheckGunReloadStep(tutorialScreen, solGame.get(), false, false,
                        "When your weapon is depleted, it automatically reloads.\n" +
                                "You can't fire when your weapon is reloading."),
                new UseAbilityStep(tutorialScreen, solGame.get(), isMobile ?
                        "Use your ability." :
                        "Use your ability (" + abilityControlHint + ")."),
                new MessageStep(tutorialScreen, solGame.get(), "Abilities consume ability charges."),
                new MessageStep(tutorialScreen, solGame.get(), "Section 3 - Money"),
                new DestroySpawnedAsteroidAroundHeroStep(tutorialScreen, solGame.get(), 1.0f, 4.0f, "Fire at the asteroid."),
                new MessageStep(tutorialScreen, solGame.get(), "Asteroids drop loot - money in this case."),
                new MessageStep(tutorialScreen, solGame.get(), "Section 4 - Items"),
                new OpenScreenStep(tutorialScreen, nuiManager,
                        solGame.get().getScreens().mainGameScreen.getInventoryButton(),
                        solGame.get().getScreens().inventoryScreen,
                        usesKeyboard ?
                        "Open your inventory (" + gameOptions.getKeyInventoryName() + ")." :
                        "Open your inventory."),
                new SelectEquippedWeaponStep(tutorialScreen,
                        solGame.get().getScreens().inventoryScreen,
                        usesKeyboard ?
                                "Select an equipped item (Move with " + gameOptions.getKeyUpName() + " and " + gameOptions.getKeyDownName() + ")" :
                                "Select an equipped item."),
                new CheckItemEquippedStep(tutorialScreen,
                        solGame.get().getScreens().inventoryScreen,
                        false,
                        usesKeyboard ? "Un-equip an item (" + gameOptions.getKeyEquipName() + ")." : "Un-equip an item."),
                new CheckItemEquippedStep(tutorialScreen,
                        solGame.get().getScreens().inventoryScreen,
                        true,
                        usesKeyboard ? "Re-equip that item (" + gameOptions.getKeyEquipName() + ")." : "Un-equip an item."),
                new MessageStep(tutorialScreen, solGame.get(), "Section 5 - Weapon Mounts"),
                new MessageStep(tutorialScreen, solGame.get(), "All ships may come with with up to two weapon mounts."),
                new MessageStep(tutorialScreen, solGame.get(), "Weapon mounts vary by their socket type."),
                new MessageStep(tutorialScreen, solGame.get(), "Weapon sockets are either fixed or rotating."),
                new MessageStep(tutorialScreen, solGame.get(), "A weapon can only be equipped if its socket type matches its mount."),
                new MessageStep(tutorialScreen, solGame.get(), "Section 6 - Shops"),
                new FlyToNearestStationStep(tutorialScreen, solGame.get(), "Fly to the station."),
                new OpenScreenStep(tutorialScreen, nuiManager,
                        solGame.get().getScreens().mainGameScreen.getTalkButton(),
                        solGame.get().getScreens().talkScreen,
                        isMobile ? "Talk to the station." : "Talk to the station (" + gameOptions.getKeyTalkName() + ")."),
                new BuyItemStep(tutorialScreen,
                        solGame.get().getScreens().talkScreen.getBuyButton(),
                        solGame.get().getScreens().inventoryScreen.getBuyItemsScreen().getBuyControl(),
                        isMobile ? "Buy an item." : "Buy an item (" + gameOptions.getKeyBuyItemName() + ")."),
                new MessageStep(tutorialScreen, solGame.get(), "Section 7 - Combat"),
                new DestroySpawnedShipsStep(tutorialScreen, solGame.get(), 1, "core:minerSmall",
                        "core:fixedBlaster", "Destroy all targets.",
                        "Enemy ships can be tough.\nOpen the pause menu and select Respawn."),
                new MessageStep(tutorialScreen, solGame.get(), "Destroyed ships drop valuable loot."),
                new MessageStep(tutorialScreen, solGame.get(), "Section 8 - Repair Kits"),
                new WaitUntilFullyRepairedStep(tutorialScreen, solGame.get(),
                        "Stay still and wait until the repair kits have repaired your hull fully."),
                new MessageStep(tutorialScreen, solGame.get(), "Section 9 - Map"),
                new OpenScreenStep(tutorialScreen, nuiManager,
                        solGame.get().getScreens().mainGameScreen.getMapButton(),
                        solGame.get().getScreens().mapScreen,
                        isMobile ? "Open the map." : "Open the map (" + gameOptions.getKeyMapName() + ")."),
                new ButtonPressStep(tutorialScreen, solGame.get().getScreens().mapScreen.getZoomInButton(), "Zoom In"),
                new ButtonPressStep(tutorialScreen, solGame.get().getScreens().mapScreen.getZoomOutButton(), "Zoom Out"),
                new MapDragStep(tutorialScreen, solGame.get(), solGame.get().getMapDrawer(), "You can move around the map by clicking/tapping and dragging."),
                new CreateWaypointStep(tutorialScreen, solGame.get(),
                        solGame.get().getScreens().mapScreen.getAddWaypointButton(),
                        "Create a waypoint."),
                new CloseScreenStep(tutorialScreen,
                        nuiManager,
                        solGame.get().getScreens().mapScreen.getCloseButton(),
                        solGame.get().getScreens().mapScreen,
                        "Close the map."),
                new FlyToHeroFirstWaypointStep(tutorialScreen, solGame.get(), "Fly to your waypoint."),
                new MessageStep(tutorialScreen, solGame.get(), "Section 10 - Hiring Mercenaries"),
                new FlyToPlanetSellingMercenariesStep(tutorialScreen, solGame.get(), "Fly to a planetary station providing mercenaries."),
                new MessageStep(tutorialScreen, solGame.get(), "When flying around planets, you'll be affected by gravity."),
                new OpenScreenStep(tutorialScreen, nuiManager,
                        solGame.get().getScreens().mainGameScreen.getTalkButton(),
                        solGame.get().getScreens().talkScreen,
                        "Talk to the station."),
                new BuyMercenaryStep(tutorialScreen, solGame.get(), 1000, "Try hiring a mercenary."),
                new CloseScreenStep(tutorialScreen, nuiManager,
                        solGame.get().getScreens().inventoryScreen.getCloseButton(),
                        solGame.get().getScreens().inventoryScreen,
                        "Close the buy screen."),
                new MessageStep(tutorialScreen, solGame.get(), "Let's see how your mercenary fights."),
                new DestroySpawnedShipsStep(tutorialScreen, solGame.get(), 1, "core:pirateSmall",
                        "core:blaster core:smallShield", "Destroy all targets.",
                        "Enemy ships can be tough.\nOpen the pause menu and select Respawn."),
                new MessageStep(tutorialScreen, solGame.get(), "Mercenaries will keep any money they collect as part of their payment."),
                new MessageStep(tutorialScreen, solGame.get(), "Section 11 - Managing Mercenaries"),
                new OpenScreenStep(tutorialScreen, nuiManager,
                        solGame.get().getScreens().mainGameScreen.getMercsButton(),
                        solGame.get().getScreens().inventoryScreen,
                        isMobile ? "Open the mercenaries menu." : "Open the mercenaries menu (" + gameOptions.getKeyMercenaryInterationName() + ")."),
                new ManageMercenariesGuidanceStep(tutorialScreen, nuiManager,
                        solGame.get().getScreens().inventoryScreen,
                        "Here you can manage your mercenaries. When you're done here, close the menu.",
                        "Here you can give items to your mercenary.",
                        "Here you can take items back from your mercenary.",
                        "Here you can manage your mercenary's equipment."),
                new FlyToNearestStarPortStep(tutorialScreen, solGame.get(), "Fly to the marked star lane."),
                new MessageStep(tutorialScreen, solGame.get(),
                        "Star lanes allow you to travel quickly between planets.\nThey cost money to use.")
        };
        
        stepNo = 0;
        steps[stepNo].start();
    }

    @Override
    public void update(SolGame game, float timeStep) {
        if (nuiManager.getTopScreen() != tutorialScreen) {
            if (nuiManager.hasScreen(tutorialScreen)) {
                tutorialScreen.moveToTop();
            } else {
                nuiManager.pushScreen(tutorialScreen);
            }
        }

        if (stepNo >= steps.length) {
            tutorialScreen.setTutorialText("The tutorial is finished. Shoot to return to the main menu.");
            if (game.getHero().getShip().getPilot().isShoot()) {
                solApplication.finishGame();
            }
            return;
        }

        if (steps[stepNo].checkComplete(timeStep)) {
            stepNo++;
            tutorialScreen.clearAllTutorialBoxes();
            if (stepNo < steps.length) {
                steps[stepNo].start();
            }
        }
    }
}
