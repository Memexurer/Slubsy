package pl.memexurer.slubsy;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class MerriPlugin extends JavaPlugin implements Listener {
    private final HashMap<UUID, UUID> merriRequests = new HashMap<>();
    private final MerriData merriData = new MerriData(this);
    private ItemStack obraczka;

    private String marryMessage;
    private String rozwodMessage;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.marryMessage = getConfig().getString("slub");
        this.rozwodMessage = getConfig().getString("rozwod");
        this.merriData.loadData();

        this.obraczka = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = this.obraczka.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Obrączki");
        this.obraczka.setItemMeta(meta);

        PlaceholderAPI.registerExpansion(new MerriPlaceholder(merriData));
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String lbl, String[] args) {
        if (!(sender instanceof Player)) return true;

        if (command.getName().equals("obraczki") && sender.hasPermission("merri.obraczki")) {
            ItemStack obraczkaa = new ItemStack(obraczka);
            obraczkaa.setAmount(2);
            ((Player) sender).getInventory().addItem(obraczkaa);
            sender.sendMessage(ChatColor.GREEN + "Otrzymales obraczke!");
        } else if (command.getName().equals("rozwod")) {
            String rozwod = merriData.rozwod(sender.getName());

            if(rozwod == null) {
                sender.sendMessage(ChatColor.RED + "Nie masz sie z kim rozwodzic!");
            } else {
                Bukkit.broadcastMessage(fixColor(rozwodMessage).replace("{PLAYER1}", sender.getName()).replace("{PLAYER2}", rozwod));
            }
        }
        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent e) {
        if(!(e.getRightClicked() instanceof Player) || e.getHand() != EquipmentSlot.HAND) return;

        if(!e.getPlayer().getInventory().getItemInMainHand().isSimilar(obraczka)) return;
        if(merriData.getMarried(e.getPlayer().getName()).isPresent()) {
            e.getPlayer().sendMessage(ChatColor.RED + "Nie mozesz poslubic drugiej osoby!");
            return;
        }
        Player rightClicked = (Player) e.getRightClicked();
        if(merriRequests.containsKey(rightClicked.getUniqueId()) && merriRequests.get(rightClicked.getUniqueId()).equals(e.getPlayer().getUniqueId())) {
            merriRequests.remove(e.getPlayer().getUniqueId());
            merriRequests.remove(rightClicked.getUniqueId());
            Bukkit.broadcastMessage(fixColor(marryMessage).replace("{PLAYER1}", e.getPlayer().getName()).replace("{PLAYER2}", rightClicked.getName()));
            merriData.setMarried(e.getPlayer().getName(), rightClicked.getName());

            World world = e.getPlayer().getWorld();
            world.playEffect(e.getPlayer().getLocation(), Effect.HEART, 1, 4);
            world.playEffect(rightClicked.getLocation(), Effect.HEART, 1, 4);

            takeOne(e.getPlayer());
            takeOne(rightClicked);
        } else if(!merriRequests.containsKey(e.getPlayer().getUniqueId())) {
            merriRequests.put(e.getPlayer().getUniqueId(), rightClicked.getUniqueId());
        }
    }

    private void takeOne(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item == null) return;
        if(item.getAmount() == 1) player.getInventory().setItemInMainHand(null);
        else item.setAmount(item.getAmount() - 1);
    }

    private String fixColor(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
