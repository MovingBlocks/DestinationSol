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
import org.destinationsol.game.SolGame;
import org.destinationsol.game.input.Mover;
import org.destinationsol.game.input.Shooter;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.responsiveUi.UiHeadlessButton;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;

public class ShipMixedControl implements ShipUiControl {
    private UiHeadlessButton upButton;
    private UiHeadlessButton downButton;
    private UiHeadlessButton shootButton;
    private UiHeadlessButton shoot2Button;
    private UiHeadlessButton abilityButton;

    private Vector2 myMouseWorldPos;
    private TextureAtlas.AtlasRegion myCursor;

    private boolean myRight;
    private boolean myLeft;

    ShipMixedControl(SolApplication solApplication, UiRelativeLayout rootUiElement) {
        GameOptions gameOptions = solApplication.getOptions();

        upButton = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyUpMouse());
        rootUiElement.addHeadlessElement(upButton);

        downButton = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyDownMouse());
        rootUiElement.addHeadlessElement(downButton);

        shootButton = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyShoot());
        rootUiElement.addHeadlessElement(shootButton);

        shoot2Button = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyShoot2());
        rootUiElement.addHeadlessElement(shoot2Button);

        abilityButton = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyAbility());
        rootUiElement.addHeadlessElement(abilityButton);

        myCursor = Assets.getAtlasRegion("engine:uiCursorTarget");
        myMouseWorldPos = new Vector2();
    }

    @Override
    public void update(SolApplication solApplication, boolean enabled) {
        blur();

        if (!enabled) {
            return;
        }

        GameOptions gameOptions = solApplication.getOptions();
        SolInputManager im = solApplication.getInputManager();
        SolGame game = solApplication.getGame();
        Hero hero = game.getHero();

        if (hero.isNonTranscendent()) {
            myMouseWorldPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.getCam().screenToWorld(myMouseWorldPos);
            float desiredAngle = SolMath.angle(hero.getPosition(), myMouseWorldPos);
            Boolean ntt = Mover.needsToTurn(hero.getAngle(), desiredAngle, hero.getRotationSpeed(), hero.getRotationAcceleration(), Shooter.MIN_SHOOT_AAD);
            if (ntt != null) {
                if (ntt) {
                    myRight = true;
                } else {
                    myLeft = true;
                }
            }
            if (!im.isMouseOnUi()) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    shootButton.maybeFlashPressed(gameOptions.getKeyShoot());
                }
                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    shoot2Button.maybeFlashPressed(gameOptions.getKeyShoot2());
                }
                if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                    abilityButton.maybeFlashPressed(gameOptions.getKeyAbility());
                }
            }
        }
    }

    @Override
    public boolean isLeft() {
        return myLeft;
    }

    @Override
    public boolean isRight() {
        return myRight;
    }

    @Override
    public boolean isUp() {
        return upButton.isOn();
    }

    @Override
    public boolean isDown() {
        return downButton.isOn();
    }

    @Override
    public boolean isShoot() {
        return shootButton.isOn();
    }

    @Override
    public boolean isShoot2() {
        return shoot2Button.isOn();
    }

    @Override
    public boolean isAbility() {
        return abilityButton.isOn();
    }

    @Override
    public TextureAtlas.AtlasRegion getInGameTex() {
        return myCursor;
    }

    @Override
    public void blur() {
        myLeft = false;
        myRight = false;
    }
}
