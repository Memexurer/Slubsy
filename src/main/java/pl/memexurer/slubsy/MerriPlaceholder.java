package pl.memexurer.slubsy;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MerriPlaceholder extends PlaceholderExpansion {
    private MerriData merriData;

    MerriPlaceholder(MerriData merriData) {
        this.merriData = merriData;
    }

    @Override
    public String onPlaceholderRequest(Player player, String args) {
        if (merriData.getMarried(player.getName()).isPresent()) {
            return ChatColor.LIGHT_PURPLE + "\u2764 ";
        } else return "";
    }

    @Override
    public String getIdentifier() {
        return "slubsy";
    }

    @Override
    public String getAuthor() {
        return "Memexurer";
    }

    @Override
    public String getVersion() {
        return "kremuwka";
    }
}
