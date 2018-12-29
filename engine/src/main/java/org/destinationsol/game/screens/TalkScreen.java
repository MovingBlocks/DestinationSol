/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol.game.screens;

import org.destinationsol.SolApplication;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolUiBaseScreen;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.responsiveUi.UiActionButton;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;
import org.destinationsol.ui.responsiveUi.UiTextBox;
import org.destinationsol.ui.responsiveUi.UiVerticalListLayout;

//TODO quickFix to at least display some buttons. Does not actually work yet.
public class TalkScreen extends SolUiBaseScreen {
    @Override
    public void onAdd() {
        final UiVerticalListLayout verticalListLayout = new UiVerticalListLayout()
                .addElement(new UiActionButton()
                        .addElement(new UiTextBox().setText("Sell").setFontSize(FontSize.MENU))
                        .setAction(uiElement -> {
                                    SolApplication.getInputManager().addScreen(SolApplication.getInstance().getGame().getScreens().inventoryScreen);
                                }
                        ))
                .addElement(new UiActionButton()
                        .addElement(new UiTextBox().setText("Buy").setFontSize(FontSize.MENU))
                        .setAction(uiElement -> {
                                    SolApplication.getInputManager().addScreen(SolApplication.getInstance().getGame().getScreens().inventoryScreen);
                                }
                        ))
                .addElement(new UiActionButton()
                        .addElement(new UiTextBox().setText("Change ship").setFontSize(FontSize.MENU))
                        .setAction(uiElement -> {
                                    SolApplication.getInputManager().addScreen(SolApplication.getInstance().getGame().getScreens().inventoryScreen);
                                }
                        ))
                .addElement(new UiActionButton()
                        .addElement(new UiTextBox().setText("Hire").setFontSize(FontSize.MENU))
                        .setAction(uiElement -> {
                                    SolApplication.getInputManager().addScreen(SolApplication.getInstance().getGame().getScreens().inventoryScreen);
                                }
                        ));
        rootUiElement = new UiRelativeLayout()
                .addElement(verticalListLayout, UiDrawer.UI_POSITION_BOTTOM, 0, -verticalListLayout.getHeight() / 2 - 10
                );
    }
}
