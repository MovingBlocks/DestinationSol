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
import org.destinationsol.game.context.Context;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BindingConfiguration {

    private List<Function<Boolean,Class>> classValidators = Lists.newArrayList();

    private List<Function<Object, Context>> scope = Lists.newArrayList();


}
