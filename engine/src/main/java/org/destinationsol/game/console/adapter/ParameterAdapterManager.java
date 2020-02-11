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
package org.destinationsol.game.console.adapter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.destinationsol.SolApplication;
import org.destinationsol.game.console.commands.PositionCommandHandler;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.Map;

public class ParameterAdapterManager {
    private final Map<Class<?>, ParameterAdapter> adapters = Maps.newHashMap();

    /**
     * @return A manager with basic adapters for wrapped primitives and {@link String}
     */
    @SuppressWarnings("unchecked")
    public static ParameterAdapterManager createBasic() {
        ParameterAdapterManager manager = new ParameterAdapterManager();

        for (Map.Entry<Class, ParameterAdapter> entry : PrimitiveAdapters.MAP.entrySet()) {
            manager.registerAdapter(entry.getKey(), entry.getValue());
        }

        return manager;
    }

    /**
     * @return A manager with basic adapters and following classes:
     */
    public static ParameterAdapterManager createCore(SolApplication application) {
        ParameterAdapterManager manager = createBasic();

        manager.registerAdapter(PositionCommandHandler.PositionFormat.class, new PositionFormatAdapter());
        manager.registerAdapter(HullConfig.class, new HullConfigAdapter(application));

        return manager;
    }

    /**
     * @return {@code true}, if the adapter didn't override a previously present adapter
     */
    public <T> boolean registerAdapter(Class<? extends T> clazz, ParameterAdapter<T> adapter) {
        return adapters.put(clazz, adapter) == null;
    }

    public boolean isAdapterRegistered(Class<?> clazz) {
        return adapters.containsKey(clazz);
    }

    /**
     * @param clazz The type of the returned object
     * @param raw   The string from which to parse
     * @return The parsed object
     * @throws ClassCastException If the {@link ParameterAdapter} is linked with an incorrect {@link java.lang.Class}.
     */
    @SuppressWarnings("unchecked")
    public <T> T parse(Class<T> clazz, String raw) throws ClassCastException {
        Preconditions.checkNotNull(raw, "The String to parse must not be null");

        ParameterAdapter adapter = getAdapter(clazz);

        Preconditions.checkNotNull(adapter, "No adapter found for " + clazz.getCanonicalName());

        return (T) adapter.parse(raw);
    }

    /**
     * @param value The object to convertToString
     * @param clazz The class pointing to the desired adapter
     * @return The composed object
     * @throws ClassCastException If the {@link ParameterAdapter} is linked with an incorrect {@link java.lang.Class}.
     */
    @SuppressWarnings("unchecked")
    public <T> String convertToString(T value, Class<? super T> clazz) throws ClassCastException {
        Preconditions.checkNotNull(value, "The Object to convertToString must not be null");

        ParameterAdapter adapter = getAdapter(clazz);

        Preconditions.checkNotNull(adapter, "No adapter found for " + clazz.getCanonicalName());

        return adapter.convertToString(value);
    }

    /**
     * @param value The object to convertToString
     * @return The composed object
     * @throws ClassCastException If the {@link ParameterAdapter} is linked with an incorrect {@link java.lang.Class}.
     */
    @SuppressWarnings("unchecked")
    public String convertToString(Object value) throws ClassCastException {
        Class<?> clazz = value.getClass();

        return convertToString(value, (Class<? super Object>) clazz);
    }

    public ParameterAdapter getAdapter(Class<?> clazz) {
        return adapters.get(clazz);
    }
}
