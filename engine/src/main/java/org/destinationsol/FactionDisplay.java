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
package org.destinationsol;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.FactionInfo;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.screens.MapScreen;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.UiDrawer;


public class FactionDisplay {
    private SolCam cam;
    private DisplayDimensions dimensions;
    private FactionInfo info;
    private SolShip solShip;
    private MapScreen mapScreen;

    private boolean isPressed = false;

    public FactionDisplay(SolGame game, FactionInfo factionInfo) {
        dimensions = SolApplication.displayDimensions;
        cam = game.getCam();
        info = factionInfo;
    }

    public void drawFactionNames(UiDrawer uiDrawer){
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z))
            isPressed = !isPressed;

        //angle has to be zero because the planet angles mess up the text display
        if(isPressed && cam.getAngle() == 0 && mapScreen.mapOpened) {

            for(SolShip ship: solShip.shipList) {
                /* use this for debugging with faction disposition, i'll probably add a ledger showing disposition in the future
                System.out.println(info.getFactionNames());
                System.out.println(info.getDisposition());
                */
                Vector2 drawPosition = getDrawPosition(ship);
                uiDrawer.drawString(ship.getFactionName(), drawPosition.x * dimensions.getRatio(), drawPosition.y, 1, false, Color.valueOf(info.getFactionColors().get(ship.getFactionID()).toString()));
            }
        }
        }


    public Vector2 getDrawPosition(SolShip ship){
        Vector2 distanceDifference = new Vector2(cam.getPosition());
        distanceDifference.sub(ship.getPosition());
        //don't mess with any of this please for the love of god
        distanceDifference.x /= cam.getViewWidth();
        distanceDifference.x = .5f - distanceDifference.x;
        distanceDifference.y /= cam.getViewHeight();
        distanceDifference.y = .5f - distanceDifference.y;
        distanceDifference.y -= .1f;
        return distanceDifference;
    }
}
