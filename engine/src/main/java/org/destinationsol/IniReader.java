/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol;

import com.badlogic.gdx.files.FileHandle;
import org.destinationsol.game.SaveManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class IniReader {
    private final HashMap<String, String> values;

    public IniReader(String fileName, SolFileReader reader) {
        values = new HashMap<>();
        List<String> lines = reader != null ? reader.read(fileName) : fileToLines(fileName);
        initValueMap(lines);
    }

    public IniReader(BufferedReader reader) {
        values = new HashMap<>();
        List<String> lines = new ArrayList<>();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
        }

        initValueMap(lines);
    }

    public static void write(String fileName, Object... keysVals) {
        boolean second = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (Object value : keysVals) {
            stringBuilder.append(value.toString());
            stringBuilder.append(second ? '\n' : '=');
            second = !second;
        }

        String path = SaveManager.getResourcePath(fileName);

        FileHandle file = new FileHandle(Paths.get(path).toFile());
        file.writeString(stringBuilder.toString(), false);
    }

    private void initValueMap(List<String> lines) {
        for (String line : lines) {
            int commentStart = line.indexOf('#');
            if (commentStart >= 0) {
                line = line.substring(0, commentStart);
            }
            String[] sides = line.split("=");
            if (sides.length < 2) {
                continue;
            }
            String key = sides[0].trim();
            String val = sides[1].trim();
            values.put(key, val);
        }
    }

    private List<String> fileToLines(String fileName) {
        String path = SaveManager.getResourcePath(fileName);

        FileHandle file = new FileHandle(Paths.get(path).toFile());

        ArrayList<String> res = new ArrayList<>();
        if (!file.exists()) {
            return res;
        }

        Collections.addAll(res, file.readString().split("\n"));

        return res;
    }

    public String getString(String key, String defaultValue) {
        Optional<String> stringOptional = Optional.ofNullable(values.get(key));
        return stringOptional.orElse(defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        Optional<String> stringOptional = Optional.ofNullable(values.get(key));
        try {
            return stringOptional.map(Integer::parseInt).orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Optional<String> stringOptional = Optional.ofNullable(values.get(key));
        Optional<Boolean> booleanOptional = stringOptional.filter(s -> s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")).map(s -> s.equalsIgnoreCase("true"));
        return booleanOptional.orElse(defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        Optional<String> stringOptional = Optional.ofNullable(values.get(key));
        try {
            return stringOptional.map(Float::parseFloat).orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
