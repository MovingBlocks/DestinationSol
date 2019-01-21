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

import org.destinationsol.engine.di.graph.DependencyGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ServiceRegistry {
    private List<Consumer<DependencyGraph>> graph = new ArrayList<>();

    protected void add(Consumer<DependencyGraph> consumer){
        graph.add(consumer);
    }

    public ServiceRegistry(){

    }

    public DependencyGraph build(){
        DependencyGraph dependencyGraph = new DependencyGraph();
        graph.forEach(consumer -> {
            consumer.accept(dependencyGraph);
        });
        return dependencyGraph;
    }

    protected  <T> InstanceExpression<T> For(Class<T> inst){
        return new InstanceExpression<T>(this);
    }

    public static class InstanceExpression<T> {

        private ServiceRegistry serviceRegistry;
        public InstanceExpression(ServiceRegistry registry){
            this.serviceRegistry = registry;
        }

        public <U> ObjectInstance<U> Use(Class<U> inst){
            return new ObjectInstance<U>(serviceRegistry);
        }
    }

    public static class ObjectInstance<T> {
        private ServiceRegistry serviceRegistry;
        public enum LifeCycle {
            Transient,
            Singleton
        }

        public ObjectInstance(ServiceRegistry serviceRegistry) {
            this.serviceRegistry = serviceRegistry;
        }


        private LifeCycle lifeCycle = LifeCycle.Transient;

        public void setLifecycle(LifeCycle lifeCycle) {
            this.lifeCycle = lifeCycle;
        }

        public void singleton() {
            setLifecycle(LifeCycle.Singleton);
        }
    }
}
