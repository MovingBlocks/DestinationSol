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

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.destinationsol.Const;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.FactionManager;
import org.destinationsol.game.HardnessCalc;
import org.destinationsol.game.Hero;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.Shield;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.screens.BorderDrawer;
import org.destinationsol.game.screens.GameScreens;
import org.destinationsol.game.screens.ZoneNameAnnouncer;
import org.destinationsol.game.ship.ShipAbility;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.UiDrawer;
import org.destinationsol.ui.nui.NUIManager;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.EmptyIfInvisibleContainer;
import org.destinationsol.ui.nui.widgets.UILabelledIcon;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.destinationsol.ui.nui.widgets.UIWarnDrawer;
import org.destinationsol.world.generators.SolarSystemGenerator;
import org.terasology.input.ButtonState;
import org.terasology.input.Keyboard;
import org.terasology.nui.AbstractWidget;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.HorizontalAlign;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.UIWidget;
import org.terasology.nui.VerticalAlign;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.events.NUIMouseButtonEvent;
import org.terasology.nui.layouts.ColumnLayout;
import org.terasology.nui.layouts.FlowLayout;
import org.terasology.nui.layouts.relative.HorizontalHint;
import org.terasology.nui.layouts.relative.RelativeLayout;
import org.terasology.nui.layouts.relative.RelativeLayoutHint;
import org.terasology.nui.layouts.relative.VerticalHint;
import org.terasology.nui.widgets.UIIconBar;
import org.terasology.nui.widgets.UIImage;
import org.terasology.nui.widgets.UILabel;
import org.terasology.nui.widgets.UILoadBar;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

/**
 * The main HUD screen displayed when in-game. This screen is responsible for the menu buttons shown
 * on the right-hand side of the UI. Through it, the menu, map, current ship inventory, communications UI
 * and mercenaries UI can be accessed.
 * It also displays status bars for weapons, ability cool-downs, health/shield percentages and HUD warnings.
 */
public class MainGameScreen extends NUIScreenLayer {
    private static final String WHITE_TEXTURE_URN = "engine:uiWhiteTex";
    private static final String COMPASS_TEXTURE_URN = "engine:uiCompass";
    private static final String LIFE_TEXTURE_URN = "engine:iconLife";
    private static final String REPAIR_ITEM_TEXTURE_URN = "engine:iconRepairItem";
    private static final String WAIT_TEXTURE_URN = "engine:iconWait";
    private static final String INFINITY_TEXTURE_URN = "engine:iconInfinity";
    private static final String CONSOLE_SCREEN_URN = "engine:console";
    private final CollisionWarnDrawerRayCastCallback warnCallback = new CollisionWarnDrawerRayCastCallback();
    private UITextureRegion whiteTexture;
    private TextureRegion compassTexture;
    private SolShip talkTarget;
    private UIWarnButton menuButton;
    private UIWarnButton mapButton;
    private UIWarnButton inventoryButton;
    private UIWarnButton talkButton;
    private UIWarnButton mercsButton;
    private ConsoleScreen consoleScreen;
    private AbstractWidget shieldStats;
    private AbstractWidget hullStats;
    private AbstractWidget gun1Stats;
    private AbstractWidget gun2Stats;
    private AbstractWidget abilityStats;
    private UILabelledIcon moneyIcon;
    private FlowLayout warnDrawers;
    private BorderDrawer borderDrawer;
    private ZoneNameAnnouncer zoneNameAnnouncer;
    private com.badlogic.gdx.graphics.Color compassTint;

    private final SolApplication solApplication;

    @Inject
    public MainGameScreen(SolApplication solApplication) {
        this.solApplication = solApplication;
    }

    @Override
    public void initialise() {
        whiteTexture = Assets.getDSTexture(WHITE_TEXTURE_URN).getUiTexture();
        compassTexture = Assets.getAtlasRegion(COMPASS_TEXTURE_URN);
        compassTint = SolColor.col(1, 0);

        consoleScreen = (ConsoleScreen) nuiManager.createScreen(CONSOLE_SCREEN_URN);

        GameOptions gameOptions = solApplication.getOptions();

        menuButton = find("menuButton", UIWarnButton.class);
        menuButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyMenu()));
        menuButton.subscribe(this::onMenuButtonClicked);

        mapButton = find("mapButton", UIWarnButton.class);
        mapButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyMap()));
        mapButton.subscribe(this::onMapButtonClicked);

        inventoryButton = find("itemsButton", UIWarnButton.class);
        inventoryButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyInventory()));
        inventoryButton.subscribe(this::onItemsButtonClicked);

        talkButton = find("talkButton", UIWarnButton.class);
        talkButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyTalk()));
        talkButton.subscribe(this::onTalkButtonClicked);

        mercsButton = find("mercsButton", UIWarnButton.class);
        mercsButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyMercenaryInteraction()));
        mercsButton.subscribe(this::onMercsButtonClicked);

        ColumnLayout statsBars = find("statsBars", ColumnLayout.class);

        shieldStats = createStatsRow("shield", new ReadOnlyBinding<UITextureRegion>() {
                    @Override
                    public UITextureRegion get() {
                        SolGame game = solApplication.getGame();
                        Hero hero = game.getHero();
                        Shield shield = hero.getShield();
                        if (shield != null) {
                            return Assets.getDSTexture(shield.getIcon(game).name).getUiTexture();
                        } else {
                            return null;
                        }
                    }
                },
                new ReadOnlyBinding<Float>() {
                    @Override
                    public Float get() {
                        Hero hero = solApplication.getGame().getHero();
                        Shield heroShield = hero.getShield();
                        if (heroShield != null) {
                            return heroShield.getLife() / heroShield.getMaxLife();
                        } else {
                            return 0.0f;
                        }
                    }
                }, new ReadOnlyBinding<String>() {
                    @Override
                    public String get() {
                        Hero hero = solApplication.getGame().getHero();
                        Shield heroShield = hero.getShield();
                        if (heroShield != null) {
                            return (int) Math.floor(heroShield.getLife()) + "/" + (int) Math.floor(heroShield.getMaxLife());
                        } else {
                            return "";
                        }
                    }
                }, new DefaultBinding<>(), null, new DefaultBinding<>(0.0f));
        statsBars.addWidget(shieldStats);

        hullStats = createStatsRow("health", new DefaultBinding<>(Assets.getDSTexture(LIFE_TEXTURE_URN).getUiTexture()),
                new ReadOnlyBinding<Float>() {
                    @Override
                    public Float get() {
                        Hero hero = solApplication.getGame().getHero();
                        if (hero.isNonTranscendent()) {
                            return hero.getLife() / hero.getHull().config.getMaxLife();
                        } else {
                            // The HUD is hidden when the hero is transcendent, so the value here doesn't matter.
                            return 1.0f;
                        }
                    }
                }, new ReadOnlyBinding<String>() {
                    @Override
                    public String get() {
                        Hero hero = solApplication.getGame().getHero();
                        if (hero.isNonTranscendent()) {
                            return (int) Math.floor(Math.max(0, hero.getLife())) + "/" + hero.getHull().config.getMaxLife();
                        } else {
                            // The HUD is hidden when the hero is transcendent, so the value here doesn't matter.
                            return "";
                        }
                    }
                }, new DefaultBinding<>(), Assets.getDSTexture(REPAIR_ITEM_TEXTURE_URN).getUiTexture(), new ReadOnlyBinding<Float>() {
                    @Override
                    public Float get() {
                        SolGame game = solApplication.getGame();
                        Hero hero = game.getHero();
                        return (float) hero.getItemContainer().count(game.getItemMan().getRepairExample());
                    }
                });
        statsBars.addWidget(hullStats);

        gun1Stats = createStatsRow("gun1", createGunIconBinding(false), createGunValueBinding(false),
                createGunValueLabelBinding(false), createGunBarIconBinding(false),
                null, createGunClipValueBinding(false));
        statsBars.addWidget(gun1Stats);

        gun2Stats = createStatsRow("gun2", createGunIconBinding(true), createGunValueBinding(true),
                createGunValueLabelBinding(true), createGunBarIconBinding(true),
                null, createGunClipValueBinding(true));
        statsBars.addWidget(gun2Stats);

        abilityStats = createStatsRow("ability", new ReadOnlyBinding<UITextureRegion>() {
            @Override
            public UITextureRegion get() {
                Hero hero = solApplication.getGame().getHero();
                ShipAbility ability = hero.getAbility();
                if (ability == null) {
                    return null;
                }

                if (ability.getCommonConfig().icon != null) {
                    return Assets.getDSTexture(ability.getCommonConfig().icon.name).getUiTexture();
                }

                SolItem example = ability.getConfig().getChargeExample();
                if (example != null) {
                    return Assets.getDSTexture(example.getIcon(solApplication.getGame()).name).getUiTexture();
                }
                return null;
            }
        }, new ReadOnlyBinding<Float>() {
            @Override
            public Float get() {
                Hero hero = solApplication.getGame().getHero();
                if (hero.getAbility() != null && hero.getAbilityAwait() > 0) {
                    return 1.0f - hero.getAbilityAwait() / hero.getAbility().getConfig().getRechargeTime();
                } else {
                    return 1.0f;
                }
            }
        }, new DefaultBinding<>(""), new ReadOnlyBinding<UITextureRegion>() {
            @Override
            public UITextureRegion get() {
                SolGame game = solApplication.getGame();
                Hero hero = game.getHero();
                return hero.getAbilityAwait() > 0 ? Assets.getDSTexture(WAIT_TEXTURE_URN).getUiTexture() : null;
            }
        }, null, new ReadOnlyBinding<Float>() {
            @Override
            public Float get() {
                SolGame game = solApplication.getGame();
                Hero hero = game.getHero();
                if (hero.getAbility() == null) {
                    return 0.0f;
                }

                SolItem example = hero.getAbility().getConfig().getChargeExample();
                if (example != null) {
                    return (float) hero.getItemContainer().count(example);
                }
                return 0.0f;
            }
        });
        statsBars.addWidget(abilityStats);

        moneyIcon = new UILabelledIcon("moneyIcon");
        moneyIcon.setSpacing(8);
        moneyIcon.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                Hero hero = solApplication.getGame().getHero();
                return Integer.toString(Math.round(hero.getMoney()));
            }
        });
        statsBars.addWidget(moneyIcon);

        statsBars.bindVisible(new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                return solApplication.getGame().getHero().isNonTranscendent();
            }
        });

        Color warnColour = new Color(1.0f, 0.5f, 0.0f, 0.5f);
        Color dangerColour = new Color(1.0f, 0.0f, 0.0f, 0.5f);

        warnDrawers = find("warnDrawers", FlowLayout.class);
        addWarnDrawer("sunWarnDrawer", warnColour, "Sun Near", new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                SolGame game = solApplication.getGame();
                Hero hero = game.getHero();
                if (hero.isTranscendent()) {
                    return false;
                }
                Vector2 position = hero.getPosition();
                float toCenter = game.getPlanetManager().getNearestSystem(position).getPosition().dst(position);
                return toCenter < SolarSystemGenerator.SUN_RADIUS;
            }
        });
        addWarnDrawer("damagedWarnDrawer", dangerColour, "Heavily Damaged", new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                Hero hero = solApplication.getGame().getHero();
                if (hero.isTranscendent()) {
                    return false;
                }
                float currentLife = hero.getLife();

                // already dead
                if (currentLife <= 0.0) {
                    return false;
                }

                int maxLife = hero.getHull().config.getMaxLife();
                return currentLife < maxLife * .3f;
            }
        });
        addWarnDrawer("collisionWarnDrawer", warnColour, "Object Near", new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                SolGame game = solApplication.getGame();
                Hero hero = game.getHero();
                if (hero.isTranscendent()) {
                    return false;
                }
                Vector2 position = hero.getPosition();
                Vector2 velocity = hero.getVelocity();
                float acc = hero.getAcceleration();
                float speed = velocity.len();
                float velocityAngle = SolMath.angle(velocity);
                if (acc <= 0 || speed < 2 * acc) {
                    return false;
                }
                // time = velocity/acceleration;
                // speed = acceleration*time*time/2 = velocity*velocity/acceleration/2;
                float breakWay = speed * speed / acc / 2;
                breakWay += 2 * speed;
                Vector2 finalPos = SolMath.getVec(0, 0);
                SolMath.fromAl(finalPos, velocityAngle, breakWay);
                finalPos.add(position);
                warnCallback.show = false;
                game.getObjectManager().getWorld().rayCast(warnCallback, position, finalPos);
                SolMath.free(finalPos);
                return warnCallback.show;
            }
        });
        addWarnDrawer("noShieldDrawer", warnColour, "No Shield", new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                Hero hero = solApplication.getGame().getHero();
                return hero.isNonTranscendent() && hero.getShield() == null;
            }
        });
        addWarnDrawer("noArmourDrawer", warnColour, "No Armor", new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                Hero hero = solApplication.getGame().getHero();
                return hero.isNonTranscendent() && hero.getArmor() == null;
            }
        });
        addWarnDrawer("noEngine", warnColour, "No Engine", new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                Hero hero = solApplication.getGame().getHero();
                return hero.isNonTranscendent() && hero.getHull().getEngine() == null;
            }
        });
        addWarnDrawer("enemyWarnDrawer", warnColour, "Dangerous Enemy", new ReadOnlyBinding<Boolean>() {
            @Override
            public Boolean get() {
                SolGame game = solApplication.getGame();
                Hero hero = game.getHero();
                if (hero.isTranscendent()) {
                    return false;
                }

                float heroCap = HardnessCalc.getShipDmgCap(hero.getShip());
                List<SolObject> objs = game.getObjectManager().getObjects();
                FactionManager fm = game.getFactionMan();
                SolCam cam = game.getContext().get(SolCam.class);
                float viewDist = cam.getViewDistance();
                float dps = 0;

                for (SolObject o : objs) {
                    if (!(o instanceof SolShip)) {
                        continue;
                    }

                    SolShip ship = (SolShip) o;

                    if (viewDist < ship.getPosition().dst(hero.getPosition())) {
                        continue;
                    }

                    if (!fm.areEnemies(hero.getShip(), ship)) {
                        continue;
                    }

                    dps += HardnessCalc.getShipDps(ship);

                    if (HardnessCalc.isDangerous(heroCap, dps)) {
                        return true;
                    }
                }

                return false;
            }
        });

        borderDrawer = new BorderDrawer();
        zoneNameAnnouncer = new ZoneNameAnnouncer();
    }

    @Override
    public void onAdded() {
        moneyIcon.setIcon(Assets.getDSTexture(solApplication.getGame().getItemMan().moneyIcon.name).getUiTexture());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        SolInputManager solInputManager = solApplication.getInputManager();
        GameScreens gameScreens = solApplication.getGame().getScreens();
        if (!nuiManager.hasScreen(gameScreens.menuScreen) &&
                !nuiManager.hasScreen(gameScreens.mapScreen)) {
            ((AbstractWidget) contents).setVisible(true);
        } else {
            ((AbstractWidget) contents).setVisible(false);
        }

        if (solInputManager.getTopScreen() != gameScreens.oldMainGameScreen) {
            // User is in an original UI menu, so disable the escape key toggling the pause menu.
            menuButton.setKey(Keyboard.Key.NONE);
        } else {
            menuButton.setKey(GDXInputUtil.GDXToNuiKey(solApplication.getOptions().getKeyMenu()));
        }

        // NOTE: Copied directly from the original MainGameScreen class. The logic hasn't been changed
        //       but some variables have been re-named to be more descriptive.
        SolGame game = solApplication.getGame();
        Hero hero = game.getHero();

        if (hero.isNonTranscendent() && !nuiManager.hasScreen(gameScreens.inventoryScreen)) {
            if (hero.getItemContainer().hasNew()) {
                inventoryButton.enableWarn();
            }

            if (hero.getMercs().hasNew()) {
                mercsButton.enableWarn();
            }
        }

        mercsButton.setEnabled(hero.isNonTranscendent());
        if (hero.isTranscendent()) {
            talkButton.setEnabled(false);
            return;
        }

        FactionManager factionManager = game.getFactionMan();

        // The ship can only communicate if there is a friendly target in range.
        talkTarget = null;
        float minDist = TalkScreen.MAX_TALK_DIST;
        float heroApproxRadius = hero.getHull().config.getApproxRadius();
        List<SolObject> objs = game.getObjectManager().getObjects();
        for (SolObject obj : objs) {
            if (!(obj instanceof SolShip)) {
                continue;
            }
            SolShip ship = (SolShip) obj;
            if (factionManager.areEnemies(hero.getShip(), ship)) {
                continue;
            }
            if (ship.getTradeContainer() == null) {
                continue;
            }
            float distance = ship.getPosition().dst(hero.getPosition());
            float shipApproxRadius = ship.getHull().config.getApproxRadius();
            if (minDist < distance - heroApproxRadius - shipApproxRadius) {
                continue;
            }
            talkTarget = ship;
            minDist = distance;
        }
        talkButton.setEnabled(talkTarget != null);

        if (consoleScreen.isConsoleJustClosed()) {
            game.setPaused(false);
        }

        shieldStats.setVisible(hero.getShield() != null);
        gun1Stats.setVisible(hero.getHull().getGun(false) != null);
        gun2Stats.setVisible(hero.getHull().getGun(true) != null);

        Gun gun1 = hero.getHull().getGun(false);
        if (gun1 != null) {
            UITextureRegion gun1ClipIcon;
            if (gun1.config.clipConf.infinite) {
                gun1ClipIcon = Assets.getDSTexture(INFINITY_TEXTURE_URN).getUiTexture();
            } else {
                gun1ClipIcon = Assets.getDSTexture(gun1.config.clipConf.icon.name).getUiTexture();
            }
            // HACK: You can't bind to the icons of a UIIconBar, so we set them during an update instead.
            gun1Stats.findAll(UIIconBar.class).iterator().next().setIcon(gun1ClipIcon);
        }

        Gun gun2 = hero.getHull().getGun(true);
        if (gun2 != null) {
            UITextureRegion gun2ClipIcon;
            if (gun2.config.clipConf.infinite) {
                gun2ClipIcon = Assets.getDSTexture(INFINITY_TEXTURE_URN).getUiTexture();
            } else {
                gun2ClipIcon = Assets.getDSTexture(gun2.config.clipConf.icon.name).getUiTexture();
            }
            // HACK: You can't bind to the icons of a UIIconBar, so we set them during an update instead.
            gun2Stats.findAll(UIIconBar.class).iterator().next().setIcon(gun2ClipIcon);
        }

        if (hero.getAbility() != null) {
            SolItem example = hero.getAbility().getConfig().getChargeExample();
            if (example != null) {
                UITextureRegion abilityIcon = Assets.getDSTexture(example.getIcon(solApplication.getGame()).name).getUiTexture();
                abilityStats.findAll(UIIconBar.class).iterator().next().setIcon(abilityIcon);
            }
        }

        zoneNameAnnouncer.update(solApplication.getGame(), solApplication.getGame().getContext());
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't render the borders on-top of the map screen.
        if (!nuiManager.hasScreen(solApplication.getGame().getScreens().mapScreen)) {
            try (NUIManager.LegacyUiDrawerWrapper wrapper = nuiManager.getLegacyUiDrawer()) {
                borderDrawer.draw(wrapper.getUiDrawer(), solApplication, solApplication.getGame().getContext());
                zoneNameAnnouncer.drawText(wrapper.getUiDrawer());
                drawHeightCompass(wrapper);
            }
        }
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (event.getState() == ButtonState.UP &&
                (event.getKey() == Keyboard.Key.GRAVE || event.getKey() == Keyboard.Key.F1) &&
                nuiManager.hasScreen(consoleScreen)) {
            nuiManager.removeScreen(consoleScreen);
            return true;
        }

        if (!contents.isVisible()) {
            return false;
        }

        if (event.getState() == ButtonState.UP &&
                (event.getKey() == Keyboard.Key.GRAVE || event.getKey() == Keyboard.Key.F1)) {
            if (!nuiManager.hasScreen(consoleScreen)) {
                nuiManager.pushScreen(consoleScreen);
                solApplication.getGame().setPaused(true);
            }
            return true;
        }

        GameOptions gameOptions = solApplication.getOptions();
        // TODO: How to handle free camera movement on Android? (note: this has never been supported)
        if (event.getKey() == GDXInputUtil.GDXToNuiKey(gameOptions.getKeyFreeCameraMovement())) {
            SolCam.DIRECT_CAM_CONTROL = event.isDown();
        }

        SolGame solGame = solApplication.getGame();
        if (event.getState() == ButtonState.UP && event.getKey() == GDXInputUtil.GDXToNuiKey(gameOptions.getKeyPause())) {
            solGame.setPaused(!solGame.isPaused());
        }

        return super.onKeyEvent(event);
    }

    @Override
    public void onMouseButtonEvent(NUIMouseButtonEvent event) {
        if (event.getState() == ButtonState.UP) {
            NUIScreenLayer topScreen = nuiManager.getTopScreen();
            if (!solApplication.getInputManager().isMouseOnUi() &&
                    topScreen != MainGameScreen.this && !(topScreen instanceof UIShipControlsScreen)) {
                nuiManager.popScreen();
                event.consume();
            }
        }
    }

    @Override
    public void onRemoved() {
        menuButton.unsubscribe(this::onMenuButtonClicked);
        mapButton.unsubscribe(this::onMapButtonClicked);
        inventoryButton.unsubscribe(this::onItemsButtonClicked);
        talkButton.unsubscribe(this::onTalkButtonClicked);
        mercsButton.unsubscribe(this::onMercsButtonClicked);
    }

    /**
     * Returns the "Menu" UI Button element.
     * @return the "Menu" UI Button
     */
    public UIWarnButton getMenuButton() {
        return menuButton;
    }

    /**
     * Returns the "Map" UI Button element.
     * @return The "Map" UI Button
     */
    public UIWarnButton getMapButton() {
        return mapButton;
    }

    /**
     * Returns the "Items" UI Button element.
     * @return The "Items" UI Button
     */
    public UIWarnButton getInventoryButton() {
        return inventoryButton;
    }

    /**
     * Returns the "Talk" UI Button element.
     * @return The "Talk" UI Button
     */
    public UIWarnButton getTalkButton() {
        return talkButton;
    }

    /**
     * Returns the "Mercs" UI Button element.
     * @return The "Mercs" UI Button
     */
    public UIWarnButton getMercsButton() {
        return mercsButton;
    }

    /**
     * Creates and adds a new warn drawer with the specified properties and activation condition.
     * @param id the id used for the drawer widget
     * @param tint the tint used for the warning background
     * @param text the text to be shown in the warning
     * @param warnBinding the condition for the warning to be shown
     * @return the {@link UIWarnDrawer} created
     */
    public UIWarnDrawer addWarnDrawer(String id, Color tint, String text, Binding<Boolean> warnBinding) {
        UIWarnDrawer warnDrawer = new UIWarnDrawer(id, whiteTexture, tint, new UILabel(text));
        warnDrawer.bindWarn(warnBinding);
        warnDrawers.addWidget(new EmptyIfInvisibleContainer(warnDrawer), new RelativeLayoutHint());
        return warnDrawer;
    }

    /**
     * Adds the provided warn drawer to be displayed when the condition {@link UIWarnDrawer#isWarning()} is true.
     * @param warnDrawer the warn-drawer instance to use
     * @return the warn-drawer instance used.
     */
    public UIWarnDrawer addWarnDrawer(UIWarnDrawer warnDrawer) {
        warnDrawers.addWidget(new EmptyIfInvisibleContainer(warnDrawer), new RelativeLayoutHint());
        return warnDrawer;
    }

    /**
     * Returns true if a warn-drawer with the specified id already exists, otherwise false.
     * @param id the id to search for
     * @return true, if the drawer exists, otherwise false.
     */
    public boolean hasWarnDrawer(String id) {
        return warnDrawers.find(id, UIWarnDrawer.class) != null;
    }

    /**
     * Returns true if the specified warn-drawer already exists, otherwise false.
     * @param warnDrawerToFind the drawer to search for
     * @return true, if the drawer exists, otherwise false.
     */
    public boolean hasWarnDrawer(UIWarnDrawer warnDrawerToFind) {
        for (UIWidget warnDrawerContainer : warnDrawers) {
            if (warnDrawerContainer instanceof EmptyIfInvisibleContainer) {
                EmptyIfInvisibleContainer container = (EmptyIfInvisibleContainer) warnDrawerContainer;
                if (container.iterator().hasNext() && container.iterator().next() == warnDrawerToFind) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Removes the warn-drawer with the specified id from use.
     * @param id the id of the warn-drawer to remove
     */
    public void removeWarnDrawer(String id) {
        UIWarnDrawer warnDrawer = warnDrawers.find(id, UIWarnDrawer.class);
        if (warnDrawer != null) {
            warnDrawers.removeWidget(warnDrawer);
        }
    }

    /**
     * Removes the specified warn drawer from use.
     * @param warnDrawer the warn-drawer instance to remove
     */
    public void removeWarnDrawer(UIWarnDrawer warnDrawer) {
        warnDrawers.removeWidget(warnDrawer);
    }

    private void drawHeightCompass(NUIManager.LegacyUiDrawerWrapper drawerWrapper) {
        SolGame game = solApplication.getGame();
        Planet np = game.getPlanetManager().getNearestPlanet();
        SolCam cam = solApplication.getGame().getCam();
        Vector2 camPos = cam.getPosition();
        if (np != null && np.getPosition().dst(camPos) < np.getFullHeight()) {
            UiDrawer drawer = drawerWrapper.getUiDrawer();

            float toPlanet = camPos.dst(np.getPosition());
            toPlanet -= np.getGroundHeight();
            if (Const.ATM_HEIGHT < toPlanet) {
                return;
            }

            float perc = toPlanet / Const.ATM_HEIGHT;
            float sz = .08f;
            float maxY = 1 - sz / 2;
            float y = 1 - perc;
            compassTint.a = SolMath.clamp(1.5f * y);
            if (maxY < y) {
                y = maxY;
            }
            float angle = np.getAngle() - cam.getAngle();
            drawer.draw(compassTexture, sz, sz, sz / 2, sz / 2, sz / 2, y, angle, compassTint);
        }
    }

    private AbstractWidget createStatsRow(String name, Binding<UITextureRegion> statIcon, Binding<Float> statBinding,
                                          Binding<String> statBarTextBinding, Binding<UITextureRegion> statBarIconBinding,
                                          UITextureRegion refillIcon, Binding<Float> refillBinding) {
        String iconId = name + "Icon";
        String statsBarTextId = name + "statsBarText";
        String refillExtraTextId = name + "refillExtraText";
        VerticalHint iconHeightHint = new VerticalHint()
                .alignTopRelativeTo(iconId, VerticalAlign.TOP)
                .alignBottomRelativeTo(iconId, VerticalAlign.BOTTOM);

        RelativeLayout relativeLayout = new RelativeLayout();
        UIImage icon = new UIImage(iconId, null);
        icon.bindTexture(statIcon);
        relativeLayout.addWidget(icon, new RelativeLayoutHint(
                new HorizontalHint().alignLeft(),
                new VerticalHint()
        ).setUsingContentWidth(true).setUsingContentHeight(true));
        UILoadBar statsBar = new UILoadBar();
        statsBar.setAnimate(false);
        statsBar.setFillTexture(Assets.getDSTexture("engine:buttonDown").getUiTexture());
        statsBar.bindValue(statBinding);
        relativeLayout.addWidget(statsBar, new RelativeLayoutHint(
                new HorizontalHint().alignLeftRelativeTo(iconId, HorizontalAlign.RIGHT, 8).alignRight(134),
                iconHeightHint
        ));
        UILabelledIcon statsValueText = new UILabelledIcon(statsBarTextId, "");
        statsValueText.setFamily("centerAlignedLabel");
        statsValueText.setSpacing(0);
        statsValueText.setIconAlign(HorizontalAlign.CENTER);
        statsValueText.bindText(statBarTextBinding);
        statsValueText.bindIcon(statBarIconBinding);
        relativeLayout.addWidget(statsValueText, new RelativeLayoutHint(
                new HorizontalHint().alignLeftRelativeTo(iconId, HorizontalAlign.RIGHT, 8).alignRight(134),
                iconHeightHint
        ));

        UIIconBar refillIconsBar = new UIIconBar();
        refillIconsBar.setMaxIcons(3);
        refillIconsBar.setMaxValue(3);
        refillIconsBar.setHalfIconMode(UIIconBar.HalfIconMode.NONE);
        // NOTE: UIIconBar doesn't support icon bindings.
        refillIconsBar.setIcon(refillIcon);
        refillIconsBar.bindValue(refillBinding);
        relativeLayout.addWidget(refillIconsBar, new RelativeLayoutHint(
                new HorizontalHint()
                        .alignLeftRelativeTo(statsBarTextId, HorizontalAlign.RIGHT, 8)
                        .alignRightRelativeTo(refillExtraTextId, HorizontalAlign.LEFT),
                iconHeightHint
        ));

        UILabelledIcon refillExtrasText = new UILabelledIcon(refillExtraTextId, "  ");
        refillExtrasText.setFamily("topLeftAlignedLabel");
        refillExtrasText.setSpacing(0);
        refillExtrasText.bindText(new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                float value = refillBinding.get();
                if (value <= 3) {
                    return "  ";
                }
                return "+" + String.format(Locale.ENGLISH, "%-2d", ((int)value - 3));
            }
        });
        relativeLayout.addWidget(refillExtrasText, new RelativeLayoutHint(
                new HorizontalHint().alignRight(),
                iconHeightHint
        ).setUsingContentWidth(true));

        return new EmptyIfInvisibleContainer(relativeLayout);
    }

    private ReadOnlyBinding<UITextureRegion> createGunIconBinding(boolean isSecondary) {
        return new ReadOnlyBinding<UITextureRegion>() {
            @Override
            public UITextureRegion get() {
                Hero hero = solApplication.getGame().getHero();
                Gun gun = hero.getHull().getGun(isSecondary);
                if (gun != null) {
                    return Assets.getDSTexture(gun.getIcon(solApplication.getGame()).name).getUiTexture();
                }
                return null;
            }
        };
    }

    private ReadOnlyBinding<Float> createGunClipValueBinding(boolean isSecondary) {
        return new ReadOnlyBinding<Float>() {
            @Override
            public Float get() {
                SolGame game = solApplication.getGame();
                Hero hero = game.getHero();
                Gun gun = hero.getHull().getGun(isSecondary);
                if (gun == null) {
                    return 0.0f;
                }
                if (gun.config.clipConf.infinite) {
                    return 1.0f;
                }
                return (float) hero.getItemContainer().count(gun.config.clipConf.example);
            }
        };
    }

    private ReadOnlyBinding<Float> createGunValueBinding(boolean isSecondary) {
        return new ReadOnlyBinding<Float>() {
            @Override
            public Float get() {
                Hero hero = solApplication.getGame().getHero();
                Gun gun = hero.getHull().getGun(isSecondary);
                if (gun == null) {
                    return 0.0f;
                }
                if (gun.reloadAwait > 0) {
                    return 1.0f - (gun.reloadAwait / gun.config.reloadTime);
                } else {
                    return (float)gun.ammo / gun.config.clipConf.size;
                }
            }
        };
    }

    private ReadOnlyBinding<String> createGunValueLabelBinding(boolean isSecondary) {
        return new ReadOnlyBinding<String>() {
            @Override
            public String get() {
                SolGame game = solApplication.getGame();
                Hero hero = game.getHero();
                Gun gun = hero.getHull().getGun(isSecondary);
                if (gun == null) {
                    return "";
                }
                if (gun.reloadAwait > 0) {
                    return "";
                } else {
                    return gun.ammo + "/" + gun.config.clipConf.size;
                }
            }
        };
    }

    private ReadOnlyBinding<UITextureRegion> createGunBarIconBinding(boolean isSecondary) {
        return new ReadOnlyBinding<UITextureRegion>() {
            @Override
            public UITextureRegion get() {
                SolGame game = solApplication.getGame();
                Hero hero = game.getHero();
                Gun gun = hero.getHull().getGun(isSecondary);
                if (gun == null) {
                    return null;
                }
                return gun.reloadAwait > 0 ? Assets.getDSTexture("engine:iconWait").getUiTexture() : null;
            }
        };
    }

    private void toggleScreen(NUIScreenLayer screen) {
        if (nuiManager.hasScreen(screen)) {
            nuiManager.removeScreen(screen);
            return;
        }

        while (!(nuiManager.getTopScreen() instanceof MainGameScreen) && !(nuiManager.getTopScreen() instanceof UIShipControlsScreen)) {
            nuiManager.popScreen();
        }
        solApplication.getInputManager().setScreen(solApplication, solApplication.getGame().getScreens().oldMainGameScreen);
        nuiManager.pushScreen(screen);
    }

    private void onMenuButtonClicked(UIWidget widget) {
        GameScreens gameScreens = solApplication.getGame().getScreens();
        toggleScreen(gameScreens.menuScreen);
    }

    private void onMapButtonClicked(UIWidget widget) {
        GameScreens gameScreens = solApplication.getGame().getScreens();
        toggleScreen(gameScreens.mapScreen);
    }

    private void onItemsButtonClicked(UIWidget widget) {
        SolGame game = solApplication.getGame();
        Hero hero = game.getHero();
        if (hero.isTranscendent()) {
            return;
        }

        GameScreens gameScreens = game.getScreens();
        gameScreens.inventoryScreen.getShowInventory().setTarget(hero.getShip());
        gameScreens.inventoryScreen.setOperations(gameScreens.inventoryScreen.getShowInventory());
        toggleScreen(gameScreens.inventoryScreen);
    }

    private void onTalkButtonClicked(UIWidget widget) {
        SolGame game = solApplication.getGame();
        if (game.getHero().isTranscendent()) {
            return;
        }

        GameScreens gameScreens = game.getScreens();
        gameScreens.talkScreen.setTarget(talkTarget);
        toggleScreen(gameScreens.talkScreen);
    }

    private void onMercsButtonClicked(UIWidget widget) {
        SolGame game = solApplication.getGame();
        Hero hero = game.getHero();
        if (hero.isTranscendent()) {
            return;
        }

        GameScreens gameScreens = game.getScreens();
        gameScreens.inventoryScreen.setOperations(gameScreens.inventoryScreen.getChooseMercenaryScreen());
        toggleScreen(gameScreens.inventoryScreen);
        hero.getMercs().markAllAsSeen();
    }

    private final class CollisionWarnDrawerRayCastCallback implements RayCastCallback {
        private boolean show;

        //TODO code from era when hero was SolShip - does this still work? (what is it supposed to do?)
        // TODO: Moved from the original MainGameScreen - still don't know what this does.
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (fixture.getBody().getUserData() instanceof SolObject) {
                SolObject o = (SolObject) fixture.getBody().getUserData();
                if (solApplication.getGame().getHero().getShip() == o) {
                    return -1;
                }
                show = true;
            }
            return 0;
        }
    }
}
