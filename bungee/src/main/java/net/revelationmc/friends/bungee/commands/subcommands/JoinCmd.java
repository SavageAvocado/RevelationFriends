package net.revelationmc.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;
import net.revelationmc.friends.bungee.config.Lang;
import net.revelationmc.friends.bungee.utils.MessageUtils;

public class JoinCmd implements SubCommand {
    private final RevelationFriendsPlugin plugin;

    public JoinCmd(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String label, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 1) {
            Lang.INVALID_ARGUMENTS.send(player, new Lang.Placeholder("%command%", "/" + label + " join <player>"));
            return;
        }

        final ProxiedPlayer friend = this.plugin.getProxy().getPlayer(args[1]);

        if (friend == null) {
            MessageUtils.message(player, "&c" + args[1] + " is not online.");
            return;
        }

        if (!this.plugin.getUserManager().getOrLoad(player.getUniqueId()).join().isFriendsWith(friend.getUniqueId())) {
            MessageUtils.message(player, "&c" + friend.getDisplayName() + " &cis not your friend.");
            return;
        }

        MessageUtils.message(player, "&aConnecting you to " + friend.getServer().getInfo().getName() + "...");
        player.connect(friend.getServer().getInfo());
    }
}
