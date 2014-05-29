package com.miloshpetrov.sol2.ui;

import com.miloshpetrov.sol2.SolCmp;

import java.util.List;

public interface SolUiScreen {
  List<SolUiControl> getControls();

  void onAdd(SolCmp cmp);

  void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs);

  boolean isCursorOnBg(SolInputMan.Ptr ptr);

  void blurCustom(SolCmp cmp);


  void drawBg(UiDrawer uiDrawer, SolCmp cmp);

  void drawImgs(UiDrawer uiDrawer, SolCmp cmp);

  void drawText(UiDrawer uiDrawer, SolCmp cmp);
}
