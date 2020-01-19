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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.console.exceptions.CommandExecutionException;
import org.destinationsol.game.console.exceptions.CommandInitializationException;
import org.destinationsol.game.console.exceptions.CommandParameterParseException;
import org.destinationsol.game.console.exceptions.CommandSuggestionException;
import org.destinationsol.game.context.Context;
import org.destinationsol.util.SpecificAccessibleObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractCommand implements ConsoleCommand {

    private final String name;
    private final String description;
    private final String helpText;
    private final SpecificAccessibleObject<Method> executionMethod;
    private ImmutableList<CommandParameter> commandParameters;
    private ImmutableList<Parameter> executionMethodParameters;
    private int requiredParameterCount;
    private String usage;
    private SolGame game;

    public AbstractCommand(String name, String description, String helpText,
                           SpecificAccessibleObject<Method> executionMethod, SolGame game, Context context) {
        Preconditions.checkNotNull(executionMethod);
        Preconditions.checkNotNull(description);
        Preconditions.checkNotNull(helpText);

        this.name = name;
        this.description = description;
        this.helpText = helpText;
        this.executionMethod = executionMethod;
        this.game = game;

        constructParametersNotNull(context);
        registerParameters();
        validateExecutionMethod();
        initUsage();
    }

    private static Set<String> convertToString(Set<Object> collection, CommandParameter parameter) {
        return collection.stream().map(parameter::convertToString).collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * @return A list of parameter types provided to the execution method.
     */
    protected abstract List<Parameter> constructParameters(Context context);

    private void constructParametersNotNull(Context context) {
        List<Parameter> constructedParameters = constructParameters(context);

        if (constructedParameters == null || constructedParameters.size() <= 0) {
            commandParameters = ImmutableList.of();
            executionMethodParameters = ImmutableList.of();
            return;
        }

        ImmutableList.Builder<CommandParameter> commandParameterBuilder = ImmutableList.builder();

        for (int i = 0; i < constructedParameters.size(); i++) {
            Parameter type = constructedParameters.get(i);

            if (type == null) {
                throw new CommandInitializationException("Invalid parameter definition #" + i + "; must not be null");
            } else if (type instanceof CommandParameter) {
                commandParameterBuilder.add((CommandParameter) type);
            }
        }

        commandParameters = commandParameterBuilder.build();
        executionMethodParameters = ImmutableList.copyOf(constructedParameters);
    }

    private void registerParameters() throws CommandInitializationException {
        requiredParameterCount = 0;
        boolean optionalFound = false;

        for (int i = 0; i < commandParameters.size(); i++) {
            CommandParameter parameter = commandParameters.get(i);

            if (parameter == null) {
                throw new CommandInitializationException("A command parameter must not be null! Index: " + i);
            }

            if (parameter.isArray() && i < commandParameters.size() - 1) {
                throw new CommandInitializationException("A varargs parameter must be at the end. Invalid: " + i + "; " + parameter.getName());
            }

            if (parameter.isRequired()) {
                if (!optionalFound) {
                    requiredParameterCount++;
                } else {
                    throw new CommandInitializationException("A command definition must not contain a required"
                            + " parameter (" + i + "; " + parameter.getName()
                            + ") after an optional parameter.");
                }
            } else if (!optionalFound) {
                optionalFound = true;
            }
        }
    }

    private void checkArgumentCompatibility(Method method) throws CommandInitializationException {
        Class<?>[] methodParameters = method.getParameterTypes();

        int executionMethodParametersSize = executionMethodParameters.size();
        int methodParameterCount = methodParameters.length;

        for (int i = 0; i < methodParameterCount || i < executionMethodParametersSize; i++) {
            if (i >= methodParameterCount) {
                throw new CommandInitializationException("Missing " + (executionMethodParametersSize - methodParameterCount)
                        + " parameters in method " + method.getName()
                        + ", follow the parameter definitions from the"
                        + " 'constructParameters' method.");
            } else if (i >= executionMethodParametersSize) {
                throw new CommandInitializationException("Too many (" + (methodParameterCount - executionMethodParametersSize)
                        + ") parameters in method " + method.getName()
                        + ", follow the parameter definitions from the"
                        + " 'constructParameters' method.");
            }

            Parameter expectedParameterType = executionMethodParameters.get(i);
            Optional<? extends Class<?>> expectedType = expectedParameterType.getProvidedType();
            Class<?> providedType = methodParameters[i];

            if (providedType.isPrimitive()) {
                providedType = Primitives.wrap(providedType);
            }

            if (expectedType.isPresent() && !expectedType.get().isAssignableFrom(providedType)) {
                throw new CommandInitializationException("Cannot assign command argument from "
                        + providedType.getSimpleName() + " to "
                        + expectedType.get().getSimpleName() + "; "
                        + "command method parameter index: " + i);
            }
        }
    }

    private void validateExecutionMethod() {
        checkArgumentCompatibility(executionMethod.getAccessibleObject());
    }

    private void initUsage() {
        StringBuilder builder = new StringBuilder(name);

        for (CommandParameter param : commandParameters) {
            builder.append(' ').append(param.getUsage());
        }

        usage = builder.toString();
    }

    private Object[] processParametersMethod(List<String> rawParameters) throws CommandParameterParseException {
        Object[] processedParameters = new Object[executionMethodParameters.size()];
        Queue<String> parameterStrings = Queues.newArrayDeque(rawParameters);

        for (int i = 0; i < executionMethodParameters.size(); i++) {
            Parameter parameterType = executionMethodParameters.get(i);

            if (parameterType instanceof CommandParameter) {
                CommandParameter parameter = (CommandParameter) parameterType;
                if (parameterStrings.isEmpty()) {
                    if (parameter.isArray()) {
                        processedParameters[i] = parameter.getArrayValue(Collections.emptyList());
                    } else {
                        processedParameters[i] = null;
                    }
                } else if (parameter.isArray()) {
                    processedParameters[i] = parameter.getArrayValue(Lists.newArrayList(parameterStrings));
                    parameterStrings.clear();
                } else {
                    processedParameters[i] = parameter.getValue(parameterStrings.poll());
                }
            } else if (parameterType == MarkerParameters.GAME) {
                processedParameters[i] = game;
            }
        }

        return processedParameters;
    }

    @Override
    public final String execute(List<String> rawParameters) throws CommandExecutionException {
        Object[] processedParameters;

        try {
            processedParameters = processParametersMethod(rawParameters);
        } catch (CommandParameterParseException e) {
            String warning = "Invalid parameter '" + e.getParameter() + "'";
            String message = e.getMessage();

            if (message != null) {
                warning += ": " + message;
            }

            return warning;
        }

        try {
            Object result = executionMethod.getAccessibleObject().invoke(executionMethod.getTarget(), processedParameters);

            return result != null ? String.valueOf(result) : null;
        } catch (InvocationTargetException t) {
            if (t.getCause() != null) {
                throw new CommandExecutionException(t.getCause()); //Skip InvocationTargetException
            } else {
                throw new CommandExecutionException(t);
            }
        } catch (IllegalAccessException | RuntimeException t) {
            throw new CommandExecutionException(t);
        }
    }

    @Override
    public final Set<String> suggest(final String currentValue, List<String> rawParameters) throws CommandSuggestionException {
        //Generate an array to be used as a parameter in the 'suggest' method
        Object[] processedParameters;

        try {
            processedParameters = processParametersMethod(rawParameters);
        } catch (CommandParameterParseException e) {
            String warning = "Invalid parameter '" + e.getParameter() + "'";
            String message = e.getMessage();

            if (message != null) {
                warning += ": " + message;
            }

            throw new CommandSuggestionException(warning);
        }

        //Get the suggested parameter to compare the result with
        CommandParameter suggestedParameter = null;
        Iterator<CommandParameter> paramIter = commandParameters.iterator();

        for (Object processedParameter : processedParameters) {
            if (game.equals(processedParameter)) {
                continue;
            }
            if (processedParameter == null) {
                suggestedParameter = paramIter.next();
                break;
            }
            paramIter.next();
        }

        if (suggestedParameter == null) {
            return Sets.newHashSet();
        }

        Set<Object> result = null;

        result = suggestedParameter.suggest(processedParameters);


        if (result == null) {
            return Sets.newHashSet();
        }

        Class<?> requiredClass = suggestedParameter.getType();

        for (Object resultComponent : result) {
            if (resultComponent == null && requiredClass.isPrimitive()) {
                throw new CommandSuggestionException("The 'suggest' method of command class " + getClass().getCanonicalName()
                        + " returns a collection containing an invalid type. Required: " + requiredClass.getCanonicalName()
                        + "; provided: null");
            } else if (resultComponent != null && !requiredClass.isAssignableFrom(resultComponent.getClass())) {
                throw new CommandSuggestionException("The 'suggest' method of command class " + getClass().getCanonicalName()
                        + " returns a collection containing an invalid type. Required: " + requiredClass.getCanonicalName()
                        + "; provided: " + resultComponent.getClass().getCanonicalName());
            }
        }

        Set<String> stringSuggestions = convertToString(result, suggestedParameter);

        //Only return results starting with currentValue
        return Sets.filter(stringSuggestions, input ->
                input != null && (currentValue == null || input.startsWith(currentValue)));
    }

    @Override
    public ImmutableList<CommandParameter> getCommandParameters() {
        return commandParameters;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getHelpText() {
        return helpText;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getRequiredParameterCount() {
        return requiredParameterCount;
    }

    @Override
    public boolean endsWithVarargs() {
        return commandParameters.size() > 0 && commandParameters.get(commandParameters.size() - 1).isArray();
    }

    @Override
    public Object getSource() {
        return executionMethod.getTarget();
    }

    @Override
    public int compareTo(ConsoleCommand o) {
        return ConsoleCommand.COMPARATOR.compare(this, o);
    }

    public SpecificAccessibleObject<Method> getExecutionMethod() {
        return executionMethod;
    }
}