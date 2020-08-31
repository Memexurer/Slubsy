package pl.memexurer.slubsy;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Optional;

public class MerriData {
    private final HashMap<String, String> merriPlayers = new HashMap<>();
    private final Configuration configuration;
    private final JavaPlugin javaPlugin;

    MerriData(JavaPlugin owningPlugin) {
        this.configuration = owningPlugin.getConfig();
        this.javaPlugin = owningPlugin;
    }

    void loadData() {
        ConfigurationSection section = configuration.getConfigurationSection("data");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            this.merriPlayers.put(key, section.getString(key));
            this.merriPlayers.put(section.getString(key), key);
        }
    }

    public Optional<String> getMarried(String playerName) {
        return Optional.ofNullable(merriPlayers.get(playerName));
    }

    public String rozwod(String playerName) {
        Optional<String> married = getMarried(playerName);
        if(!married.isPresent()) return null;

        this.merriPlayers.remove(married.get());
        this.merriPlayers.remove(playerName);
        this.configuration.set("data." + playerName, null);
        this.configuration.set("data." + married.get(), null);
        this.javaPlugin.saveConfig();
        return married.get();
    }

    public boolean setMarried(String playerName, String lovedName) {
        if(this.merriPlayers.containsKey(playerName)) return false;
        this.merriPlayers.put(playerName, lovedName);
        this.merriPlayers.put(lovedName, playerName);
        this.configuration.set("data." + playerName, lovedName);
        this.javaPlugin.saveConfig();

        return true;
    }
}
