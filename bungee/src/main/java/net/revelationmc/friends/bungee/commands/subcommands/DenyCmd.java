package net.revelationmc.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;
import net.revelationmc.friends.bungee.model.user.User;
import net.revelationmc.friends.bungee.utils.MessageUtils;

public class DenyCmd implements SubCommand {
    private final RevelationFriendsPlugin plugin;

    public DenyCmd(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 1) {
            MessageUtils.message(player, "&cInvalid arguments! Try: /friend deny <player>");
            return;
        }

        final User user = this.plugin.getUserManager().getOrLoad(player.getUniqueId()).join();
        final String target = args[1];

        this.plugin.getUserManager().getUuid(target)
                .whenComplete((uuid, err) -> {
                    if (err != null) {
                        MessageUtils.message(player, "&cA fatal error occurred executing this command! Please contact an administrator.");
                        err.printStackTrace();
                        return;
                    }

                    if (!user.hasRequestFrom(uuid)) {
                        MessageUtils.message(player, "&cYou do not have a pending friend request from " + target);
                        return;
                    }

                    user.removeFriendRequest(user.getFriendRequest(uuid));
                    this.plugin.getStorage().saveUser(user).join();

                    final ProxiedPlayer friendPlayer = this.plugin.getProxy().getPlayer(uuid);
                    if (friendPlayer != null) {
                        MessageUtils.message(friendPlayer, "&c" + player.getDisplayName() + " has denied your friend request.");
                    }

                    MessageUtils.message(player, "&cYou have denied " + target + "'s friend request.");
                });
    }
}
