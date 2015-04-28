package com.miloshpetrov.sol2.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.miloshpetrov.sol2.SolAppListener;
import com.miloshpetrov.sol2.SolFileReader;
import com.miloshpetrov.sol2.game.DebugOptions;
import com.miloshpetrov.sol2.GameOptions;
import com.miloshpetrov.sol2.soundtest.SoundTestListener;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SolDesktop {
    public static void main(String[] argv) {
        if (false) {
            new LwjglApplication(new SoundTestListener(), "sound test", 800, 600, false);
            return;
        }

        boolean devBuild = java.nio.file.Files.exists(Paths.get("devBuild"));
        if (devBuild) {
            DebugOptions.DEV_ROOT_PATH = "main/"; // Lets the game run from source without a tweaked working directory
        }
        MyReader reader = new MyReader();
        DebugOptions.read(reader);

        LwjglApplicationConfiguration c = new LwjglApplicationConfiguration();
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
            c.addIcon("res/icon.png", Files.FileType.Internal);
        } else {
            c.addIcon(DebugOptions.DEV_ROOT_PATH + "res/icon.png", Files.FileType.Absolute);
        }

        new LwjglApplication(new SolAppListener(), c);
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
