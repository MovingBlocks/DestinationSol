/*
 * Copyright 2021 The Terasology Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.ui.nui.screens.mainMenu;

import org.destinationsol.Const;
import org.destinationsol.SolApplication;
import org.destinationsol.common.In;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.joml.Math;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.skin.UISkin;
import org.terasology.nui.skin.UISkinBuilder;
import org.terasology.nui.widgets.UILabel;

import java.util.ArrayList;

public class CreditsScreen extends NUIScreenLayer {
    private static final float MAX_AWAIT = 6f;
    @In
    private SolApplication solApplication;
    private UILabel creditsText;
    private ArrayList<String> myPages = new ArrayList<>();
    private UISkin textSkin;
    private Color textColor;
    private int pageIndex;
    private float pageProgressPercent;

    @Override
    public void initialise() {
        textColor = new Color(Color.white);

        String[][] creditsSections = {
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
                        "JGelfand, AvaLanCS, scirelli",
                        "Sigma-One, vampcat, malanius",
                        "AonoZan, ererbe, SurajDutta",
                        "jasyohuang, Steampunkery",
                        "Graviton48, Adrijaned, MaxBorsch",
                        "sohil123, FieryPheonix909",
                        "digitalripperynr, NicholasBatesNZ",
                        "Pendi, Torpedo99, AndyTechGuy",
                        "BenjaminAmos, dannykelemen",
                        "msteiger, oniatus, arpitkamboj",
                        "manas96, IsaacLic, Mpcs, ZPendi",
                        "nailorcngci, FearlessTobi, ujjman",
                        "ThisIsPIRI"
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

        for (String[] creditsSection : creditsSections) {
            StringBuilder page = new StringBuilder();
            for (String creditsLine : creditsSection) {
                page.append(creditsLine).append("\n");
            }
            myPages.add(page.toString());
        }

        creditsText = find("creditsText", UILabel.class);
        textSkin = new UISkinBuilder()
                .setBaseSkin(getSkin())
                .setFamily(creditsText.getFamily())
                .setTextColor(textColor)
                .setTextShadowColor(new Color(Color.transparent))
                .build();
        creditsText.setSkin(textSkin);

        KeyActivatedButton cancelButton = find("cancelButton", KeyActivatedButton.class);
        cancelButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyEscape()));
        cancelButton.subscribe(button -> {
            nuiManager.pushScreen(solApplication.getMenuScreens().main);
            nuiManager.removeScreen(this);
        });
    }

    @Override
    public void onAdded() {
        pageIndex = 0;
        pageProgressPercent = 0;
        textColor.setAlpha(0.0f);

        creditsText.setText(myPages.get(pageIndex));
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        pageProgressPercent += Const.REAL_TIME_STEP / MAX_AWAIT;
        if (pageProgressPercent > 1) {
            pageProgressPercent = 0;
            pageIndex++;
            if (pageIndex >= myPages.size()) {
                pageIndex = 0;
            }
            creditsText.setText(myPages.get(pageIndex));
        }
        float alpha = pageProgressPercent * 2;
        if (alpha > 1) {
            alpha = 2 - alpha;
        }
        alpha *= 3;
        textColor.setAlpha(Math.clamp(0.0f, 1.0f, alpha));
        textSkin.getDefaultStyleFor(creditsText.getFamily()).setTextColor(textColor);

        solApplication.getMenuBackgroundManager().update();
    }

    @Override
    public void onDraw(Canvas canvas) {
        try (NUIManager.LegacyUiDrawerWrapper wrapper = nuiManager.getLegacyUiDrawer()) {
            solApplication.getMenuBackgroundManager().draw(wrapper.getUiDrawer());
        }

        super.onDraw(canvas);
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }

}
