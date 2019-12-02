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
package org.destinationsol.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.destinationsol.Const;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.screens.MainGameScreen;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.DisplayDimensions;

public class SolCam implements UpdateAwareSystem {
    public static final float CAM_ROT_SPD = 90f;
    private static final float VIEWPORT_HEIGHT = 5f;
    private static final float MAX_ZOOM_SPD = 5f;
    private static final float MED_ZOOM_SPD = 3f;
    private static final float ZOOM_CHG_SPD = .1f;
    private static final float MOVE_SPD = 3f;
    private static final float MAX_SHAKE = .07f;
    private static final float SHAKE_DAMP = MAX_SHAKE;
    public static boolean DIRECT_CAM_CONTROL = false;
    private final CamRotStrategy myCamRotStrategy;
    private final OrthographicCamera myCam;
    private final ScreenViewport viewport;
    private final Vector3 myTmpVec;

    private float myPrevHeroLife;
    private float myShake;
    private float myAngle;
    private float myZoom;
    private Vector2 position;
    private DisplayDimensions displayDimensions;

    public SolCam() {
        displayDimensions = SolApplication.displayDimensions;
        myCamRotStrategy = new CamRotStrategy.ToPlanet();
        myCam = new OrthographicCamera(VIEWPORT_HEIGHT * displayDimensions.getRatio(), -VIEWPORT_HEIGHT);
        viewport = new ScreenViewport(myCam);
        myZoom = calcZoom(Const.CAM_VIEW_DIST_GROUND);
        position = new Vector2();
        myTmpVec = new Vector3();
    }

    public Matrix4 getMtx() {
        return myCam.combined;
    }

    @Override
    public void update(SolGame game, float timeStep) {
        if (game.isPaused()) {
            updateMapZoom(game, timeStep);
            return;
        }

        Hero hero = game.getHero();
        float life = hero.getLife();
        if (hero.isDead() || DIRECT_CAM_CONTROL) {
            applyInput(game);
        } else {
            Vector2 heroPos = hero.getPosition();
            if (myZoom * VIEWPORT_HEIGHT < heroPos.dst(position)) {
                position.set(heroPos);
                game.getObjectManager().resetDelays();
            } else {
                Vector2 moveDiff = SolMath.getVec(hero.getVelocity());
                moveDiff.scl(timeStep);
                position.add(moveDiff);
                SolMath.free(moveDiff);
                float moveSpeed = MOVE_SPD * timeStep;
                position.x = SolMath.approach(position.x, heroPos.x, moveSpeed);
                position.y = SolMath.approach(position.y, heroPos.y, moveSpeed);
            }
        }

        if (life < myPrevHeroLife) {
            float shakeDiff = .1f * MAX_SHAKE * (myPrevHeroLife - life);
            myShake = SolMath.approach(myShake, MAX_SHAKE, shakeDiff);
        } else {
            myShake = SolMath.approach(myShake, 0, SHAKE_DAMP * timeStep);
        }
        myPrevHeroLife = life;

        Vector2 position = SolMath.fromAl(SolRandom.randomFloat(180), myShake);
        position.add(this.position);
        applyPos(position.x, position.y);
        SolMath.free(position);

        float desiredAngle = myCamRotStrategy.getRotation(this.position, game);
        float rotationSpeed = CAM_ROT_SPD * timeStep;
        myAngle = SolMath.approachAngle(myAngle, desiredAngle, rotationSpeed);
        applyAngle();

        updateMapZoom(game, timeStep);
    }

    private void updateMapZoom(SolGame game, float timeStep) {
        float desiredViewDistance = getDesiredViewDistance(game);
        float desiredZoom = calcZoom(desiredViewDistance);
        myZoom = SolMath.approach(myZoom, desiredZoom, ZOOM_CHG_SPD * timeStep);
        applyZoom(game.getMapDrawer());
        myCam.update();
        viewport.update(displayDimensions.getWidth(), -displayDimensions.getHeight());
        viewport.setUnitsPerPixel(1 / (displayDimensions.getHeight() / VIEWPORT_HEIGHT));
    }

    private float getDesiredViewDistance(SolGame game) {
        Hero hero = game.getHero();
        if (hero.isAlive() && hero.isTranscendent()) {
            return Const.CAM_VIEW_DIST_SPACE;
        } else if (hero.isDead()) {
            return Const.CAM_VIEW_DIST_GROUND;
        } else {
            float speed = hero.getVelocity().len();
            float desiredViewDistance = Const.CAM_VIEW_DIST_SPACE;
            Planet nearestPlanet = game.getPlanetManager().getNearestPlanet(position);
            if (nearestPlanet.getFullHeight() < nearestPlanet.getPosition().dst(position) && MAX_ZOOM_SPD < speed) {
                desiredViewDistance = Const.CAM_VIEW_DIST_JOURNEY;
            } else if (nearestPlanet.isNearGround(position) && speed < MED_ZOOM_SPD) {
                desiredViewDistance = Const.CAM_VIEW_DIST_GROUND;
            }
            desiredViewDistance += hero.getHull().config.getApproxRadius();
            return desiredViewDistance;
        }
    }

    private float calcZoom(float vd) {
        float h = vd * SolMath.sqrt(2);
        return h / VIEWPORT_HEIGHT;
    }

    private void applyZoom(MapDrawer mapDrawer) {
        if (mapDrawer.isToggled()) {
            myCam.zoom = mapDrawer.getZoom();
            return;
        }
        if (DebugOptions.ZOOM_OVERRIDE != 0) {
            myCam.zoom = DebugOptions.ZOOM_OVERRIDE;
            return;
        }
        myCam.zoom = myZoom;
    }

    private void applyPos(float posX, float posY) {
        myCam.position.set(posX, posY, 0);
    }

    private void applyInput(SolGame game) {
        MainGameScreen screen = game.getScreens().mainGameScreen;
        boolean d = screen.isCameraDown();
        boolean u = screen.isCameraUp();
        boolean l = screen.isCameraLeft();
        boolean r = screen.isCameraRight();
        Vector2 v = SolMath.getVec();
        if (l != r) {
            v.x = SolMath.toInt(r);
        }
        if (d != u) {
            v.y = SolMath.toInt(d);
        }
        v.scl(MOVE_SPD * game.getTimeStep());
        SolMath.rotate(v, myAngle);
        position.add(v);
        SolMath.free(v);
    }

    private void applyAngle() {
        Vector2 v = SolMath.getVec(0, 1);
        SolMath.rotate(v, myAngle);
        myCam.up.set(v.x, v.y, 0); // up is actually down, fcuk!!
        SolMath.free(v);
    }

    public float getViewDistance() {
        return getViewDistance(myZoom);
    }

    public float getViewDistance(float zoom) {
        float r = myCam.viewportWidth / myCam.viewportHeight;
        return .5f * VIEWPORT_HEIGHT * SolMath.sqrt(1 + r * r) * zoom;
    }

    /**
     * @return angle of a vector pointing right on screen
     */
    public float getAngle() {
        return myAngle;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPos(Vector2 position) {
        this.position.set(position);
    }

    public void drawDebug(GameDrawer drawer) {
        float hOver2 = VIEWPORT_HEIGHT * myZoom / 2;
        float wOver2 = hOver2 * displayDimensions.getRatio();
        Vector2 dr = SolMath.getVec(wOver2, hOver2);
        SolMath.rotate(dr, myAngle);
        Vector2 dl = SolMath.getVec(-wOver2, hOver2);
        SolMath.rotate(dl, myAngle);
        Vector2 ul = SolMath.getVec(dr);
        ul.scl(-1);
        Vector2 ur = SolMath.getVec(dl);
        ur.scl(-1);
        dr.add(position);
        dl.add(position);
        ul.add(position);
        ur.add(position);

        float lw = getRealLineWidth();
        drawer.drawLine(drawer.debugWhiteTexture, dr, dl, SolColor.WHITE, lw, false);
        drawer.drawLine(drawer.debugWhiteTexture, dl, ul, SolColor.WHITE, lw, false);
        drawer.drawLine(drawer.debugWhiteTexture, ul, ur, SolColor.WHITE, lw, false);
        drawer.drawLine(drawer.debugWhiteTexture, ur, dr, SolColor.WHITE, lw, false);

        SolMath.free(dr);
        SolMath.free(dl);
        SolMath.free(ul);
        SolMath.free(ur);
    }

    public float getRealLineWidth() {
        return getViewHeight(myCam.zoom) / Gdx.graphics.getHeight();
    }

    public float getViewHeight() {
        return getViewHeight(myZoom);
    }

    public float getViewHeight(float zoom) {
        float r = -myCam.viewportHeight * zoom;
        if (r < 0) {
            throw new AssertionError("negative view height");
        }
        return r;
    }

    public float getViewWidth() {
        float r = myCam.viewportWidth * myZoom;
        if (r < 0) {
            throw new AssertionError("negative view width");
        }
        return r;
    }

    public float getRealZoom() {
        return myCam.zoom;
    }

    public boolean isVisible(Vector2 position) {
        Vector2 rp = SolMath.toRel(position, myAngle, this.position);
        boolean res = isRelVisible(rp);
        SolMath.free(rp);
        return res;
    }

    public boolean isRelVisible(Vector2 rp) {
        float wHalf = getViewWidth() / 2;
        if (wHalf < SolMath.abs(rp.x)) {
            return false;
        }
        float hHalf = getViewHeight() / 2;
        if (hHalf < SolMath.abs(rp.y)) {
            return false;
        }
        return true;
    }

    public float getDebugFontSize() {
        return .04f * getRealZoom();
    }

    public void screenToWorld(Vector2 position) {
        myTmpVec.set(position, 0);
        myCam.unproject(myTmpVec);
        position.x = myTmpVec.x;
        position.y = myTmpVec.y;
    }

    public Vector2 worldToScreen(SolShip ship) {
        Vector2 distanceDifference = new Vector2(this.getPosition());
        distanceDifference.sub(ship.getPosition());
        distanceDifference.x /= this.getViewWidth();
        distanceDifference.x = .5f - distanceDifference.x;
        distanceDifference.y /= this.getViewHeight();
        distanceDifference.y = .5f - distanceDifference.y;
        return distanceDifference;
    }
}
