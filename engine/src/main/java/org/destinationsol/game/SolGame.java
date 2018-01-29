/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.destinationsol.CommonDrawer;
import org.destinationsol.Const;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.DebugCol;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolNullOptionalException;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.asteroid.AsteroidBuilder;
import org.destinationsol.game.chunk.ChunkManager;
import org.destinationsol.game.drawables.DrawableDebugger;
import org.destinationsol.game.drawables.DrawableManager;
import org.destinationsol.game.farBg.FarBackgroundManagerOld;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.BeaconDestProvider;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.input.UiControlledPilot;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.LootBuilder;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.item.TradeConfig;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.particle.ParticleManager;
import org.destinationsol.game.particle.SpecialEffects;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.planet.SolSystem;
import org.destinationsol.game.planet.SunSingleton;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.ShipBuilder;
import org.destinationsol.game.ship.SloMo;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.game.sound.OggSoundManager;
import org.destinationsol.game.sound.SpecialSounds;
import org.destinationsol.mercenary.MercenaryUtils;
import org.destinationsol.ui.DebugCollector;
import org.destinationsol.ui.TutorialManager;
import org.destinationsol.ui.UiDrawer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class SolGame {
    private static Logger logger = LoggerFactory.getLogger(SolGame.class);

    static String MERC_SAVE_FILE = "mercenaries.json";

    private final GameScreens gameScreens;
    private final SolCamera camera;
    private final ObjectManager objectManager;
    private final SolApplication solApplication;
    private final DrawableManager drawableManager;
    private final PlanetManager planetManager;
    private final ChunkManager chunkManager;
    private final ParticleManager particleManager;
    private final AsteroidBuilder asteroidBuilder;
    private final LootBuilder lootBuilder;
    private final ShipBuilder shipBuilder;
    private final HullConfigManager hullConfigManager;
    private final GridDrawer gridDrawer;
    private final FarBackgroundManagerOld farBackgroundManagerOld;
    private final FactionManager factionManager;
    private final MapDrawer mapDrawer;
    private final ShardBuilder shardBuilder;
    private final ItemManager itemManager;
    private final StarPort.Builder starPortBuilder;
    private final OggSoundManager soundManager;
    private final DrawableDebugger drawableDebugger;
    private final SpecialSounds specialSounds;
    private final SpecialEffects specialEffects;
    private final GameColors gameColors;
    private final BeaconHandler beaconHandler;
    private final MountDetectDrawer mountDetectDrawer;
    private final @Nullable TutorialManager tutorialManager;
    private final GalaxyFiller galaxyFiller;
    private final ArrayList<SolItem> respawnItems;
    private Optional<SolShip> hero;
    private String shipName; // Not updated in-game. Can be changed using setter
    private float timeStep;
    private float time;
    private boolean paused;
    private Optional<StarPort.Transcendent> transcendentHero;
    private float timeFactor;
    private float respawnMoney;
    private HullConfig respawnHull;

    public SolGame(SolApplication cmp, String shipName, boolean isTutorial, @NotNull CommonDrawer commonDrawer) {
        solApplication = cmp;
        GameDrawer drawer = new GameDrawer(commonDrawer);
        gameColors = new GameColors();
        soundManager = solApplication.getSoundManager();
        specialSounds = new SpecialSounds(soundManager);
        drawableManager = new DrawableManager(drawer);
        camera = new SolCamera(drawer.r);
        gameScreens = new GameScreens(drawer.r, cmp);
        tutorialManager = isTutorial ? new TutorialManager(commonDrawer.r, gameScreens, cmp.isMobile(), cmp.getOptions(), this) : null;
        farBackgroundManagerOld = new FarBackgroundManagerOld();
        shipBuilder = new ShipBuilder();
        EffectTypes effectTypes = new EffectTypes();
        specialEffects = new SpecialEffects(effectTypes, gameColors);
        itemManager = new ItemManager(soundManager, effectTypes, gameColors);
        AbilityCommonConfigs abilityCommonConfigs = new AbilityCommonConfigs(effectTypes, gameColors, soundManager);
        hullConfigManager = new HullConfigManager(itemManager, abilityCommonConfigs);
        SolNames solNames = new SolNames();
        planetManager = new PlanetManager(hullConfigManager, gameColors, itemManager);
        SolContactListener contactListener = new SolContactListener(this);
        factionManager = new FactionManager();
        objectManager = new ObjectManager(contactListener, factionManager);
        gridDrawer = new GridDrawer();
        chunkManager = new ChunkManager();
        particleManager = new ParticleManager();
        asteroidBuilder = new AsteroidBuilder();
        lootBuilder = new LootBuilder();
        mapDrawer = new MapDrawer(commonDrawer.h);
        shardBuilder = new ShardBuilder();
        galaxyFiller = new GalaxyFiller();
        starPortBuilder = new StarPort.Builder();
        drawableDebugger = new DrawableDebugger();
        beaconHandler = new BeaconHandler();
        mountDetectDrawer = new MountDetectDrawer();
        respawnItems = new ArrayList<>();
        timeFactor = 1;

        // from this point we're ready!
        planetManager.fill(solNames);
        createPlayer(shipName);
        createMercs();
        SolMath.checkVectorsTaken(null);
    }

    // uh, this needs refactoring
    private void createPlayer(String shipName) {
        Optional<String> shipNameOptional = Optional.ofNullable(shipName);
        ShipConfig shipConfig = shipNameOptional
                .map(y -> ShipConfig.load(hullConfigManager, y, itemManager, this))
                .orElseGet(() -> SaveManager.readShip(hullConfigManager, itemManager, this));

        // Added temporarily to remove warnings. Handle this more gracefully inside the SaveManager.readShip and the ShipConfig.load methods
        assert shipConfig != null;

        galaxyFiller.fill(this, hullConfigManager, itemManager);

        Vector2 pos = galaxyFiller.getPlayerSpawnPos(this);
        camera.setPos(pos);

        Pilot pilot;
        if (solApplication.getOptions().controlType == GameOptions.CONTROL_MOUSE) {
            beaconHandler.init(this, pos);
            pilot = new AiPilot(new BeaconDestProvider(), true, Faction.LAANI, false, "you", Const.AI_DET_DIST);
        } else {
            pilot = new UiControlledPilot(gameScreens.mainScreen);
        }

        float money = respawnMoney != 0 ? respawnMoney : tutorialManager != null ? 200 : shipConfig.money;

        HullConfig hull = respawnHull != null ? respawnHull : shipConfig.hull;

        String itemsStr = !respawnItems.isEmpty() ? "" : shipConfig.items;

        boolean giveAmmo = shipNameOptional.isPresent() && respawnItems.isEmpty();
        hero = Optional.of(shipBuilder.buildNewFar(this, new Vector2(pos), null, 0, 0, pilot, itemsStr, hull, null, true, money, new TradeConfig(), giveAmmo).toObj(this));

        ItemContainer itemContainer = hero.orElseThrow(SolNullOptionalException::new).getItemContainer();
        if (!respawnItems.isEmpty()) {
            for (SolItem item : respawnItems) {
                itemContainer.add(item);
                // Ensure that previously equipped items stay equipped
                if (item.isEquipped() > 0) {
                    if (item instanceof Gun) {
                        hero.orElseThrow(SolNullOptionalException::new).maybeEquip(this, item, item.isEquipped() == 2, true);
                    } else {
                        hero.orElseThrow(SolNullOptionalException::new).maybeEquip(this, item, true);
                    }
                }
            }
        } else if (tutorialManager != null) {
            for (int i = 0; i < 50; i++) {
                if (itemContainer.groupCount() > 1.5f * Const.ITEM_GROUPS_PER_PAGE) {
                    break;
                }
                SolItem item = itemManager.random();
                if (!(item instanceof Gun) && item.getIcon(this) != null && itemContainer.canAdd(item)) {
                    itemContainer.add(item.copy());
                }
            }
        }
        itemContainer.markAllAsSeen();

        // Don't change equipped items across load/respawn
        //AiPilot.reEquip(this, myHero);

        objectManager.addObjDelayed(hero.orElseThrow(SolNullOptionalException::new));
        objectManager.resetDelays();
    }

    /**
     * Creates and spawns the players mercenaries from their JSON file.
     */
    private void createMercs() {

        if (!SaveManager.resourceExists(MERC_SAVE_FILE)) {
            return;
        }

        String path = SaveManager.getResourcePath(MERC_SAVE_FILE);
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            if (new File(path).length() == 0) {
                bufferedReader.close();
                return;
            }
        } catch (IOException e) {
            logger.error("Could not save mercenaries!");
            e.printStackTrace();
            return;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType();
        ArrayList<HashMap<String, String>> mercs = gson.fromJson(bufferedReader, type);

        MercItem mercItems;
        for (HashMap<String, String> node : mercs) {
            mercItems = new MercItem(
                    new ShipConfig(hullConfigManager.getConfig(node.get("hull")), node.get("items"), Integer.parseInt(node.get("money")), -1f, null, itemManager));
            MercenaryUtils.createMerc(this, hero, mercItems);
        }

    }

    public void onGameEnd() {
        saveShip();
        objectManager.dispose();
    }

    public void saveShip() {
        if (tutorialManager != null) {
            return;
        }

        HullConfig hull;
        float money;
        ArrayList<SolItem> items;
        if (hero.isPresent()) {
            hull = hero.orElseThrow(SolNullOptionalException::new).getHull().config;
            money = hero.orElseThrow(SolNullOptionalException::new).getMoney();
            items = new ArrayList<>();
            for (List<SolItem> group : hero.orElseThrow(SolNullOptionalException::new).getItemContainer()) {
                for (SolItem i : group) {
                    items.add(0, i);
                }
            }
        } else if (transcendentHero.isPresent()) {
            FarShip farH = transcendentHero.orElseThrow(SolNullOptionalException::new).getShip();
            hull = farH.getHullConfig();
            money = farH.getMoney();
            items = new ArrayList<>();
            for (List<SolItem> group : farH.getIc()) {
                for (SolItem i : group) {
                    items.add(0, i);
                }
            }
        } else {
            hull = respawnHull;
            money = respawnMoney;
            items = respawnItems;
        }

        SaveManager.writeShip(hull, money, items, this);
    }

    public GameScreens getScreens() {
        return gameScreens;
    }

    public void update() {
        drawableDebugger.update(this);

        if (paused) {
            camera.updateMap(this); // update zoom only for map
            mapDrawer.update(this); // animate map icons
            return;
        }

        timeFactor = DebugOptions.GAME_SPEED_MULTIPLIER;
        hero.map(SolShip::getAbility)
                .filter(ability -> ability instanceof SloMo)
                .ifPresent(ability -> timeFactor *= ((SloMo) ability).getFactor());
        timeStep = Const.REAL_TIME_STEP * timeFactor;
        time += timeStep;

        planetManager.update(this);
        camera.update(this);
        chunkManager.update(this);
        mountDetectDrawer.update(this);
        objectManager.update(this);
        drawableManager.update(this);
        mapDrawer.update(this);
        soundManager.update(this);
        beaconHandler.update(this);

        hero = Optional.empty();
        transcendentHero = Optional.empty();
        for (SolObject obj : objectManager.getObjs()) {
            if ((obj instanceof SolShip)) {
                SolShip ship = (SolShip) obj;
                Pilot prov = ship.getPilot();
                if (prov.isPlayer()) {
                    hero = Optional.of(ship);
                    break;
                }
            }

            if (obj instanceof StarPort.Transcendent) {
                StarPort.Transcendent trans = (StarPort.Transcendent) obj;
                FarShip ship = trans.getShip();
                if (ship.getPilot().isPlayer()) {
                    transcendentHero = Optional.of(trans);
                    break;
                }
            }
        }

        if (tutorialManager != null) {
            tutorialManager.update();
        }
    }

    public void draw() {
        drawableManager.draw(this);
    }

    public void drawDebug(GameDrawer drawer) {
        if (DebugOptions.GRID_SZ > 0) {
            gridDrawer.draw(drawer, this, DebugOptions.GRID_SZ, drawer.debugWhiteTex);
        }
        planetManager.drawDebug(drawer, this);
        objectManager.drawDebug(drawer, this);
        if (DebugOptions.ZOOM_OVERRIDE != 0) {
            camera.drawDebug(drawer);
        }
        drawDebugPoint(drawer, DebugOptions.DEBUG_POINT, DebugCol.POINT);
        drawDebugPoint(drawer, DebugOptions.DEBUG_POINT2, DebugCol.POINT2);
        drawDebugPoint(drawer, DebugOptions.DEBUG_POINT3, DebugCol.POINT3);
    }

    private void drawDebugPoint(GameDrawer drawer, Vector2 dp, Color col) {
        if (dp.x != 0 || dp.y != 0) {
            float sz = camera.getRealLineWidth() * 5;
            drawer.draw(drawer.debugWhiteTex, sz, sz, sz / 2, sz / 2, dp.x, dp.y, 0, col);
        }
    }

    public float getTimeStep() {
        return timeStep;
    }

    public SolCamera getCam() {
        return camera;
    }

    public SolApplication getCmp() {
        return solApplication;
    }

    public DrawableManager getDrawableManager() {
        return drawableManager;
    }

    public ObjectManager getObjectManager() {
        return objectManager;
    }

    public PlanetManager getPlanetManager() {
        return planetManager;
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public AsteroidBuilder getAsteroidBuilder() {
        return asteroidBuilder;
    }

    public LootBuilder getLootBuilder() {
        return lootBuilder;
    }

    public Optional<SolShip> getHero() {
        return hero;
    }

    public ShipBuilder getShipBuilder() {
        return shipBuilder;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public HullConfigManager getHullConfigs() {
        return hullConfigManager;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        DebugCollector.warn(this.paused ? "game paused" : "game resumed");
    }

    public void respawn() {
        hero.ifPresent(hero -> {
            beforeHeroDeath();
            objectManager.removeObjDelayed(hero);
        });
        transcendentHero.map(StarPort.Transcendent::getShip)
                .ifPresent(farShip -> setRespawnState(farShip.getMoney(), farShip.getIc(), farShip.getHullConfig()));
        transcendentHero.ifPresent(objectManager::removeObjDelayed);

        createPlayer(null);
    }

    public FactionManager getFactionMan() {
        return factionManager;
    }

    public boolean isPlaceEmpty(Vector2 pos, boolean considerPlanets) {
        if (considerPlanets) {
            Planet np = planetManager.getNearestPlanet(pos);
            boolean inPlanet = np.getPos().dst(pos) < np.getFullHeight();

            if (inPlanet) {
                return false;
            }
        }

        SolSystem ns = planetManager.getNearestSystem(pos);
        if (ns.getPos().dst(pos) < SunSingleton.SUN_HOT_RAD) {
            return false;
        }

        List<SolObject> objs = objectManager.getObjs();
        for (SolObject o : objs) {
            if (!o.hasBody()) {
                continue;
            }

            if (pos.dst(o.getPosition()) < objectManager.getRadius(o)) {
                return false;
            }
        }

        for (FarObjData fod : objectManager.getFarObjs()) {
            FarObj o = fod.fo;

            if (!o.hasBody()) {
                continue;
            }

            if (pos.dst(o.getPos()) < o.getRadius()) {
                return false;
            }
        }

        return true;
    }

    public MapDrawer getMapDrawer() {
        return mapDrawer;
    }

    public ShardBuilder getShardBuilder() {
        return shardBuilder;
    }

    public FarBackgroundManagerOld getFarBgManOld() {
        return farBackgroundManagerOld;
    }

    public GalaxyFiller getGalaxyFiller() {
        return galaxyFiller;
    }

    public StarPort.Builder getStarPortBuilder() {
        return starPortBuilder;
    }

    public @Nullable StarPort.Transcendent getTranscendentHero() {
        return transcendentHero.orElse(null);
    }

    public GridDrawer getGridDrawer() {
        return gridDrawer;
    }

    public OggSoundManager getSoundManager() {
        return soundManager;
    }

    public float getTime() {
        return time;
    }

    public void drawDebugUi(UiDrawer uiDrawer) {
        drawableDebugger.draw(uiDrawer);
    }

    public SpecialSounds getSpecialSounds() {
        return specialSounds;
    }

    public SpecialEffects getSpecialEffects() {
        return specialEffects;
    }

    public GameColors getCols() {
        return gameColors;
    }

    public float getTimeFactor() {
        return timeFactor;
    }

    public BeaconHandler getBeaconHandler() {
        return beaconHandler;
    }

    public MountDetectDrawer getMountDetectDrawer() {
        return mountDetectDrawer;
    }

    public TutorialManager getTutMan() {
        return tutorialManager;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String newName) {
        shipName = newName;
    }

    public void beforeHeroDeath() {
        if (!hero.isPresent()) {
            return;
        }

        float money = hero.orElseThrow(SolNullOptionalException::new).getMoney();
        ItemContainer ic = hero.orElseThrow(SolNullOptionalException::new).getItemContainer();

        setRespawnState(money, ic, hero.orElseThrow(SolNullOptionalException::new).getHull().config);

        hero.orElseThrow(SolNullOptionalException::new).setMoney(money - respawnMoney);
        for (SolItem item : respawnItems) {
            ic.remove(item);
        }
    }

    private void setRespawnState(float money, ItemContainer ic, HullConfig hullConfig) {
        respawnMoney = .75f * money;
        respawnHull = hullConfig;
        respawnItems.clear();
        for (List<SolItem> group : ic) {
            for (SolItem item : group) {
                boolean equipped = hero.map(hero -> hero.maybeUnequip(this, item, false)).orElse(true);
                if (equipped || SolMath.test(.75f)) {
                    respawnItems.add(0, item);
                }
            }
        }
    }
}
