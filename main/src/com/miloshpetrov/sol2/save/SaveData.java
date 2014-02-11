package com.miloshpetrov.sol2.save;

import com.miloshpetrov.sol2.game.FarObj;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.planet.SolSystem;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
  public final List<FarObj> farObjs;
  public final List<SolSystem> systems;
  public final List<Planet> planets;

  public SaveData() {
    farObjs = new ArrayList<FarObj>();
    planets = new ArrayList<Planet>();
    systems = new ArrayList<SolSystem>();
  }
}
