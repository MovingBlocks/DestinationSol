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

import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.Armor;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.ui.nui.screens.TutorialScreen;

public class ItemTypesExplanationStep extends MessageStep {
    private enum ExplanationType {
        // TODO: Pass these in as constructor parameters instead.
        WEAPONS(Gun.class, "You can mine asteroids and attack enemies with guns."),
        ARMOUR(Armor.class, "Armour makes attacks less effective against you."),
        SHIELDS(Shield.class, "Shields absorb energy-based projectiles until depleted."),
        LAST(null, "");

        private final Class<? extends SolItem> itemClass;
        private final String explanation;

        ExplanationType(Class<? extends SolItem> itemClass, String explanation) {
            this.itemClass = itemClass;
            this.explanation = explanation;
        }

        public Class<? extends SolItem> getItemClass() {
            return itemClass;
        }

        public String getExplanation() {
            return explanation;
        }

        public ExplanationType next() {
            return ExplanationType.values()[this.ordinal()+1];
        }
    };
    private ExplanationType explanationType;

    public ItemTypesExplanationStep(TutorialScreen tutorialScreen, SolGame game) {
        super(tutorialScreen, game, "");
    }

    @Override
    public void start() {
        super.start();
        explanationType = ExplanationType.WEAPONS;
        tutorialScreen.setTutorialText(explanationType.getExplanation());
    }

    @Override
    public boolean checkComplete(float timeStep) {
        game.getScreens().inventoryScreen.getItemUIControlsForTutorialByType(explanationType.getItemClass()).get(0).enableWarn();
        boolean complete = super.checkComplete(timeStep);
        if (complete) {
            explanationType = explanationType.next();
            if (explanationType == ExplanationType.LAST) {
                return true;
            }
            stepTimer = 0.0f;
            interactComplete = false;
            tutorialScreen.setTutorialText(explanationType.getExplanation());
        }
        return false;
    }
}
