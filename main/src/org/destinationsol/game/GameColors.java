package org.destinationsol.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.files.FileManager;

import java.util.HashMap;

public class GameColors {

    public final Color fire;
    public final Color smoke;
    public final Color hullLights;

    private final HashMap<String, Color> colors;

    public GameColors() {
        JsonReader r = new JsonReader();
        FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("colors.json");
        JsonValue node = r.parse(configFile);
        colors = new HashMap<String, Color>();

        for (JsonValue colVal : node) {
            Color c = load(colVal.asString());
            colors.put(colVal.name, c);
        }

        fire = get("fire");
        smoke = get("smoke");
        hullLights = get("hullLights");
    }

    public Color get(String name) {
        Color result = colors.get(name);

        if (result == null) {
            throw new AssertionError("Color " + name + " is not defined.");
        }

        return result;
    }

    public Color load(String s) {
        if (s.contains(" ")) {
            return SolColorUtil.load(s);
        }

        return get(s);
    }
}
