package net.savagedev.friends.spigot.listeners;

import net.savagedev.friends.spigot.RevelationFriends;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitE implements Listener {
    private final RevelationFriends plugin;

    public QuitE(RevelationFriends plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuitE(final PlayerQuitEvent e) {
        this.plugin.removeFriends(e.getPlayer().getUniqueId());
    }
}
