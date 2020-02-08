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
package org.destinationsol.ui.nui.screens;

import org.destinationsol.ui.nui.NUIScreenLayer;
import org.terasology.math.geom.Vector2i;
import org.terasology.nui.Canvas;
import org.terasology.nui.UIWidget;
import org.terasology.nui.widgets.UIDropdown;
import org.terasology.nui.widgets.UIDropdownScrollable;
import org.terasology.nui.widgets.UIList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MigTestScreen extends NUIScreenLayer {
    @Override
    public void initialise() {
        List<String> values = Arrays.asList("one", "two", "three", "12345678901234567890");
        String selectedValue = values.get(1);

        for (String id : new String[]{"dropdown1", "dropdown2", "dropdown3", "dropdown4"}) {
            contents.find(id, UIDropdown.class).setOptions(values);
            contents.find(id, UIDropdown.class).setSelection(selectedValue);
        }

        for (String id : new String[]{"dropdownScrollable1", "dropdownScrollable2", "dropdownScrollable3", "dropdownScrollable4"}) {
            contents.find(id, UIDropdownScrollable.class).setVisibleOptions(2);
            contents.find(id, UIDropdownScrollable.class).setOptions(values);
            contents.find(id, UIDropdownScrollable.class).setSelection(selectedValue);
        }

        for (String id : new String[]{"list1", "list2", "list3", "list4"}) {
            contents.find(id, UIList.class).setList(values);
        }
    }
}
