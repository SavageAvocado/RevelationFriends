package net.revelationmc.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;
import net.revelationmc.friends.bungee.model.friend.FriendRequest;
import net.revelationmc.friends.bungee.model.user.User;
import net.revelationmc.friends.bungee.model.user.UserSetting;
import net.revelationmc.friends.bungee.utils.MessageUtils;

public class AddCmd implements SubCommand {
    private final RevelationFriendsPlugin plugin;

    public AddCmd(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 1) {
            MessageUtils.message(player, "&cInvalid arguments! Try: /friend add <player>");
            return;
        }

        final String target = args[1];

        if (target.equalsIgnoreCase(player.getName())) {
            MessageUtils.message(player, "&cYou can't friend yourself! :(");
            return;
        }

        final User user = this.plugin.getUserManager().getOrLoad(player.getUniqueId()).join();
        this.plugin.getUserManager().getUuid(target)
                .whenComplete((uuid, err) -> {
                    if (err != null) {
                        MessageUtils.message(player, "&cA fatal error occurred executing this command! Please contact an administrator.");
                        err.printStackTrace();
                        return;
                    }

                    if (uuid == null) {
                        MessageUtils.message(player, String.format("&cUnknown player! (%s)", target));
                        return;
                    }

                    if (user.isFriendsWith(uuid)) {
                        MessageUtils.message(player, "&cYou are already friends with " + target);
                        return;
                    }

                    if (user.hasRequestFrom(uuid)) {
                        MessageUtils.message(player, "&aYou have a pending friend request from " + target);
                        return;
                    }

                    final User friendUser = this.plugin.getUserManager().getOrLoad(uuid).join();
                    if (!friendUser.getSettings().get(UserSetting.ALLOW_FRIEND_REQUESTS)) {
                        MessageUtils.message(player, "&c" + target + " is not allowing friend requests.");
                        return;
                    }

                    if (friendUser.hasRequestFrom(player.getUniqueId())) {
                        MessageUtils.message(player, "&cYou already sent a friend request to " + target + ".");
                        return;
                    }

                    final FriendRequest request = new FriendRequest(player, uuid);
                    friendUser.addFriendRequest(request);

                    this.plugin.getStorage().saveUser(friendUser).join();

                    final ProxiedPlayer friendPlayer = this.plugin.getProxy().getPlayer(uuid);
                    if (friendPlayer != null) {
                        MessageUtils.message(friendPlayer, this.getRequestMessage(player));
                    }

                    MessageUtils.message(player, "&aYou have sent a friend request to " + target);
                });
    }

    private TextComponent getRequestMessage(ProxiedPlayer sender) {
        TextComponent message = new TextComponent(MessageUtils.color("&aNew friend request! &7" + sender.getDisplayName() + " "));

        TextComponent accept = new TextComponent(MessageUtils.color("&a[\u2713] "));
        HoverEvent accept_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&aClick to accept this friend request.")).create());
        ClickEvent accept_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept " + sender.getName());
        accept.setHoverEvent(accept_hover);
        accept.setClickEvent(accept_click);

        TextComponent deny = new TextComponent(MessageUtils.color("&c[\u2717]"));
        HoverEvent deny_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&cClick to deny this friend request.")).create());
        ClickEvent deny_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f deny " + sender.getName());
        deny.setHoverEvent(deny_hover);
        deny.setClickEvent(deny_click);

        return new TextComponent(message, accept, deny);
    }
}
