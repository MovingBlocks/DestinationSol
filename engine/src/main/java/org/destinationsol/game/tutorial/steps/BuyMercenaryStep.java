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
import org.destinationsol.ui.nui.screens.TutorialScreen;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.nui.HorizontalAlign;

public class BuyMercenaryStep extends TutorialStep {
    private final TutorialScreen tutorialScreen;
    private final SolGame game;
    private final int giftMoney;
    private final String message;
    private boolean hireButtonPressed = false;
    private boolean hireMercenaryButtonPressed = false;
    private UIWarnButton hireButton;
    private UIWarnButton hireMercenaryButton;

    public BuyMercenaryStep(TutorialScreen tutorialScreen, SolGame game, int giftMoney, String message) {
        this.tutorialScreen = tutorialScreen;
        this.game = game;
        this.giftMoney = giftMoney;
        this.message = message;
    }

    public void start() {
        Hero hero = game.getHero();
        hero.setMoney(hero.getMoney() + giftMoney);
        tutorialScreen.setTutorialText(message, HorizontalAlign.LEFT);
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
            hireMercenaryButton.enableWarn();
        }

        if (hireButtonPressed && hireMercenaryButtonPressed) {
            game.getSolApplication().getNuiManager().popScreen();
            return true;
        }
        return false;
    }
}
