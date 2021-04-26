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
package org.destinationsol;


import org.destinationsol.game.context.Context;
import org.terasology.context.Lifetime;
import org.terasology.gestalt.di.ServiceRegistry;

public class ContextWrapperService extends ServiceRegistry {
    public ContextWrapperService() {
        this.with(Context.class).use(ContextWrapper.class).lifetime(Lifetime.Singleton);
    }
}
