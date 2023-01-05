package com.expectale.minecartlimiter;

import com.expectale.minecartlimiter.command.MinecartLimiterCommand;
import com.expectale.minecartlimiter.configuration.ConfigurationLoader;
import com.expectale.minecartlimiter.configuration.MinecartLimiterConfiguration;
import com.expectale.minecartlimiter.listener.MineCartListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MineCartLimiter extends JavaPlugin {

    private static MineCartLimiter INSTANCE;

    public static MineCartLimiter getINSTANCE() {
        return INSTANCE;
    }

    private MinecartLimiterConfiguration configuration;

    public MinecartLimiterConfiguration getConfiguration() {
        return configuration;
    }

    public void reloadConfig() {
        Checker.stop();
        ConfigurationLoader configurationLoader = new ConfigurationLoader(this, "config.yml", new File(getDataFolder(), "config.yml"));
        configuration = new MinecartLimiterConfiguration(configurationLoader.getConfiguration());
        Checker.start();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        reloadConfig();
        getCommand("ml").setExecutor(new MinecartLimiterCommand());
        getServer().getPluginManager().registerEvents(new MineCartListener(), this);
        getLogger().info("MineCartLimiter is enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("MineCartLimiter is disabled");
        Checker.stop();
    }
}
