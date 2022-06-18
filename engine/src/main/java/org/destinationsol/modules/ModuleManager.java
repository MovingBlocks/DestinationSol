/*
 * Copyright 2020 MovingBlocks
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
package org.destinationsol.modules;

import com.google.common.collect.Sets;
import org.destinationsol.entitysystem.ComponentSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.di.BeanContext;
import org.terasology.gestalt.module.Module;
import org.terasology.gestalt.module.ModuleEnvironment;
import org.terasology.gestalt.module.ModuleFactory;
import org.terasology.gestalt.module.ModuleMetadata;
import org.terasology.gestalt.module.ModulePathScanner;
import org.terasology.gestalt.module.ModuleRegistry;
import org.terasology.gestalt.module.sandbox.APIScanner;
import org.terasology.gestalt.module.sandbox.ModuleSecurityManager;
import org.terasology.gestalt.module.sandbox.ModuleSecurityPolicy;
import org.terasology.gestalt.module.sandbox.StandardPermissionProviderFactory;
import org.terasology.gestalt.naming.Name;
import org.terasology.gestalt.naming.Version;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ReflectPermission;
import java.security.Policy;
import java.util.List;
import java.util.Set;

/**
 * A class used for loading and managing modules in Destination Sol.
 */
public class ModuleManager implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(ModuleManager.class);
    // The API whitelist is based off Terasology's
    // https://github.com/MovingBlocks/Terasology/blob/948676050a7827dac5e04927087832ffc462da41/engine/src/main/java/org/terasology/engine/module/ExternalApiWhitelist.java
    protected static final String[] API_WHITELIST = new String[]{
            "java.lang",
            "java.lang.invoke",
            "java.lang.ref",
            "java.math",
            "java.util",
            "java.util.concurrent",
            "java.util.concurrent.atomic",
            "java.util.concurrent.locks",
            "java.util.function",
            "java.util.regex",
            "java.util.stream",
            "java.util.zip",
            "java.awt",
            "java.awt.geom",
            "java.awt.image",
            "jdk.internal.reflect",
            "com.google.common.annotations",
            "com.google.common.cache",
            "com.google.common.collect",
            "com.google.common.base",
            "com.google.common.math",
            "com.google.common.primitives",
            "com.google.common.util.concurrent",
            "gnu.trove",
            "gnu.trove.decorator",
            "gnu.trove.function",
            "gnu.trove.iterator",
            "gnu.trove.iterator.hash",
            "gnu.trove.list",
            "gnu.trove.list.array",
            "gnu.trove.list.linked",
            "gnu.trove.map",
            "gnu.trove.map.hash",
            "gnu.trove.map.custom_hash",
            "gnu.trove.procedure",
            "gnu.trove.procedure.array",
            "gnu.trove.queue",
            "gnu.trove.set",
            "gnu.trove.set.hash",
            "gnu.trove.stack",
            "gnu.trove.stack.array",
            "gnu.trove.strategy",
            "javax.vecmath",
            "sun.reflect",
            "org.json",
            "com.badlogic.gdx.math",
            // Many classes still use LibGDX directly
            "com.badlogic.gdx.graphics",
            "com.badlogic.gdx.graphics.g2d",
            // The hull config exposes a box2d body instance
            "com.badlogic.gdx.physics",
            "com.badlogic.gdx.physics.box2d",
            // NUI doesn't use gestalt's @API annotations anymore, so they are replicated here
            "org.terasology.input",
            "org.terasology.input.device",
            "org.terasology.input.device.nulldevices",
            "org.terasology.nui",
            "org.terasology.nui.asset",
            "org.terasology.nui.asset.font",
            "org.terasology.nui.canvas",
            "org.terasology.nui.databinding",
            "org.terasology.nui.events",
            "org.terasology.nui.itemRendering",
            "org.terasology.nui.layouts",
            "org.terasology.nui.layouts.miglayout",
            "org.terasology.nui.layouts.relative",
            "org.terasology.nui.properties",
            "org.terasology.nui.reflection",
            "org.terasology.nui.skin",
            "org.terasology.nui.translate",
            "org.terasology.nui.util",
            "org.terasology.nui.widgets",
            "org.terasology.nui.widgets.treeView",
            "org.terasology.nui.widgets.types",
            "org.terasology.nui.widgets.types.builtin",
            "org.terasology.nui.widgets.types.builtin.object",
            "org.terasology.nui.widgets.types.builtin.util",
            "org.terasology.nui.widgets.types.math",
            "org.terasology.reflection.metadata"
    };
    protected static final Class<?>[] CLASS_WHITELIST = new Class<?>[]{
            InvocationTargetException.class,
            LoggerFactory.class,
            Logger.class,
            java.nio.ByteBuffer.class,
            java.nio.IntBuffer.class,
            // This class only operates on Class<?> or Object instances,
            // effectively adding a way to access arrays without knowing their type
            // beforehand. It's safe despite being in java.lang.reflect.
            java.lang.reflect.Array.class,
            java.io.DataInput.class,
            java.io.DataOutput.class,
            java.io.EOFException.class,
            java.io.FileNotFoundException.class,
            java.io.IOException.class,
            java.io.UTFDataFormatException.class,
            /* All sorts of readers */
            java.io.Reader.class,
            java.io.BufferedReader.class,
            java.io.FilterReader.class,
            java.io.InputStreamReader.class,
            java.io.PipedReader.class,
            java.io.StringReader.class,
            /* All sorts of writers */
            java.io.Writer.class,
            java.io.BufferedWriter.class,
            java.io.FilterWriter.class,
            java.io.OutputStreamWriter.class,
            java.io.PipedWriter.class,
            java.io.StringWriter.class,
            /* All sorts of input streams */
            java.io.InputStream.class,
            java.io.BufferedInputStream.class,
            java.io.ByteArrayInputStream.class,
            java.io.DataInputStream.class,
            java.io.FilterInputStream.class,
            java.io.PipedInputStream.class,
            java.io.PushbackInputStream.class,
            /* All sorts of output streams */
            java.io.OutputStream.class,
            java.io.BufferedOutputStream.class,
            java.io.ByteArrayOutputStream.class,
            java.io.DataOutputStream.class,
            java.io.FilterOutputStream.class,
            java.io.PipedOutputStream.class,
            /* Gestalt entity-system classes */
            org.terasology.gestalt.entitysystem.component.Component.class,
            org.terasology.gestalt.entitysystem.component.EmptyComponent.class,
            org.terasology.gestalt.entitysystem.event.Event.class,
            org.terasology.gestalt.entitysystem.entity.EntityRef.class,
            org.terasology.gestalt.entitysystem.entity.EntityIterator.class,
            org.terasology.gestalt.entitysystem.entity.EntityManager.class,
            org.terasology.gestalt.entitysystem.event.EventResult.class,
            org.terasology.gestalt.entitysystem.event.ReceiveEvent.class,
            org.terasology.gestalt.entitysystem.prefab.GeneratedFromRecipeComponent.class,
            /* Gestalt DI classes */
            org.terasology.context.AbstractBeanDefinition.class,
            org.terasology.context.BeanResolution.class,
            org.terasology.context.Argument.class,
            org.terasology.context.DefaultArgument.class,
            org.terasology.context.AnnotationMetadata.class,
            org.terasology.context.DefaultAnnotationMetadata.class,
            org.terasology.context.AnnotationValue.class,
            org.terasology.context.DefaultAnnotationValue.class,
            org.terasology.context.annotation.Index.class,
            org.terasology.context.annotation.IndexInherited.class,
            org.terasology.context.annotation.RegisterSystem.class,
            org.terasology.context.annotation.UsedByGeneratedCode.class,
            org.terasology.context.annotation.Service.class,
            org.terasology.context.annotation.Scoped.class,
            org.terasology.context.annotation.Transient.class,
            org.terasology.gestalt.di.exceptions.DependencyResolutionException.class,
            org.terasology.context.exception.DependencyInjectionException.class,
            javax.inject.Inject.class,
            org.terasology.gestalt.entitysystem.prefab.Prefab.class,
            org.terasology.gestalt.entitysystem.prefab.GeneratedFromRecipeComponent.class,
            /* NUI classes */
            org.terasology.input.device.InputDevice.class,
            org.terasology.input.device.KeyboardDevice.class,
            org.terasology.input.device.MouseDevice.class,
            org.terasology.reflection.MappedContainer.class,
            org.terasology.reflection.TypeInfo.class
    };

    protected static ModuleEnvironment environment;
    private final ModuleFactory moduleFactory;
    private final ModulePathScanner scanner;
    private final BeanContext beanContext;
    private final FacadeModuleConfig moduleConfig;
    protected ModuleRegistry registry;
    protected Module engineModule;
    private Set<Module> builtInModules;

    @Inject
    public ModuleManager(BeanContext beanContext, ModuleFactory moduleFactory, ModuleRegistry moduleRegistry,
                         ModulePathScanner scanner, FacadeModuleConfig moduleConfig) {
        this.moduleFactory = moduleFactory;
        this.registry = moduleRegistry;
        this.scanner = scanner;
        this.beanContext = beanContext;
        this.moduleConfig = moduleConfig;
    }

    public void init() throws Exception {
        try {
            engineModule = moduleConfig.createEngineModule();
            Module nuiModule = moduleFactory.createPackageModule(new ModuleMetadata(new Name("nui"), new Version("2.0.0")),"org.terasology.nui");

            // scan for all standard modules
            File modulesRoot = moduleConfig.getModulesPath();
            scanner.scan(registry, modulesRoot);

            builtInModules = Sets.newHashSet();
            builtInModules.add(engineModule);
            builtInModules.add(nuiModule);
            registry.addAll(builtInModules);

            Set<Module> requiredModules = Sets.newHashSet();
            requiredModules.addAll(registry);

            loadEnvironment(requiredModules);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void loadEnvironment(Set<Module> modules) {
        modules.addAll(builtInModules);

        StandardPermissionProviderFactory permissionFactory = new StandardPermissionProviderFactory();
        for (String api : API_WHITELIST) {
            permissionFactory.getBasePermissionSet().addAPIPackage(api);
        }
        for (Class<?> apiClass : CLASS_WHITELIST) {
            permissionFactory.getBasePermissionSet().addAPIClass(apiClass);
        }

        for (Class<?> apiClass : moduleConfig.getAPIClasses()) {
            permissionFactory.getBasePermissionSet().addAPIClass(apiClass);
        }

        // The JSON serializers need to reflect classes to discover what exists
        permissionFactory.getBasePermissionSet().grantPermission("com.google.gson", ReflectPermission.class);
        permissionFactory.getBasePermissionSet().grantPermission("com.google.gson.internal", ReflectPermission.class);
        permissionFactory.getBasePermissionSet().grantPermission("com.google.gson", RuntimePermission.class);
        permissionFactory.getBasePermissionSet().grantPermission("com.google.gson.internal", RuntimePermission.class);

        APIScanner scanner = new APIScanner(permissionFactory);
        for(Module module: modules){
            scanner.scan(module.getClassIndex());
        }

        if (moduleConfig.useSecurityManager()) {
            Policy.setPolicy(new ModuleSecurityPolicy());
            System.setSecurityManager(new ModuleSecurityManager());
        }

        environment = new ModuleEnvironment(beanContext, modules, permissionFactory, moduleConfig.getClassLoaderSupplier());
    }

    public ModuleEnvironment getEnvironment() {
        return environment;
    }

    public Set<Module> getBuiltInModules() {
        return builtInModules;
    }

    //TODO: REMOVE THIS
    public static ModuleEnvironment getEnvironmentStatic() {
        return environment;
    }

    public void printAvailableModules() {
        for (Module module : registry) {
            logger.info("Module Discovered: {}", module);
        }
    }

    public ModuleRegistry getRegistry() {
        return registry;
    }

    public void dispose() {
        environment.close();
    }

    @Override
    public void close() throws Exception {
        this.dispose();
    }
}
