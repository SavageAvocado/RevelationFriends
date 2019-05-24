package net.savagedev.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.User;
import net.savagedev.friends.bungee.utils.MessageUtils;

import java.util.UUID;

@SuppressWarnings("Duplicates")
public class RequestsCmd extends SubCommand {
    public RequestsCmd(RevelationFriends plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer user = (ProxiedPlayer) sender;
        User fuser = this.plugin().getUserManager().get(user.getUniqueId());

        MessageUtils.message(user, "&8&l&m--------------[&6 Friend Requests &8&l&m]--------------");

        if (fuser.getFriendRequests().size() <= 0) {
            MessageUtils.message(user, "&cNo pending friend requests.");
            MessageUtils.message(user, "&8&l&m------------------------------------------");
            return;
        }

        for (UUID uuid : fuser.getFriendRequests()) {
            String username = this.plugin().getUserManager().getUsername(uuid);
            MessageUtils.message(user, this.getRequestMessage(username));
        }

        MessageUtils.message(user, "&8&l&m------------------------------------------");
    }

    private TextComponent getRequestMessage(String sender) {
        TextComponent message = new TextComponent(MessageUtils.color("&3" + sender + " "));

        TextComponent accept = new TextComponent(MessageUtils.color("&a[✔] "));
        HoverEvent accept_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&aClick to accept this friend request.")).create());
        ClickEvent accept_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept " + sender);
        accept.setHoverEvent(accept_hover);
        accept.setClickEvent(accept_click);

        TextComponent deny = new TextComponent(MessageUtils.color("&c[✘]"));
        HoverEvent deny_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&cClick to deny this friend request.")).create());
        ClickEvent deny_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f deny " + sender);
        deny.setHoverEvent(deny_hover);
        deny.setClickEvent(deny_click);

        return new TextComponent(message, accept, deny);
    }
}
