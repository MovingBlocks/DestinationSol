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
package org.destinationsol.assets;

import org.destinationsol.assets.music.AndroidOggMusicFileFormat;
import org.destinationsol.assets.music.OggMusic;
import org.destinationsol.assets.music.OggMusicData;
import org.destinationsol.assets.sound.AndroidOggSoundFileFormat;
import org.destinationsol.assets.sound.OggSound;
import org.destinationsol.assets.sound.OggSoundData;
import org.destinationsol.assets.ui.UIFormat;
import org.destinationsol.assets.ui.UISkinFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.gestalt.assets.Asset;
import org.terasology.gestalt.assets.AssetData;
import org.terasology.gestalt.assets.AssetType;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.format.producer.AssetFileDataProducer;
import org.terasology.gestalt.assets.module.ModuleAwareAssetTypeManager;
import org.terasology.gestalt.assets.module.ModuleDependencyResolutionStrategy;
import org.terasology.gestalt.assets.module.ModuleEnvironmentDependencyProvider;
import org.terasology.gestalt.di.BeanContext;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;
import org.terasology.gestalt.entitysystem.component.management.ComponentTypeIndex;
import org.terasology.gestalt.entitysystem.prefab.Prefab;
import org.terasology.gestalt.entitysystem.prefab.PrefabJsonFormat;
import org.terasology.gestalt.module.ModuleEnvironment;
import org.terasology.gestalt.naming.Name;
import org.terasology.nui.UIWidget;
import org.terasology.nui.asset.UIElement;
import org.terasology.nui.reflection.WidgetLibrary;
import org.terasology.nui.skin.UISkinAsset;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Singleton
public class AssetHelper {
    private static final Logger logger = LoggerFactory.getLogger(AssetHelper.class);
    private final BeanContext beanContext;
    private ModuleAwareAssetTypeManager assetTypeManager;

    @Inject
    public AssetHelper(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    public void init(ModuleEnvironment environment,
                     WidgetLibrary widgetLibrary,
                     ModuleAwareAssetTypeManager assetTypeManager,
                     ComponentManager componentManager,
                     boolean isMobile) {
        this.assetTypeManager = assetTypeManager;

        if (isMobile) {
            AssetType<OggSound, OggSoundData> soundType = assetTypeManager.createAssetType(OggSound.class, OggSound::new, "sounds");
            AssetType<OggMusic, OggMusicData> musicType = assetTypeManager.createAssetType(OggMusic.class, OggMusic::new, "music");

            assetTypeManager.getAssetFileDataProducer(soundType).addAssetFormat(new AndroidOggSoundFileFormat());
            assetTypeManager.getAssetFileDataProducer(musicType).addAssetFormat(new AndroidOggMusicFileFormat());
        }

        assetTypeManager.getAssetFileDataProducer(
                        assetTypeManager.createAssetType(Prefab.class, Prefab::new, "prefabs"))
                .addAssetFormat(new PrefabJsonFormat.Builder(
                        new ComponentTypeIndex(environment, new ModuleDependencyResolutionStrategy(
                                new ModuleEnvironmentDependencyProvider(environment))),
                        componentManager, assetTypeManager.getAssetManager()).create());

        // The NUI widgets are loaded here so that they can be found when reading the UI JSON files (in UIFormat.UIWidgetTypeAdapter)
//        ReflectFactory reflectFactory = new ReflectionReflectFactory();
        for (Class<? extends UIWidget> widgetClass : environment.getSubtypesOf(UIWidget.class)) {
            Name moduleName = environment.getModuleProviding(widgetClass);
            widgetLibrary.register(new ResourceUrn(moduleName, new Name(widgetClass.getSimpleName())), widgetClass);
        }

        // TODO inject this
        assetTypeManager.createAssetType(UISkinAsset.class, UISkinAsset::new, "skins");
        ((AssetFileDataProducer) assetTypeManager.getAssetType(UISkinAsset.class).get().getProducers().get(0)).addAssetFormat(new UISkinFormat(widgetLibrary));

        // TODO inject this
        assetTypeManager.createAssetType(UIElement.class, UIElement::new, "ui");
        ((AssetFileDataProducer) assetTypeManager.getAssetType(UIElement.class).get().getProducers().get(0)).addAssetFormat(new UIFormat(widgetLibrary,beanContext));

        assetTypeManager.switchEnvironment(environment);
        Assets.initialize(this);

    }

    public <T extends Asset<U>, U extends AssetData> Optional<T> get(ResourceUrn urn, Class<T> type) {
        return assetTypeManager.getAssetManager().getAsset(urn, type);
    }

    public Set<ResourceUrn> list(Class<? extends Asset<?>> type) {
        return assetTypeManager.getAssetManager().getAvailableAssets(type);
    }

    public Set<ResourceUrn> listAssets(Class<? extends Asset<?>> type, String asset) {
        return assetTypeManager.getAssetManager().resolve(asset, type);
    }

    public Set<ResourceUrn> listAssets(Class<? extends Asset<?>> type, String asset, Name... excluding) {
        Set<ResourceUrn> list = listAssets(type, asset);
        list.removeIf(urn -> {
            for (Name module : excluding) {
                if (urn.getModuleName().equals(module)) {
                    return true;
                }
            }
            return false;
        });
        return list;
    }

    public void switchEnvironment(ModuleEnvironment environment) {
        assetTypeManager.switchEnvironment(environment);
    }

    public void dispose() {
        try {
            assetTypeManager.unloadEnvironment();
            assetTypeManager.close();
        } catch (IOException e) {
            logger.error("Error closing assetTypeManager", e);
        }
    }

    public boolean isAssetLoaded(ResourceUrn urn, Class<? extends Asset<?>> type) {
        return assetTypeManager.getAssetManager().isLoaded(urn, type);
    }
}
