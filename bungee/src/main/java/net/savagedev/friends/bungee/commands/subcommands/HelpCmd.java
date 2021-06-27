package net.savagedev.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

public class HelpCmd extends SubCommand {
    private final List<TextComponent> helpMessages;

    public HelpCmd(RevelationFriends plugin) {
        super(plugin);

        this.helpMessages = new ArrayList<>();
        this.init();
    }

    private void init() {
        this.helpMessages.add(this.buildHelpMessage("&8/&6f accept <player> &8-&7 Accepts a pending friend request.", "/f accept "));
        this.helpMessages.add(this.buildHelpMessage("&8/&6f add <player> &8-&7 Sends a player a friend request.", "/f add "));
        this.helpMessages.add(this.buildHelpMessage("&8/&6f deny <player> &8-&7 Denies a pending friend request.", "/f deny "));
        this.helpMessages.add(this.buildHelpMessage("&8/&6f join <player> &8-&7 Connects you to a friend's server.", "/f join "));
        this.helpMessages.add(this.buildHelpMessage("&8/&6f list &8-&7 Lists your friends.", "/f list"));
        this.helpMessages.add(this.buildHelpMessage("&8/&6f remove <player> &8-&7 Removes a player from your friend list.", "/f remove "));
        this.helpMessages.add(this.buildHelpMessage("&8/&6f requests &8-&7 Lists all of your pending friend requests.", "/f requests"));
        this.helpMessages.add(this.buildHelpMessage("&8/&6f settings &8-&7 Opens the friends settings menu.", "/f settings"));
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer user = (ProxiedPlayer) sender;

        MessageUtils.message(user, "&8&l&m------------[&6 Friends Help &8&l&m]------------");

        for (TextComponent message : this.helpMessages) {
            MessageUtils.message(user, message);
        }

        MessageUtils.message(user, "&8&l&m------------------------------------------");
    }

    private TextComponent buildHelpMessage(String helpMessage, String command) {
        TextComponent message = new TextComponent(MessageUtils.color(helpMessage));
        HoverEvent message_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&aClick to paste command in chat.")).create());
        ClickEvent message_click = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
        message.setHoverEvent(message_hover);
        message.setClickEvent(message_click);
        return message;
    }
}