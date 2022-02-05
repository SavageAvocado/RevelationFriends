package net.revelationmc.friends.spigot;

import net.revelationmc.friends.spigot.listeners.ConnectionListener;
import net.revelationmc.friends.spigot.listeners.DamageListener;
import net.revelationmc.friends.spigot.messaging.PluginMessageManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RevelationFriendsPlugin extends JavaPlugin {
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
        pluginManager.registerEvents(new ConnectionListener(this), this);
        pluginManager.registerEvents(new DamageListener(this), this);
    }

    private void createScoreboard(Player player, String nickname) {
        final Scoreboard scoreboard = this.getServer().getScoreboardManager().getNewScoreboard();
        final Objective obj = scoreboard.registerNewObjective("ServerName", "dummy", "Test Server");
        obj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        Team team = scoreboard.getTeam(player.getUniqueId().toString());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getUniqueId().toString());
        }
        team.setPrefix(ChatColor.translateAlternateColorCodes('&', nickname));
        player.setScoreboard(scoreboard);
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
