package net.savagedev.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.User;
import net.savagedev.friends.bungee.user.friend.Friend;
import net.savagedev.friends.bungee.utils.MessageUtils;
import net.savagedev.friends.bungee.utils.io.FileUtils;

import java.io.File;
import java.util.UUID;

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

        ProxiedPlayer user = (ProxiedPlayer) sender;

        if (args.length == 1) {
            MessageUtils.message(user, this.plugin().getConfig(RevelationFriends.ConfigType.LANG).getString("error.invalid-arguments").replace("%cmd%", "accept <player>"));
            return;
        }

        User fuser = this.plugin().getUserManager().get(user.getUniqueId());
        String target = args[1];

        if (!fuser.getFriendRequests().contains(this.plugin().getUserManager().getUniqueId(target))) {
            MessageUtils.message(user, "&cYou do not have a pending friend request from " + target);
            return;
        }

        if (!this.addFriend(user, target)) {
            MessageUtils.message(user, String.format("&cAn error occurred accepting %s's friend request. Please report this to an admin.", target));
            return;
        }

        MessageUtils.message(user, "&aYou are now friends with " + target);
    }

    private boolean addFriend(ProxiedPlayer user, String target_name) {
        ProxiedPlayer target = this.plugin().getProxy().getPlayer(target_name);
        long time = System.currentTimeMillis();
        UUID uuid = user.getUniqueId();

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

            target_storage_file.set(String.format("friends.%s.username", uuid), user.getName());
            target_storage_file.set(String.format("friends.%s.time", uuid), time);
            FileUtils.save(target_storage_file, target_file);

            this.plugin().getUserManager().get(uuid).addFriend(new Friend(this.plugin().getUserManager().getUsername(target_uuid), target_uuid, time));
            this.plugin().getUserManager().save(uuid);
            return true;
        }

        User fuser = this.plugin().getUserManager().get(uuid);
        UUID target_uuid = target.getUniqueId();

        fuser.removeFriendRequest(target_uuid);

        this.plugin().getUserManager().get(target_uuid).addFriend(new Friend(user.getName(), uuid, time));
        fuser.addFriend(new Friend(target.getName(), target_uuid, time));

        this.plugin().getUserManager().save(target_uuid);
        this.plugin().getUserManager().save(uuid);

        MessageUtils.message(target, "&a" + user.getDisplayName() + " accepted your friend request.");
        return true;

        /*if (friend == null) {
            UUID uuid = this.plugin().getUserManager().getUniqueId(friendName);
            String username = this.plugin().getUserManager().getUsername(uuid);

            User fuser = this.plugin().getUserManager().get(user.getUniqueId());

            fuser.removeFriendRequest(uuid);
            fuser.addFriend(new Friend(username, uuid, time));

            this.plugin().getUserManager().save(user.getUniqueId());

            this.plugin().getMySQL().update(String.format("DELETE FROM revelation_friends_requests WHERE id = \'%s\'", uuid.toString()));
            this.plugin().getMySQL().update(String.format("INSERT INTO revelation_friends_data (id, friend_id, time) VALUES (\'%s\', \'%s\', " + time + ");", user.getUniqueId().toString(), uuid.toString()));
            this.plugin().getMySQL().update(String.format("INSERT INTO revelation_friends_data (id, friend_id, time) VALUES (\'%s\', \'%s\', " + time + ");", uuid.toString(), user.getUniqueId().toString()));
            return;
        }

        User fuser = this.plugin().getUserManager().get(user.getUniqueId());

        fuser.removeFriendRequest(friend.getUniqueId());
        fuser.addFriend(new Friend(friend.getName(), friend.getUniqueId(), time));
        this.plugin().getUserManager().get(friend.getUniqueId()).addFriend(new Friend(user.getName(), user.getUniqueId(), time));

        this.plugin().getUserManager().save(user.getUniqueId());


        this.plugin().getMySQL().update(String.format("DELETE FROM revelation_friends_requests WHERE id = \'%s\'", friend.getUniqueId().toString()));
        this.plugin().getMySQL().update(String.format("INSERT INTO revelation_friends_data (id, friend_id, time) VALUES(\'%s\', \'%s\', " + time + ");", user.getUniqueId().toString(), friend.getUniqueId().toString()));
        this.plugin().getMySQL().update(String.format("INSERT INTO revelation_friends_data (id, friend_id, time) VALUES(\'%s\', \'%s\', " + time + ");", friend.getUniqueId().toString(), user.getUniqueId().toString()));
        MessageUtils.message(friend, "&a" + user.getDisplayName() + " accepted your friend request.");*/
    }
}
