package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.DebugCol;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.asteroid.AsteroidBuilder;
import com.miloshpetrov.sol2.game.chunk.ChunkMan;
import com.miloshpetrov.sol2.game.dra.DraDebugger;
import com.miloshpetrov.sol2.game.dra.DraMan;
import com.miloshpetrov.sol2.game.farBg.FarBgMan;
import com.miloshpetrov.sol2.game.farBg.FarBgManOld;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.input.*;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.particle.*;
import com.miloshpetrov.sol2.game.planet.*;
import com.miloshpetrov.sol2.game.screens.GameScreens;
import com.miloshpetrov.sol2.game.ship.*;
import com.miloshpetrov.sol2.game.sound.SoundMan;
import com.miloshpetrov.sol2.game.sound.SpecialSounds;
import com.miloshpetrov.sol2.menu.GameOptions;
import com.miloshpetrov.sol2.save.SaveData;
import com.miloshpetrov.sol2.ui.DebugCollector;
import com.miloshpetrov.sol2.ui.UiDrawer;

public class SolGame {

  public static final float RESPAWN_MONEY_PERC = .5f;
  private final GameScreens myScreens;
  private final SolCam myCam;
  private final ObjMan myObjMan;
  private final SolCmp myCmp;
  private final boolean myTut;
  private final DraMan myDraMan;
  private final PlanetMan myPlanetMan;
  private final TexMan myTexMan;
  private final ChunkMan myChunkMan;
  private final PartMan myPartMan;
  private final AsteroidBuilder myAsteroidBuilder;
  private final LootBuilder myLootBuilder;
  private final ShipBuilder myShipBuilder;
  private final HullConfigs myHullConfigs;
  private final GridDrawer myGridDrawer;
  private final FarBgMan myFarBgMan;
  private final FarBgManOld myFarBgManOld;
  private final FractionMan myFractionMan;
  private final MapDrawer myMapDrawer;
  private final ShardBuilder myShardBuilder;
  private final ItemMan myItemMan;
  private final StarPort.Builder myStarPortBuilder;
  private final SoundMan mySoundMan;
  private final PlayerSpawnConfig myPlayerSpawnConfig;
  private final DraDebugger myDraDebugger;
  private final SpecialSounds mySpecialSounds;
  private final EffectTypes myEffectTypes;
  private final SpecialEffects mySpecialEffects;
  private final GameCols myCols;
  private final AbilityCommonConfigs myAbilityCommonConfigs;
  private final SolNames myNames;
  private final BeaconHandler myBeaconHandler;

  private SolShip myHero;
  private float myTimeStep;
  private float myTime;
  private boolean myPaused;
  private final GalaxyFiller myGalaxyFiller;
  private StarPort.Transcendent myTranscendentHero;
  private float myTimeFactor;
  private float myRespawnMoney;

  public SolGame(SolCmp cmp, SaveData sd, TexMan texMan, boolean tut) {
    myCmp = cmp;
    myTut = tut;
    Drawer drawer = new Drawer(texMan);
    myCols = new GameCols();
    mySoundMan = new SoundMan();
    mySpecialSounds = new SpecialSounds(mySoundMan);
    myDraMan = new DraMan(drawer);
    myCam = new SolCam(drawer.r);
    myScreens = new GameScreens(drawer.r, cmp);
    myTexMan = texMan;
    myFarBgManOld = new FarBgManOld(myTexMan);
    myShipBuilder = new ShipBuilder();
    myEffectTypes = new EffectTypes();
    mySpecialEffects = new SpecialEffects(myEffectTypes, myTexMan, myCols);
    myItemMan = new ItemMan(myTexMan, mySoundMan, myEffectTypes, myCols);
    myAbilityCommonConfigs = new AbilityCommonConfigs(myEffectTypes, myTexMan, myCols, mySoundMan);
    myHullConfigs = new HullConfigs(myShipBuilder, texMan, myItemMan, myAbilityCommonConfigs, mySoundMan);
    myNames = new SolNames();
    myPlanetMan = new PlanetMan(myTexMan, myHullConfigs, myCols, myItemMan);
    SolContactListener contactListener = new SolContactListener(this);
    myFractionMan = new FractionMan(myTexMan);
    myObjMan = new ObjMan(contactListener, myFractionMan);
    myGridDrawer = new GridDrawer(texMan);
    myChunkMan = new ChunkMan(myHullConfigs, myTexMan);
    myPartMan = new PartMan();
    myAsteroidBuilder = new AsteroidBuilder(myTexMan);
    myLootBuilder = new LootBuilder();
    myFarBgMan = new FarBgMan();
    myMapDrawer = new MapDrawer(myTexMan);
    myShardBuilder = new ShardBuilder(myTexMan);
    myGalaxyFiller = new GalaxyFiller();
    myStarPortBuilder = new StarPort.Builder();
    myPlayerSpawnConfig = PlayerSpawnConfig.load(myHullConfigs);
    myDraDebugger = new DraDebugger();
    myBeaconHandler = new BeaconHandler(texMan);

    // from this point we're ready!
    myTimeFactor = 1;
    myPlanetMan.fill(sd, myNames);
    myObjMan.fill(this, sd);
    if (sd == null) {
      myGalaxyFiller.fill(this);
      createPlayer();
    }
    SolMath.checkVectorsTaken(null);
  }

  private void createPlayer() {
    Vector2 pos = myGalaxyFiller.getPlayerSpawnPos(this);
    Pilot pilot;
    if (myCmp.getOptions().controlType == GameOptions.CONTROL_MOUSE) {
      myBeaconHandler.init(this, pos);
      pilot = new AiPilot(new BeaconDestProvider(), true, Fraction.LAANI, false, "you", Const.AI_DET_DIST);
    } else {
      pilot = new UiControlledPilot(myScreens.mainScreen);
    }
    ShipConfig shipConfig = DebugOptions.GOD_MODE ? myPlayerSpawnConfig.godShipConfig : myPlayerSpawnConfig.shipConfig;
    float money = myRespawnMoney == 0 ? shipConfig.money : myRespawnMoney;
    myRespawnMoney = 0;
    myHero = myShipBuilder.buildNewFar(this, new Vector2(pos), null, 0, 0, pilot, shipConfig.items,
      shipConfig.hull, null, true, money, null).toObj(this);

    if (myTut) {
      myHero.getHull().setEngine(this, myHero, null);
      ItemContainer ic = myHero.getItemContainer();
      GunItem secondary = (GunItem)myItemMan.getExample("wbo").copy();
      ic.add(secondary);
      GunItem slowGun = (GunItem)myItemMan.getExample("sg").copy();
      ic.add(slowGun);
      myHero.getHull().getGunMount(true).setGun(this, myHero, secondary, shipConfig.hull.g2UnderShip);
      int toAdd = 2 * Const.ITEMS_PER_PAGE - ic.size();
      for (int i = 0; i < toAdd; i++) {
        ic.add(myItemMan.random());
      }
      myHero.setMoney(100);
    }
    myCam.setPos(pos);

    myObjMan.addObjDelayed(myHero);
    myObjMan.resetDelays();
  }

  public void dispose() {
    myDraMan.dispose();
    myObjMan.dispose();
  }

  public GameScreens getScreens() {
    return myScreens;
  }

  public void update() {
    myDraDebugger.update(this);

    if (myPaused) return;

    myTimeFactor = DebugOptions.GAME_SPEED_MULTIPLIER;
    if (myHero != null) {
      ShipAbility ability = myHero.getAbility();
      if (ability instanceof SloMo) {
        float factor = ((SloMo) ability).getFactor();
        myTimeFactor *= factor;
      }
    }
    myTimeStep = Const.REAL_TIME_STEP * myTimeFactor;
    myTime += myTimeStep;

    myPlanetMan.update(this);
    myCam.update(this);
    myChunkMan.update(this);
    myObjMan.update(this);
    myDraMan.update(this);
    myMapDrawer.update(this);
    mySoundMan.update(this);
    myBeaconHandler.update(this);

    myHero = null;
    myTranscendentHero = null;
    for (SolObj obj : myObjMan.getObjs()) {
      if ((obj instanceof SolShip)) {
        SolShip ship = (SolShip) obj;
        Pilot prov = ship.getPilot();
        if (prov.isPlayer()) {
          myHero = ship;
          break;
        }
      }
      if (obj instanceof StarPort.Transcendent) {
        StarPort.Transcendent trans = (StarPort.Transcendent) obj;
        FarShip ship = trans.getShip();
        if (ship.getPilot().isPlayer()) {
          myTranscendentHero = trans;
          break;
        }
      }
    }
  }

  public void draw() {
    myDraMan.draw(this);
  }

  public void drawDebug(Drawer drawer) {
    if (DebugOptions.GRID_SZ > 0) myGridDrawer.draw(drawer, this, DebugOptions.GRID_SZ);
    myPlanetMan.drawDebug(drawer, this);
    myObjMan.drawDebug(drawer, this);
    if (DebugOptions.ZOOM_OVERRIDE != 0) myCam.drawDebug(drawer);
    drawDebugPoint(drawer, DebugOptions.DEBUG_POINT, DebugCol.POINT);
    drawDebugPoint(drawer, DebugOptions.DEBUG_POINT2, DebugCol.POINT2);
    drawDebugPoint(drawer, DebugOptions.DEBUG_POINT3, DebugCol.POINT3);
  }

  private void drawDebugPoint(Drawer drawer, Vector2 dp, Color col) {
    if (dp.x != 0 || dp.y != 0) {
      float sz = myCam.getRealLineWidth() * 5;
      drawer.draw(myTexMan.whiteTex, sz, sz, sz / 2, sz / 2, dp.x, dp.y, 0, col);
    }
  }

  public float getTimeStep() {
    return myTimeStep;
  }

  public SolCam getCam() {
    return myCam;
  }

  public SolCmp getCmp() {
    return myCmp;
  }

  public DraMan getDraMan() {
    return myDraMan;
  }

  public ObjMan getObjMan() {
    return myObjMan;
  }

  public TexMan getTexMan() {
    return myTexMan;
  }

  public PlanetMan getPlanetMan() {
    return myPlanetMan;
  }

  public PartMan getPartMan() {
    return myPartMan;
  }

  public AsteroidBuilder getAsteroidBuilder() {
    return myAsteroidBuilder;
  }

  public LootBuilder getLootBuilder() {
    return myLootBuilder;
  }

  public SolShip getHero() {
    return myHero;
  }

  public ShipBuilder getShipBuilder() {
    return myShipBuilder;
  }

  public ItemMan getItemMan() {
    return myItemMan;
  }

  public HullConfigs getHullConfigs() {
    return myHullConfigs;
  }

  public boolean isPaused() {
    return myPaused;
  }

  public void setPaused(boolean paused) {
    myPaused = paused;
    DebugCollector.warn(myPaused ? "game paused" : "game resumed");
  }

  public void respawn() {
    if (myHero != null) {
      myRespawnMoney = RESPAWN_MONEY_PERC * myHero.getMoney();
      myObjMan.removeObjDelayed(myHero);
    }
    createPlayer();
  }

  public FractionMan getFractionMan() {
    return myFractionMan;
  }

  public boolean isPlaceEmpty(Vector2 pos) {
    Planet np = myPlanetMan.getNearestPlanet(pos);
    boolean inPlanet = np.getPos().dst(pos) < np.getFullHeight();
    if (inPlanet) return false;
    SolSystem ns = myPlanetMan.getNearestSystem(pos);
    if (ns.getPos().dst(pos) < SunSingleton.SUN_HOT_RAD) return false;
    for (SolObj o : myObjMan.getObjs()) {
      if (!o.hasBody()) continue;
      if (pos.dst(o.getPos()) < myObjMan.getRadius(o)) {
        return false;
      }
    }
    for (FarObj o : myObjMan.getFarObjs()) {
      if (!o.hasBody()) continue;
      if (pos.dst(o.getPos()) < o.getRadius()) {
        return false;
      }
    }
    return true;
  }

  public MapDrawer getMapDrawer() {
    return myMapDrawer;
  }

  public ShardBuilder getShardBuilder() {
    return myShardBuilder;
  }

  public FarBgManOld getFarBgManOld() {
    return myFarBgManOld;
  }

  public GalaxyFiller getGalaxyFiller() {
    return myGalaxyFiller;
  }

  public StarPort.Builder getStarPortBuilder() {
    return myStarPortBuilder;
  }

  public StarPort.Transcendent getTranscendentHero() {
    return myTranscendentHero;
  }

  public boolean isTut() {
    return myTut;
  }

  public GridDrawer getGridDrawer() {
    return myGridDrawer;
  }

  public SoundMan getSoundMan() {
    return mySoundMan;
  }

  public float getTime() {
    return myTime;
  }

  public void drawDebugUi(UiDrawer uiDrawer) {
    myDraDebugger.draw(uiDrawer, this);
  }

  public PlayerSpawnConfig getPlayerSpawnConfig() {
    return myPlayerSpawnConfig;
  }

  public SpecialSounds getSpecialSounds() {
    return mySpecialSounds;
  }

  public SpecialEffects getSpecialEffects() {
    return mySpecialEffects;
  }

  public GameCols getCols() {
    return myCols;
  }

  public float getTimeFactor() {
    return myTimeFactor;
  }

  public void setRespawnMoney(float money) {
    myRespawnMoney = money;
  }

  public BeaconHandler getBeaconHandler() {
    return myBeaconHandler;
  }
}
