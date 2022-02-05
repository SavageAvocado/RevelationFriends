package net.revelationmc.friends.spigot.messaging;

import net.revelationmc.friends.common.ProtocolConstants;
import net.revelationmc.friends.spigot.RevelationFriendsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PluginMessageManager implements PluginMessageListener {
    private final RevelationFriendsPlugin plugin;

    public PluginMessageManager(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
        this.init();
    }

    private void init() {
        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, ProtocolConstants.CHANNEL, this);
        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, ProtocolConstants.CHANNEL);
    }

    public void close() {
        this.plugin.getServer().getMessenger().unregisterIncomingPluginChannel(this.plugin, ProtocolConstants.CHANNEL);
        this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(this.plugin, ProtocolConstants.CHANNEL);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player user, byte[] message) {
        if (!channel.equals(ProtocolConstants.CHANNEL)) {
            return;
        }

        try (final DataInputStream input = null/*DataStreamUtils.newDataInput(message)*/) {
            String action = input.readUTF();

            if (action.equalsIgnoreCase("GetFriends")) {
                UUID uuid = UUID.fromString(input.readUTF());
                String[] friend_array = input.readUTF().split(",");

                List<UUID> friends = new ArrayList<>();

                for (String friend_uuid : friend_array) {
                    friends.add(UUID.fromString(friend_uuid));
                }

                this.plugin.setFriends(uuid, friends);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Player user, String... message) {
        // user.sendPluginMessage(this.plugin, ProtocolConstants.CHANNEL, DataStreamUtils.toByteArray(message));
    }
}