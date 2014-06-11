package com.miloshpetrov.sol2.menu;

import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.ui.SolLayouts;

public class MenuScreens {
  public final MainScreen main;
  public final OptionsScreen options;
  public final ResoScreen resoScreen;
  public final CreditsScreen credits;
  public final NewGameScreen newGame;
  public final NewShipScreen newShip;

  public MenuScreens(SolLayouts layouts, TexMan texMan, boolean mobile, float r) {
    MenuLayout menuLayout = layouts.menuLayout;
    main = new MainScreen(menuLayout, texMan, mobile, r);
    options = new OptionsScreen(menuLayout);
    resoScreen = new ResoScreen(menuLayout);
    credits = new CreditsScreen(r);
    newGame = new NewGameScreen(menuLayout);
    newShip = new NewShipScreen(menuLayout);
  }

}
