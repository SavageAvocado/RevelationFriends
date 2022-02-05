package net.revelationmc.friends.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.revelationmc.friends.bungee.model.friend.Friend;
import net.revelationmc.friends.bungee.model.user.UserSetting;
import net.revelationmc.friends.bungee.utils.MessageUtils;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;

import java.util.UUID;

public class ServerListener implements Listener {
    private final RevelationFriendsPlugin plugin;

    public ServerListener(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerSwitchE(final ServerSwitchEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        if (this.plugin.getUserManager().getLoggingIn().contains(uuid)) {
            this.plugin.getUserManager().getLoggingIn().remove(uuid);
            return;
        }

        for (Friend friend : this.plugin.getUserManager().getOrLoad(uuid).join().getFriends()) {
            if (friend.isOnline()) {
                final ProxiedPlayer friendPlayer = this.plugin.getProxy().getPlayer(friend.getUuid());
                if (this.plugin.getUserManager().getOrLoad(friendPlayer.getUniqueId()).join().getSettings().get(UserSetting.SERVER_CHANGE_MESSAGES)) {
                    MessageUtils.message(friendPlayer, "&3Friends &8|&7 " + player.getDisplayName() + " &7connected to " + player.getServer().getInfo().getName() + ".");
                }
            }
        }
    }
}
