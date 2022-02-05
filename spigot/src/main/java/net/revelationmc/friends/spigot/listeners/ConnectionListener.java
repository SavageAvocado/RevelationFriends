package net.revelationmc.friends.spigot.listeners;

import net.revelationmc.friends.spigot.RevelationFriendsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    private final RevelationFriendsPlugin plugin;

    public ConnectionListener(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoinE(final PlayerJoinEvent e) {
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            this.plugin.getMessageManager().send(e.getPlayer(), "GetFriends", e.getPlayer().getUniqueId().toString());
        }, 20L);
    }

    @EventHandler
    public void onQuitE(final PlayerQuitEvent e) {
        this.plugin.removeFriends(e.getPlayer().getUniqueId());
    }
}
