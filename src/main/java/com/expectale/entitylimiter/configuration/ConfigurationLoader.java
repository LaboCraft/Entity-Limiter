package com.expectale.entitylimiter.configuration;


import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigurationLoader {
    private final JavaPlugin _instance;
    private final File _file;
    private final String _fileName;
    private Configuration _configuration = null;

    public ConfigurationLoader(final JavaPlugin instance, final String fileName, final File file) {
        _instance = instance;
        _fileName = fileName;
        _file = file;
        if (!_file.exists()) {
            saveDefault();
        }
        _configuration = YamlConfiguration.loadConfiguration(_file);
    }

    public Configuration getConfiguration() {
        return _configuration;
    }

    private void saveDefault() {
        if (!_file.getParentFile().exists()) {
            _file.getParentFile().mkdir();
        }

        try (InputStream in = _instance.getResource(_fileName)) {
            Files.copy(in, _file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}