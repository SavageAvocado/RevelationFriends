package net.savagedev.friends.spigot.listeners;

import net.savagedev.friends.spigot.RevelationFriends;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinE implements Listener {
    private final RevelationFriends plugin;

    public JoinE(RevelationFriends plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoinE(final PlayerJoinEvent e) {
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            this.plugin.getMessageManager().send(e.getPlayer(), "GetFriends", e.getPlayer().getUniqueId().toString());
        }, 20L);
    }
}
