package net.savagedev.friends.bungee.messaging;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.friend.Friend;
import net.savagedev.friends.common.DataStreamUtils;
import net.savagedev.friends.common.ProtocolConstraints;

import java.io.DataInput;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class PluginMessageManager implements Listener {
    private final RevelationFriends plugin;

    public PluginMessageManager(RevelationFriends plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, this);
        this.plugin.getProxy().registerChannel(ProtocolConstraints.CHANNEL);
    }

    public void close() {
        this.plugin.getProxy().unregisterChannel(ProtocolConstraints.CHANNEL);
    }

    @EventHandler
    public void onPluginMessageE(final PluginMessageEvent e) {
        if (!e.getTag().equals(ProtocolConstraints.CHANNEL)) {
            return;
        }

        DataInput input = DataStreamUtils.newDataInput(e.getData());

        try {
            String action = input.readUTF();

            if (action.equalsIgnoreCase("GetFriends")) {
                UUID uuid = UUID.fromString(input.readUTF());

                ProxiedPlayer user = this.plugin.getProxy().getPlayer(uuid);

                if (user == null) {
                    this.plugin.getProxy().getLogger().log(Level.INFO, String.format("[RevelationFriends] Player %s not online. Not sending friend list.", uuid.toString()));
                    return;
                }

                StringBuilder friends = new StringBuilder();

                for (Friend friend : this.plugin.getUserManager().get(uuid).getFriends()) {
                    friends.append(friend.getUniqueId().toString()).append(",");
                }

                this.send(user.getServer().getInfo(), "GetFriends", uuid.toString(), friends.toString().trim());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void send(ServerInfo server, String... message) {
        server.sendData(ProtocolConstraints.CHANNEL, DataStreamUtils.toByteArray(message));
    }
}
