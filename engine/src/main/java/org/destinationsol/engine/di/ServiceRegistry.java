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
package org.destinationsol.engine.di;

import com.google.common.collect.Lists;
import org.destinationsol.engine.di.build.BindBuilder;
import org.destinationsol.engine.di.graph.BaseContext;
import org.destinationsol.engine.di.graph.Binding;
import org.destinationsol.engine.di.graph.Context;
import org.destinationsol.engine.di.graph.RootContext;

import java.util.List;

public abstract class ServiceRegistry {

    private BaseContext context;
    private List<Binding> bindings = Lists.newArrayList();

    public ServiceRegistry() {
    }

    public <T> BindBuilder<T> bind(Class<T> implementation) {
        Binding<T> binding = new Binding<T>(implementation);
        addBinding(binding);
        return new BindBuilder<T>(binding);
    }

    public abstract void load();

    public <T> void addBinding(Binding<T> binding) {
        bindings.add(binding);
        context.addBinding(binding);
    }

    public void onLoad(BaseContext context) {
        this.context = context;
        load();
    }

}
