package net.savagedev.friends.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.savagedev.friends.bungee.commands.FriendCmd;
import net.savagedev.friends.bungee.listeners.JoinE;
import net.savagedev.friends.bungee.listeners.QuitE;
import net.savagedev.friends.bungee.listeners.ServerSwitchE;
import net.savagedev.friends.bungee.messaging.PluginMessageManager;
import net.savagedev.friends.bungee.user.UserManager;
import net.savagedev.friends.bungee.utils.io.FileUtils;

import java.io.File;

public class RevelationFriends extends Plugin {
    private PluginMessageManager messageManager;
    private PluginManager pluginManager;
    private UserManager userManager;
    private Configuration langFile;
    private Configuration config;

    @Override
    public void onEnable() {
        this.loadConfig();
        this.loadUtils();
        this.loadCommands();
        this.loadListeners();
    }

    @Override
    public void onDisable() {
        this.messageManager.close();
    }

    private void loadUtils() {
        this.pluginManager = this.getProxy().getPluginManager();
        this.messageManager = new PluginMessageManager(this);
        this.userManager = new UserManager(this);
    }

    private void loadConfig() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        FileUtils.create("config.yml", configFile);
        this.config = FileUtils.load(configFile);

        File langFile = new File(this.getDataFolder(), "lang.yml");
        FileUtils.create("lang.yml", langFile);
        this.langFile = FileUtils.load(langFile);
    }

    private void loadCommands() {
        this.pluginManager.registerCommand(this, new FriendCmd(this, "friend", "", "friends", "fr", "f"));
    }

    private void loadListeners() {
        this.pluginManager.registerListener(this, new ServerSwitchE(this));
        this.pluginManager.registerListener(this, new JoinE(this));
        this.pluginManager.registerListener(this, new QuitE(this));
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public Configuration getConfig(ConfigType type) {
        if (type == ConfigType.LANG) {
            return this.langFile;
        }

        return this.config;
    }

    public enum ConfigType {
        MAIN, LANG
    }
}
