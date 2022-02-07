package net.revelationmc.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;
import net.revelationmc.friends.bungee.model.friend.Friendship;
import net.revelationmc.friends.bungee.model.user.User;
import net.revelationmc.friends.bungee.utils.MessageUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ListCmd implements SubCommand {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy h:mma");
    private static final int PAGE_SIZE = 8;

    private static final TextComponent OPEN_BRACKET = new TextComponent(MessageUtils.color("&8["));
    private static final TextComponent CLOSED_BRACKET = new TextComponent(MessageUtils.color("&8]"));

    private final RevelationFriendsPlugin plugin;

    public ListCmd(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String label, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        final String pageString = args.length == 1 ? "1" : args[1];
        final ProxiedPlayer player = (ProxiedPlayer) sender;

        final User fuser = this.plugin.getUserManager().getOrLoad(player.getUniqueId()).join();

        if (fuser.getFriends().size() <= 0) {
            MessageUtils.message(player, "&8&l&m------------------------------------------");
            MessageUtils.message(player, "&cYou don't have any friends yet. :(");
            MessageUtils.message(player, "&8&l&m------------------------------------------");
            return;
        }

        try {
            final int page = Integer.parseInt(pageString);
            long friendListPages = Math.round(((float) fuser.getFriends().size() / PAGE_SIZE) + .375);
            if (Integer.parseInt(pageString) <= 0 || Integer.parseInt(pageString) > friendListPages) {
                MessageUtils.message(player, "&cInvalid page number!");
                return;
            }

            MessageUtils.message(player, this.getHeader(page));

            final List<Friendship> friendships = this.getSortedFriendsList(fuser.getFriends());

            for (int i = ((page - 1) * PAGE_SIZE) > friendships.size() ? 0 : (page - 1) * PAGE_SIZE; i < Math.min(PAGE_SIZE * page, friendships.size()); i++) {
                final Friendship friendship = friendships.get(i);

                if (friendship.isOnline()) {
                    MessageUtils.message(player, this.getOnlineFriendMessage(friendship));
                    continue;
                }

                MessageUtils.message(player, this.getOfflineFriendMessage(friendship));
            }

            MessageUtils.message(player, "&8&l&m-------------------------------------------");
        } catch (NumberFormatException ignored) {
            MessageUtils.message(player, "&cPage must be a number!");
        }


    }

    private List<Friendship> getSortedFriendsList(Collection<Friendship> friendships) {
        List<Friendship> sorted = new ArrayList<>();

        sorted.addAll(this.getOnlineFriends(friendships));
        sorted.addAll(this.getOfflineFriends(friendships));

        return sorted;
    }

    private List<Friendship> getOnlineFriends(Collection<Friendship> friendships) {
        List<Friendship> online = new ArrayList<>();

        for (Friendship friendship : friendships) {

            if (friendship.isOnline()) {
                online.add(friendship);
            }
        }

        return online;
    }

    private List<Friendship> getOfflineFriends(Collection<Friendship> friendships) {
        List<Friendship> offline = new ArrayList<>();

        for (Friendship friendship : friendships) {
            if (!friendship.isOnline()) {
                offline.add(friendship);
            }
        }

        return offline;
    }

    private TextComponent getHeader(int page) {
        TextComponent first_line = new TextComponent(MessageUtils.color("&8&l&m------------[&r "));
        TextComponent second_line = new TextComponent(MessageUtils.color(" &8&l&m]------------"));

        TextComponent previous_page = new TextComponent(MessageUtils.color("&7\u00AB"));
        HoverEvent previous_page_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&7Previous page")).create());
        ClickEvent previous_page_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f list " + (page - 1));
        previous_page.setHoverEvent(previous_page_hover);
        previous_page.setClickEvent(previous_page_click);

        TextComponent page_info = new TextComponent(MessageUtils.color(" &6Friend list page&8:&6 " + page + " "));

        TextComponent next_page = new TextComponent(MessageUtils.color("&7\u00BB"));
        HoverEvent next_page_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&7Next page")).create());
        ClickEvent next_page_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f list " + (page + 1));
        next_page.setHoverEvent(next_page_hover);
        next_page.setClickEvent(next_page_click);

        return new TextComponent(first_line, previous_page, page_info, next_page, second_line);
    }

    private TextComponent getOnlineFriendMessage(Friendship friendship) {
        final ProxiedPlayer player = this.plugin.getProxy().getPlayer(friendship.getUuid());

        TextComponent remove = new TextComponent(MessageUtils.color("&c&l\u2717"));
        HoverEvent remove_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&cClick to unfriend " + player.getDisplayName())).create());
        ClickEvent remove_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f remove " + player.getName());
        remove.setHoverEvent(remove_hover);
        remove.setClickEvent(remove_click);

        TextComponent message = new TextComponent(MessageUtils.color(" &b\u270E "));
        HoverEvent message_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&bClick to message " + player.getDisplayName())).create());
        ClickEvent message_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/m " + player.getName());
        message.setHoverEvent(message_hover);
        message.setClickEvent(message_click);

        TextComponent join = new TextComponent(MessageUtils.color("&a\u27A4"));
        HoverEvent join_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&aClick to connect to " + player.getServer().getInfo().getName())).create());
        ClickEvent join_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f join " + player.getName());
        join.setHoverEvent(join_hover);
        join.setClickEvent(join_click);

        TextComponent username = new TextComponent(MessageUtils.color(" &a" + player.getDisplayName()));
        HoverEvent friend_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&7Friends since&8:&a " + this.getDate(friendship.getAdded()) + "\n&7Status&8:&a Online at " + player.getServer().getInfo().getName())).create());
        username.setHoverEvent(friend_hover);

        return new TextComponent(OPEN_BRACKET, remove, message, join, CLOSED_BRACKET, username);
    }

    private TextComponent getOfflineFriendMessage(Friendship friendship) {
        TextComponent remove = new TextComponent(MessageUtils.color("&c&l\u2717"));
        HoverEvent remove_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&cClick to unfriend " + friendship.getUsername())).create());
        ClickEvent remove_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f remove " + friendship.getUsername());
        remove.setHoverEvent(remove_hover);
        remove.setClickEvent(remove_click);

        TextComponent username = new TextComponent(MessageUtils.color(" &c" + friendship.getUsername()));
        HoverEvent friend_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&7Friends since&8:&a " + this.getDate(friendship.getAdded()) + "\n&7Status&8:&c Offline")).create());
        username.setHoverEvent(friend_hover);

        return new TextComponent(OPEN_BRACKET, remove, CLOSED_BRACKET, username);
    }

    private String getDate(long time) {
        return DATE_FORMAT.format(new Date(time));
    }
}
