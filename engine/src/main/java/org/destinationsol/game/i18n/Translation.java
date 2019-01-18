/*
 * Copyright 2019 MovingBlocks
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
package org.destinationsol.game.i18n;

import org.destinationsol.GameOptions;

/**
 * Translates information using included lang files
 */
public class Translation {

    public static String getLocale() {
       return GameOptions.getLocale();
    }

    /**
     * Syntax should contain a $ at the start and enclosed within braces
     * Included should be the module, a colon (:), the file name, a hashtag (#), and the key
     * Example: ${core:menu#play}
     *
     * @param data A String containing that points towards the lang file and key
     * @return The translated String
     */
    public static String translate(String data) {
        TranslationData translationData = new TranslationData();
        return translationData.getTranslation(data);
    }
}
