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
package org.destinationsol.game.projectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.Faction;
import org.destinationsol.game.FactionManager;
import org.destinationsol.game.FarObj;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.LightSrc;
import org.destinationsol.game.particle.ParticleSrc;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class Projectile implements SolObject {

    private static final float MIN_ANGLE_TO_GUIDE = 2f;
    private final ArrayList<Drawable> myDrawables;
    private final ProjectileBody myBody;
    private final Faction myFaction;
    private final ParticleSrc myBodyEffect;
    private final ParticleSrc myTrailEffect;
    private final LightSrc myLightSrc;
    private final ProjectileConfig myConfig;

    private boolean myShouldRemove;
    private SolObject myObstacle;
    private boolean myDamageDealt;

    public Projectile(SolGame game, float angle, Vector2 muzzlePos, Vector2 gunSpd, Faction faction,
                      ProjectileConfig config, boolean varySpd) {
        myDrawables = new ArrayList<>();
        myConfig = config;

        Drawable drawable;
        if (myConfig.stretch) {
            drawable = new MyDrawable(this, myConfig.tex, myConfig.texSz);
        } else {
            drawable = new RectSprite(myConfig.tex, myConfig.texSz, myConfig.origin.x, myConfig.origin.y, new Vector2(), DrawableLevel.PROJECTILES, 0, 0, SolColor.WHITE, false);
        }
        myDrawables.add(drawable);
        float spdLen = myConfig.spdLen;
        if (varySpd) {
            spdLen *= SolMath.rnd(.9f, 1.1f);
        }
        if (myConfig.physSize > 0) {
            myBody = new BallProjectileBody(game, muzzlePos, angle, this, gunSpd, spdLen, myConfig);
        } else {
            myBody = new PointProjectileBody(angle, muzzlePos, gunSpd, spdLen, this, game, myConfig.acc);
        }
        myFaction = faction;
        myBodyEffect = buildEffect(game, myConfig.bodyEffect, DrawableLevel.PART_BG_0, null, true);
        myTrailEffect = buildEffect(game, myConfig.trailEffect, DrawableLevel.PART_BG_0, null, false);
        if (myConfig.lightSz > 0) {
            Color col = SolColor.WHITE;
            if (myBodyEffect != null) {
                col = myConfig.bodyEffect.tint;
            }
            myLightSrc = new LightSrc(myConfig.lightSz, true, 1f, new Vector2(), col);
            myLightSrc.collectDras(myDrawables);
        } else {
            myLightSrc = null;
        }
    }

    private ParticleSrc buildEffect(SolGame game, EffectConfig ec, DrawableLevel drawableLevel, Vector2 pos, boolean inheritsSpd) {
        if (ec == null) {
            return null;
        }
        ParticleSrc res = new ParticleSrc(ec, -1, drawableLevel, new Vector2(), inheritsSpd, game, pos, myBody.getSpd(), 0);
        if (res.isContinuous()) {
            res.setWorking(true);
            myDrawables.add(res);
        } else {
            game.getPartMan().finish(game, res, pos);
        }
        return res;
    }

    @Override
    public void update(SolGame game) {
        myBody.update(game);
        if (myObstacle != null) {
            if (!myDamageDealt) {
                myObstacle.receiveDmg(myConfig.dmg, game, myBody.getPos(), myConfig.dmgType);
            }
            if (myConfig.density > 0) {
                myObstacle = null;
                myDamageDealt = true;
            } else {
                collided(game);
                if (myConfig.emTime > 0 && myObstacle instanceof SolShip) {
                    ((SolShip) myObstacle).disableControls(myConfig.emTime, game);
                }
                return;
            }
        }
        if (myLightSrc != null) {
            myLightSrc.update(true, myBody.getAngle(), game);
        }
        maybeGuide(game);
        game.getSoundManager().play(game, myConfig.workSound, null, this);
    }

    private void maybeGuide(SolGame game) {
        if (myConfig.guideRotSpd == 0) {
            return;
        }
        float ts = game.getTimeStep();
        SolShip ne = game.getFactionMan().getNearestEnemy(game, this);
        if (ne == null) {
            return;
        }
        float desiredAngle = myBody.getDesiredAngle(ne);
        float angle = getAngle();
        float diffAngle = SolMath.norm(desiredAngle - angle);
        if (SolMath.abs(diffAngle) < MIN_ANGLE_TO_GUIDE) {
            return;
        }
        float rot = ts * myConfig.guideRotSpd;
        diffAngle = SolMath.clamp(diffAngle, -rot, rot);
        myBody.changeAngle(diffAngle);
    }

    private void collided(SolGame game) {
        myShouldRemove = true;
        Vector2 pos = myBody.getPos();
        buildEffect(game, myConfig.collisionEffect, DrawableLevel.PART_FG_1, pos, false);
        buildEffect(game, myConfig.collisionEffectBg, DrawableLevel.PART_FG_0, pos, false);
        if (myConfig.collisionEffectBg != null) {
            game.getPartMan().blinks(pos, game, myConfig.collisionEffectBg.sz);
        }
        game.getSoundManager().play(game, myConfig.collisionSound, null, this);
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return myShouldRemove;
    }

    @Override
    public void onRemove(SolGame game) {
        Vector2 pos = myBody.getPos();
        if (myBodyEffect != null) {
            game.getPartMan().finish(game, myBodyEffect, pos);
        }
        if (myTrailEffect != null) {
            game.getPartMan().finish(game, myTrailEffect, pos);
        }
        myBody.onRemove(game);
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
        if (myConfig.density > 0) {
            return;
        }
        collided(game);
    }

    @Override
    public boolean receivesGravity() {
        return !myConfig.massless;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
        myBody.receiveForce(force, game, acc);
    }

    @Override
    public Vector2 getPosition() {
        return myBody.getPos();
    }

    @Override
    public FarObj toFarObj() {
        return null;
    }

    @Override
    public List<Drawable> getDrawables() {
        return myDrawables;
    }

    @Override
    public float getAngle() {
        return myBody.getAngle();
    }

    @Override
    public Vector2 getSpd() {
        return myBody.getSpd();
    }

    @Override
    public void handleContact(SolObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                              SolGame game, Vector2 collPos) {
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    public Faction getFaction() {
        return myFaction;
    }

    public boolean shouldCollide(SolObject o, Fixture f, FactionManager factionManager) {
        if (o instanceof SolShip) {
            SolShip s = (SolShip) o;
            if (!factionManager.areEnemies(s.getPilot().getFaction(), myFaction)) {
                return false;
            }
            if (s.getHull().getShieldFixture() == f) {
                if (myConfig.density > 0) {
                    return false;
                }
                Shield shield = s.getShield();
                if (shield == null || !shield.canAbsorb(myConfig.dmgType)) {
                    return false;
                }
            }
            return true;
        }
        if (o instanceof Projectile) {
            if (!factionManager.areEnemies(((Projectile) o).myFaction, myFaction)) {
                return false;
            }
        }
        return true;
    }

    public void setObstacle(SolObject o, SolGame game) {
        if (!shouldCollide(o, null, game.getFactionMan())) {
            return; // happens for some reason when projectile is just created
        }
        myObstacle = o;
    }

    public boolean isMassless() {
        return myConfig.massless;
    }

    public ProjectileConfig getConfig() {
        return myConfig;
    }

    private static class MyDrawable implements Drawable {
        private final Projectile myProjectile;
        private final TextureAtlas.AtlasRegion myTex;
        private final float myWidth;

        public MyDrawable(Projectile projectile, TextureAtlas.AtlasRegion tex, float width) {
            myProjectile = projectile;
            myTex = tex;
            myWidth = width;
        }

        @Override
        public Texture getTex0() {
            return myTex.getTexture();
        }

        @Override
        public TextureAtlas.AtlasRegion getTex() {
            return myTex;
        }

        @Override
        public DrawableLevel getLevel() {
            return DrawableLevel.PROJECTILES;
        }

        @Override
        public void update(SolGame game, SolObject o) {
        }

        @Override
        public void prepare(SolObject o) {
        }

        @Override
        public Vector2 getPos() {
            return myProjectile.getPosition();
        }

        @Override
        public Vector2 getRelPos() {
            return Vector2.Zero;
        }

        @Override
        public float getRadius() {
            return myProjectile.myConfig.texSz / 2;
        }

        @Override
        public void draw(GameDrawer drawer, SolGame game) {
            float h = myWidth;
            float minH = game.getCam().getRealLineWidth() * 3;
            if (h < minH) {
                h = minH;
            }
            Vector2 pos = myProjectile.getPosition();
            float w = myProjectile.getSpd().len() * game.getTimeStep();
            if (w < 4 * h) {
                w = 4 * h;
            }
            drawer.draw(myTex, w, h, w, h / 2, pos.x, pos.y, SolMath.angle(myProjectile.getSpd()), SolColor.LG);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean okToRemove() {
            return false;
        }

    }

}
