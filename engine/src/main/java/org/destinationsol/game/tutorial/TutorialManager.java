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
import org.destinationsol.game.item.Armor;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.tutorial.steps.ButtonPressStep;
import org.destinationsol.game.tutorial.steps.BuyItemStep;
import org.destinationsol.game.tutorial.steps.BuyMercenaryStep;
import org.destinationsol.game.tutorial.steps.ChangeTutorialSectionStep;
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
import org.destinationsol.game.tutorial.steps.ItemTypesExplanationStep;
import org.destinationsol.game.tutorial.steps.ManageMercenariesGuidanceStep;
import org.destinationsol.game.tutorial.steps.MapDragStep;
import org.destinationsol.game.tutorial.steps.MessageStep;
import org.destinationsol.game.tutorial.steps.OpenScreenStep;
import org.destinationsol.game.tutorial.steps.SelectEquippedWeaponStep;
import org.destinationsol.game.tutorial.steps.SlowVelocityStep;
import org.destinationsol.game.tutorial.steps.ThrustForwardsStep;
import org.destinationsol.game.tutorial.steps.TravelThroughStarPortStep;
import org.destinationsol.game.tutorial.steps.TurnLeftRightStep;
import org.destinationsol.game.tutorial.steps.UseAbilityStep;
import org.destinationsol.game.tutorial.steps.WaitUntilFullyRepairedStep;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.screens.MainGameScreen;
import org.destinationsol.ui.nui.screens.TutorialScreen;
import org.terasology.context.exception.BeanNotFoundException;
import org.terasology.gestalt.di.BeanContext;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for running the in-game tutorial. It delegates the individual tasks to {@link TutorialStep}
 * instances.
 * @see TutorialStep
 */
public class TutorialManager implements UpdateAwareSystem {
    private final NUIManager nuiManager;
    private final TutorialScreen tutorialScreen;
    private final SolApplication solApplication;
    private final Provider<SolGame> solGame;
    private final BeanContext beanContext;
    private List<TutorialStep> steps;
    private int stepNo;
    private String currentTutorialHeading;

    @Inject
    public TutorialManager(NUIManager nuiManager, SolApplication solApplication, Provider<SolGame> game, BeanContext beanContext) {
        this.nuiManager = nuiManager;
        this.tutorialScreen = (TutorialScreen) nuiManager.createScreen("engine:tutorialScreen");
        this.solApplication = solApplication;
        this.solGame = game;
        this.beanContext = beanContext;
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
        boolean usesKeyboard = !isMobile && (controlType != GameOptions.ControlType.MOUSE);

        // A lot of input names will vary by control type. In some control types, the inputs are also hard-coded.
        // Rather than a lot of messy conditional statements in-line with the step definitions, we'll set things up here.
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

        Map<Class<? extends SolItem>, String> itemTypesExplanations = new HashMap<>();
        itemTypesExplanations.put(Engine.class, "Engines allow your ship to move.");
        itemTypesExplanations.put(Gun.class, "You can mine asteroids and attack enemies with guns.");
        itemTypesExplanations.put(Armor.class, "Armour makes attacks less effective against you.");
        itemTypesExplanations.put(Shield.class, "Shields absorb energy-based projectiles until depleted.");

        steps = new ArrayList<>(Arrays.asList(
                new ChangeTutorialSectionStep("Movement"),
                new TurnLeftRightStep(isMobile ? "Turn left and right." : "Turn left and right (" + turnControlHint + ")."),
                new ThrustForwardsStep(isMobile ? "Thrust forwards." : "Thrust forwards (" + thrustForwardControlHint + ")."),
                new SlowVelocityStep(0.1f, "Turn around and thrust again to slow down.\n\nTry slowing to a stop."),
                new FlyToRandomWaypointAroundHeroStep(1.0f, 2.5f, "Fly to the waypoint."),
                new ChangeTutorialSectionStep("Weapons"),
                new FireGunStep(isMobile ? "Fire your gun." : "Fire your gun (" + shootControlHint + ")."),
                new CheckGunReloadStep(false, true, "Firing weapons drains your ammunition. Keep on firing."),
                new CheckGunReloadStep(false, false,
                        "Your weapon reloads when depleted.\n" +
                                "You can't fire when reloading."),
                new UseAbilityStep(isMobile ? "Use your ability." : "Use your ability (" + abilityControlHint + ")."),
                new MessageStep("Abilities consume ability charges."),
                new ChangeTutorialSectionStep("Money"),
                new DestroySpawnedAsteroidAroundHeroStep(1.0f, 2.5f, "Fire at the asteroid."),
                new MessageStep("Asteroids drop money. You can fly into it to collect it."),
                new ChangeTutorialSectionStep("Items"),
                new OpenScreenStep(
                        solGame.get().getScreens().mainGameScreen.getInventoryButton(),
                        solGame.get().getScreens().inventoryScreen,
                        usesKeyboard ?
                        "Open your inventory (" + gameOptions.getKeyInventoryName() + ")." :
                        "Open your inventory."),
                new ItemTypesExplanationStep(itemTypesExplanations, new Class[] {
                        Engine.class,
                        Gun.class,
                        Armor.class,
                        Shield.class
                }),
                new SelectEquippedWeaponStep(
                        usesKeyboard ?
                                "Select an equipped item (Move with " + gameOptions.getKeyUpName() + " and " + gameOptions.getKeyDownName() + ")" :
                                "Select an equipped item."),
                new CheckItemEquippedStep(false,
                        usesKeyboard ? "Un-equip an item (" + gameOptions.getKeyEquipName() + ")." : "Un-equip an item."),
                new CheckItemEquippedStep(true,
                        usesKeyboard ? "Re-equip that item (" + gameOptions.getKeyEquipName() + ")." : "Un-equip an item."),
                new CloseScreenStep(
                        solGame.get().getScreens().inventoryScreen.getCloseButton(),
                        solGame.get().getScreens().inventoryScreen,
                        isMobile ? "Close your inventory (tap outside of the inventory)." : "Close your inventory."),
                new ChangeTutorialSectionStep("Weapon Mounts"),
                new MessageStep("All ships may come with up to two weapon mounts."),
                new MessageStep("Weapon mounts are either fixed or rotating."),
                new MessageStep("You can only equip weapons on matching mounts."),
                new ChangeTutorialSectionStep("Shops"),
                new FlyToNearestStationStep("Fly to the station."),
                new OpenScreenStep(
                        solGame.get().getScreens().mainGameScreen.getTalkButton(),
                        solGame.get().getScreens().talkScreen,
                        isMobile ? "Talk to the station." : "Talk to the station (" + gameOptions.getKeyTalkName() + ")."),
                new BuyItemStep(usesKeyboard ? "Select Buy (" + gameOptions.getKeyBuyMenuName() + ")." : "Select Buy.",
                        isMobile ? "Buy an item." : "Buy an item (" + gameOptions.getKeyBuyItemName() + ")."),
                new ChangeTutorialSectionStep("Combat"),
                new MessageStep("Shoot at ships to destroy them.\n"),
                new DestroySpawnedShipsStep(1, "core:minerSmall",
                        "core:fixedBlaster", "Destroy the targeted ship.",
                        "Enemy ships can be tough.\nOpen the pause menu and select Respawn."),
                new MessageStep("Destroyed ships drop valuable loot."),
                new ChangeTutorialSectionStep("Repair Kits"),
                new WaitUntilFullyRepairedStep("Stay still and wait until the repair kits have repaired your hull fully."),
                new ChangeTutorialSectionStep("Map"),
                new OpenScreenStep(
                        solGame.get().getScreens().mainGameScreen.getMapButton(),
                        solGame.get().getScreens().mapScreen,
                        isMobile ? "Open the map." : "Open the map (" + gameOptions.getKeyMapName() + ")."),
                new ButtonPressStep(solGame.get().getScreens().mapScreen.getZoomInButton(), "Zoom In"),
                new ButtonPressStep(solGame.get().getScreens().mapScreen.getZoomOutButton(), "Zoom Out"),
                new MapDragStep("You can drag the map to move around."),
                new CreateWaypointStep("Create a waypoint near your ship.",
                        "That's too far away.\n\n" +
                        "Remove it and place one closer to your ship."),
                new CloseScreenStep(
                        solGame.get().getScreens().mapScreen.getCloseButton(),
                        solGame.get().getScreens().mapScreen,
                        "Close the map."),
                new FlyToHeroFirstWaypointStep("Fly to your waypoint.", "Create a waypoint near your ship."),
                new ChangeTutorialSectionStep("Planets"),
                new FlyToPlanetSellingMercenariesStep("Head towards a planet.", "Look for the planetary station."),
                new ChangeTutorialSectionStep("Mercenaries"),
                new MessageStep("When flying around planets, you'll be affected by gravity."),
                new OpenScreenStep(
                        solGame.get().getScreens().mainGameScreen.getTalkButton(),
                        solGame.get().getScreens().talkScreen,
                        "Talk to the station."),
                new BuyMercenaryStep(1000,
                        usesKeyboard ? "Select Hire (" + gameOptions.getKeyHireShipMenuName() + ")." : "Select Hire.",
                        "Try hiring a mercenary."),
                new MessageStep("Mercenaries will fight for you. They keep any money they collect as part of their payment."),
                new OpenScreenStep(
                        solGame.get().getScreens().mainGameScreen.getMercsButton(),
                        solGame.get().getScreens().inventoryScreen,
                        isMobile ? "Open the mercenaries menu." : "Open the mercenaries menu (" + gameOptions.getKeyMercenaryInterationName() + ")."),
                new ManageMercenariesGuidanceStep(
                        "Here you can manage your mercenaries. When you're done here, close the menu.",
                        "Here you can give items to your mercenary.",
                        "Here you can take items back from your mercenary.",
                        "Here you can manage your mercenary's equipment."),
                new ChangeTutorialSectionStep("Star Lanes"),
                new FlyToNearestStarPortStep("Fly to the marked star lane."),
                new MessageStep("For a small fee, star lanes allow you to travel quickly between planets."),
                new TravelThroughStarPortStep("Fly into the centre to travel across the star lane."),
                new ChangeTutorialSectionStep("Finish"),
                new MessageStep("That's it! The tutorial is finished. You will be returned to the main menu.")
        ));

        for (TutorialStep step : steps) {
            try {
                beanContext.inject(step);
            } catch (BeanNotFoundException ignore) {
            }
        }

        stepNo = 0;
        TutorialStep firstStep = steps.get(stepNo);
        firstStep.start();
        tutorialScreen.setTutorialText(firstStep.getTutorialText(), firstStep.getTutorialBoxPosition());
    }

    /**
     * Checks if a step instance is already present in the tutorial.
     * @param step the step to check for
     * @return true, if the step exists, otherwise false
     */
    public boolean hasStep(TutorialStep step) {
        return steps.contains(step);
    }

    /**
     * Appends a step to the end of the tutorial list.
     * @param step the step to add
     */
    public void addStep(TutorialStep step) {
        steps.add(step);
    }

    @Override
    public void update(SolGame game, float timeStep) {
        // Move the tutorial overlay back to the front of the UI stack, if needed.
        if (nuiManager.getTopScreen() != tutorialScreen) {
            if (nuiManager.hasScreen(tutorialScreen)) {
                tutorialScreen.moveToTop();
            } else {
                nuiManager.pushScreen(tutorialScreen);
            }
        }

        TutorialStep currentStep = steps.get(stepNo);
        setUpTutorialBox(currentStep);
        if (currentStep.checkComplete(timeStep)) {
            stepNo++;
            tutorialScreen.clearAllTutorialBoxes();
            if (stepNo < steps.size()) {
                TutorialStep newStep = steps.get(stepNo);
                newStep.start();
                setUpTutorialBox(newStep);
            } else {
                solApplication.finishGame();
            }
        }
    }

    private void setUpTutorialBox(TutorialStep tutorialStep) {
        if (tutorialStep.getTutorialHeading() != null) {
            currentTutorialHeading = tutorialStep.getTutorialHeading();
        }

        tutorialScreen.setTutorialText(tutorialStep.getTutorialText(), tutorialStep.getTutorialBoxPosition());
        tutorialScreen.setTutorialHeading(currentTutorialHeading, tutorialStep.getTutorialBoxPosition());
        if (tutorialStep.getRequiredInput() != null) {
            tutorialScreen.setInteractHintInput(tutorialStep.getTutorialBoxPosition(), tutorialStep.getRequiredInput());
            tutorialScreen.setInteractEvent(tutorialStep.getTutorialBoxPosition(), tutorialStep.getInputHandler());
        }
    }

    /**
     * This method should be called when either the game or the tutorial ends. It cleans-up the UI changes made for the tutorial.
     */
    public void onGameEnd() {
        MainGameScreen mainGameScreen = solGame.get().getMainGameScreen();
        mainGameScreen.getTalkButton().setVisible(true);
        mainGameScreen.getMapButton().setVisible(true);
        mainGameScreen.getInventoryButton().setVisible(true);
        mainGameScreen.getMercsButton().setVisible(true);
    }
}
