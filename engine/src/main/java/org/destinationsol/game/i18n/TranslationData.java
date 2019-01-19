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

import org.destinationsol.assets.Assets;
import org.destinationsol.assets.lang.Lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationData {
    private static Pattern pattern = Pattern.compile("^\\$\\{\\w+:\\w+#\\w+}$");

    protected String getTranslation(String data) {
        syntaxCheck(data);
        String fileName = data.substring(2, data.indexOf("#"));
        String key = data.substring(data.indexOf("#") + 1, data.indexOf("}"));
        return findString(fileName, key);
    }

    private String findString(String fileName, String key) {
        Lang lang = Assets.getLang(fileName);
        String entry = lang.getLangValue().getString(key);
        Lang localeLang = Assets.getLang(fileName + "_" + Translation.getLocale());
        return localeLang.getLangValue().getString(entry);
    }

    private void syntaxCheck(String data) {
        Matcher matcher = pattern.matcher(data);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid syntax for lang lookup");
        }
    }
}
