/*
 * Copyright 2022 The Terasology Foundation
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

import org.destinationsol.SolApplication;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.gestalt.module.Module;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.itemRendering.StringTextRenderer;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIList;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This screen is used to select the modules that should be active when playing a particular save.
 * You can activate and de-activate modules only when initially creating a game.
 * This is to prevent side-effects from new modules being introduced unexpectedly.
 */
public class ModulesScreen extends NUIScreenLayer {
    private final SolApplication solApplication;
    private final ModuleManager moduleManager;
    private UIList<Module> moduleList;
    private Set<Module> selectedModules;

    @Inject
    public ModulesScreen(SolApplication solApplication, ModuleManager moduleManager) {
        this.solApplication = solApplication;
        this.moduleManager = moduleManager;
    }

    @Override
    public void initialise() {
        selectedModules = new HashSet<>();

        moduleList = find("modulesList", UIList.class);

        moduleList.setItemRenderer(new StringTextRenderer<Module>() {
            @Override
            public String getString(Module value) {
                if (!selectedModules.contains(value)) {
                    return value.getId().toString();
                } else {
                    return value.getId().toString() + " (Active)";
                }
            }
        });
        moduleList.subscribe((list, module) -> {
            if (selectedModules.contains(module)) {
                selectedModules.remove(module);
            } else {
                selectedModules.add(module);
            }
        });

        UIButton activateButton = find("activateButton", UIButton.class);
        activateButton.bindEnabled(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                Module selectedModule = moduleList.getSelection();
                return selectedModule != null && !selectedModules.contains(selectedModule);
            }
        });
        activateButton.subscribe(button -> selectedModules.add(moduleList.getSelection()));

        UIButton deactivateButton = find("deactivateButton", UIButton.class);
        deactivateButton.bindEnabled(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                Module selectedModule = moduleList.getSelection();
                return selectedModule != null && selectedModules.contains(selectedModule);
            }
        });
        deactivateButton.subscribe(button -> selectedModules.remove(moduleList.getSelection()));

        KeyActivatedButton confirmButton = find("confirmButton", KeyActivatedButton.class);
        confirmButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyEscape()));
        confirmButton.subscribe(button -> {
            nuiManager.setScreen(solApplication.getMenuScreens().newShip);
        });
    }

    @Override
    public void onAdded() {
        List<Module> modules = new ArrayList<>(moduleManager.getRegistry().getModuleIds().stream().map(moduleId -> moduleManager.getRegistry().getLatestModuleVersion(moduleId)).collect(Collectors.toList()));
        modules.removeAll(moduleManager.getBuiltInModules());
        moduleList.setList(modules);
    }

    public Set<Module> getSelectedModules() {
        return selectedModules;
    }

    public void setSelectedModules(Set<Module> selectedModules) {
        this.selectedModules = selectedModules;
    }
}
