package net.revelationmc.friends.bungee.messaging;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.revelationmc.friends.bungee.model.friend.Friendship;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;
import net.revelationmc.friends.common.ProtocolConstants;

import java.io.DataInput;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class PluginMessageManager implements Listener {
    private final RevelationFriendsPlugin plugin;

    public PluginMessageManager(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, this);
        this.plugin.getProxy().registerChannel(ProtocolConstants.CHANNEL);
    }

    public void close() {
        this.plugin.getProxy().unregisterChannel(ProtocolConstants.CHANNEL);
    }

    @EventHandler
    public void onPluginMessageE(final PluginMessageEvent e) {
        if (!e.getTag().equals(ProtocolConstants.CHANNEL)) {
            return;
        }

        final DataInput input = null;
        // DataInput input = DataStreamUtils.newDataInput(e.getData());

        try {
            String action = input.readUTF();

            if (action.equalsIgnoreCase("GetFriends")) {
                UUID uuid = UUID.fromString(input.readUTF());

                final ProxiedPlayer user = this.plugin.getProxy().getPlayer(uuid);

                if (user == null) {
                    this.plugin.getProxy().getLogger().log(Level.INFO, String.format("[RevelationFriends] Player %s not online. Not sending friend list.", uuid));
                    return;
                }

                StringBuilder friends = new StringBuilder();

                for (Friendship friendship : this.plugin.getUserManager().getOrLoad(uuid).join().getFriends()) {
                    friends.append(friendship.getUuid().toString()).append(",");
                }

                this.send(user.getServer().getInfo(), "GetFriends", uuid.toString(), friends.toString().trim());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void send(ServerInfo server, String... message) {
        // server.sendData(ProtocolConstants.CHANNEL, DataStreamUtils.toByteArray(message));
    }
}
