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
package org.destinationsol.engine;

import com.google.common.collect.Sets;
import org.destinationsol.ModuleManager;
import org.destinationsol.assets.audio.OggMusic;
import org.destinationsol.assets.audio.OggMusicFileFormat;
import org.destinationsol.assets.audio.OggSound;
import org.destinationsol.assets.audio.OggSoundFileFormat;
import org.destinationsol.assets.emitters.Emitter;
import org.destinationsol.assets.emitters.EmitterFileFormat;
import org.destinationsol.assets.fonts.Font;
import org.destinationsol.assets.fonts.FontFileFormat;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.JsonFileFormat;
import org.destinationsol.assets.textures.DSTexture;
import org.destinationsol.assets.textures.DSTextureFileFormat;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.context.internal.ContextImpl;
import org.destinationsol.rendering.CanvasRenderer;
import org.destinationsol.rendering.LibGdxCanvas;
import org.terasology.assets.format.producer.AssetFileDataProducer;
import org.terasology.assets.management.AssetTypeManager;
import org.terasology.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.entitysystem.component.CodeGenComponentManager;
import org.terasology.entitysystem.component.ComponentManager;
import org.terasology.entitysystem.core.EntityManager;
import org.terasology.entitysystem.entity.inmemory.InMemoryEntityManager;
import org.terasology.entitysystem.event.impl.DelayedEventSystem;
import org.terasology.entitysystem.event.impl.EventProcessor;
import org.terasology.entitysystem.event.impl.EventProcessorBuilder;
import org.terasology.entitysystem.event.impl.ImmediateEventSystem;
import org.terasology.entitysystem.transaction.TransactionManager;
import org.terasology.module.Module;
import org.terasology.module.ModuleFactory;
import org.terasology.module.ModulePathScanner;
import org.terasology.module.ModuleRegistry;
import org.terasology.module.TableModuleRegistry;
import org.terasology.valuetype.TypeLibrary;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class DefaultEngine implements EngineFactory {

    private final TransactionManager transactionManager;
    private final ComponentManager componentManager;
    private final EventProcessor eventProcessor;

    public DefaultEngine() {
        componentManager = new CodeGenComponentManager(new TypeLibrary());
        transactionManager = new TransactionManager();
        eventProcessor = new EventProcessorBuilder().build();
    }


    @Override
    public CanvasRenderer canvas() {
        return new LibGdxCanvas();
    }

    @Override
    public EntityManager entityManager() {
        return new InMemoryEntityManager(componentManager, transactionManager);
    }

    @Override
    public DelayedEventSystem delayedEventSystem() {
        return new DelayedEventSystem(transactionManager, eventProcessor);
    }

    @Override
    public ImmediateEventSystem immediateEventSystem() {
        return new ImmediateEventSystem(transactionManager, eventProcessor);
    }

    @Override
    public AssetTypeManager assetTypeManager() {
        ModuleAwareAssetTypeManager assetTypeManager = new ModuleAwareAssetTypeManager();
        ((AssetFileDataProducer) assetTypeManager.createAssetType(OggSound.class, OggSound::new, "sounds").getProducers().get(0)).addAssetFormat(new OggSoundFileFormat());
        ((AssetFileDataProducer) assetTypeManager.createAssetType(OggMusic.class, OggMusic::new, "music").getProducers().get(0)).addAssetFormat(new OggMusicFileFormat());
        ((AssetFileDataProducer) assetTypeManager.createAssetType(Font.class, Font::new, "fonts").getProducers().get(0)).addAssetFormat(new FontFileFormat());
        ((AssetFileDataProducer) assetTypeManager.createAssetType(Emitter.class, Emitter::new, "emitters").getProducers().get(0)).addAssetFormat(new EmitterFileFormat());
        ((AssetFileDataProducer) assetTypeManager.createAssetType(Json.class, Json::new, "collisionMeshes", "ships", "items", "configs", "grounds", "mazes", "asteroids").getProducers().get(0)).addAssetFormat(new JsonFileFormat());
        ((AssetFileDataProducer) assetTypeManager.createAssetType(DSTexture.class, DSTexture::new, "textures", "ships", "items", "grounds", "mazes", "asteroids").getProducers().get(0)).addAssetFormat(new DSTextureFileFormat());
        return assetTypeManager;
    }

    @Override
    public Context context() {
        return new ContextImpl();
    }

    @Override
    public Set<Module> modules() throws IOException, URISyntaxException {
        URI engineClasspath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        Module engineModule = new ModuleFactory().createModule(Paths.get(engineClasspath));

        ModuleRegistry registry = new TableModuleRegistry();
        Path modulesRoot;
        if (DebugOptions.DEV_ROOT_PATH != null) {
            modulesRoot = Paths.get(".").resolve("modules");
        } else {
            modulesRoot = Paths.get(".").resolve("..").resolve("modules");
        }
        new ModulePathScanner().scan(registry, modulesRoot);

        Set<Module> requiredModules = Sets.newHashSet();
        requiredModules.add(engineModule);
        requiredModules.addAll(registry);

        return requiredModules;

    }
}
