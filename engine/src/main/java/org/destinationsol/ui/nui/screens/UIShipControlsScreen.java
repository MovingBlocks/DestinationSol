package org.destinationsol.ui.nui.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.Hero;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.screens.ShipUiControl;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.terasology.input.ButtonState;
import org.terasology.input.Keyboard;
import org.terasology.nui.AbstractWidget;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.widgets.UIButton;

public class UIShipControlsScreen extends NUIScreenLayer implements ShipUiControl {
    public KeyActivatedButton leftButton;
    public KeyActivatedButton rightButton;
    public KeyActivatedButton forwardButton;
    public KeyActivatedButton gun1Button;
    public KeyActivatedButton gun2Button;
    public KeyActivatedButton abilityButton;
    private Keyboard.Key downKey;
    private boolean downKeyHeld;

    @Override
    public void initialise() {
        SolApplication solApplication = nuiManager.getSolApplication();
        GameOptions gameOptions = solApplication.getOptions();

        leftButton = find("leftButton", KeyActivatedButton.class);
        leftButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyLeft()));

        rightButton = find("rightButton", KeyActivatedButton.class);
        rightButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyRight()));

        forwardButton = find("forwardButton", KeyActivatedButton.class);
        forwardButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyUp()));

        gun1Button = find("gun1Button", KeyActivatedButton.class);
        gun1Button.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyShoot()));

        gun2Button = find("gun2Button", KeyActivatedButton.class);
        gun2Button.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyShoot2()));

        abilityButton = find("abilityButton", KeyActivatedButton.class);
        abilityButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyAbility()));

        downKey = GDXInputUtil.GDXToNuiKey(gameOptions.getKeyDown());

        if (!solApplication.isMobile()) {
            leftButton.setVisible(false);
            rightButton.setVisible(false);
            forwardButton.setVisible(false);
            gun1Button.setVisible(false);
            gun2Button.setVisible(false);
            abilityButton.setVisible(false);
        }
    }

    /**
     * Implementation of {@link org.terasology.nui.UIWidget#update(float)}
     * @param delta the time elapsed since the last update cycle
     */
    @Override
    public void update(float delta) {
        SolApplication solApplication = nuiManager.getSolApplication();

        // Hide controls if the use is looking at a different main game screen.
        ((AbstractWidget)contents).setVisible(solApplication.getInputManager().getTopScreen() == solApplication.getGame().getScreens().mainGameScreen);
        super.update(delta);
    }

    /**
     * Implementation of {@link ShipUiControl#update(SolApplication, boolean)}
     * @param solApplication an instance of the game's {@link SolApplication}
     * @param enabled are the UI controls currently enabled
     */
    @Override
    public void update(SolApplication solApplication, boolean enabled) {
        if (!enabled) {
            leftButton.setEnabled(false);
            rightButton.setEnabled(false);
            forwardButton.setEnabled(false);
            gun1Button.setEnabled(false);
            gun2Button.setEnabled(false);
            abilityButton.setEnabled(false);
            return;
        }

        Hero hero = solApplication.getGame().getHero();
        boolean hasEngine = hero.isNonTranscendent() && hero.getHull().getEngine() != null;
        forwardButton.setEnabled(hasEngine);
        leftButton.setEnabled(hasEngine);
        rightButton.setEnabled(hasEngine);

        Gun g1 = hero.isTranscendent() ? null : hero.getHull().getGun(false);
        gun1Button.setEnabled(g1 != null && g1.ammo > 0);
        Gun g2 = hero.isTranscendent() ? null : hero.getHull().getGun(true);
        gun2Button.setEnabled(g2 != null && g2.ammo > 0);

        // The ability button needs to de-press before it is disabled,
        // as otherwise it causes the button to be pressed again when it is enabled.
        if (!abilityButton.getMode().equals(UIButton.DOWN_MODE)) {
            abilityButton.setEnabled(hero.isNonTranscendent() && hero.canUseAbility());
        }
    }

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (event.getKey() == downKey) {
            downKeyHeld = (event.getState() == ButtonState.DOWN);
            return true;
        }

        return super.onKeyEvent(event);
    }

    @Override
    public boolean isLeft() {
        return leftButton.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    public boolean isRight() {
        return rightButton.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    public boolean isUp() {
        return forwardButton.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    public boolean isDown() {
        // TODO
        return downKeyHeld;
    }

    @Override
    public boolean isShoot() {
        return gun1Button.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    public boolean isShoot2() {
        return gun2Button.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    public boolean isAbility() {
        return abilityButton.getMode().equals(UIButton.DOWN_MODE);
    }
}
