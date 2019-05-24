package net.savagedev.friends.bungee.listeners;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.User;
import net.savagedev.friends.bungee.user.friend.Friend;
import net.savagedev.friends.bungee.utils.MessageUtils;

import java.util.UUID;

public class QuitE implements Listener {
    private final RevelationFriends plugin;

    public QuitE(RevelationFriends plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuitE(final PlayerDisconnectEvent e) {
        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> {
            String username = e.getPlayer().getName();
            UUID uuid = e.getPlayer().getUniqueId();

            for (Friend friend : this.plugin.getUserManager().get(e.getPlayer().getUniqueId()).getFriends()) {
                if (friend.isOnline()) {
                    UUID fuuid = friend.getUniqueId();
                    if (this.plugin.getUserManager().get(fuuid).getSetting(User.Setting.LEAVE_MESSAGES)) {
                        MessageUtils.message(friend.getPlayer(), "&3Friends &8|&7 " + e.getPlayer().getDisplayName() + " &7left.");
                    }
                }
            }

            this.plugin.getUserManager().unCache(uuid);
        });
    }
}
