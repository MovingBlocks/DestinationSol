package com.miloshpetrov.sol2.menu;

import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.save.SaveMan;
import com.miloshpetrov.sol2.ui.SolLayouts;
import com.miloshpetrov.sol2.ui.SolUiScreen;

public class MenuScreens {
  public final SolUiScreen main;
  public final OptionsScreen options;
  public final HalpScreen halpScreen;
  public final ResoScreen resoScreen;

  public MenuScreens(SolLayouts layouts, SaveMan saveMan, TexMan texMan, boolean mobile) {

    MenuLayout menuLayout = layouts.menuLayout;
    main = new MainScreen(menuLayout, saveMan, texMan, mobile);
    options = new OptionsScreen(menuLayout, mobile);
    halpScreen = new HalpScreen(menuLayout);
    resoScreen = new ResoScreen(menuLayout);
  }

}
