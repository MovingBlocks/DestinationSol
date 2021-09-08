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
package org.destinationsol.assets.ui;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.terasology.input.Keyboard;

import java.lang.reflect.Type;
import java.util.Locale;

/**
 * A {@link JsonDeserializer} that deserialises keyboard keys from their names.
 *
 * {@link CaseInsensitiveEnumTypeAdapterFactory} cannot be used as the {@link Keyboard.Key#toString()} method returns
 * different names.
 */
public class KeyboardKeyTypeAdapter implements JsonDeserializer<Keyboard.Key> {
    @Override
    public Keyboard.Key deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String keyName = json.getAsString().toUpperCase(Locale.ENGLISH);
        try {
            return Enum.valueOf(Keyboard.Key.class, keyName);
        } catch (IllegalArgumentException ignore) {
            return (Keyboard.Key) Keyboard.Key.find(keyName);
        } catch (NullPointerException ignore) {
            return Keyboard.Key.NONE;
        }
    }
}
