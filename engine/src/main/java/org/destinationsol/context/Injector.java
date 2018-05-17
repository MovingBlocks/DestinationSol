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
import org.destinationsol.common.Share;
import org.destinationsol.common.ShareScope;
import org.destinationsol.context.internal.ContextImpl;

import java.lang.reflect.Field;

public class Injector {
    private static final Context appLevelContext = new ContextImpl();
    private static Context gameLevelContext = null;

    private Injector() { }

    public static void newGame() {
        gameLevelContext = new ContextImpl(appLevelContext);
    }

    public static void endGame() {
        gameLevelContext = null;
    }

    public static void inject(Object object, boolean appLevel) {
        if (!appLevel && gameLevelContext == null) {
            throw new AssertionError("Requesting game context without game.");
        }
        final Field[] declaredAnnotationsByType = object.getClass().getDeclaredFields();
        for (Field field : declaredAnnotationsByType) {
            if (field.getDeclaredAnnotationsByType(In.class).length > 0) {
                field.setAccessible(true);
                try {
                    field.set(object, (appLevel ? appLevelContext : gameLevelContext).get(field.getType()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T, U extends T> void share(Class<T> clazz, U object) {
        final Class<?> objectClass = object.getClass();
        if (objectClass.getDeclaredAnnotationsByType(Share.class).length > 0) {
            if (objectClass.getDeclaredAnnotation(Share.class).shareScope() == ShareScope.APP) {
                appLevelContext.put(clazz, object);
            } else {
                gameLevelContext.put(clazz, object);
            }
        }
    }
}
