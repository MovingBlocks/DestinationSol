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
package org.destinationsol.game.planet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.Const;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.drawables.SpriteManager;
import org.destinationsol.game.faction.Faction;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.OrbiterDestProvider;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.input.StillGuard;
import org.destinationsol.game.item.TradeConfig;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetObjectsBuilder {
    private static final float TOP_TILE_SZ = 2f;

    private static final float MAX_CLOUD_PIECE_SZ = 1.5f;
    private static final float MAX_CLOUD_PIECE_ROT_SPD = 5f;
    private static final int MAX_CLOUD_PIECE_COUNT = 30;
    private static final float MAX_CLOUD_PIECE_DIST_SHIFT = 1f;
    private static final float MAX_CLOUD_LINEAR_SPD = .1f;
    private static final float AVG_CLOUD_LINEAR_WIDTH = 3f;

    private static final float DECO_PACK_SZ = 5f;
    private static final float DECO_PACK_ANGULAR_WIDTH = 360 * DECO_PACK_SZ / (2 * MathUtils.PI * Const.MAX_GROUND_HEIGHT);

    private static final Logger logger = LoggerFactory.getLogger(PlanetObjectsBuilder.class);

    private ArrayList<FarShip> groundShips = new ArrayList<>();

    public float createPlanetObjs(SolGame game, Planet planet) {
        if (DebugOptions.NO_OBJS) {
            return 0;
        }
        float minRadius = createGround(game, planet);

        CloudBuilder cloudBuilder = planet.getConfig().cloudBuilder;
        createClouds(game, planet, cloudBuilder);
        createDecorations(game, planet);
        if (planet.getConfig().skyConfig != null) {
            Sky sky = new Sky(game, planet);
            game.getObjectManager().addObjDelayed(sky);
        }
        createShips(game, planet);
        return minRadius;
    }

    private void createShips(SolGame game, Planet planet) {
        ConsumedAngles takenAngles = new ConsumedAngles();

        //Create a station on the Planet
        buildStation(game, planet, takenAngles);

        float groundHeight = planet.getGroundHeight();
        //Place ground enemies
        PlanetConfig config = planet.getConfig();
        buildGroundEnemies(game, planet, takenAngles, groundHeight, config);

        for (FarShip ship : groundShips) {
            game.getObjectManager().addFarObjNow(ship);
        }

        //Place enemies in the air
        buildOrbitEnemies(game, planet, groundHeight, config.lowOrbitalEnemiesBuilder);
        buildOrbitEnemies(game, planet, groundHeight, config.highOrbitalEnemiesBuilder);
    }

    private void buildStation(SolGame game, Planet planet, ConsumedAngles takenAngles) {
        ShipConfig stationConfig = planet.getConfig().stationConfig;
        if (stationConfig != null) {
            buildGroundShip(game, planet, stationConfig, planet.getConfig().tradeConfig, game.getFactionMan().getBuilderForHull(stationConfig.hull), takenAngles, "Station");
        }
    }

    private void buildGroundEnemies(SolGame game, Planet planet, ConsumedAngles takenAngles, float groundHeight, PlanetConfig config) {
        for (ShipConfig groundEnemy : config.groundEnemies) {
            int count = (int) (groundEnemy.density * groundHeight);
            for (int i = 0; i < count; i++) {
                buildGroundShip(game, planet, groundEnemy, null, game.getFactionMan().getBuilderForHull(groundEnemy.hull), takenAngles, null);
            }
        }
    }

    private void buildOrbitEnemies(SolGame game, Planet planet, float groundHeight, OrbitalEnemiesBuilder orbitalEnemiesBuilder) {
        if (orbitalEnemiesBuilder.getOrbitalEnemies().isEmpty()) {
            return;
        }

        HashMap<ShipConfig, Integer> counts = new HashMap<>();
        int totalCount = 0;
        for (ShipConfig oe : orbitalEnemiesBuilder.getOrbitalEnemies()) {
            int count = (int) (orbitalEnemiesBuilder.getAtmospherePercentage() * oe.density * groundHeight * Const.ATM_HEIGHT);
            counts.put(oe, count);
            totalCount += count;
        }

        float stepPercentage = orbitalEnemiesBuilder.getAtmospherePercentage() / totalCount;
        float heightPercentage = orbitalEnemiesBuilder.getOffsetPercentage();

        for (ShipConfig shipConfig : orbitalEnemiesBuilder.getOrbitalEnemies()) {
            int count = counts.get(shipConfig);
            for (int i = 0; i < count; i++) {
                FarShip enemy = buildOrbitEnemy(game, planet, heightPercentage, shipConfig, orbitalEnemiesBuilder.getDetectionDistance());
                game.getObjectManager().addFarObjNow(enemy);
                heightPercentage += stepPercentage;
            }
        }
    }

    private float createGround(SolGame game, Planet planet) {
        // helper values
        float maxRadius = planet.getGroundHeight() - TOP_TILE_SZ / 2;
        int columns = (int) (2 * MathUtils.PI * maxRadius / TOP_TILE_SZ);
        if (columns <= 0) {
            throw new AssertionError("Error creating planet ground!");
        }
        int rows = planet.getConfig().rowCount;

        // helper arrays
        float[] radii = new float[rows]; // Plural of radius https://dictionary.cambridge.org/dictionary/english/radius
        float[] tileSizes = new float[rows];
        float currentRadius = maxRadius;
        for (int row = 0; row < rows; row++) {
            float tileSize = 2 * MathUtils.PI * currentRadius / columns;
            radii[row] = currentRadius;
            tileSizes[row] = tileSize;
            currentRadius -= tileSize;
        }
        float minRadius = radii[rows - 1] - tileSizes[rows - 1] / 2;

        Tile[][] tileMap = new GroundBuilder(planet.getConfig(), columns, rows).build();

        // create ground
        for (int row = 0; row < rows; row++) {
            float tileDistance = radii[row];
            float tileSize = tileSizes[row];
            for (int col = 0; col < columns; col++) {
                Tile tile = tileMap[col][row];
                if (tile == null) {
                    continue;
                }
                float toPlanetRelAngle = 360f * col / columns;
                if (tile.points.isEmpty()) {
                    FarTileObject farTileObject = new FarTileObject(planet, toPlanetRelAngle, tileDistance, tileSize, tile);
                    game.getObjectManager().addFarObjNow(farTileObject);
                } else {
                    TileObject tileObject = new TileObjBuilder().build(game, tileSize, toPlanetRelAngle, tileDistance, tile, planet);
                    game.getObjectManager().addObjNow(game, tileObject);
                }
            }
        }

        return minRadius;
    }

    private void createClouds(SolGame game, Planet planet, CloudBuilder cloudBuilder) {
        List<TextureAtlas.AtlasRegion> cloudTextures = cloudBuilder.getCloudTextures();
        if (cloudTextures.isEmpty()) {
            return;
        }
        int cloudCount = SolRandom.randomInt(.7f, (int) (cloudBuilder.getCloudDensity() * Const.ATM_HEIGHT * planet.getGroundHeight()));
        for (int i = 0; i < cloudCount; i++) {
            FarPlanetSprites cloud = createCloud(planet, cloudTextures, cloudBuilder);
            game.getObjectManager().addFarObjNow(cloud);
        }
    }

    private FarPlanetSprites createCloud(Planet planet, List<TextureAtlas.AtlasRegion> cloudTextures, CloudBuilder cloudBuilder) {
        float distancePercentage = SolRandom.randomFloat(cloudBuilder.getAtmosphereStartingPercentage(), cloudBuilder.getAtmosphereEndingPercentage());
        float distance = planet.getGroundHeight() - TOP_TILE_SZ + .9f * Const.ATM_HEIGHT * distancePercentage;
        float angle = SolRandom.randomFloat(180);

        List<Drawable> drawables = new ArrayList<>();
        float sizePercentage = SolRandom.randomFloat(cloudBuilder.getCloudWidthStartingPercentage(), cloudBuilder.getCloudWidthEndingPercentage());
        float linearWidth = sizePercentage * (distancePercentage + .5f) * AVG_CLOUD_LINEAR_WIDTH;
        float maxAngleShift = SolMath.arcToAngle(linearWidth, distance);
        float maxDistanceShift = (1 - distancePercentage) * MAX_CLOUD_PIECE_DIST_SHIFT;

        int pieceCount = (int) (sizePercentage * MAX_CLOUD_PIECE_COUNT);
        for (int i = 0; i < pieceCount; i++) {
            RectSprite cloudSprite = createCloudSprite(cloudTextures, maxAngleShift, maxDistanceShift, distance);
            drawables.add(cloudSprite);
        }
        float rotationSpeed = SolRandom.randomFloat(.1f, 1) * SolMath.arcToAngle(MAX_CLOUD_LINEAR_SPD, distance);

        return new FarPlanetSprites(planet, angle, distance, drawables, rotationSpeed);
    }

    private RectSprite createCloudSprite(List<TextureAtlas.AtlasRegion> cloudTextures, float maxAngleShift, float maxDistanceShift, float baseDistance) {
        TextureAtlas.AtlasRegion texture = new TextureAtlas.AtlasRegion(SolRandom.randomElement(cloudTextures));
        if (SolRandom.test(.5f)) {
            texture.flip(!texture.isFlipX(), !texture.isFlipY());
        }
        float relativeAngleShift = SolRandom.randomFloat(1);
        float distancePercentage = 1 - SolMath.abs(relativeAngleShift);
        float size = .5f * (1 + distancePercentage) * MAX_CLOUD_PIECE_SZ;

        float relativeAngle = SolRandom.randomFloat(30);
        float rotationSpeed = SolRandom.randomFloat(MAX_CLOUD_PIECE_ROT_SPD);
        float angleShift = relativeAngleShift * maxAngleShift;
        float distanceShift = maxDistanceShift == 0 ? 0 : distancePercentage * SolRandom.randomFloat(0, maxDistanceShift);
        float distance = baseDistance + distanceShift;
        Vector2 basePosition = SolMath.getVec(0, -baseDistance);
        Vector2 relativePosition = new Vector2(0, -distance);
        SolMath.rotate(relativePosition, angleShift);
        relativePosition.sub(basePosition);
        SolMath.free(basePosition);

        return SpriteManager.createSprite(texture.name, size, 0, 0, relativePosition, DrawableLevel.CLOUDS, relativeAngle, rotationSpeed, SolColor.WHITE, false);
    }

    private void createDecorations(SolGame game, Planet planet) {
        float groundHeight = planet.getGroundHeight();
        Vector2 planetPos = planet.getPosition();
        float planetAngle = planet.getAngle();
        Map<Vector2, List<Drawable>> collector = new HashMap<>();
        PlanetConfig config = planet.getConfig();
        for (DecoConfig decoConfig : config.deco) {
            addDeco(game, groundHeight, planetPos, collector, decoConfig);
        }

        for (Map.Entry<Vector2, List<Drawable>> entry : collector.entrySet()) {
            Vector2 position = entry.getKey();
            List<Drawable> drawables = entry.getValue();
            float angle = SolMath.angle(planetPos, position) - planetAngle;
            float distance = position.dst(planetPos);
            FarPlanetSprites planetSprites = new FarPlanetSprites(planet, angle, distance, drawables, 0);
            game.getObjectManager().addFarObjNow(planetSprites);
        }
    }

    private void addDeco(SolGame game, float groundHeight, Vector2 planetPos,
                         Map<Vector2, List<Drawable>> collector, DecoConfig decoConfig) {
        World world = game.getObjectManager().getWorld();
        ConsumedAngles consumed = new ConsumedAngles();

        final Vector2 rayCasted = new Vector2();
        RayCastCallback rayCastCallback = (fixture, point, normal, fraction) -> {
            if (!(fixture.getBody().getUserData() instanceof TileObject)) {
                    return -1;
                }
                rayCasted.set(point);
                return fraction;
            };

        int decorationCount = (int) (2 * MathUtils.PI * groundHeight * decoConfig.density);
        for (int i = 0; i < decorationCount; i++) {
            float decorationSize = SolRandom.randomFloat(decoConfig.szMin, decoConfig.szMax);
            float angularHalfWidth = SolMath.angularWidthOfSphere(decorationSize / 2, groundHeight);

            float decorationAngle = 0;
            for (int j = 0; j < 5; j++) {
                decorationAngle = SolRandom.randomFloat(180);
                if (!consumed.isConsumed(decorationAngle, angularHalfWidth)) {
                    consumed.add(decorationAngle, angularHalfWidth);
                    break;
                }
            }

            SolMath.fromAl(rayCasted, decorationAngle, groundHeight);
            rayCasted.add(planetPos);
            world.rayCast(rayCastCallback, rayCasted, planetPos);
            float decorationDistance = rayCasted.dst(planetPos);

            float baseAngle = SolMath.windowCenter(decorationAngle, DECO_PACK_ANGULAR_WIDTH);
            float baseDistance = SolMath.windowCenter(decorationDistance, DECO_PACK_SZ);
            Vector2 basePosition = SolMath.fromAl(baseAngle, baseDistance).add(planetPos);
            Vector2 decoRelativePosition = new Vector2(rayCasted).sub(basePosition);
            SolMath.rotate(decoRelativePosition, -baseAngle - 90);
            float decorationRelativeAngle = decorationAngle - baseAngle;

            TextureAtlas.AtlasRegion decorationTexture = new TextureAtlas.AtlasRegion(SolRandom.randomElement(decoConfig.texs));
            if (decoConfig.allowFlip && SolRandom.test(.5f)) {
                decorationTexture.flip(!decorationTexture.isFlipX(), !decorationTexture.isFlipY());
            }

            RectSprite sprite = SpriteManager.createSprite(decorationTexture.name, decorationSize, decoConfig.orig.x, decoConfig.orig.y, decoRelativePosition, DrawableLevel.DECO, decorationRelativeAngle, 0, SolColor.WHITE, false);
            List<Drawable> drawables = collector.get(basePosition);
            if (drawables == null) {
                drawables = new ArrayList<>();
                collector.put(new Vector2(basePosition), drawables);
            }
            drawables.add(sprite);
            SolMath.free(basePosition);
        }
    }

    private void buildGroundShip(SolGame game, Planet planet, ShipConfig shipConfig, TradeConfig tradeConfig,
                                 Faction faction, ConsumedAngles takenAngles, String mapHint) {
        Vector2 position = game.getPlanetManager().findFlatPlace(game, planet, takenAngles, shipConfig.hull.getApproxRadius());
        boolean goodSpot = true;
        boolean station = shipConfig.hull.getType() == HullConfig.Type.STATION;
        String shipItems = shipConfig.items;
        boolean hasRepairer;
        hasRepairer = game.getHero().getFaction().getRelation(faction) >= 0;
        int money = shipConfig.money;
        float height = position.len();
        float aboveGround;
        if (station) {
            aboveGround = shipConfig.hull.getSize() * .75f - shipConfig.hull.getOrigin().y;
        } else {
            aboveGround = shipConfig.hull.getSize();
        }
        position.scl((height + aboveGround) / height);
        SolMath.toWorld(position, position, planet.getAngle(), planet.getPosition());

        Vector2 distanceToPlanet = SolMath.getVec(planet.getPosition()).sub(position);
        logger.info("Planet: " + planet.getName() + ", Station Vector: " + distanceToPlanet.toString());
        float angle = 0;
        //TODO: Determine why distanceToPlanet is occasionally equal to {NaN, NaN}
        if (!distanceToPlanet.equals(new Vector2(Float.NaN, Float.NaN))) {
            angle = SolMath.angle(distanceToPlanet) - 180;
        } else {
            goodSpot = false;
        }
        if (station) {
            angle += 90;
        }

        if (goodSpot) {
            Vector2 velocity = new Vector2(distanceToPlanet).nor();

            Pilot provider = new AiPilot(new StillGuard(position, game, shipConfig), false, faction, true, mapHint, Const.AI_DET_DIST);
            groundShips.add(game.getShipBuilder().buildNewFar(game, position, velocity, angle, 0, provider, shipItems, shipConfig.hull,
                    null, hasRepairer, money, tradeConfig, true));
        }
        SolMath.free(distanceToPlanet);
    }

    private FarShip buildOrbitEnemy(SolGame game, Planet planet, float heightPercentage, ShipConfig shipConfig, float detectionDistance) {
        float height = planet.getGroundHeight() + heightPercentage * Const.ATM_HEIGHT;
        Vector2 position = new Vector2();
        SolMath.fromAl(position, SolRandom.randomFloat(180), height);
        Vector2 planetPosition = planet.getPosition();
        position.add(planetPosition);
        float speed = SolMath.sqrt(planet.getGravitationConstant() / height);
        boolean clockwise = SolRandom.test(.5f);
        if (!clockwise) {
            speed *= -1;
        }
        Vector2 velocity = new Vector2(0, -speed);
        Vector2 directionToPlanet = SolMath.distVec(position, planetPosition);
        SolMath.rotate(velocity, SolMath.angle(directionToPlanet));
        SolMath.free(directionToPlanet);

        OrbiterDestProvider destProvider = new OrbiterDestProvider(planet, height, clockwise);
        Pilot provider = new AiPilot(destProvider, false, game.getFactionMan().getBuilderForHull(shipConfig.hull), true, null, detectionDistance);

        int money = shipConfig.money;

        return game.getShipBuilder().buildNewFar(game, position, velocity, 0, 0, provider, shipConfig.items, shipConfig.hull,
                null, false, money, null, true);
    }

}
