package org.destinationsol.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.files.FileManager;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.sound.SoundManager;

public class AbilityCommonConfigs {
    public final AbilityCommonConfig teleport;
    public final AbilityCommonConfig emWave;
    public final AbilityCommonConfig unShield;
    public final AbilityCommonConfig knockBack;
    public final AbilityCommonConfig sloMo;

    public AbilityCommonConfigs(EffectTypes effectTypes, TextureManager textureManager, GameColors cols, SoundManager soundManager) {
        JsonReader r = new JsonReader();

        FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("abilities.json");
        JsonValue node = r.parse(configFile);
        teleport = AbilityCommonConfig.load(node.get("teleport"), effectTypes, textureManager, cols, configFile, soundManager);
        emWave = AbilityCommonConfig.load(node.get("emWave"), effectTypes, textureManager, cols, configFile, soundManager);
        unShield = AbilityCommonConfig.load(node.get("unShield"), effectTypes, textureManager, cols, configFile, soundManager);
        knockBack = AbilityCommonConfig.load(node.get("knockBack"), effectTypes, textureManager, cols, configFile, soundManager);
        sloMo = AbilityCommonConfig.load(node.get("sloMo"), effectTypes, textureManager, cols, configFile, soundManager);
    }
}
