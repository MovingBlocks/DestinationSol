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

import org.destinationsol.engine.di.BindingConfiguration;
import org.destinationsol.game.context.Context;

import java.util.function.Function;

public class Binding<T> {
    private Class<T> base;
    private BindingConfiguration configuration;


    public Binding(Class<T> type){
        this(type,new BindingConfiguration());
    }

    public Binding(Class<T> type, BindingConfiguration configuration){
        this.configuration = configuration;
        this.base = type;
    }

    public Class<T> getBaseType(){
        return base;
    }

    public void AddScopeCallback(Function<Object, Context> callback){
        configuration.addScopeCallback(callback);
    }
}
