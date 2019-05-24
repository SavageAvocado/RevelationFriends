package net.savagedev.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.User;
import net.savagedev.friends.bungee.utils.MessageUtils;
import net.savagedev.friends.bungee.utils.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("Duplicates")
public class AddCmd extends SubCommand {
    public AddCmd(RevelationFriends plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer user = (ProxiedPlayer) sender;

        if (args.length == 1) {
            MessageUtils.message(user, this.plugin().getConfig(RevelationFriends.ConfigType.LANG).getString("error.invalid-arguments").replace("%cmd%", "add <player>"));
            return;
        }

        String target = args[1];

        if (target.equalsIgnoreCase(user.getName())) {
            MessageUtils.message(user, "&cYou can't friend yourself! :(");
            return;
        }

        User fuser = this.plugin().getUserManager().get(user.getUniqueId());
        UUID fuuid = this.plugin().getUserManager().getUniqueId(target);

        if (fuuid == null) {
            MessageUtils.message(user, String.format("&cUnknown player! (%s)", target));
            return;
        }

        if (fuser.isFriends(fuuid)) {
            MessageUtils.message(user, "&cYou are already friends with " + target);
            return;
        }

        if (!this.plugin().getUserManager().isAllowingFriendRequests(fuuid)) {
            MessageUtils.message(user, "&c" + target + " is not allowing friend requests.");
            return;
        }

        if (this.plugin().getUserManager().hasRequestFrom(user.getUniqueId(), fuuid)) {
            MessageUtils.message(user, "&cYou already sent a friend request to " + target + ".");
            return;
        }

        if (fuser.getFriendRequests().contains(fuuid)) {
            MessageUtils.message(user, "&aYou have a pending friend request from " + target);
            return;
        }

        if (!this.sendRequest(user, target)) {
            MessageUtils.message(user, String.format("&cAn error occurred sending a friend request to %s. Please report this to an admin.", target));
            return;
        }

        MessageUtils.message(user, "&aYou have sent a friend request to " + target);
    }

    private boolean sendRequest(ProxiedPlayer user, String target_name) {
        ProxiedPlayer target = this.plugin().getProxy().getPlayer(target_name);

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

            List<String> requests = target_storage_file.getStringList("requests");
            requests.add(user.getUniqueId().toString());

            target_storage_file.set("requests", requests);
            FileUtils.save(target_storage_file, target_file);
            return true;
        }

        UUID target_uuid = target.getUniqueId();

        this.plugin().getUserManager().get(target_uuid).addFriendRequest(user.getUniqueId());
        this.plugin().getUserManager().save(target_uuid);

        MessageUtils.message(target, this.getRequestMessage(user));
        return true;

        /*if (friend == null) {
            UUID uuid = this.plugin().getUserManager().getUniqueId(to);

            this.plugin().getMySQL().update(String.format("INSERT INTO revelation_friends_requests (id, requested) VALUES(\'%s\', \'%s\');", from.getUniqueId().toString(), uuid.toString()));
            return;
        }

        this.plugin().getMySQL().update(String.format("INSERT INTO revelation_friends_requests (id, requested) VALUES(\'%s\', \'%s\');", from.getUniqueId().toString(), friend.getUniqueId().toString()));
        this.plugin().getUserManager().get(friend.getUniqueId()).addFriendRequest(from.getUniqueId());
        MessageUtils.message(friend, this.getRequestMessage(from));*/
    }

    private TextComponent getRequestMessage(ProxiedPlayer sender) {
        TextComponent message = new TextComponent(MessageUtils.color("&aNew friend request! &7" + sender.getDisplayName() + " "));

        TextComponent accept = new TextComponent(MessageUtils.color("&a[✔] "));
        HoverEvent accept_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&aClick to accept this friend request.")).create());
        ClickEvent accept_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept " + sender.getName());
        accept.setHoverEvent(accept_hover);
        accept.setClickEvent(accept_click);

        TextComponent deny = new TextComponent(MessageUtils.color("&c[✘]"));
        HoverEvent deny_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&cClick to deny this friend request.")).create());
        ClickEvent deny_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f deny " + sender.getName());
        deny.setHoverEvent(deny_hover);
        deny.setClickEvent(deny_click);

        return new TextComponent(message, accept, deny);
    }
}
