/*
 * Copyright 2018 MovingBlocks
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
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.context.Context;
import org.destinationsol.game.input.Mover;
import org.destinationsol.game.input.Shooter;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;

import java.util.List;

public class ShipMixedControl implements ShipUiControl {
    public final SolUiControl upCtrl;
    public final SolUiControl shootCtrl;
    public final SolUiControl shoot2Ctrl;
    public final SolUiControl abilityCtrl;
    private final SolUiControl myDownCtrl;
    private final Vector2 mouseWorldPosition;
    private final TextureAtlas.AtlasRegion myCursor;
    private boolean turnRight;
    private boolean turnLeft;

    ShipMixedControl(SolApplication solApplication, List<SolUiControl> controls) {
        GameOptions gameOptions = solApplication.getOptions();
        myCursor = Assets.getAtlasRegion("engine:uiCursorTarget");
        mouseWorldPosition = new Vector2();
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
        SolGame game = solApplication.getGame();
        if (!enabled || problemWithProjections(game.getCam())) {
            return;
        }
        SolInputManager im = solApplication.getInputManager();
        Hero hero = game.getHero();
        if (hero.isNonTranscendent()) {
            mouseWorldPosition.set(Gdx.input.getX(), Gdx.input.getY());
            game.getCam().screenToWorld(mouseWorldPosition);
            SolMath.assetReal(mouseWorldPosition, hero.getPosition());
            float desiredAngle = SolMath.angle(hero.getPosition(), mouseWorldPosition);
            Boolean needsToTurn = Mover.needsToTurn(hero.getAngle(), desiredAngle, hero.getRotationSpeed(), hero.getRotationAcceleration(), Shooter.MIN_SHOOT_AAD);
            if (needsToTurn != null) {
                if (needsToTurn) {
                    turnRight = true;
                } else {
                    turnLeft = true;
                }
            }
            if (!im.isMouseOnUi() && !solApplication.getNuiManager().isMouseOnUi()) {
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
    }

    /**
     * When alt+tabbing in full screen mode, the client area can become 0 with/height, and when alt+tabbing back,
     * the camera matrix can contain NaN values for the first frame
     * @return true if projections should not be done this update
     */
    private boolean problemWithProjections(SolCam camera) {
        return Gdx.graphics.getWidth() == 0 || Gdx.graphics.getHeight() == 0 ||
                camera.getViewWidth() <= 0.f || camera.getViewHeight() <= 0.f || !camera.isMatrixValid();
    }

    @Override
    public boolean isLeft() {
        return turnLeft;
    }

    @Override
    public boolean isRight() {
        return turnRight;
    }

    @Override
    public boolean isUp() {
        return upCtrl.isOn();
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

    @Override
    public void blur() {
        turnLeft = false;
        turnRight = false;
    }
}
