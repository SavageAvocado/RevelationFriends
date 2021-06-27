package net.savagedev.friends.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.commands.async.AsyncCommand;
import net.savagedev.friends.bungee.commands.subcommands.*;
import net.savagedev.friends.bungee.utils.MessageUtils;

import java.util.HashMap;
import java.util.Map;

public class FriendCmd extends AsyncCommand {
    private final Map<String, SubCommand> subCommands;

    public FriendCmd(RevelationFriends plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
        this.subCommands = new HashMap<>();
        this.init();
    }

    private void init() {
        this.subCommands.putIfAbsent("accept", new AcceptCmd(this.plugin()));
        this.subCommands.putIfAbsent("add", new AddCmd(this.plugin()));
        this.subCommands.putIfAbsent("deny", new DenyCmd(this.plugin()));
        this.subCommands.putIfAbsent("help", new HelpCmd(this.plugin()));
        this.subCommands.putIfAbsent("join", new JoinCmd(this.plugin()));
        this.subCommands.putIfAbsent("list", new ListCmd(this.plugin()));
        this.subCommands.putIfAbsent("remove", new RemoveCmd(this.plugin()));
        this.subCommands.putIfAbsent("requests", new RequestsCmd(this.plugin()));
        this.subCommands.putIfAbsent("settings", new SettingsCmd(this.plugin()));
    }

    @Override
    protected void command(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer user = (ProxiedPlayer) sender;

        if (args.length == 0) {
            MessageUtils.message(user, "&cInvalid arguments! Try: /friend help");
            return;
        }

        final SubCommand command = this.getCommand(args[0].toLowerCase());

        if (command == null) {
            MessageUtils.message(user, "&cInvalid arguments! Try: /friend " + this.getSuggestion(args[0]));
            return;
        }

        command.execute(sender, args);
    }

    private SubCommand getCommand(String name) {
        if (this.subCommands.containsKey(name)) {
            return this.subCommands.get(name);
        }

        return null;
    }

    private String getSuggestion(String partialArgument) {
        for (String command : this.subCommands.keySet()) {
            if (command.startsWith(partialArgument.toLowerCase())) {
                return command;
            }
        }

        return "help";
    }
}
