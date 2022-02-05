package net.revelationmc.friends.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;
import net.revelationmc.friends.bungee.model.friend.Friend;
import net.revelationmc.friends.bungee.model.user.User;
import net.revelationmc.friends.bungee.model.user.UserSetting;
import net.revelationmc.friends.bungee.utils.MessageUtils;

import java.util.UUID;

public class ConnectionListener implements Listener {
    private final RevelationFriendsPlugin plugin;

    public ConnectionListener(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(LoginEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final String username = event.getConnection().getName();
        final UUID uuid = event.getConnection().getUniqueId();

        event.registerIntent(this.plugin);

        this.plugin.getStorage().createUser(uuid, username).thenAccept(v -> event.completeIntent(this.plugin));

        final User user = this.plugin.getUserManager().getOrLoad(uuid).join();
        for (Friend friend : user.getFriends()) {
            if (friend.isOnline() && this.plugin.getUserManager().getOrLoad(friend.getUuid()).join().getSettings().get(UserSetting.JOIN_MESSAGES)) {
                MessageUtils.message(this.plugin.getProxy().getPlayer(friend.getUuid()), "&3Friends &8|&7 " + username + " &7joined.");
            }
        }
    }

    @EventHandler
    public void on(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        this.plugin.getUserManager().getLoggingIn().add(player.getUniqueId());

        int requestsCount = this.plugin.getUserManager().getOrLoad(player.getUniqueId()).join().getFriendRequests().size();

        if (requestsCount > 0) {
            MessageUtils.message(player, String.format("&aYou have %o pending friend request%s (/f requests)", requestsCount, (requestsCount == 1 ? "" : "s")));
        }
    }

    @EventHandler
    public void on(PlayerDisconnectEvent event) {
        final User user = this.plugin.getUserManager().unload(event.getPlayer().getUniqueId());
        this.plugin.getStorage().saveUser(user)
                .whenComplete((v, err) -> {
                    for (Friend friend : user.getFriends()) {
                        if (friend.isOnline() && this.plugin.getUserManager().getOrLoad(friend.getUuid()).join().getSettings().get(UserSetting.QUIT_MESSAGES)) {
                            MessageUtils.message(this.plugin.getProxy().getPlayer(friend.getUuid()), "&3Friends &8|&7 " + event.getPlayer().getDisplayName() + " &7left.");
                        }
                    }
                });
    }
}
