package net.savagedev.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.savagedev.friends.bungee.RevelationFriends;

public abstract class SubCommand {
    private final RevelationFriends plugin;

    public SubCommand(RevelationFriends plugin) {
        this.plugin = plugin;
    }

    public abstract void execute(CommandSender user, String... args);

    RevelationFriends plugin() {
        return this.plugin;
    }
}