package org.destinationsol.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.SolFileReader;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.soundtest.SoundTestListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SolDesktop {
    public static void main(String[] argv) {
        if (false) {
            new LwjglApplication(new SoundTestListener(), "sound test", 800, 600);
            return;
        }

        LwjglApplicationConfiguration c = new LwjglApplicationConfiguration();
        boolean devBuild = java.nio.file.Files.exists(Paths.get("devBuild"));
        if (devBuild) {
            // TODO: Not sure this ever really made sense ... should probably just improve the loading of debug config instead of running out of the resource dir
            DebugOptions.DEV_ROOT_PATH = "core/build/classes/"; // Lets the game run from source without a tweaked working directory
            c.vSyncEnabled = false; //Setting to false disables vertical sync
            c.foregroundFPS = 0; //disables foreground fps throttling
            c.backgroundFPS = 0; //disables background fps throttling
        }
        MyReader reader = new MyReader();
        DebugOptions.read(reader);


        if (DebugOptions.EMULATE_MOBILE) {
            c.width = 640;
            c.height = 480;
            c.fullscreen = false;
        } else {
            GameOptions d = new GameOptions(false, reader);
            c.width = d.x;
            c.height = d.y;
            c.fullscreen = d.fullscreen;
        }

        c.title = "Destination Sol";
        if (DebugOptions.DEV_ROOT_PATH == null) {
            c.addIcon("assets/icon.png", Files.FileType.Internal);
        } else {
            c.addIcon(DebugOptions.DEV_ROOT_PATH + "assets/icon.png", Files.FileType.Absolute);
        }

        new LwjglApplication(new SolApplication(), c);
    }

    private static class MyReader implements SolFileReader {
        @Override
        public List<String> read(String fileName) {
            if (DebugOptions.DEV_ROOT_PATH != null) {
                fileName = DebugOptions.DEV_ROOT_PATH + fileName;
            }
            ArrayList<String> lines = new ArrayList<String>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                String line = "";
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                br.close();
            } catch (IOException ignore) {
            }
            return lines;
        }
    }
}
