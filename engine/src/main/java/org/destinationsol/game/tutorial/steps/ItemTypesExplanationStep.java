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

import java.util.Map;

public class ItemTypesExplanationStep extends MessageStep {
    private final Map<Class<? extends SolItem>, String> itemExplanations;
    private final Class<? extends SolItem>[] itemTypes;
    private int itemTypeNo;

    public ItemTypesExplanationStep(TutorialScreen tutorialScreen, SolGame game,
                                    Map<Class<? extends SolItem>, String> itemExplanations,
                                    Class<? extends SolItem>[] itemTypes) {
        super(tutorialScreen, game, "");
        this.itemExplanations = itemExplanations;
        this.itemTypes = itemTypes;
        this.itemTypeNo = 0;
    }

    @Override
    public void start() {
        super.start();
        itemTypeNo = 0;
        tutorialScreen.setTutorialText(itemExplanations.get(itemTypes[itemTypeNo]));
    }

    @Override
    public boolean checkComplete(float timeStep) {
        game.getScreens().inventoryScreen.getItemUIControlsForTutorialByType(itemTypes[itemTypeNo]).get(0).enableWarn();
        boolean complete = super.checkComplete(timeStep);
        if (complete) {
            itemTypeNo++;
            if (itemTypeNo >= itemTypes.length) {
                return true;
            }
            stepTimer = 0.0f;
            interactComplete = false;
            tutorialScreen.setTutorialText(itemExplanations.get(itemTypes[itemTypeNo]));
        }
        return false;
    }
}
