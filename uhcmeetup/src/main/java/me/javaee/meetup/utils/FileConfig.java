package me.javaee.meetup.utils;

import me.javaee.meetup.Meetup;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileConfig {

    public String fileName;
    public File configFile;
    private FileConfiguration config;

    public FileConfig(String fileName) {
        this.fileName = fileName;
        this.configFile = new File(Meetup.getPlugin().getDataFolder(), fileName);

        if (!this.configFile.exists()) {
            this.configFile.getParentFile().mkdirs();

            if (Meetup.getPlugin().getResource(fileName) == null) {
                try {
                    this.configFile.createNewFile();
                } catch (IOException e) {
                    Meetup.getPlugin().getLogger().severe("Failed to create new file " + fileName);
                }
            } else {
                Meetup.getPlugin().saveResource(fileName, false);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public FileConfig(File file, String fileName) {
        this.configFile = new File(file, fileName);

        if (!this.configFile.exists()) {
            this.configFile.getParentFile().mkdirs();

            if (Meetup.getPlugin().getResource(fileName) == null) {
                try {
                    this.configFile.createNewFile();
                } catch (IOException e) {
                    Meetup.getPlugin().getLogger().severe("Failed to create new file " + fileName);
                }
            } else {
                Meetup.getPlugin().saveResource(fileName, false);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void save() {
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Could not save config file " + this.configFile.toString());
            e.printStackTrace();
        }
    }
}