package com.miloshpetrov.sol2.ui;

import com.miloshpetrov.sol2.SolCmp;

import java.util.List;

public interface SolUiScreen {
  List<SolUiControl> getControls();

  void updateCustom(SolCmp cmp, SolInputMan.Ptr[] ptrs);

  void drawPre(UiDrawer uiDrawer, SolCmp cmp);

  boolean isCursorOnBg(SolInputMan.Ptr ptr);

  void onAdd(SolCmp cmp);

  void drawPost(UiDrawer uiDrawer, SolCmp cmp);
}
