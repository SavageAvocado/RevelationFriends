package net.revelationmc.friends.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.revelationmc.friends.bungee.listeners.ConnectionListener;
import net.revelationmc.friends.bungee.listeners.ServerListener;
import net.revelationmc.friends.bungee.messaging.PluginMessageManager;
import net.revelationmc.friends.bungee.model.user.UserManager;
import net.revelationmc.friends.bungee.storage.Storage;
import net.revelationmc.friends.bungee.storage.implementation.sql.mysql.MySqlStorage;
import net.revelationmc.friends.bungee.commands.FriendCmd;

public class RevelationFriendsPlugin extends Plugin {
    private PluginMessageManager messageManager;
    private PluginManager pluginManager;
    private UserManager userManager;

    private Storage storage;

    @Override
    public void onEnable() {
        this.initStorage();
        this.loadUtils();
        this.loadCommands();
        this.loadListeners();
    }

    @Override
    public void onDisable() {
        this.messageManager.close();
        this.storage.shutdown();
    }

    private void initStorage() {
        this.storage = new Storage(new MySqlStorage("localhost", 3306, "friends", "friends", "p4ssw0rd"));
        //this.storage = new Storage(new YamlStorage(this.getDataFolder().toPath().resolve("data")));
        this.storage.init();
    }

    private void loadUtils() {
        this.pluginManager = this.getProxy().getPluginManager();
        this.messageManager = new PluginMessageManager(this);
        this.userManager = new UserManager(this);
    }

    private void loadCommands() {
        this.pluginManager.registerCommand(this, new FriendCmd(this, "friend", "", "friends", "fr", "f"));
    }

    private void loadListeners() {
        this.pluginManager.registerListener(this, new ConnectionListener(this));
        this.pluginManager.registerListener(this, new ServerListener(this));
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public Storage getStorage() {
        return this.storage;
    }
}
