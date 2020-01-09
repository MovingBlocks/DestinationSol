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
package org.destinationsol.game.console;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.annotations.Command;
import org.destinationsol.game.console.annotations.CommandParam;
import org.destinationsol.game.context.Context;
import org.destinationsol.util.InjectionHelper;
import org.destinationsol.util.SpecificAccessibleObject;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

public final class MethodCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(MethodCommand.class);
    private static final String ENTITY_REF_NAME = "org.terasology.entitySystem.entity.EntityRef";

    private MethodCommand(String name, String description, String helpText,
                          SpecificAccessibleObject<Method> executionMethod, SolGame game, Context context) {
        super(name, description, helpText, executionMethod, game, context);
    }

    /**
     * Creates a new {@code ReferencedCommand} to a specific method
     * annotated with {@link
     *
     * @param specificMethod The method to reference to
     * @return The command reference object created
     */
    public static MethodCommand referringTo(SpecificAccessibleObject<Method> specificMethod,
                                            SolGame game, Context context) {
        Method method = specificMethod.getAccessibleObject();
        Command commandAnnotation = method.getAnnotation(Command.class);

        Preconditions.checkNotNull(commandAnnotation);

        String nameString = commandAnnotation.value();

        if (nameString.length() <= 0) {
            nameString = method.getName();
        }

        String name = nameString;

        return new MethodCommand(
                name,
                commandAnnotation.shortDescription(),
                commandAnnotation.helpText(),
                specificMethod, game, context
        );
    }

    /**
     * Registers all available command methods annotated with {@link
     */
    public static void registerAvailable(Object provider, Console console, SolGame game, Context context) {
        Predicate<? super Method> predicate = Predicates.<Method>and(ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withAnnotation(Command.class));
        Set<Method> commandMethods = ReflectionUtils.getAllMethods(provider.getClass(), predicate);
        for (Method method : commandMethods) {
            if (!hasSenderAnnotation(method)) {
                logger.error("Command {} provided by {} contains a EntityRef without @Sender annotation, may cause a NullPointerException", method.getName(), provider.getClass().getSimpleName());
            }
            logger.debug("Registering command method {} in class {}", method.getName(), method.getDeclaringClass().getCanonicalName());
            try {
                SpecificAccessibleObject<Method> specificMethod = new SpecificAccessibleObject<>(method, provider);
                MethodCommand command = referringTo(specificMethod, game, context);
                console.registerCommand(command);
                logger.debug("Registered command method {} in class {}", method.getName(), method.getDeclaringClass().getCanonicalName());
            } catch (RuntimeException t) {
                logger.error("Failed to load command method {} in class {}", method.getName(), method.getDeclaringClass().getCanonicalName(), t);
            }
        }
    }

    private static boolean hasSenderAnnotation(Method method) {
        return true;
    }

    @Override
    protected List<Parameter> constructParameters(SolGame game, Context context) {
        SpecificAccessibleObject<Method> specificExecutionMethod = getExecutionMethod();
        Method executionMethod = specificExecutionMethod.getAccessibleObject();
        Class<?>[] methodParameters = executionMethod.getParameterTypes();
        Annotation[][] methodParameterAnnotations = executionMethod.getParameterAnnotations();
        List<Parameter> parameters = Lists.newArrayListWithExpectedSize(methodParameters.length);
        System.out.println("AAAAAAAAAAAAAABBBBBBBBBBBBBBBABAAA +" + methodParameters.length);

        for (int i = 0; i < methodParameters.length; i++) {
            parameters.add(getParameterTypeFor(methodParameters[i], methodParameterAnnotations[i],
                    game, context));
        }

        return parameters;
    }

    private static Parameter getParameterTypeFor(Class<?> type, Annotation[] annotations,
                                                 SolGame game, Context context) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof CommandParam) {
                CommandParam parameterAnnotation
                        = (CommandParam) annotation;
                String name = parameterAnnotation.value();
                Class<? extends CommandParameterSuggester> suggesterClass = parameterAnnotation.suggester();
                boolean required = parameterAnnotation.required();
                CommandParameterSuggester  suggester = InjectionHelper.createWithConstructorInjection(suggesterClass,
                        context);

                if (type.isArray()) {
                    Class<?> childType = type.getComponentType();

                    return CommandParameter.array(name, childType, required, suggester, game, context);
                } else {
                    return CommandParameter.single(name, type, required, suggester, game, context);
                }
            }
        }

        return MarkerParameters.INVALID;
    }
}
