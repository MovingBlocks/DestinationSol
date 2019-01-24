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
package org.destinationsol.engine.di.graph;

import com.google.common.collect.Lists;
import org.destinationsol.engine.di.ServiceRegistry;

import java.util.Collection;

public class BaseContext extends RootContext {
    public BaseContext() {
    }

    public void configure(Collection<ServiceRegistry> servicies) {
        for (ServiceRegistry service : servicies) {
            service.onLoad(this);
        }
    }

    public <T> void addBinding(Binding binding) {
        Collection<Binding> collection = bindings.get(binding.getBaseType());
        if (collection == null) {
            collection = bindings.put(binding.getBaseType(), Lists.newArrayList());
        }
        collection.add(binding);
    }
}
