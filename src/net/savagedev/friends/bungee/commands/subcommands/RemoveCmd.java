package net.savagedev.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.User;
import net.savagedev.friends.bungee.utils.MessageUtils;
import net.savagedev.friends.bungee.utils.io.FileUtils;

import java.io.File;
import java.util.UUID;

@SuppressWarnings("Duplicates")
public class RemoveCmd extends SubCommand {
    public RemoveCmd(RevelationFriends plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer user = (ProxiedPlayer) sender;

        if (args.length == 1) {
            MessageUtils.message(user, this.plugin().getConfig(RevelationFriends.ConfigType.LANG).getString("error.invalid-arguments").replace("%cmd%", "remove <player>"));
            return;
        }

        if (!this.plugin().getUserManager().get(user.getUniqueId()).isFriends(this.plugin().getUserManager().getUniqueId(args[1]))) {
            MessageUtils.message(user, "&cYou are not friends with " + args[1]);
            return;
        }

        String target = args[0];

        if (!this.removeFriend(user, target)) {
            MessageUtils.message(user, String.format("&cAn error occurred removing %s from your friends list. Please report this to an admin.", target));
            return;
        }

        MessageUtils.message(user, String.format("&cYou have removed %s from your friends list.", target));
    }

    private boolean removeFriend(ProxiedPlayer user, String target_name) {
        ProxiedPlayer target = this.plugin().getProxy().getPlayer(target_name);
        User fuser = this.plugin().getUserManager().get(user.getUniqueId());

        if (target == null) {
            UUID target_uuid = this.plugin().getUserManager().getUniqueId(target_name);

            if (target_uuid == null) {
                return false;
            }

            File target_file = new File(this.plugin().getDataFolder(), String.format("storage/%s.yml", target_uuid.toString()));
            Configuration target_storage_file = FileUtils.load(target_file);

            if (target_storage_file == null) {
                return false;
            }

            target_storage_file.set(String.format("friends.%s", user.getUniqueId()), null);
            FileUtils.save(target_storage_file, target_file);

            fuser.removeFriend(fuser.getFriend(target_uuid));
            this.plugin().getUserManager().save(user.getUniqueId());
            return true;
        }

        UUID target_uuid = target.getUniqueId();

        User target_fuser = this.plugin().getUserManager().get(target_uuid);
        target_fuser.removeFriend(target_fuser.getFriend(user.getUniqueId()));
        this.plugin().getUserManager().save(target_uuid);

        fuser.removeFriend(fuser.getFriend(target_uuid));
        this.plugin().getUserManager().save(user.getUniqueId());

        MessageUtils.message(target, "&c" + user.getDisplayName() + " has removed you from their friends list.");
        return true;

        /*if (friend == null) {
            UUID uuid = this.plugin().getUserManager().getUniqueId(exFriend);

            fuser.removeFriend(fuser.getFriend(uuid));
            this.plugin().getMySQL().update(String.format("DELETE FROM revelation_friends_data WHERE id = \'%s\' AND friend_id = \'%s\';", user.getUniqueId().toString(), uuid.toString()));
            this.plugin().getMySQL().update(String.format("DELETE FROM revelation_friends_data WHERE id = \'%s\' AND friend_id = \'%s\';", uuid.toString(), user.getUniqueId().toString()));
            return;
        }

        fuser.removeFriend(fuser.getFriend(friend.getUniqueId()));
        this.plugin().getUserManager().get(friend.getUniqueId()).removeFriend(this.plugin().getUserManager().get(friend.getUniqueId()).getFriend(user.getUniqueId()));
        this.plugin().getMySQL().update(String.format("DELETE FROM revelation_friends_data WHERE id = \'%s\' AND friend_id = \'%s\';", user.getUniqueId().toString(), friend.getUniqueId().toString()));
        this.plugin().getMySQL().update(String.format("DELETE FROM revelation_friends_data WHERE id = \'%s\' AND friend_id = \'%s\';", friend.getUniqueId().toString(), user.getUniqueId().toString()));
        MessageUtils.message(friend, "&c" + user.getDisplayName() + " has removed you from their friends list.");*/
    }
}
