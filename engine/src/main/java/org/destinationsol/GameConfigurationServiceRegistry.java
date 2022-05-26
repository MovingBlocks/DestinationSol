package org.destinationsol;

import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.WorldConfig;
import org.destinationsol.game.maze.MazeConfigManager;
import org.destinationsol.game.planet.BeltConfigManager;
import org.destinationsol.game.planet.PlanetConfigManager;
import org.destinationsol.game.planet.SolarSystemConfigManager;
import org.destinationsol.world.GalaxyBuilder;
import org.destinationsol.world.generators.LargeSolarSystemGenerator;
import org.destinationsol.world.generators.SolarSystemGeneratorImpl;
import org.terasology.context.Lifetime;
import org.terasology.gestalt.di.ServiceRegistry;

public class GameConfigurationServiceRegistry extends ServiceRegistry {

    public GameConfigurationServiceRegistry(WorldConfig worldConfig) {
        this.with(WorldConfig.class).use(() -> worldConfig).lifetime(Lifetime.Singleton);
        this.with(GalaxyBuilder.class).lifetime(Lifetime.Singleton);
        this.with(LargeSolarSystemGenerator.class).lifetime(Lifetime.Singleton);
        this.with(SolarSystemGeneratorImpl.class).lifetime(Lifetime.Singleton);
        this.with(HullConfigManager.class).lifetime(Lifetime.Singleton);
        this.with(MazeConfigManager.class).lifetime(Lifetime.Singleton);
        this.with(PlanetConfigManager.class).lifetime(Lifetime.Singleton);
        this.with(BeltConfigManager.class).lifetime(Lifetime.Singleton);
        this.with(SolarSystemConfigManager.class).lifetime(Lifetime.Singleton);
    }

}
