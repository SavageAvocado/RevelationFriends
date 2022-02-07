package net.revelationmc.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;

public interface SubCommand {
    void execute(CommandSender user, String label, String... args);
}