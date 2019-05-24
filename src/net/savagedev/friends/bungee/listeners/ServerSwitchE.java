package net.savagedev.friends.bungee.listeners;

import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.User;
import net.savagedev.friends.bungee.user.friend.Friend;
import net.savagedev.friends.bungee.utils.MessageUtils;

import java.util.UUID;

public class ServerSwitchE implements Listener {
    private final RevelationFriends plugin;

    public ServerSwitchE(RevelationFriends plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerSwitchE(final ServerSwitchEvent e) {
        if (this.plugin.getUserManager().isLogin(e.getPlayer().getUniqueId())) {
            this.plugin.getUserManager().removeLogin(e.getPlayer().getUniqueId());
            return;
        }

        for (Friend friend : this.plugin.getUserManager().get(e.getPlayer().getUniqueId()).getFriends()) {
            if (friend.isOnline()) {
                UUID uuid = friend.getUniqueId();
                if (this.plugin.getUserManager().get(uuid).getSetting(User.Setting.SERVER_SWITCH_MESSAGES)) {
                    MessageUtils.message(friend.getPlayer(), "&3Friends &8|&7 " + e.getPlayer().getDisplayName() + " &7connected to " + e.getPlayer().getServer().getInfo().getName() + ".");
                }
            }
        }
    }
}
