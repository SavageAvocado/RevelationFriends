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
import net.revelationmc.friends.bungee.utils.MessageUtils;

public class RequestsCmd implements SubCommand {
    private final RevelationFriendsPlugin plugin;

    public RequestsCmd(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String label, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;
        final User user = this.plugin.getUserManager().getOrLoad(player.getUniqueId()).join();

        MessageUtils.message(player, "&8&l&m--------------[&6 Friend Requests &8&l&m]--------------");

        if (user.getFriendRequests().size() <= 0) {
            MessageUtils.message(player, "&cNo pending friend requests.");
            MessageUtils.message(player, "&8&l&m------------------------------------------");
            return;
        }

        for (FriendRequest request : user.getFriendRequests()) {
            MessageUtils.message(player, this.getRequestMessage(request.getSenderName()));
        }

        MessageUtils.message(player, "&8&l&m------------------------------------------");
    }

    private TextComponent getRequestMessage(String sender) {
        TextComponent message = new TextComponent(MessageUtils.color("&3" + sender + " "));

        TextComponent accept = new TextComponent(MessageUtils.color("&a[&l✔&a] "));
        HoverEvent accept_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&aClick to accept this friend request.")).create());
        ClickEvent accept_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept " + sender);
        accept.setHoverEvent(accept_hover);
        accept.setClickEvent(accept_click);

        TextComponent deny = new TextComponent(MessageUtils.color("&c[&l✘&a]"));
        HoverEvent deny_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&cClick to deny this friend request.")).create());
        ClickEvent deny_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f deny " + sender);
        deny.setHoverEvent(deny_hover);
        deny.setClickEvent(deny_click);

        return new TextComponent(message, accept, deny);
    }
}