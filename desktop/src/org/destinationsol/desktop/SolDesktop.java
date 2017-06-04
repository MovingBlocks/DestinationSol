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
package org.destinationsol.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.SolFileReader;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.soundtest.SoundTestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.crashreporter.CrashReporter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SolDesktop {
    private static Logger logger = LoggerFactory.getLogger(SolDesktop.class);
    public static void main(String[] argv) {
        if (false) {
            new LwjglApplication(new SoundTestListener(), "sound test", 800, 600);
            return;
        }

        LwjglApplicationConfiguration applicationConfig = new LwjglApplicationConfiguration();
        boolean devBuild = java.nio.file.Files.exists(Paths.get("devBuild"));
        if (devBuild) {
            DebugOptions.DEV_ROOT_PATH = "main" + File.separator; // Lets the game run from source without a tweaked working directory
            applicationConfig.vSyncEnabled = false; //Setting to false disables vertical sync
            applicationConfig.foregroundFPS = 0; //disables foreground fps throttling
            applicationConfig.backgroundFPS = 0; //disables background fps throttling
        }
        MyReader reader = new MyReader();
        DebugOptions.read(reader);

        if (DebugOptions.EMULATE_MOBILE) {
            applicationConfig.width = 640;
            applicationConfig.height = 480;
            applicationConfig.fullscreen = false;
        } else {
            GameOptions d = new GameOptions(false, reader);
            applicationConfig.width = d.x;
            applicationConfig.height = d.y;
            applicationConfig.fullscreen = d.fullscreen;
        }

        applicationConfig.title = "Destination Sol";
        if (DebugOptions.DEV_ROOT_PATH == null) {
            applicationConfig.addIcon("res/icon.png", Files.FileType.Internal);
        } else {
            applicationConfig.addIcon(DebugOptions.DEV_ROOT_PATH + "res/icon.png", Files.FileType.Absolute);
        }

        /*
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            // Get the exception stack trace string
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            ex.printStackTrace(printWriter);
            String exceptionString = stringWriter.getBuffer().toString();
            logger.error("This exception was not caught:", ex);

            // Create a crash dump file
            String fileName = "crash-" + new SimpleDateFormat("yyyy-dd-MM_HH-mm-ss").format(new Date()) + ".log";
            List<String> lines = Collections.singletonList(exceptionString);
            Path logPath = new MyReader().create(fileName, lines).toAbsolutePath().getParent();

            // Run asynchronously so that the error message view is not blocked
            new Thread(() -> CrashReporter.report(ex, logPath)).start();
        });
        */

        new LwjglApplication(new SolApplication(), applicationConfig);
    }

    private static class MyReader implements SolFileReader {
        @Override
        public Path create(String fileName, List<String> lines) {
            if (DebugOptions.DEV_ROOT_PATH != null) {
                fileName = DebugOptions.DEV_ROOT_PATH + fileName;
            }
            Path file = Paths.get(fileName);
            try {
                java.nio.file.Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                logger.error("Failed to write to file",e);
            }
            return file;
        }

        @Override
        public List<String> read(String fileName) {
            if (DebugOptions.DEV_ROOT_PATH != null) {
                fileName = DebugOptions.DEV_ROOT_PATH + fileName;
            }

            ArrayList<String> lines = new ArrayList<>();

            try {
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                br.close();
            } catch (IOException ignore) { }

            return lines;
        }
    }
}
