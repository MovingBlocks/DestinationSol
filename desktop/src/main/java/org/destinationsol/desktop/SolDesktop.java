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
package org.destinationsol.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.SolFileReader;
import org.destinationsol.game.DebugOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.crashreporter.CrashReporter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is the desktop (PC) entry point for the whole DestinationSol application. It handles the creation and
 * launching of LwjglApplication from {@link SolApplication}.
 */
public final class SolDesktop {

    private static Logger logger = LoggerFactory.getLogger(SolDesktop.class);

    /**
     * Specifies the commandline option to pass to the application for it to generate no crash reports.
     */
    private static final String NO_CRASH_REPORT = "-noCrashReport";

    /**
     * This class is basically only a holder for the Java's {@code main(String[])} method, thus needs not to be
     * instantiated.
     */
    private SolDesktop() {
    }

    public static void main(String[] argv) {
        LwjglApplicationConfiguration applicationConfig = new LwjglApplicationConfiguration();
        //TODO: Is checking for a presence of the file really the way we want to determine if it is a debug build?
        handleDevBuild(applicationConfig);
        MyReader reader = new MyReader();
        DebugOptions.read(reader);

        // Set screen width, height...
        setScreenDimensions(applicationConfig, reader);

        // Set the application's title, icon...
        applicationConfig.title = "Destination Sol";
        if (DebugOptions.DEV_ROOT_PATH == null) {
            applicationConfig.addIcon("src/main/resources/icon.png", Files.FileType.Internal);
        } else {
            applicationConfig.addIcon(DebugOptions.DEV_ROOT_PATH + "/icon.png", Files.FileType.Absolute);
        }

        handleCrashReporting(argv);

        // Everything is set up correctly, launch the application
        new LwjglApplication(new SolApplication(), applicationConfig);
    }

    /**
     * When on dev build, use specific settings for vSync and FPS throttling.
     *
     * Whether a build is a dev build is found out by checking of a file "devBuild" in the root directory of DestSol.
     * Those specific option means disabling vSync, and increasing foreground FPS throttling to allow for a swifter
     * game, while lowering it for the background to not eat as much resources. Since game time flow is dependent on
     * FPS, this also means that on dev build, the game may run faster in foreground than background, which is not
     * something we exactly want. Also, since the default FPS for non-dev builds is 60, it ensures that the game will
     * run at the same sane speed in production and the same speed in foreground as well as background.
     *
     * @param applicationConfig App config to configure.
     */
    private static void handleDevBuild(LwjglApplicationConfiguration applicationConfig) {
        boolean devBuild = java.nio.file.Files.exists(Paths.get("devBuild"));
        if (devBuild) {
            DebugOptions.DEV_ROOT_PATH = "engine/src/main/resources/"; // Lets the game run from source without a tweaked working directory
            applicationConfig.vSyncEnabled = false; // Setting to false disables vertical sync
            applicationConfig.foregroundFPS = 100; // Use 0 to disable foreground fps throttling
            applicationConfig.backgroundFPS = 10; // Use 0 to disable background fps throttling
        }
    }

    /**
     * When flag {@link #NO_CRASH_REPORT} is NOT passed in, overload the uncaught exception behaviour to create a crash
     * dump and report the crash.
     *
     * @param argv App's cmdline args.
     */
    private static void handleCrashReporting(String[] argv) {
        if (Stream.of(argv).noneMatch(s -> s.equals(NO_CRASH_REPORT))) {
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
        }
    }

    /**
     * Set up window resolution.
     *
     * When flag {@link DebugOptions#EMULATE_MOBILE} is set, make the app window the size of mobile screen. Otherwise,
     * load the window resolution from game options.
     *
     * @param applicationConfig App config to configure
     * @param reader {@link SolFileReader} to read stored settings with.
     */
    private static void setScreenDimensions(LwjglApplicationConfiguration applicationConfig, MyReader reader) {
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
    }

    /**
     * Provides the implementation of SolFileReader used by this class.
     */
    //TODO Since this is currently the only implementation of SolFileReader, consider making this into a self-standing class with static methods. Also, consider uniting SolFileReader and IniReader.
    private static class MyReader implements SolFileReader {
        @Override
        public Path create(String fileName, List<String> lines) {
            String path;
            if (DebugOptions.DEV_ROOT_PATH != null) {
                path = DebugOptions.DEV_ROOT_PATH;
            } else {
                path = "src/main/resources/";
            }
            path += fileName;

            Path file = Paths.get(path);
            try {
                java.nio.file.Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                logger.error("Failed to write to file", e);
            }
            return file;
        }

        @Override
        public List<String> read(String fileName) {
            String path;
            if (DebugOptions.DEV_ROOT_PATH != null) {
                path = DebugOptions.DEV_ROOT_PATH;
            } else {
                path = "src/main/resources/";
            }
            path += fileName;

            ArrayList<String> lines = new ArrayList<>();

            try {
                BufferedReader br = new BufferedReader(new FileReader(path));
                String line;
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
