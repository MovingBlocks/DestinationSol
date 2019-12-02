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

package org.destinationsol.game.chunk;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.Faction;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.asteroid.FarAsteroid;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.FarDrawable;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.MoveDestProvider;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.input.StillGuard;
import org.destinationsol.game.maze.Maze;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.planet.SolSystem;
import org.destinationsol.game.planet.SysConfig;
import org.destinationsol.game.planet.SystemBelt;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.ArrayList;
import java.util.Optional;

public class ChunkFiller {
    private static final float DUST_DENSITY = .2f;
    private static final float ASTEROID_DENSITY = .008f;
    private static final float MIN_SYS_A_SZ = .5f;
    private static final float MAX_SYS_A_SZ = 1.2f;
    private static final float MIN_BELT_A_SZ = .4f;
    private static final float MAX_BELT_A_SZ = 2.4f;
    private static final float JUNK_MAX_SZ = .3f;
    private static final float JUNK_MAX_ROT_SPD = 45f;
    private static final float JUNK_MAX_SPD_LEN = .3f;
    private static final float FAR_JUNK_MAX_SZ = 2f;
    private static final float FAR_JUNK_MAX_ROT_SPD = 10f;
    private static final float ENEMY_MAX_SPD = .3f;
    private static final float ENEMY_MAX_ROT_SPD = 15f;
    private static final float DUST_SZ = .02f;
    private static final float MAX_A_SPD = .2f;
    private static final float BELT_A_DENSITY = .04f;
    private static final float MAZE_ZONE_BORDER = 20;
    private final TextureAtlas.AtlasRegion dustTexture;

    public ChunkFiller() {
        dustTexture = Assets.getAtlasRegion("engine:spaceDecorationDust");
    }

    /**
     * Fill the background of a given chunk with floating junk.
     *
     * @param game    The {@link SolGame} instance to work with
     * @param chunk   The coordinates of the chunk
     * @param removeController
     * @param fillFarBackground   Determines which of the background layers should be filled. <code>true</code> fills the layers furthest away, <code>false</code> fills the closer one.
     */
    public void fill(SolGame game, Vector2 chunk, RemoveController removeController, boolean fillFarBackground) {
        if (DebugOptions.NO_OBJS) {
            return;
        }

        // Determine the center of the chunk by multiplying the chunk coordinates with the chunk size and adding half a chunk's size
        Vector2 chunkCenter = new Vector2(chunk);
        chunkCenter.scl(Const.CHUNK_SIZE);
        chunkCenter.add(Const.CHUNK_SIZE / 2, Const.CHUNK_SIZE / 2);

        // Define the density multiplier for different layers of junk in the far background
        // Dirty hack - since we want to edit this in getConfig(), it needs to be an Object
        float[] densityMultiplier = {1};

        // Get the environment configuration
        Optional<SpaceEnvConfig> config = getConfig(game, chunkCenter, densityMultiplier, removeController, fillFarBackground);

        if (fillFarBackground) {
            config.ifPresent(spaceEnvConfig -> {
                fillFarJunk(game, chunkCenter, removeController, DrawableLevel.FAR_DECO_3, spaceEnvConfig, densityMultiplier[0]);
                fillFarJunk(game, chunkCenter, removeController, DrawableLevel.FAR_DECO_1, spaceEnvConfig, densityMultiplier[0]);
                fillFarJunk(game, chunkCenter, removeController, DrawableLevel.FAR_DECO_2, spaceEnvConfig, densityMultiplier[0]);
            });
        } else {
            fillDust(game, chunkCenter, removeController);
            config.ifPresent(spaceEnvConfig -> fillJunk(game, removeController, spaceEnvConfig, chunkCenter));
        }
    }

    private Optional<SpaceEnvConfig> getConfig(SolGame game, Vector2 chunkCenter, float[] densityMultiplier,
                                     RemoveController removeController, boolean fillFarBackground) {
        PlanetManager planetManager = game.getPlanetManager();
        SolSystem system = planetManager.getNearestSystem(chunkCenter);
        float distanceToSystem = system.getPosition().dst(chunkCenter);
        if (distanceToSystem < system.getRadius()) {
            if (distanceToSystem < Const.SUN_RADIUS) {
                return Optional.empty();
            }
            for (SystemBelt belt : system.getBelts()) {
                if (belt.contains(chunkCenter)) {
                    if (!fillFarBackground) {
                        fillAsteroids(game, removeController, true, chunkCenter);
                    }
                    SysConfig beltConfig = belt.getConfig();
                    for (ShipConfig enemyConfig : beltConfig.tempEnemies) {
                        if (!fillFarBackground) {
                            fillEnemies(game, removeController, enemyConfig, chunkCenter);
                        }
                    }
                    return Optional.of(beltConfig.envConfig);
                }
            }
            float percentage = distanceToSystem / system.getRadius() * 2;
            if (percentage > 1) {
                percentage = 2 - percentage;
            }
            densityMultiplier[0] = percentage;
            if (!fillFarBackground) {
                Planet planet = planetManager.getNearestPlanet(chunkCenter);
                float distanceToPlanet = planet.getPosition().dst(chunkCenter);
                boolean isPlanetNear = distanceToPlanet < planet.getFullHeight() + Const.CHUNK_SIZE;
                if (!isPlanetNear) {
                    fillForSys(game, chunkCenter, removeController, system);
                }
            }
            return Optional.of(system.getConfig().envConfig);
        }
        Maze maze = planetManager.getNearestMaze(chunkCenter);
        float distanceToMaze = maze.getPos().dst(chunkCenter);
        float zoneRadius = maze.getRadius() + MAZE_ZONE_BORDER;
        if (distanceToMaze < zoneRadius) {
            densityMultiplier[0] = 1 - distanceToMaze / zoneRadius;
            return Optional.of(maze.getConfig().envConfig);
        }
        return Optional.empty();
    }

    private void fillForSys(SolGame game, Vector2 chunkCenter, RemoveController removeController, SolSystem system) {
        SysConfig config = system.getConfig();
        Vector2 mainStationPosition = game.getGalaxyFiller().getMainStationPosition();
        Vector2 startPosition = mainStationPosition == null ? new Vector2() : mainStationPosition;
        float distanceToStartPosition = chunkCenter.dst(startPosition);
        if (Const.CHUNK_SIZE < distanceToStartPosition) {
            fillAsteroids(game, removeController, false, chunkCenter);
            ArrayList<ShipConfig> enemies = system.getPosition().dst(chunkCenter) < system.getInnerRadius() ? config.innerTempEnemies : config.tempEnemies;
            for (ShipConfig enemyConfig : enemies) {
                fillEnemies(game, removeController, enemyConfig, chunkCenter);
            }
        }
    }

    private void fillEnemies(SolGame game, RemoveController removeController, ShipConfig enemyConfig, Vector2 chunkCenter) {
        int enemyCount = getEntityCount(enemyConfig.density);
        if (enemyCount == 0) {
            return;
        }

        for (int i = 0; i < enemyCount; i++) {
            Optional<Vector2> enemyPosition = getFreeRndPos(game, chunkCenter);
            enemyPosition.ifPresent(enemyPos -> {
                FarShip ship = buildSpaceEnemy(game, enemyPos, removeController, enemyConfig);
                game.getObjectManager().addFarObjNow(ship);
            });
        }
    }

    private FarShip buildSpaceEnemy(SolGame game, Vector2 position, RemoveController remover, ShipConfig enemyConf) {
        Vector2 velocity = new Vector2();
        SolMath.fromAl(velocity, SolRandom.randomFloat(180), SolRandom.randomFloat(0, ENEMY_MAX_SPD));
        float rotationSpeed = SolRandom.randomFloat(ENEMY_MAX_ROT_SPD);
        MoveDestProvider dp = new StillGuard(position, game, enemyConf);
        Pilot provider = new AiPilot(dp, false, Faction.EHAR, true, null, Const.AI_DET_DIST);
        HullConfig config = enemyConf.hull;
        int money = enemyConf.money;
        float angle = SolRandom.randomFloat(180);
        return game.getShipBuilder().buildNewFar(game, position, velocity, angle, rotationSpeed, provider, enemyConf.items, config,
                remover, false, money, null, true);
    }

    private void fillAsteroids(SolGame game, RemoveController remover, boolean forBelt, Vector2 chunkCenter) {
        float density = forBelt ? BELT_A_DENSITY : ASTEROID_DENSITY;
        int count = getEntityCount(density);
        if (count == 0) {
            return;
        }

        for (int i = 0; i < count; i++) {
            Optional<Vector2> asteroidPosition = getFreeRndPos(game, chunkCenter);
            asteroidPosition.ifPresent(asteroidPos -> {
                float minSz = forBelt ? MIN_BELT_A_SZ : MIN_SYS_A_SZ;
                float maxSz = forBelt ? MAX_BELT_A_SZ : MAX_SYS_A_SZ;
                float sz = SolRandom.randomFloat(minSz, maxSz);
                Vector2 velocity = new Vector2();
                SolMath.fromAl(velocity, SolRandom.randomFloat(180), MAX_A_SPD);

                FarAsteroid a = game.getAsteroidBuilder().buildNewFar(asteroidPos, velocity, sz, remover);
                game.getObjectManager().addFarObjNow(a);
            });
        }
    }

    /**
     * Add a bunch of a certain type of junk to the background layers furthest away.
     * <p/>
     * This type of junk does not move on its own, it merely changes position as the camera moves, simulating different
     * depths relative to the camera.
     *
     * @param game       The {@link SolGame} instance to work with
     * @param chunkCenter   The center of the chunk
     * @param remover
     * @param drawableLevel   The depth of the junk
     * @param conf       The environment configuration
     * @param densityMul A density multiplier. This will be multiplied with the density defined in the environment configuration
     */
    private void fillFarJunk(SolGame game, Vector2 chunkCenter, RemoveController remover, DrawableLevel drawableLevel,
                             SpaceEnvConfig conf, float densityMul) {
        int count = getEntityCount(conf.farJunkDensity * densityMul);
        if (count == 0) {
            return;
        }

        ArrayList<Drawable> drawables = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            // Select a random far junk texture
            TextureAtlas.AtlasRegion tex = new TextureAtlas.AtlasRegion(SolRandom.randomElement(conf.farJunkTextures));
            // Flip atlas for every other piece of junk
            if (SolRandom.test(.5f)) {
                tex.flip(!tex.isFlipX(), !tex.isFlipY());
            }
            // Choose a random size (within a range)
            float sz = SolRandom.randomFloat(.3f, 1) * FAR_JUNK_MAX_SZ;
            // Apply a random rotation speed
            float rotationSpeed = SolRandom.randomFloat(FAR_JUNK_MAX_ROT_SPD);
            // Select a random position in the chunk centered around chunkCenter, relative to the position of the chunk.
            Vector2 junkPos = getRndPos(chunkCenter);
            junkPos.sub(chunkCenter);

            // Create the resulting sprite and add it to the list
            RectSprite s = new RectSprite(tex, sz, 0, 0, junkPos, drawableLevel, SolRandom.randomFloat(180), rotationSpeed, SolColor.DDG, false);
            drawables.add(s);
        }

        // Create a common FarDrawable instance for the pieces of junk and only allow the junk to be drawn when it's not hidden by a planet
        FarDrawable so = new FarDrawable(drawables, new Vector2(chunkCenter), new Vector2(), remover, true);
        // Add the collection of objects to the object manager
        game.getObjectManager().addFarObjNow(so);
    }

    /**
     * Add a bunch of a certain type of junk to the background layer closest to the front.
     * <p/>
     * This type of junk moves at the same speed as the camera (similar to the dust) but additionally has its own floating
     * direction and angle for every individual piece of junk.
     *
     * @param game     The {@link SolGame} instance to work with
     * @param remover
     * @param conf     The environment configuration
     * @param chunkCenter The center of the chunk
     */
    private void fillJunk(SolGame game, RemoveController remover, SpaceEnvConfig conf, Vector2 chunkCenter) {
        int count = getEntityCount(conf.junkDensity);
        if (count == 0) {
            return;
        }

        for (int i = 0; i < count; i++) {
            // Select a random position in the chunk centered around chunkCenter, relative to the entire map.
            Vector2 junkPos = getRndPos(chunkCenter);

            // Select a random junk atlas
            TextureAtlas.AtlasRegion tex = new TextureAtlas.AtlasRegion(SolRandom.randomElement(conf.junkTextures));
            // Flip atlas for every other piece of junk
            if (SolRandom.test(.5f)) {
                tex.flip(!tex.isFlipX(), !tex.isFlipY());
            }
            // Choose a random size (within a range)
            float sz = SolRandom.randomFloat(.3f, 1) * JUNK_MAX_SZ;
            // Apply a random rotation speed
            float rotationSpeed = SolRandom.randomFloat(JUNK_MAX_ROT_SPD);

            // Create the resulting sprite and add it to the list as the only element
            RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DrawableLevel.DECO, SolRandom.randomFloat(180), rotationSpeed, SolColor.LG, false);
            ArrayList<Drawable> drawables = new ArrayList<>();
            drawables.add(s);

            // Create a FarDrawable instance for this piece of junk and only allow it to be drawn when it's not hidden by a planet
            Vector2 velocity = new Vector2();
            SolMath.fromAl(velocity, SolRandom.randomFloat(180), SolRandom.randomFloat(JUNK_MAX_SPD_LEN));
            FarDrawable so = new FarDrawable(drawables, junkPos, velocity, remover, true);
            // Add the object to the object manager
            game.getObjectManager().addFarObjNow(so);
        }
    }

    /**
     * Add specks of dust to the background layer closest to the front.
     * <p/>
     * Dust is fixed in the world and therefore moves opposite to the cameras movement.
     *
     * @param game     The {@link SolGame} instance to work with
     * @param chunkCenter The center of the chunk
     * @param remover
     */
    private void fillDust(SolGame game, Vector2 chunkCenter, RemoveController remover) {
        ArrayList<Drawable> drawables = new ArrayList<>();
        int count = getEntityCount(DUST_DENSITY);
        if (count == 0) {
            return;
        }

        for (int i = 0; i < count; i++) {
            // Select a random position in the chunk centered around chunkCenter, relative to the position of the chunk.
            Vector2 dustPos = getRndPos(chunkCenter);
            dustPos.sub(chunkCenter);
            // Create the resulting sprite and add it to the list
            RectSprite s = new RectSprite(dustTexture, DUST_SZ, 0, 0, dustPos, DrawableLevel.DECO, 0, 0, SolColor.WHITE, false);
            drawables.add(s);
        }

        // Create a common FarDrawable instance for the specks of dust and only allow the dust to be drawn when it's not hidden by a planet
        FarDrawable so = new FarDrawable(drawables, chunkCenter, new Vector2(), remover, true);
        game.getObjectManager().addFarObjNow(so);
    }

    /**
     * Find a random position in a chunk centered around chunkCenter, relative to the entire map, and make sure it is not yet
     * occupied by another entity.
     * <p/>
     * Up to 100 tries will be made to find an unoccupied position; if by then none has been found, <code>null</code> will be returned.
     *
     * @param game        The {@link SolGame} instance to work with
     * @param chunkCenter The center of a chunk in which a random position should be found
     * @return A random, unoccupied position in a chunk centered around chunkCenter, relative to the entire map, or <code>null</code> if within 100 tries no unoccupied position has been found
     */
    private Optional<Vector2> getFreeRndPos(SolGame game, Vector2 chunkCenter) {
        for (int i = 0; i < 100; i++) {
            Vector2 position = getRndPos(chunkCenter);
            if (game.isPlaceEmpty(position, true)) {
                return Optional.of(position);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns a random position in a chunk centered around chunkCenter, relative to the entire map.
     *
     * The random position is returned in new object.
     *
     * @param chunkCenter The center of a chunk in which a random position should be found
     * @return A random position in a chunk centered around chunkCenter, relative to the entire map, in new object.
     */
    private Vector2 getRndPos(Vector2 chunkCenter) {
        Vector2 position = new Vector2(chunkCenter);
        position.x += SolRandom.randomFloat(Const.CHUNK_SIZE / 2);
        position.y += SolRandom.randomFloat(Const.CHUNK_SIZE / 2);
        return position;
    }

    /**
     * Determine the number of objects per chunk for a given density, based on the chunk size.
     * If the number turns out to be less than 1, 1 will be returned randomly with a probability of the resulting number, otherwise 0.
     *
     * @param density The density of the objects per chunk
     * @return The number of objects for the chunk based on the given density.
     */
    private int getEntityCount(float density) {
        float amt = Const.CHUNK_SIZE * Const.CHUNK_SIZE * density;
        if (amt >= 1) {
            return (int) amt;
        }
        return SolRandom.test(amt) ? 1 : 0;
    }

}
