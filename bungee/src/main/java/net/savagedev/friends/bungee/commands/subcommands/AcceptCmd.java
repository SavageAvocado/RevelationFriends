package net.savagedev.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.model.friend.FriendRequest;
import net.savagedev.friends.bungee.model.user.User;
import net.savagedev.friends.bungee.utils.MessageUtils;

@SuppressWarnings("Duplicates")
public class AcceptCmd extends SubCommand {
    public AcceptCmd(RevelationFriends plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 1) {
            MessageUtils.message(player, "&cInvalid arguments! Try: /friend accept <player>");
            return;
        }

        final User user = this.plugin().getUserManager().getOrLoad(player.getUniqueId()).join();
        final String targetName = args[1];

        this.plugin().getUserManager().getUuid(targetName)
                .whenComplete((uuid, err) -> {
                    if (err != null) {
                        MessageUtils.message(player, "&cA fatal error occurred executing this command! Please contact an administrator.");
                        err.printStackTrace();
                        return;
                    }

                    if (!user.hasRequestFrom(uuid)) {
                        MessageUtils.message(player, "&cYou do not have a pending friend request from " + targetName + ".");
                        return;
                    }

                    final FriendRequest request = user.getFriendRequest(uuid);
                    user.removeFriendRequest(request);

                    final User friendUser = this.plugin().getUserManager().getOrLoad(uuid).join();
                    friendUser.addFriend(player);
                    user.addFriend(request);

                    this.plugin().getStorage().saveUser(friendUser).join();
                    this.plugin().getStorage().saveUser(user).join();

                    final ProxiedPlayer friendPlayer = this.plugin().getProxy().getPlayer(uuid);
                    if (friendPlayer != null) {
                        MessageUtils.message(friendPlayer, "&a" + player.getDisplayName() + " accepted your friend request.");
                    }

                    MessageUtils.message(player, "&aYou are now friends with " + targetName);
                });
    }
}
