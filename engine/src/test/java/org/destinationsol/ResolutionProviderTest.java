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
package org.destinationsol;

import com.badlogic.gdx.Graphics;
import org.destinationsol.Resolution;
import org.destinationsol.ResolutionProvider;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


public class ResolutionProviderTest {

    private ResolutionProvider resolutionProvider;

    @Test
    public void testIncreaseReturnsResolutionForSingleDisplayMode() {
        resolutionProvider = new ResolutionProvider(Collections.singletonList(new TestDisplayMode(100, 50)));
        Resolution resolution = resolutionProvider.increase();
        assertResolution(resolution, 100, 50);
    }

    @Test
    public void testIncreaseCirclesThroughAllResolutions() {
        resolutionProvider = new ResolutionProvider(Arrays.asList(new TestDisplayMode(100, 50), new TestDisplayMode(200, 100)));
        assertResolution(resolutionProvider.increase(), 200, 100);
        assertResolution(resolutionProvider.increase(), 100, 50);
        assertResolution(resolutionProvider.increase(), 200, 100);
        assertResolution(resolutionProvider.increase(), 100, 50);
    }

    @Test
    public void testOrderOfResolutions() {
        resolutionProvider = new ResolutionProvider(Arrays.asList(new TestDisplayMode(100, 100), new TestDisplayMode(1000, 100), new TestDisplayMode(100, 50)));
        assertResolution(resolutionProvider.increase(), 100, 100);
        assertResolution(resolutionProvider.increase(), 1000, 100);
        assertResolution(resolutionProvider.increase(), 100, 50);
    }

    @Test
    public void testGetResolution() {
        resolutionProvider = new ResolutionProvider(Arrays.asList(new TestDisplayMode(100, 50), new TestDisplayMode(200, 100)));
        assertResolution(resolutionProvider.getResolution(), 100, 50);
        resolutionProvider.increase();
        assertResolution(resolutionProvider.getResolution(), 200, 100);
    }

    class TestDisplayMode extends Graphics.DisplayMode {

        protected TestDisplayMode(int width, int height) {
            super(width, height, 1, 1);
        }
    }

    private void assertResolution(Resolution resolution, int width, int height) {
        assertThat(resolution.getWidth()).isEqualTo(width);
        assertThat(resolution.getHeight()).isEqualTo(height);
    }
}
