package net.savagedev.friends.spigot;

import net.savagedev.friends.spigot.listeners.EntityDamageE;
import net.savagedev.friends.spigot.listeners.JoinE;
import net.savagedev.friends.spigot.listeners.QuitE;
import net.savagedev.friends.spigot.messaging.PluginMessageManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RevelationFriends extends JavaPlugin {
    private PluginMessageManager messageManager;
    private Map<UUID, List<UUID>> friends;

    @Override
    public void onEnable() {
        this.loadUtils();
        this.loadConfig();
        this.loadListeners();
    }

    @Override
    public void onDisable() {
        this.messageManager.close();
    }

    private void loadUtils() {
        this.messageManager = new PluginMessageManager(this);
        this.friends = new HashMap<>();

        if (this.getServer().getOnlinePlayers().isEmpty()) {
            return;
        }

        for (Player user : this.getServer().getOnlinePlayers()) {
            this.getMessageManager().send(user, "GetFriends", user.getUniqueId().toString());
        }
    }

    private void loadConfig() {
        this.saveDefaultConfig();
    }

    private void loadListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new EntityDamageE(this), this);
        pluginManager.registerEvents(new JoinE(this), this);
        pluginManager.registerEvents(new QuitE(this), this);
    }

    public void setFriends(UUID uuid, List<UUID> friends) {
        this.friends.putIfAbsent(uuid, friends);
    }

    public void removeFriends(UUID uuid) {
        this.friends.remove(uuid);
    }

    public PluginMessageManager getMessageManager() {
        return this.messageManager;
    }

    public List<UUID> getFriends(UUID uuid) {
        return this.friends.get(uuid);
    }
}
