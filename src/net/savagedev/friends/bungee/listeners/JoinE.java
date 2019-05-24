package net.savagedev.friends.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.User;
import net.savagedev.friends.bungee.user.friend.Friend;
import net.savagedev.friends.bungee.utils.MessageUtils;

import java.util.UUID;

public class JoinE implements Listener {
    private final RevelationFriends plugin;

    public JoinE(RevelationFriends plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLoginE(final LoginEvent e) {
        if (e.isCancelled()) {
            return;
        }

        String username = e.getConnection().getName();
        UUID uuid = e.getConnection().getUniqueId();

        this.plugin.getUserManager().update(uuid, username);

        this.plugin.getUserManager().create(uuid);
        this.plugin.getUserManager().load(uuid);

        for (Friend friend : this.plugin.getUserManager().get(e.getConnection().getUniqueId()).getFriends()) {
            if (friend.isOnline()) {
                if (this.plugin.getUserManager().get(friend.getUniqueId()).getSetting(User.Setting.JOIN_MESSAGES)) {
                    MessageUtils.message(friend.getPlayer(), "&3Friends &8|&7 " + e.getConnection().getName() + " &7joined.");
                }
            }
        }
    }

    @EventHandler
    public void onPostLoginE(final PostLoginEvent e) {
        ProxiedPlayer user = e.getPlayer();

        this.plugin.getUserManager().addLogin(e.getPlayer().getUniqueId());
        int requestsCount = this.plugin.getUserManager().get(user.getUniqueId()).getFriendRequests().size();

        if (requestsCount >= 1) {
            MessageUtils.message(user, String.format("&aYou have %o pending friend request%s (/f requests)", requestsCount, (requestsCount == 1 ? "" : "s")));
        }
    }
}
