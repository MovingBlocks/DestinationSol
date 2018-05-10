/*
 * Copyright 2017 MovingBlocks
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
package org.destinationsol.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.input.Mover;
import org.destinationsol.game.input.Shooter;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;

import java.util.List;

public class ShipMixedControl implements ShipUiControl {
    private static final float THROTTLE_INCREMENT = 0.1f;

    public final SolUiControl upCtrl;
    public final SolUiControl shootCtrl;
    public final SolUiControl shoot2Ctrl;
    public final SolUiControl abilityCtrl;
    private final SolUiControl myDownCtrl;
    private final Vector2 myMouseWorldPos;
    private final TextureAtlas.AtlasRegion myCursor;

    private float orientation;
    private float throttle;

    ShipMixedControl(SolApplication solApplication, List<SolUiControl> controls) {
        GameOptions gameOptions = solApplication.getOptions();
        myCursor = Assets.getAtlasRegion("engine:uiCursorTarget");
        myMouseWorldPos = new Vector2();
        upCtrl = new SolUiControl(null, false, gameOptions.getKeyUpMouse());
        controls.add(upCtrl);
        myDownCtrl = new SolUiControl(null, false, gameOptions.getKeyDownMouse());
        controls.add(myDownCtrl);
        shootCtrl = new SolUiControl(null, false, gameOptions.getKeyShoot());
        controls.add(shootCtrl);
        shoot2Ctrl = new SolUiControl(null, false, gameOptions.getKeyShoot2());
        controls.add(shoot2Ctrl);
        abilityCtrl = new SolUiControl(null, false, gameOptions.getKeyAbility());
        controls.add(abilityCtrl);
    }

    @Override
    public void update(SolApplication solApplication, boolean enabled) {
        GameOptions gameOptions = solApplication.getOptions();
        blur();
        if (!enabled) {
            return;
        }
        SolInputManager im = solApplication.getInputManager();
        SolGame game = solApplication.getGame();
        Hero hero = game.getHero();
        if (hero.isNonTranscendent()) {
            myMouseWorldPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.getCam().screenToWorld(myMouseWorldPos);
            orientation = SolMath.angle(hero.getPosition(), myMouseWorldPos);

            if (!im.isMouseOnUi()) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    shootCtrl.maybeFlashPressed(gameOptions.getKeyShoot());
                }
                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    shoot2Ctrl.maybeFlashPressed(gameOptions.getKeyShoot2());
                }
                if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                    abilityCtrl.maybeFlashPressed(gameOptions.getKeyAbility());
                }
            }
        }

        // TODO: Probably make a throttle slider of some sort too
        if (upCtrl.isJustOff()) {
            throttle += THROTTLE_INCREMENT;
        }
        if (myDownCtrl.isJustOff()) {
            throttle -= THROTTLE_INCREMENT;
        }

        throttle = SolMath.clamp(throttle);
    }

    @Override
    public float getThrottle() {
        return throttle;
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public boolean isDown() {
        return myDownCtrl.isOn();
    }

    @Override
    public boolean isShoot() {
        return shootCtrl.isOn();
    }

    @Override
    public boolean isShoot2() {
        return shoot2Ctrl.isOn();
    }

    @Override
    public boolean isAbility() {
        return abilityCtrl.isOn();
    }

    @Override
    public TextureAtlas.AtlasRegion getInGameTex() {
        return myCursor;
    }
}
