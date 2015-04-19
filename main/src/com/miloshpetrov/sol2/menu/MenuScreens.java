package com.miloshpetrov.sol2.menu;

import com.miloshpetrov.sol2.TextureManager;
import com.miloshpetrov.sol2.ui.SolLayouts;

public class MenuScreens {
  public final MainScreen main;
  public final OptionsScreen options;
  public final ResolutionScreen resolutionScreen;
  public final CreditsScreen credits;
  public final LoadingScreen loading;
  public final NewGameScreen newGame;
  public final NewShipScreen newShip;

  public MenuScreens(SolLayouts layouts, TextureManager textureManager, boolean mobile, float r) {
    MenuLayout menuLayout = layouts.menuLayout;
    main = new MainScreen(menuLayout, textureManager, mobile, r);
    options = new OptionsScreen(menuLayout);
    resolutionScreen = new ResolutionScreen(menuLayout);
    credits = new CreditsScreen(r);
    loading = new LoadingScreen();
    newGame = new NewGameScreen(menuLayout);
    newShip = new NewShipScreen(menuLayout);
  }

}
