package net.revelationmc.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;
import net.revelationmc.friends.bungee.model.user.User;
import net.revelationmc.friends.bungee.utils.MessageUtils;

public class RemoveCmd implements SubCommand {
    private final RevelationFriendsPlugin plugin;

    public RemoveCmd(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 1) {
            MessageUtils.message(player, "&cInvalid arguments! Try: /friend remove <player>");
            return;
        }

        final User user = this.plugin.getUserManager().getOrLoad(player.getUniqueId()).join();

        final String target = args[1];

        this.plugin.getUserManager().getUuid(target)
                .whenComplete((uuid, err) -> {
                    if (err != null) {
                        MessageUtils.message(player, "&cA fatal error occurred executing this command! Please contact an administrator.");
                        err.printStackTrace();
                    } else {
                        if (user.getFriend(uuid) == null) {
                            MessageUtils.message(player, "&cYou are not friends with " + target);
                            return;
                        }

                        final User friendUser = this.plugin.getUserManager().getOrLoad(uuid).join();
                        friendUser.removeFriend(player.getUniqueId());
                        user.removeFriend(uuid);

                        this.plugin.getStorage().saveUser(friendUser).join();
                        this.plugin.getStorage().saveUser(user).join();

                        final ProxiedPlayer friendPlayer = this.plugin.getProxy().getPlayer(uuid);
                        if (friendPlayer != null) {
                            MessageUtils.message(friendPlayer, "&c" + player.getDisplayName() + " has removed you from their friends list.");
                        }
                        MessageUtils.message(player, String.format("&cYou have removed %s from your friends list.", target));
                    }
                });
    }
}
