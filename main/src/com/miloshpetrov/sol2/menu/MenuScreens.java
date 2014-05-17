package com.miloshpetrov.sol2.menu;

import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.save.SaveMan;
import com.miloshpetrov.sol2.ui.SolLayouts;

public class MenuScreens {
  public final MainScreen main;
  public final OptionsScreen options;
  public final ResoScreen resoScreen;

  public MenuScreens(SolLayouts layouts, SaveMan saveMan, TexMan texMan, boolean mobile) {

    MenuLayout menuLayout = layouts.menuLayout;
    main = new MainScreen(menuLayout, saveMan, texMan, mobile);
    options = new OptionsScreen(menuLayout);
    resoScreen = new ResoScreen(menuLayout);
  }

}
