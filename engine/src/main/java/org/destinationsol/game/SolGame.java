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
package org.destinationsol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import org.destinationsol.Const;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.audio.SpecialSounds;
import org.destinationsol.common.DebugCol;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.di.Qualifier.NewGame;
import org.destinationsol.di.Qualifier.ShipName;
import org.destinationsol.di.Qualifier.Tut;
import org.destinationsol.di.components.SolGameComponent;
import org.destinationsol.game.asteroid.AsteroidBuilder;
import org.destinationsol.game.drawables.DrawableManager;
import org.destinationsol.game.farBg.FarBackgroundManagerOld;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.LootBuilder;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.particle.PartMan;
import org.destinationsol.game.particle.SpecialEffects;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.planet.SolSystem;
import org.destinationsol.game.planet.SunSingleton;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.ship.ShipAbility;
import org.destinationsol.game.ship.ShipBuilder;
import org.destinationsol.game.ship.SloMo;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.mercenary.MercenaryUtils;
import org.destinationsol.ui.DebugCollector;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.TutorialManager;
import org.destinationsol.ui.UiDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SolGame {

    private static Logger logger = LoggerFactory.getLogger(SolGame.class);

    private float timeStep;
    private float time;
    private boolean paused;
    private float timeFactor;
    private RespawnState respawnState;
    private Hero hero;

    Set<UpdateAwareSystem> updateSystems;
    Set<UpdateAwareSystem> onPausedUpdateSystems;

    SolGameComponent component;
    String shipName;
    boolean tutorial;
    boolean isNewGame;

    public SolGame(SolGameComponent gameComponent,
                    String shipName,
                    boolean tut,
                    boolean isNewGame) {
        this.component = gameComponent;
        this.shipName = shipName;
        this.tutorial = tut;
        this.isNewGame = isNewGame;
    }

    public void initilize(){
        updateSystems = component.updateSystems();
        onPausedUpdateSystems = component.onPausedUpdateSystems();

        timeFactor = 1;

        // from this point we're ready!
        respawnState = new RespawnState();
        component.planetManager().fill(new SolNames(), component.worldConfig().numberOfSystems);
        createGame(shipName, isNewGame);
        if (!isNewGame) {
            createAndSpawnMercenariesFromSave();
        }
        SolMath.checkVectorsTaken(null);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                saveShip();
//                Console.getInstance().println("Game saved");
            }
        }, 0, 30);
    }


    private void createGame(String shipName, boolean shouldSpawnOnGalaxySpawnPosition) {
        /*
         * shipName will be null on respawn and continue, meaning the old ship will be loaded.
         * If shipName is not null then a new ship has to be created.
         */
        boolean isNewShip = shipName != null;
        ShipConfig shipConfig = readShipFromConfigOrLoadFromSaveIfNull(shipName, isNewShip);
        if (!respawnState.isPlayerRespawned()) {
            component.galaxyFiller().fill(this,component.hullConfigManager(), component.itemManager(), shipConfig.hull.getInternalName().split(":")[0]);
        }
        hero = new PlayerCreator().createPlayer(shipConfig,
                shouldSpawnOnGalaxySpawnPosition,
                respawnState,
                this,
                component.gameOptions().controlType == GameOptions.ControlType.MOUSE,
                isNewShip);
    }

    private ShipConfig readShipFromConfigOrLoadFromSaveIfNull(String shipName, boolean isNewShip) {
        if (isNewShip) {
            return ShipConfig.load(component.hullConfigManager(), shipName, component.itemManager());
        } else {
            return SaveManager.readShip(component.hullConfigManager(), component.itemManager());
        }
    }

    private void createAndSpawnMercenariesFromSave() {
        List<MercItem> mercenaryItems = new MercenarySaveLoader().loadMercenariesFromSave(component.hullConfigManager(),component.itemManager());
        for (MercItem mercenaryItem : mercenaryItems) {
            MercenaryUtils.createMerc(this, hero, mercenaryItem);
        }
    }

    public void onGameEnd() {
        // If the hero tries to exit while dead, respawn them first, then save
        if (hero.isDead()) {
            respawn();
        }
        saveShip();
        saveWorld();
        component.objectManager().dispose();
    }

    /**
     * Saves the world's seed so we can regenerate the same world later
     */
    public void saveWorld() {
        if (component.tutorialManager().isPresent()) {
            return;
        }

        SaveManager.saveWorld(getPlanetManager().getSystems().size());
    }

    private void saveShip() {
        if (component.tutorialManager().isPresent()) {
            return;
        }

        HullConfig hull;
        float money;
        List<SolItem> items;

        if (hero.isAlive()) {
            hull = hero.isTranscendent() ? hero.getTranscendentHero().getShip().getHullConfig() : hero.getHull().config;
            money = hero.getMoney();
            items = new ArrayList<>();
            for (List<SolItem> group : hero.getItemContainer()) {
                for (SolItem i : group) {
                    items.add(0, i);
                }
            }
        } else {
            hull = respawnState.getRespawnHull();
            money = respawnState.getRespawnMoney();
            items = respawnState.getRespawnItems();
        }

        SaveManager.writeShips(hull, money, items, hero, component.hullConfigManager());
    }

    public GameScreens getScreens() {
        return component.gameScreens();
    }

    public void update() {
        if (paused) {
            onPausedUpdateSystems.forEach(system -> system.update(this, timeStep));
        } else {
            updateTime();
            updateSystems.forEach(system -> system.update(this, timeStep));
        }
    }

    private void updateTime() {
        scaleTimeStep();
        time += timeStep;
    }

    private void scaleTimeStep() {
        timeFactor = DebugOptions.GAME_SPEED_MULTIPLIER;
        if (hero.isAlive() && hero.isNonTranscendent()) {
            ShipAbility ability = hero.getAbility();
            if (ability instanceof SloMo) {
                float factor = ((SloMo) ability).getFactor();
                timeFactor *= factor;
            }
        }
        timeStep = Const.REAL_TIME_STEP * timeFactor;
    }

    public void draw() {
        component.drawableManager().draw(this);
    }

    public void drawDebug(GameDrawer drawer) {
        if (DebugOptions.GRID_SZ > 0) {
            component.gridDrawer().draw(drawer, component.camera(), DebugOptions.GRID_SZ, drawer.debugWhiteTexture);
        }
        component.planetManager().drawDebug(drawer, this);
        component.objectManager().drawDebug(drawer, this);
        if (DebugOptions.ZOOM_OVERRIDE != 0) {
            component.camera().drawDebug(drawer);
        }
        drawDebugPoint(drawer, DebugOptions.DEBUG_POINT, DebugCol.POINT);
        drawDebugPoint(drawer, DebugOptions.DEBUG_POINT2, DebugCol.POINT2);
        drawDebugPoint(drawer, DebugOptions.DEBUG_POINT3, DebugCol.POINT3);
    }

    private void drawDebugPoint(GameDrawer drawer, Vector2 dp, Color col) {
        if (dp.x != 0 || dp.y != 0) {
            float sz = component.camera().getRealLineWidth() * 5;
            drawer.draw(drawer.debugWhiteTexture, sz, sz, sz / 2, sz / 2, dp.x, dp.y, 0, col);
        }
    }

    public float getTimeStep() {
        return timeStep;
    }

    public SolCam getCam() {
        return component.camera();
    }

    public DrawableManager getDrawableManager() {
        return component.drawableManager();
    }

    public ObjectManager getObjectManager() {
        return component.objectManager();
    }

    public PlanetManager getPlanetManager() {
        return component.planetManager();
    }

    public PartMan getPartMan() {
        return component.partMan();
    }

    public AsteroidBuilder getAsteroidBuilder() {
        return component.asteroidBuilder();
    }

    public LootBuilder getLootBuilder() {
        return component.lootBuilder();
    }

    public Hero getHero() {
        return hero;
    }

    public ShipBuilder getShipBuilder() {
        return component.shipBuilder();
    }

    public ItemManager getItemMan() {
        return component.itemManager();
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        DebugCollector.warn(this.paused ? "game paused" : "game resumed");
    }

    public void respawn() {
        respawnState.setPlayerRespawned(true);
        if (hero.isAlive()) {
            setRespawnState();
            component.objectManager().removeObjDelayed(hero.getShip());
        }
        createGame(null, true);
    }

    public FactionManager getFactionMan() {
        return component.factionManager();
    }

    public boolean isPlaceEmpty(Vector2 position, boolean considerPlanets) {
        if (considerPlanets) {
            Planet np = component.planetManager().getNearestPlanet(position);
            boolean inPlanet = np.getPosition().dst(position) < np.getFullHeight();

            if (inPlanet) {
                return false;
            }
        }

        SolSystem ns = component.planetManager().getNearestSystem(position);
        if (ns.getPosition().dst(position) < SunSingleton.SUN_HOT_RAD) {
            return false;
        }

        List<SolObject> objs = component.objectManager().getObjects();
        for (SolObject o : objs) {
            if (!o.hasBody()) {
                continue;
            }

            if (position.dst(o.getPosition()) < component.objectManager().getRadius(o)) {
                return false;
            }
        }

        for (FarObjData fod : component.objectManager().getFarObjs()) {
            FarObject o = fod.fo;

            if (!o.hasBody()) {
                continue;
            }

            if (position.dst(o.getPosition()) < o.getRadius()) {
                return false;
            }
        }

        return true;
    }

    public MapDrawer getMapDrawer() {
        return component.mapDrawer();
    }

    public ShardBuilder getShardBuilder() {
        return component.shardBuilder();
    }

    public FarBackgroundManagerOld getFarBackgroundgManagerOld() {
        return component.farBackgroundManagerOld();
    }

    public GalaxyFiller getGalaxyFiller() {
        return component.galaxyFiller();
    }

    public StarPort.Builder getStarPortBuilder() {
        return component.starPortBuilder();
    }

    public GridDrawer getGridDrawer() {
        return component.gridDrawer();
    }

    public OggSoundManager getSoundManager() {
        return component.soundManager();
    }

    public float getTime() {
        return time;
    }

    public void drawDebugUi(UiDrawer uiDrawer) {
        component.drawableDebugger().draw(uiDrawer);
    }

    public SpecialSounds getSpecialSounds() {
        return component.specialSounds();
    }

    public SpecialEffects getSpecialEffects() {
        return component.specialEffects();
    }

    public GameColors getCols() {
        return component.gameColors();
    }

    public float getTimeFactor() {
        return timeFactor;
    }

    public BeaconHandler getBeaconHandler() {
        return component.beaconHandler();
    }

    public MountDetectDrawer getMountDetectDrawer() {
        return component.mountDetectDrawer();
    }

    public TutorialManager getTutMan() {
        return component.tutorialManager().get();
    }

    public SolInputManager inputManager(){
        return component.inputManager();
    }

    public SolApplication getSolApplication(){
        return component.solApplication();
    }

    public void setRespawnState() {
        respawnState.setRespawnMoney(.75f * hero.getMoney());
        hero.setMoney(respawnState.getRespawnMoney()); // to update the display while the camera waits for respawn if the player died
        respawnState.setRespawnHull(hero.isNonTranscendent() ? hero.getHull().getHullConfig() : hero.getTranscendentHero().getShip().getHullConfig());
        respawnState.getRespawnItems().clear();
        respawnState.setPlayerRespawned(true);
        for (List<SolItem> group : hero.getItemContainer()) {
            for (SolItem item : group) {
                boolean equipped = hero.isTranscendent() || hero.maybeUnequip(this, item, false);
                if (equipped || SolRandom.test(.75f)) {
                    respawnState.getRespawnItems().add(item);
                }
            }
        }
    }

}
