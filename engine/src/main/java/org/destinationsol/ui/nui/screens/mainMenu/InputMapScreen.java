/*
 * Copyright 2021 The Terasology Foundation
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
package org.destinationsol.ui.nui.screens.mainMenu;

import org.destinationsol.Const;
import org.destinationsol.SolApplication;
import org.destinationsol.common.In;
import org.destinationsol.menu.InputConfigItem;
import org.destinationsol.menu.InputMapControllerScreen;
import org.destinationsol.menu.InputMapKeyboardScreen;
import org.destinationsol.menu.InputMapMixedScreen;
import org.destinationsol.menu.InputMapOperations;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.nui.Canvas;
import org.terasology.nui.UIWidget;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.layouts.ColumnLayout;
import org.terasology.nui.layouts.RowLayout;
import org.terasology.nui.layouts.RowLayoutHint;
import org.terasology.nui.layouts.relative.RelativeLayout;
import org.terasology.nui.widgets.UIBox;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UILabel;

import java.util.Iterator;
import java.util.List;

/**
 * This screen allows the user to re-map the key bindings used for in-game controls.
 * It is accessible via the {@link OptionsScreen}.
 */
public class InputMapScreen extends NUIScreenLayer {
    private static final Logger logger = LoggerFactory.getLogger(InputMapScreen.class);
    @In
    private SolApplication solApplication;
    private UILabel titleLabel;
    private ColumnLayout inputMapRows;
    private KeyActivatedButton nextButton;
    private KeyActivatedButton previousButton;
    private RelativeLayout keyPressOverlay;
    private UILabel keyPressMessage;
    private InputMapOperations inputOperations;
    private InputMapKeyboardScreen inputMapKeyboardScreen;
    private InputMapControllerScreen inputMapControllerScreen;
    private InputMapMixedScreen inputMapMixedScreen;
    private int selectedIndex;
    private UIWidget selectedRow;
    private int page;

    @Override
    public void initialise() {
        titleLabel = find("title", UILabel.class);

        inputMapRows = find("inputMapRows", ColumnLayout.class);

        nextButton = find("nextButton", KeyActivatedButton.class);
        nextButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyRight()));
        nextButton.subscribe(button -> {
            int inputCount = inputOperations.getItems(solApplication.getOptions()).size();
            selectedIndex += Const.ITEM_GROUPS_PER_PAGE;
            page++;
            button.setEnabled((selectedIndex + Const.ITEM_GROUPS_PER_PAGE) < inputCount);
            previousButton.setEnabled(true);

            updateInputRows();
        });

        previousButton = find("previousButton", KeyActivatedButton.class);
        previousButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyLeft()));
        previousButton.subscribe(button -> {
            selectedIndex -= Const.ITEM_GROUPS_PER_PAGE;
            page--;
            button.setEnabled(selectedIndex >= Const.ITEM_GROUPS_PER_PAGE);
            nextButton.setEnabled(true);

            updateInputRows();
        });

        for (int inputNo = selectedIndex; inputNo < Const.ITEM_GROUPS_PER_PAGE; inputNo++) {
            inputMapRows.addWidget(createInputMapRow(inputNo));
        }

        UIButton resetButton = find("defaultsButton", UIButton.class);
        resetButton.subscribe(button -> {
            inputOperations.resetToDefaults(solApplication.getOptions());
            updateInputRows();
        });

        UIButton saveButton = find("saveButton", UIButton.class);
        saveButton.subscribe(button -> {
            inputOperations.save(solApplication.getOptions());
            nuiManager.setScreen(solApplication.getMenuScreens().options);
        });

        KeyActivatedButton cancelButton = find("cancelButton", KeyActivatedButton.class);
        cancelButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyEscape()));
        cancelButton.subscribe(button -> {
            if (inputOperations.isEnterNewKey()) {
                // Cancel new input assignment if present (press escape to keep the current assignment)
                inputOperations.setEnterNewKey(false);
                return;
            }

            nuiManager.setScreen(solApplication.getMenuScreens().options);
        });

        keyPressOverlay = find("keyPressOverlay", RelativeLayout.class);
        keyPressMessage = keyPressOverlay.find("keyPressMessage", UILabel.class);

        inputMapKeyboardScreen = new InputMapKeyboardScreen();
        inputMapControllerScreen = new InputMapControllerScreen();
        inputMapMixedScreen = new InputMapMixedScreen();
    }

    @Override
    public void onAdded() {
        if (inputOperations == null) {
            logger.error("You must call setOperations before adding InputMapScreen!");
            nuiManager.setScreen(solApplication.getMenuScreens().options);
            return;
        }

        titleLabel.setText(inputOperations.getHeader());

        selectedIndex = 0;
        selectedRow = null;
        page = 0;
        keyPressOverlay.setVisible(false);
        inputOperations.onAdd(solApplication);

        List<InputConfigItem> inputs = inputOperations.getItems(solApplication.getOptions());
        nextButton.setEnabled(Const.ITEM_GROUPS_PER_PAGE < inputs.size());
        previousButton.setEnabled(false);

        updateInputRows();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (selectedRow != null) {
            UIButton assignButton = selectedRow.find("assignButton", UIButton.class);
            assignButton.setActive(inputOperations.isEnterNewKey());
            if (!inputOperations.isEnterNewKey()) {
                selectedRow = null;
                assignButton.setText(inputOperations.getItems(solApplication.getOptions()).get(inputOperations.getSelectedIndex()).getInputKey());
                updateInputRows();
                keyPressOverlay.setVisible(false);
            }
        }

        solApplication.getMenuBackgroundManager().update();
    }

    @Override
    public void onDraw(Canvas canvas) {
        try (NUIManager.LegacyUiDrawerWrapper wrapper = nuiManager.getLegacyUiDrawer()) {
            solApplication.getMenuBackgroundManager().draw(wrapper.getUiDrawer());
        }

        super.onDraw(canvas);
    }

    public void setOperations(InputMapOperations operations) {
        this.inputOperations = operations;
    }

    public InputMapKeyboardScreen getInputMapKeyboardScreen() {
        return inputMapKeyboardScreen;
    }

    public InputMapControllerScreen getInputMapControllerScreen() {
        return inputMapControllerScreen;
    }

    public InputMapMixedScreen getInputMapMixedScreen() {
        return inputMapMixedScreen;
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }

    private UIWidget createInputMapRow(int index) {
        RowLayout rowLayout = new RowLayout();
        rowLayout.setFamily("inputMapRow");
        UIBox inputNameBox = new UIBox();
        inputNameBox.setContent(new UILabel("inputName", "Input"));
        rowLayout.addWidget(inputNameBox, new RowLayoutHint().setRelativeWidth(0.6f));
        UIButton assignButton = new UIButton("assignButton", "<Assign>");
        assignButton.subscribe(button -> {
            inputOperations.setSelectedIndex(index + (page * Const.ITEM_GROUPS_PER_PAGE));
            inputOperations.setEnterNewKey(true);
            selectedRow = button;
            updateInputRows();
            keyPressMessage.setText(inputOperations.getDisplayDetail());
            keyPressOverlay.setVisible(true);
        });
        rowLayout.addWidget(assignButton, new RowLayoutHint().setRelativeWidth(0.4f));
        return rowLayout;
    }

    private void updateInputRows() {
        List<InputConfigItem> inputs = inputOperations.getItems(solApplication.getOptions());
        Iterator<UIWidget> rowsIterator = inputMapRows.iterator();
        rowsIterator.next(); // Ignore the first row, since it's the header.
        UIWidget row = rowsIterator.next();
        for (int inputNo = selectedIndex; inputNo < selectedIndex + Const.ITEM_GROUPS_PER_PAGE; inputNo++) {
            boolean emptyRow = inputNo >= inputs.size();
            boolean enabled = !(inputOperations.isEnterNewKey() || emptyRow);

            UILabel inputNameLabel = row.find("inputName", UILabel.class);
            inputNameLabel.setText(emptyRow ? "" : inputs.get(inputNo).getDisplayName());
            inputNameLabel.setEnabled(enabled);

            UIButton assignButton = row.find("assignButton", UIButton.class);
            assignButton.setText(emptyRow ? "" : inputs.get(inputNo).getInputKey());
            assignButton.setActive(inputOperations.getSelectedIndex() == inputNo && inputOperations.isEnterNewKey());
            assignButton.setEnabled(assignButton.isActive() || enabled);

            if (rowsIterator.hasNext()) {
                row = rowsIterator.next();
            }
        }
    }
}
