package net.sacredlabyrinth.phaed.simpleclans.managers;


import java.io.File;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class LanguageManager {
    private FileConfiguration language;

    public LanguageManager() {
        load();
    }

    public void load() {
        SimpleClans plugin = SimpleClans.getInstance();
        language = YamlConfiguration.loadConfiguration( new File( plugin.getDataFolder(), "language.yml") );
        language.setDefaults( YamlConfiguration.loadConfiguration( new InputStreamReader( plugin.getResource( "language.yml" ) ) ) );
    
    }
    
    public String get(String key) {
        return language.getString( key ); 
    }  
}
