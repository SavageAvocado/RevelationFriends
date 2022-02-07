package net.revelationmc.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.config.Lang;
import net.revelationmc.friends.bungee.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

public class HelpCmd implements SubCommand {
    private static final List<BaseComponent[]> MESSAGES = new ArrayList<>();

    private static void addHelpMessage(String message, String command) {
        MESSAGES.add(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', message))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Click to paste command in chat.")))
                .create()
        );
    }

    static {
        addHelpMessage("&8/&6f accept <player> &8-&7 Accepts a pending friend request.", "/f accept ");
        addHelpMessage("&8/&6f add <player> &8-&7 Sends a player a friend request.", "/f add ");
        addHelpMessage("&8/&6f deny <player> &8-&7 Denies a pending friend request.", "/f deny ");
        addHelpMessage("&8/&6f join <player> &8-&7 Connects you to a friend's server.", "/f join ");
        addHelpMessage("&8/&6f list &8-&7 Lists your friends.", "/f list");
        addHelpMessage("&8/&6f remove <player> &8-&7 Removes a player from your friend list.", "/f remove ");
        addHelpMessage("&8/&6f requests &8-&7 Lists all of your pending friend requests.", "/f requests");
        addHelpMessage("&8/&6f settings &8-&7 Opens the friends settings menu.", "/f settings");
    }

    @Override
    public void execute(CommandSender sender, String label, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        Lang.COMMAND_LIST_HEADER.send(player);

        for (BaseComponent[] components : MESSAGES) {
            player.sendMessage(components);
        }

        Lang.COMMAND_LIST_FOOTER.send(player);
    }
}