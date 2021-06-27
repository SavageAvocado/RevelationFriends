package net.savagedev.friends.spigot.listeners;

import net.md_5.bungee.api.ChatColor;
import net.savagedev.friends.spigot.RevelationFriends;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DamageListener implements Listener {
    private final RevelationFriends plugin;
    private final List<String> messages;


    public DamageListener(RevelationFriends plugin) {
        this.messages = plugin.getConfig().getStringList("friend-hit-messages");
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageE(final EntityDamageByEntityEvent e) {
        Entity attacker = e.getDamager();

        if (!(attacker instanceof Player)) {
            return;
        }

        Entity victim = e.getEntity();

        if (!(victim instanceof Player)) {
            return;
        }

        if (this.plugin.getConfig().getStringList("pvp-enabled").stream().anyMatch(attacker.getLocation().getWorld().getName()::equalsIgnoreCase)) {
            return;
        }

        if (this.plugin.getFriends(attacker.getUniqueId()).contains(victim.getUniqueId())) {
            attacker.sendMessage(ChatColor.translateAlternateColorCodes('&', this.messages.get(ThreadLocalRandom.current().nextInt(this.messages.size()))));
            e.setCancelled(true);
        }
    }
}
