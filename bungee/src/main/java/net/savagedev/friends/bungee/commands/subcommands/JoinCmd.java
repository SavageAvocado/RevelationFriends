package net.savagedev.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.utils.MessageUtils;

public class JoinCmd extends SubCommand {
    public JoinCmd(RevelationFriends plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer user = (ProxiedPlayer) sender;

        if (args.length == 1) {
            MessageUtils.message(user, "&cInvalid arguments! Try: /friend join <player>");
            return;
        }

        final ProxiedPlayer friend = this.plugin().getProxy().getPlayer(args[1]);

        if (friend == null) {
            MessageUtils.message(user, "&c" + args[1] + " is not online.");
            return;
        }

        if (!this.plugin().getUserManager().getOrLoad(user.getUniqueId()).join().isFriendsWith(friend.getUniqueId())) {
            MessageUtils.message(user, "&c" + friend.getDisplayName() + " &cis not your friend.");
            return;
        }

        MessageUtils.message(user, "&aConnecting you to " + friend.getServer().getInfo().getName() + "...");
        user.connect(friend.getServer().getInfo());
    }
}
