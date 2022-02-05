package net.revelationmc.friends.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;
import net.revelationmc.friends.bungee.commands.async.AsyncCommand;
import net.revelationmc.friends.bungee.commands.subcommands.*;
import net.revelationmc.friends.bungee.utils.MessageUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FriendCmd extends AsyncCommand {
    private final Map<String, SubCommand> subCommands = new ConcurrentHashMap<>();

    public FriendCmd(RevelationFriendsPlugin plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
        this.init();
    }

    private void init() {
        this.subCommands.putIfAbsent("accept", new AcceptCmd(this.plugin()));
        this.subCommands.putIfAbsent("add", new AddCmd(this.plugin()));
        this.subCommands.putIfAbsent("deny", new DenyCmd(this.plugin()));
        this.subCommands.putIfAbsent("help", new HelpCmd());
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

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {
            MessageUtils.message(player, "&cInvalid arguments! Try: /friend help");
            return;
        }

        final SubCommand command = this.getCommand(args[0].toLowerCase());

        if (command == null) {
            MessageUtils.message(player, "&cInvalid arguments! Try: /friend " + this.getSuggestion(args[0]));
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
