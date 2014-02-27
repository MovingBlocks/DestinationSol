package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.DebugCol;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.asteroid.AsteroidBuilder;
import com.miloshpetrov.sol2.game.chunk.ChunkMan;
import com.miloshpetrov.sol2.game.dra.DraMan;
import com.miloshpetrov.sol2.game.farBg.FarBgMan;
import com.miloshpetrov.sol2.game.farBg.FarBgManOld;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.input.Pilot;
import com.miloshpetrov.sol2.game.input.PlayerPilot;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.particle.PartMan;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.planet.PlanetMan;
import com.miloshpetrov.sol2.game.screens.GameScreens;
import com.miloshpetrov.sol2.game.ship.*;
import com.miloshpetrov.sol2.game.sound.SoundMan;
import com.miloshpetrov.sol2.save.SaveData;

public class SolGame {

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
  private final ItemContainer myChangeShips;
  private final ItemMan myItemMan;
  private final TradeMan myTradeMan;
  private final StarPort.Builder myStarPortBuilder;
  private final SoundMan mySoundMan;
  private final PlayerSpawnConfig myPlayerSpawnConfig;

  private SolShip myHero;
  private float myTimeStep;
  private float myTime;
  private boolean myPaused;
  private final GalaxyFiller myGalaxyFiller;
  private StarPort.Transcendent myTranscendentHero;

  public SolGame(SolCmp cmp, SaveData sd, TexMan texMan, boolean tut) {
    myCmp = cmp;
    myTut = tut;
    Drawer drawer = new Drawer();
    mySoundMan = new SoundMan();
    myDraMan = new DraMan(drawer);
    myCam = new SolCam(drawer.r);
    myScreens = new GameScreens(drawer.r, cmp);
    myTexMan = texMan;
    myFarBgManOld = new FarBgManOld(myTexMan);
    myShipBuilder = new ShipBuilder();
    myHullConfigs = new HullConfigs(myShipBuilder, texMan);
    myPlanetMan = new PlanetMan(myTexMan, myHullConfigs);
    SolContactListener contactListener = new SolContactListener(this);
    myObjMan = new ObjMan(contactListener);
    myGridDrawer = new GridDrawer();
    myChunkMan = new ChunkMan();
    myPartMan = new PartMan(myTexMan);
    myAsteroidBuilder = new AsteroidBuilder();
    myLootBuilder = new LootBuilder();
    myItemMan = new ItemMan(myTexMan, mySoundMan);
    myFractionMan = new FractionMan(myTexMan);
    myFarBgMan = new FarBgMan();
    myMapDrawer = new MapDrawer(myTexMan);
    myShardBuilder = new ShardBuilder();
    myGalaxyFiller = new GalaxyFiller();
    myChangeShips = createChangeShips(myHullConfigs);
    myTradeMan = new TradeMan();
    myStarPortBuilder = new StarPort.Builder();
    myPlayerSpawnConfig = PlayerSpawnConfig.load(myHullConfigs);

    // from this point we're ready!
    myPlanetMan.fill(sd);
    myObjMan.fill(this, sd);
    if (sd == null) {
      myGalaxyFiller.fill(this);
      createPlayer();
    }
    SolMath.checkVectorsTaken(null);
  }

  private ItemContainer createChangeShips(HullConfigs hullConfigs) {
    ItemContainer res = new ItemContainer();
    res.add(new ShipItem(hullConfigs.getConfig("orbiter"), "Orbiter", "A cool ship", 300));
    res.add(new ShipItem(hullConfigs.getConfig("vanguard"), "Vanguard", "Cooler ship", 600));
    return res;
  }

  private void createPlayer() {
    Vector2 pos = myGalaxyFiller.getPlayerSpawnPos(this, myPlayerSpawnConfig.nearPlanet);
    Pilot pip = new PlayerPilot(myScreens.mainScreen);
    boolean god = DebugAspects.GOD_MODE;
    HullConfig config = myPlayerSpawnConfig.hullConfig;
    String items = myPlayerSpawnConfig.items;
    int money = myPlayerSpawnConfig.money;
    if (god) {
      config = myHullConfigs.getConfig("vanguard");
      items = "mg rl sBig aBig e rep:1:6 sloMo:1:6 b:1:6 r:1:6";
      money = 1000;
    }
    myHero = myShipBuilder.buildNew(this, new Vector2(pos), null, 0, 0, pip, items, config, true, true, null, true, money, null);
    if (myTut) {
      myHero.getHull().setEngine(this, myHero, null);
      ItemContainer ic = myHero.getItemContainer();
      GunItem secondary = (GunItem)myItemMan.getExample("wbo").copy();
      ic.add(secondary);
      GunItem slowGun = (GunItem)myItemMan.getExample("sg").copy();
      ic.add(slowGun);
      myHero.getHull().getGunMount(true).setGun(this, myHero, secondary);
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
    if (myPaused) return;

    myTimeStep = Const.REAL_TIME_STEP * DebugAspects.DEBUG_SLOWDOWN;
    if (myHero != null) myTimeStep *= myHero.getSloMoFactor();
    myTime += myTimeStep;

    myCam.update(this);
    myPlanetMan.update(this);
    myChunkMan.update(this);
    myObjMan.update(this);
    myDraMan.update(this);
    myTradeMan.update(this);
    myMapDrawer.update(this);
    mySoundMan.update(this);

    myHero = null;
    myTranscendentHero = null;
    for (SolObj obj : myObjMan.getObjs()) {
      if ((obj instanceof SolShip)) {
        SolShip ship = (SolShip) obj;
        Pilot prov = ship.getPilot();
        if (prov instanceof PlayerPilot) {
          myHero = ship;
          break;
        }
      }
      if (obj instanceof StarPort.Transcendent) {
        StarPort.Transcendent trans = (StarPort.Transcendent) obj;
        FarShip ship = trans.getShip();
        if (ship.getPilot() instanceof PlayerPilot) {
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
    if (DebugAspects.GRID_SZ > 0) myGridDrawer.draw(drawer, this, DebugAspects.GRID_SZ);
    myPlanetMan.drawDebug(drawer, this);
    myObjMan.drawDebug(drawer, this);
    if (DebugAspects.ZOOM_OVERRIDE != 0) myCam.drawDebug(drawer);
    Vector2 dp = DebugAspects.DEBUG_POINT;
    if (dp.x != 0 || dp.y != 0) {
      float sz = myCam.getRealLineWidth() * 5;
      drawer.draw(myTexMan.whiteTex, sz, sz, sz / 2, sz / 2, dp.x, dp.y, 0, DebugCol.POINT);
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

  public void pause() {
    myPaused = true;
  }

  public void resume() {
    myPaused = false;
  }

  public void respawn() {
    if (myHero != null) {
      myObjMan.removeObjDelayed(myHero);
    }
    createPlayer();
  }

  public FractionMan getFractionMan() {
    return myFractionMan;
  }

  public boolean isPlaceEmpty(Vector2 pos) {
    Planet np = myPlanetMan.getNearestPlanet(pos);
    return np.getFullHeight() < np.getPos().dst(pos);
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

  public ItemContainer getChangeShips() {
    return myChangeShips;
  }

  public TradeMan getTradeMan() {
    return myTradeMan;
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

}
