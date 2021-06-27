package net.savagedev.friends.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.model.friend.Friend;
import net.savagedev.friends.bungee.model.user.User;
import net.savagedev.friends.bungee.model.user.UserSetting;
import net.savagedev.friends.bungee.utils.MessageUtils;

import java.util.UUID;

public class ConnectionListener implements Listener {
    private final RevelationFriends plugin;

    public ConnectionListener(RevelationFriends plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLoginE(final LoginEvent e) {
        if (e.isCancelled()) {
            return;
        }

        final String username = e.getConnection().getName();
        final UUID uuid = e.getConnection().getUniqueId();

        this.plugin.getStorage().createUser(uuid, username).join();

        final User user = this.plugin.getUserManager().getOrLoad(uuid).join();
        for (Friend friend : user.getFriends()) {
            if (friend.isOnline() && this.plugin.getUserManager().getOrLoad(friend.getUuid()).join().getSettings().get(UserSetting.JOIN_MESSAGES)) {
                MessageUtils.message(this.plugin.getProxy().getPlayer(friend.getUuid()), "&3Friends &8|&7 " + username + " &7joined.");
            }
        }
    }

    @EventHandler
    public void onPostLoginE(final PostLoginEvent e) {
        final ProxiedPlayer player = e.getPlayer();

        this.plugin.getUserManager().getLoggingIn().add(player.getUniqueId());

        int requestsCount = this.plugin.getUserManager().getOrLoad(player.getUniqueId()).join().getFriendRequests().size();

        if (requestsCount > 0) {
            MessageUtils.message(player, String.format("&aYou have %o pending friend request%s (/f requests)", requestsCount, (requestsCount == 1 ? "" : "s")));
        }
    }

    @EventHandler
    public void onQuitE(final PlayerDisconnectEvent e) {
        final User user = this.plugin.getUserManager().unload(e.getPlayer().getUniqueId());
        this.plugin.getStorage().saveUser(user)
                .whenComplete((v, err) -> {
                    for (Friend friend : user.getFriends()) {
                        if (friend.isOnline() && this.plugin.getUserManager().getOrLoad(friend.getUuid()).join().getSettings().get(UserSetting.QUIT_MESSAGES)) {
                            MessageUtils.message(this.plugin.getProxy().getPlayer(friend.getUuid()), "&3Friends &8|&7 " + e.getPlayer().getDisplayName() + " &7left.");
                        }
                    }
                });
    }
}
