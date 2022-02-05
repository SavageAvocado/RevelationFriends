package net.revelationmc.friends.bungee.commands.async;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;

public abstract class AsyncCommand extends Command {
    private final RevelationFriendsPlugin plugin;

    public AsyncCommand(RevelationFriendsPlugin plugin, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> this.command(commandSender, strings));
    }

    protected abstract void command(CommandSender user, String... args);

    protected RevelationFriendsPlugin plugin() {
        return this.plugin;
    }
}
