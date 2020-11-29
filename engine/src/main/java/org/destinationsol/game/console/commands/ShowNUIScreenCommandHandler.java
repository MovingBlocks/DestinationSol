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
package org.destinationsol.game.console.commands;

import org.destinationsol.assets.Assets;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.annotations.Command;
import org.destinationsol.game.console.annotations.CommandParam;
import org.destinationsol.game.console.annotations.Game;
import org.destinationsol.game.console.annotations.RegisterCommands;
import org.destinationsol.game.console.suggesters.NUIScreenSuggester;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.nui.asset.UIElement;

/**
 * A command used to display a particular NUI screen
 */
@RegisterCommands
public class ShowNUIScreenCommandHandler {
    @Command(shortDescription = "Displays a NUI screen")
    public String showNUIScreen(@Game SolGame game, @CommandParam(value = "screen", suggester = NUIScreenSuggester.class) String screen) {
        NUIManager nuiManager = game.getSolApplication().getNuiManager();
        nuiManager.pushScreen((NUIScreenLayer) Assets.getAssetHelper().get(new ResourceUrn(screen), UIElement.class).get().getRootWidget());
        return "Screen displayed.";
    }
}
