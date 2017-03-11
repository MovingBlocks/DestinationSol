/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.menu;

import com.badlogic.gdx.graphics.Color;
import org.destinationsol.Const;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class CreditsScreen implements SolUiScreen {
    public static final float MAX_AWAIT = 6f;
    private final ArrayList<SolUiControl> myControls;
    private final SolUiControl myCloseCtrl;
    private final ArrayList<String> myPages;
    private final Color myColor;

    private int myIdx;
    private float myPerc;

    public CreditsScreen(float r, GameOptions gameOptions) {
        myControls = new ArrayList<SolUiControl>();
        myCloseCtrl = new SolUiControl(MainScreen.creditsBtnRect(r), true, gameOptions.getKeyEscape());
        myCloseCtrl.setDisplayName("Close");
        myControls.add(myCloseCtrl);
        myColor = SolColor.col(1, 1);

        myPages = new ArrayList<String>();
        String[][] sss = {
                {
                        "A game from",
                        "",
                        "MovingBlocks"
                },
                {
                        "Original Creators",
                        "",
                        "Idea, coding, team lead:",
                        "Milosh Petrov",
                        "",
                        "Drawing:",
                        "Kent C. Jensen",
                        "",
                        "Additional coding:",
                        "Nika \"NoiseDoll\" Burimenko",
                        "",
                        "Additional drawing:",
                        "Julia Nikolaeva"
                },
                {
                        "Contributors on GitHub",
                        "",
                        "Cervator, Rulasmur",
                        "theotherjay, LinusVanElswijk",
                        "SimonC4, grauerkoala, rzats",
                        "LadySerenaKitty, askneller",
                        "JGelfand, AvaLanCS"
                },
                {
                        "Soundtrack by NeonInsect"
                },
                {
                        "Game engine:",
                        "LibGDX",
                        "",
                        "Windows wrapper:",
                        "Launch4J"
                },
                {
                        "Font:",
                        "\"Jet Set\" by Captain Falcon",
                        "",
                        "Sounds by FreeSound.org users:",
                        "Smokum, Mattpavone",
                        "Hanstimm, Sonidor,",
                        "Isaac200000, TheHadnot, Garzul",
                        "Dpoggioli, Raremess, Giddykipper,",
                        "Steveygos93",
                },
        };
        for (String[] ss : sss) {
            StringBuilder page = new StringBuilder();
            for (String s : ss) {
                page.append(s).append("\n");
            }
            myPages.add(page.toString());
        }

    }

    @Override
    public List<SolUiControl> getControls() {
        return myControls;
    }

    @Override
    public void onAdd(SolApplication cmp) {
        myIdx = 0;
        myPerc = 0;
        myColor.a = 0;
    }

    @Override
    public void updateCustom(SolApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
        if (myCloseCtrl.isJustOff()) {
            cmp.getInputMan().setScreen(cmp, cmp.getMenuScreens().main);
            return;
        }
        myPerc += Const.REAL_TIME_STEP / MAX_AWAIT;
        if (myPerc > 1) {
            myPerc = 0;
            myIdx++;
            if (myIdx >= myPages.size()) myIdx = 0;
        }
        float a = myPerc * 2;
        if (a > 1) a = 2 - a;
        a *= 3;
        myColor.a = SolMath.clamp(a);
    }

    @Override
    public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
        return false;
    }

    @Override
    public void blurCustom(SolApplication cmp) {
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication cmp) {
    }

    @Override
    public void drawImgs(UiDrawer uiDrawer, SolApplication cmp) {
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication cmp) {
        uiDrawer.drawString(myPages.get(myIdx), uiDrawer.r / 2, .5f, FontSize.MENU, true, myColor);
    }

    @Override
    public boolean reactsToClickOutside() {
        return false;
    }
}
