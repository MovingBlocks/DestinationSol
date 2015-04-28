package com.miloshpetrov.sol2.ui;

import com.miloshpetrov.sol2.SolApplication;

import java.util.List;

public interface SolUiScreen {
  List<SolUiControl> getControls();

  void onAdd(SolApplication cmp);

  void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside);

  boolean isCursorOnBg(SolInputManager.Ptr ptr);

  void blurCustom(SolApplication cmp);


  void drawBg(UiDrawer uiDrawer, SolApplication cmp);

  void drawImgs(UiDrawer uiDrawer, SolApplication cmp);

  void drawText(UiDrawer uiDrawer, SolApplication cmp);

  boolean reactsToClickOutside();
}
