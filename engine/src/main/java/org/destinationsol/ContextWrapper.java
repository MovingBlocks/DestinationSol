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
package org.destinationsol;

import org.destinationsol.game.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.di.BeanContext;
import org.terasology.gestalt.di.exceptions.BeanResolutionException;

import javax.inject.Inject;

@Deprecated
public class ContextWrapper implements Context {

    private static final Logger logger = LoggerFactory.getLogger(ContextWrapper.class);
    protected BeanContext context;

    @Inject
    public ContextWrapper(BeanContext beanContext) {
        this.context = beanContext;
    }

    @Override
    public <T> T get(Class<? extends T> type) {
        try {
            return (T) context.getBean(type);
        } catch (BeanResolutionException e){
            logger.warn("Bean [{}] not found",type);
            return null;
        }
    }

    @Override
    public <T, U extends T> void put(Class<T> type, U object) {
        throw new RuntimeException("Cannot insert values into wrapped context: please use the BeanContext directly");
    }

    @Override
    public <T, U extends T> void remove(Class<T> type, U object) {
        throw new RuntimeException("Cannot insert values into wrapped context: please use the BeanContext directly");
    }
}
