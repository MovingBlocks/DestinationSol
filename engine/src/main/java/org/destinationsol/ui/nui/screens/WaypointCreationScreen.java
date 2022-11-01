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

package org.destinationsol.ui.nui.screens;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.Waypoint;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.nui.Color;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.backends.libgdx.GdxColorUtil;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.UIImage;
import org.terasology.nui.widgets.UISlider;

import javax.inject.Inject;

/**
 * This screen is responsible for choosing the colour of a waypoint and then placing it.
 * The position of the waypoint to place is decided in the {@link MapScreen}.
 *
 * TODO: This screen is essentially a basic colour picker. Maybe it should be adapted into a generic widget instead?
 */
public class WaypointCreationScreen extends NUIScreenLayer {
    private final SolApplication solApplication;
    private UISlider redSlider;
    private UISlider greenSlider;
    private UISlider blueSlider;
    private UIImage colourPreview;
    private KeyActivatedButton doneButtton;
    private KeyActivatedButton cancelButton;
    private Color chosenColour;
    private Vector2 waypointPosition;

    @Inject
    public WaypointCreationScreen(SolApplication solApplication) {
        this.solApplication = solApplication;
    }

    @Override
    public void initialise() {
        colourPreview = find("colourPreview", UIImage.class);
        colourPreview.bindTint(new ReadOnlyBinding<Color>() {
            @Override
            public Color get() {
                return chosenColour;
            }
        });

        redSlider = find("redSlider", UISlider.class);
        redSlider.bindValue(new Binding<Float>() {
            @Override
            public Float get() {
                return (float) chosenColour.r();
            }

            @Override
            public void set(Float value) {
                chosenColour.setRed(value.intValue());
            }
        });

        greenSlider = find("greenSlider", UISlider.class);
        greenSlider.bindValue(new Binding<Float>() {
            @Override
            public Float get() {
                return (float) chosenColour.g();
            }

            @Override
            public void set(Float value) {
                chosenColour.setGreen(value.intValue());
            }
        });

        blueSlider = find("blueSlider", UISlider.class);
        blueSlider.bindValue(new Binding<Float>() {
            @Override
            public Float get() {
                return (float) chosenColour.b();
            }

            @Override
            public void set(Float value) {
                chosenColour.setBlue(value.intValue());
            }
        });

        GameOptions gameOptions = solApplication.getOptions();

        doneButtton = find("doneButton", KeyActivatedButton.class);
        doneButtton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyShoot()));
        doneButtton.subscribe(button -> {
            SolGame solGame = solApplication.getGame();

            Waypoint waypoint = new Waypoint(waypointPosition, GdxColorUtil.terasologyToGDXColor(chosenColour),
                    solGame.getMapDrawer().getWaypointTexture());
            solGame.getHero().addWaypoint(waypoint);
            solGame.getObjectManager().addObjDelayed(waypoint);

            MapScreen mapScreen = solGame.getScreens().mapScreen;
            mapScreen.setWaypointButtonsEnabled(true);
            nuiManager.popScreen();
        });

        cancelButton = find("cancelButton", KeyActivatedButton.class);
        cancelButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyClose()));
        cancelButton.subscribe(button -> {
            MapScreen mapScreen = solApplication.getGame().getScreens().mapScreen;
            mapScreen.setWaypointButtonsEnabled(true);
            nuiManager.popScreen();
        });

        chosenColour = new Color(1.0f, 1.0f, 1.0f);
        waypointPosition = new Vector2();
    }

    public void setWaypointPosition(Vector2 waypointPosition) {
        this.waypointPosition = waypointPosition;
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }
}
