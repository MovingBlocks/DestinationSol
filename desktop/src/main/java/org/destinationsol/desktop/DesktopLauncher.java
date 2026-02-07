/*
 * Copyright 2026 The Terasology Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.SolFileReader;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.modules.FacadeModuleConfig;
import org.destinationsol.ui.ResizeSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.context.Lifetime;
import org.terasology.crashreporter.CrashReporter;
import org.terasology.gestalt.di.ServiceRegistry;
import org.terasology.gestalt.module.ModulePathScanner;

import java.awt.*;
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

public final class DesktopLauncher {
    private static Logger logger = LoggerFactory.getLogger(DesktopLauncher.class);

    /**
     * Specifies the commandline option to pass to the application for it to generate no crash reports.
     */
    private static final String NO_CRASH_REPORT = "-noCrashReport";

    /**
     * Specifies the commandline option to pass to the application for it to not show a splash-screen.
     */
    private static final String NO_SPLASH_SCREEN = "-noSplash";

    /**
     * The colour for the splash-screen logo to be shown in.
     */
    private static final Color LOGO_COLOUR = Color.LIGHT_GRAY;

    /**
     * This class is basically only a holder for the Java's {@code main(String[])} method, thus needs not to be
     * instantiated.
     */
    private DesktopLauncher() {
    }

    public static void launchGame(String[] argv, Class<? extends FacadeModuleConfig> facadeModuleConfigClass,
                                  Class<? extends ModulePathScanner> moduleScannerClass) {
        launchGame(argv, facadeModuleConfigClass, moduleScannerClass, new ServiceRegistry());
    }

    public static void launchGame(String[] argv, Class<? extends FacadeModuleConfig> facadeModuleConfigClass,
                                  Class<? extends ModulePathScanner> moduleScannerClass, ServiceRegistry extraRegistrations) {
        SplashScreen splash = null;
        try {
            splash = SplashScreen.getSplashScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean useSplash = (splash != null) && Stream.of(argv).noneMatch(s -> s.equals(NO_SPLASH_SCREEN));
        if (useSplash) {
            Graphics2D splashScreenGraphics = splash.createGraphics();
            Rectangle splashBounds = splash.getBounds();
            splashScreenGraphics.setColor(LOGO_COLOUR);
            splashScreenGraphics.setPaintMode();
            splashScreenGraphics.fillRect(0, 0, splashBounds.width, splashBounds.height);
            splash.update();
        }

        Lwjgl3ApplicationConfiguration applicationConfig = new Lwjgl3ApplicationConfiguration();
        //TODO: Is checking for a presence of the file really the way we want to determine if it is a debug build?
        handleDevBuild(applicationConfig);
        DesktopLauncher.MyReader reader = new DesktopLauncher.MyReader();
        DebugOptions.read(reader);

        GameOptions options = new GameOptions(false, reader);
        // Set screen width, height...
        setScreenDimensions(applicationConfig, options);

        // Set the application's title, icon...
        applicationConfig.setTitle("Destination Sol");
        if (DebugOptions.DEV_ROOT_PATH == null) {
            applicationConfig.setWindowIcon(Files.FileType.Internal, "icon.png");
        } else {
            applicationConfig.setWindowIcon(Files.FileType.Absolute, DebugOptions.DEV_ROOT_PATH + "/icon.png");
        }

        handleCrashReporting(argv);


        if (useSplash) {
            splash.close();
        }
        // Everything is set up correctly, launch the application
        DesktopLauncher.DesktopServices desktopServiceRegistry = new DesktopLauncher.DesktopServices(facadeModuleConfigClass, moduleScannerClass);
        desktopServiceRegistry.includeRegistry(extraRegistrations);
        SolApplication application = new SolApplication(100, desktopServiceRegistry);
        SolApplication.addResizeSubscriber(new DesktopLauncher.FullScreenWindowPositionAdjustment(!options.fullscreen));
        // Everything is set up correctly, launch the application
        new Lwjgl3Application(application, applicationConfig);
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
    private static void handleDevBuild(Lwjgl3ApplicationConfiguration applicationConfig) {
        boolean devBuild = java.nio.file.Files.exists(Paths.get("devBuild"));
        if (devBuild) {
            DebugOptions.DEV_ROOT_PATH = "engine/src/main/resources/"; // Lets the game run from source without a tweaked working directory
            applicationConfig.useVsync(false); // Setting to false disables vertical sync
            //The LWJGL3 backend does not support FPS throttling in the foreground
            //applicationConfig.foregroundFPS = 100; // Use 0 to disable foreground fps throttling
            //applicationConfig.backgroundFPS = 10; // Use 0 to disable background fps throttling
            applicationConfig.setIdleFPS(10);
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
                java.util.List<String> lines = Collections.singletonList(exceptionString);
                Path logPath = Paths.get(new DesktopLauncher.MyReader().create(fileName, lines)).getParent();

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
     * @param options {@link GameOptions} the configuration to read from.
     */
    private static void setScreenDimensions(Lwjgl3ApplicationConfiguration applicationConfig, GameOptions options) {
        if (DebugOptions.EMULATE_MOBILE) {
            applicationConfig.setWindowedMode(640, 480);
        } else {
            if (options.fullscreen) {
                com.badlogic.gdx.Graphics.DisplayMode mode = null;
                for (com.badlogic.gdx.Graphics.DisplayMode displayMode : Lwjgl3ApplicationConfiguration.getDisplayModes()) {
                    if (displayMode.width == options.x && displayMode.height == options.y) {
                        mode = displayMode;
                    }
                }
                if (mode != null) {
                    applicationConfig.setFullscreenMode(mode);
                } else {
                    logger.warn("The resolution {}x{} is not supported in fullscreen mode!", options.x, options.y);
                }
            } else {
                applicationConfig.setWindowedMode(options.x, options.y);
            }
        }
    }

    private static class DesktopServices extends ServiceRegistry {
        public DesktopServices(Class<? extends FacadeModuleConfig> facadeModuleConfigClass, Class<? extends ModulePathScanner> moduleScannerClass) {
            this.with(FacadeModuleConfig.class).lifetime(Lifetime.Singleton).use(facadeModuleConfigClass);
            this.with(ModulePathScanner.class).lifetime(Lifetime.Singleton).use(moduleScannerClass);
        }
    }

    /**
     * Provides the implementation of SolFileReader used by this class.
     */
    //TODO Since this is currently the only implementation of SolFileReader, consider making this into a self-standing class with static methods. Also, consider uniting SolFileReader and IniReader.
    private static class MyReader implements SolFileReader {
        @Override
        public String create(String fileName, java.util.List<String> lines) {
            String path = "";
            if (DebugOptions.DEV_ROOT_PATH != null) {
                path = DebugOptions.DEV_ROOT_PATH;
            }
            path += fileName;

            Path file = Paths.get(path);
            try {
                java.nio.file.Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                logger.error("Failed to write to file", e);
            }
            return file.toAbsolutePath().toString();
        }

        @Override
        public List<String> read(String fileName) {
            String path = "";
            if (DebugOptions.DEV_ROOT_PATH != null) {
                path = DebugOptions.DEV_ROOT_PATH;
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

    private static final class FullScreenWindowPositionAdjustment implements ResizeSubscriber {
        private boolean lastFullScreenState;

        public FullScreenWindowPositionAdjustment(boolean lastFullScreenState) {
            this.lastFullScreenState = lastFullScreenState;
        }

        @Override
        public void resize() {
            //If the game has gone from full-screen to windowed
            if (lastFullScreenState && !Gdx.graphics.isFullscreen()) {
                Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
                ((Lwjgl3Graphics) Gdx.graphics).getWindow().setPosition(mode.width / 4, mode.height / 4);
            }

            lastFullScreenState = Gdx.graphics.isFullscreen();
        }
    }
}
