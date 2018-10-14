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
package org.destinationsol.menu;

import com.badlogic.gdx.Graphics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResolutionProvider {

    private List<Resolution> resolutions;
    private int currentPosition;

    public ResolutionProvider(List<Graphics.DisplayMode> displayModes) {
        currentPosition = 0;

        Stream<Resolution> allResolutions = displayModes.stream().map((displayMode) -> new Resolution(displayMode.width, displayMode.height));
        resolutions = allResolutions.filter(distinctByStringValue())
                .sorted(Comparator.comparing(Resolution::getWidth).thenComparing(Resolution::getHeight))
                .collect(Collectors.toList());
    }

    public Resolution increase() {
        currentPosition = ++currentPosition % resolutions.size();
        return resolutions.get(currentPosition);
    }

    public Resolution getResolution() {
        return resolutions.get(currentPosition);
    }

    private Predicate<Resolution> distinctByStringValue() {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(t.toString());
    }
}
