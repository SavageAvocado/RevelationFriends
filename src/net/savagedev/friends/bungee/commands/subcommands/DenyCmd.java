package net.savagedev.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.User;
import net.savagedev.friends.bungee.utils.MessageUtils;

import java.util.UUID;

@SuppressWarnings("Duplicates")
public class DenyCmd extends SubCommand {
    public DenyCmd(RevelationFriends plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer user = (ProxiedPlayer) sender;

        if (args.length == 1) {
            MessageUtils.message(user, this.plugin().getConfig(RevelationFriends.ConfigType.LANG).getString("error.invalid-arguments").replace("%cmd%", "deny <player>"));
            return;
        }

        User fuser = this.plugin().getUserManager().get(user.getUniqueId());
        String target = args[0];

        UUID target_uuid = this.plugin().getProxy().getPlayer(target) != null ? this.plugin().getProxy().getPlayer(target).getUniqueId() : this.plugin().getUserManager().getUniqueId(target);

        if (!fuser.getFriendRequests().contains(target_uuid)) {
            MessageUtils.message(user, "&cYou do not have a pending friend request from " + target);
            return;
        }

        if (!this.denyRequest(user, target)) {
            MessageUtils.message(user, String.format("&cAn error occurred denying %s's friend request. Please report this to an admin.", target));
            return;
        }

        MessageUtils.message(user, "&cYou have denied " + target + "'s friend request.");
    }

    private boolean denyRequest(ProxiedPlayer user, String target_name) {
        ProxiedPlayer target = this.plugin().getProxy().getPlayer(target_name);

        if (target == null) {
            UUID target_uuid = this.plugin().getUserManager().getUniqueId(target_name);

            if (target_uuid == null) {
                return false;
            }

            this.plugin().getUserManager().get(user.getUniqueId()).removeFriendRequest(target_uuid);
            this.plugin().getUserManager().save(user.getUniqueId());
            return true;
        }

        this.plugin().getUserManager().get(user.getUniqueId()).removeFriendRequest(target.getUniqueId());
        this.plugin().getUserManager().save(user.getUniqueId());

        MessageUtils.message(target, "&c" + user.getDisplayName() + " has denied your friend request.");
        return true;

        /*if (friend == null) {
            UUID uuid = this.plugin().getUserManager().getUniqueId(sender);

            this.plugin().getUserManager().get(user.getUniqueId()).removeFriendRequest(uuid);
            this.plugin().getMySQL().update(String.format("DELETE FROM revelation_friends_requests WHERE id = \'%s\';", uuid.toString()));
            return;
        }

        this.plugin().getUserManager().get(user.getUniqueId()).removeFriendRequest(friend.getUniqueId());
        this.plugin().getMySQL().update(String.format("DELETE FROM revelation_friends_requests WHERE id = \'%s\';", friend.getUniqueId().toString()));
        MessageUtils.message(friend, "&c" + user.getDisplayName() + " has denied your friend request.");*/
    }
}
