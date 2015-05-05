package com.miloshpetrov.sol2.files;

import com.badlogic.gdx.files.FileHandle;
import com.miloshpetrov.sol2.game.DebugOptions;
import com.miloshpetrov.sol2.game.ship.HullConfig;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Linus on 4-5-2015.
 */
public final class HullConfigReader {

    public static HullConfigReader getInstance() {
        if(instance == null) {
            instance = new HullConfigReader();
        }

        return instance;
    }

    private List<HullConfig> getHullConfigs() {
        List<FileHandle> hullDirectories = getHullDirectories();
        List<HullConfig> hullConfigs = new ArrayList<HullConfig>(hullDirectories.size());

        for(FileHandle handle: hullDirectories) {
            hullConfigs.add( read(handle) );
        }

        return hullConfigs;
    }

    private List<FileHandle> getHullDirectories() {
        List<FileHandle> subDirectories = new LinkedList<FileHandle>();

        for(FileHandle handle: fileManager.getHullsDirectory().list()) {
            if(handle.isDirectory()) {
                subDirectories.add(handle);
            }
        }

        return subDirectories;
    }

    private HullConfig read(FileHandle hullConfigFile) {
        //HullConfig hullConfig = new HullConfig();

        return null;//hullConfig;
    }

    private final FileManager fileManager;

    private HullConfigReader() {
        fileManager = FileManager.getInstance();
    }

    private static HullConfigReader instance = null;
}
