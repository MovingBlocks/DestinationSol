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
package org.destinationsol.context;

import org.destinationsol.common.In;

import java.util.stream.Stream;

public class Injector {
    public static void inject(Object object, Context context) {
        Stream.of(object.getClass().getDeclaredFields())
                .filter(field ->
                        Stream.of(field.getDeclaredAnnotations()).anyMatch(annotation -> annotation.annotationType() == In.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        field.set(object, context.get(field.getType()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }
}
