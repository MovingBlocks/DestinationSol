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

package org.destinationsol.game.tutorial.steps;

import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.tutorial.TutorialStep;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.HorizontalAlign;

import javax.inject.Inject;

/**
 * A tutorial step that completes when the user has navigated to the {@link org.destinationsol.game.screens.HireShipsScreen}
 * and hired a mercenary. The player is gifted some money to allow them to afford this.
 */
public class BuyMercenaryStep extends TutorialStep {
    @Inject
    protected SolGame game;
    private final int giftMoney;
    private final String hireMessage;
    private final String hireMercenaryMessage;
    private boolean hireButtonPressed = false;
    private boolean hireMercenaryButtonPressed = false;
    private UIWarnButton hireButton;
    private UIWarnButton hireMercenaryButton;

    @Inject
    protected BuyMercenaryStep() {
        throw new RuntimeException("Attempted to instantiate TutorialStep via DI. This is not supported.");
    }

    public BuyMercenaryStep(int giftMoney, String hireMessage, String hireMercenaryMessage) {
        this.giftMoney = giftMoney;
        this.hireMessage = hireMessage;
        this.hireMercenaryMessage = hireMercenaryMessage;
    }

    public void start() {
        Hero hero = game.getHero();
        hero.setMoney(hero.getMoney() + giftMoney);
        setTutorialBoxPosition(HorizontalAlign.LEFT);
        setTutorialText(hireMessage);
        hireButton = game.getScreens().talkScreen.getHireButton();
        hireButton.subscribe(button -> {
            hireButtonPressed = true;
        });
        hireMercenaryButton = game.getScreens().inventoryScreen.getHireShipsScreen().getHireControl();
        hireMercenaryButton.subscribe(button -> {
            hireMercenaryButtonPressed = true;
        });
    }
    public boolean checkComplete(float timeStep) {
        Hero hero = game.getHero();
        if (hero.getMoney() < giftMoney) {
            // Make sure that the player always has enough money to hire a mercenary.
            hero.setMoney(giftMoney);
        }

        if (!hireButtonPressed) {
            hireButton.enableWarn();
        } else {
            setTutorialText(hireMercenaryMessage);
            hireMercenaryButton.enableWarn();
        }

        if (hireButtonPressed && hireMercenaryButtonPressed) {
            game.getSolApplication().getNuiManager().popScreen();
            return true;
        }
        return false;
    }
}
