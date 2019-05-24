package net.savagedev.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.User;
import net.savagedev.friends.bungee.user.friend.Friend;
import net.savagedev.friends.bungee.utils.MessageUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListCmd extends SubCommand {
    private TextComponent closed_bracket;
    private SimpleDateFormat dateFormat;
    private TextComponent open_bracket;
    private int pageSize;

    public ListCmd(RevelationFriends plugin) {
        super(plugin);
        this.init();
    }

    private void init() {
        this.pageSize = this.plugin().getConfig(RevelationFriends.ConfigType.MAIN).getInt("settings.page-size");
        this.closed_bracket = new TextComponent(MessageUtils.color("&8]"));
        this.open_bracket = new TextComponent(MessageUtils.color("&8["));
        this.dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mma");
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        String pageString = args.length == 1 ? "1" : args[1];
        ProxiedPlayer user = (ProxiedPlayer) sender;

        User fuser = this.plugin().getUserManager().get(user.getUniqueId());
        int page = Integer.valueOf(pageString);

        if (fuser.getFriends().size() <= 0) {
            MessageUtils.message(user, "&8&l&m------------------------------------------");
            MessageUtils.message(user, "&cYou don't have any friends yet. :(");
            MessageUtils.message(user, "&8&l&m------------------------------------------");
            return;
        }

        long friendListPages = Math.round(((float) fuser.getFriends().size() / this.pageSize) + .375);

        if (!this.isInteger(pageString) || Integer.valueOf(pageString) <= 0 || Integer.valueOf(pageString) > friendListPages) {
            MessageUtils.message(user, "&cInvalid page number!");
            return;
        }

        MessageUtils.message(user, this.getHeader(page));

        List<Friend> friends = this.getSortedFriendsList(fuser.getFriends());

        for (int i = ((page - 1) * this.pageSize) > friends.size() ? 0 : (page - 1) * this.pageSize; i < ((this.pageSize * page) > friends.size() ? friends.size() : this.pageSize * page); i++) {
            Friend friend = friends.get(i);

            if (friend.isOnline()) {
                MessageUtils.message(user, this.getOnlineFriendMessage(friend));
                continue;
            }

            MessageUtils.message(user, this.getOfflineFriendMessage(friend));
        }

        MessageUtils.message(user, "&8&l&m------------------------------------------");
    }

    private List<Friend> getSortedFriendsList(List<Friend> friends) {
        List<Friend> sorted = new ArrayList<>();

        sorted.addAll(this.getOnlineFriends(friends));
        sorted.addAll(this.getOfflineFriends(friends));

        return sorted;
    }

    private List<Friend> getOnlineFriends(List<Friend> friends) {
        List<Friend> online = new ArrayList<>();

        for (Friend friend : friends) {

            if (friend.isOnline()) {
                online.add(friend);
            }
        }

        return online;
    }

    private List<Friend> getOfflineFriends(List<Friend> friends) {
        List<Friend> offline = new ArrayList<>();

        for (Friend friend : friends) {
            if (!friend.isOnline()) {
                offline.add(friend);
            }
        }

        return offline;
    }

    private TextComponent getHeader(int page) {
        TextComponent first_line = new TextComponent(MessageUtils.color("&8&l&m------------[&r "));
        TextComponent second_line = new TextComponent(MessageUtils.color(" &8&l&m]------------"));

        TextComponent previous_page = new TextComponent(MessageUtils.color("&7«"));
        HoverEvent previous_page_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&7Previous page")).create());
        ClickEvent previous_page_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f list " + (page - 1));
        previous_page.setHoverEvent(previous_page_hover);
        previous_page.setClickEvent(previous_page_click);

        TextComponent page_info = new TextComponent(MessageUtils.color(" &6Friend list page&8:&6 " + page + " "));

        TextComponent next_page = new TextComponent(MessageUtils.color("&7»"));
        HoverEvent next_page_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&7Next page")).create());
        ClickEvent next_page_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f list " + (page + 1));
        next_page.setHoverEvent(next_page_hover);
        next_page.setClickEvent(next_page_click);

        return new TextComponent(first_line, previous_page, page_info, next_page, second_line);
    }

    private TextComponent getFooter() {
        TextComponent first_line = new TextComponent(MessageUtils.color("&8&l&m------------[&r "));
        TextComponent second_line = new TextComponent(MessageUtils.color(" &8&l&m]------------"));

        return new TextComponent(first_line, second_line);
    }

    private TextComponent getOnlineFriendMessage(Friend friend) {
        TextComponent remove = new TextComponent(MessageUtils.color("&c✘"));
        HoverEvent remove_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&cClick to unfriend " + friend.getPlayer().getDisplayName())).create());
        ClickEvent remove_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f remove " + friend.getPlayer().getName());
        remove.setHoverEvent(remove_hover);
        remove.setClickEvent(remove_click);

        TextComponent message = new TextComponent(MessageUtils.color(" &b✎ "));
        HoverEvent message_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&bClick to message " + friend.getPlayer().getDisplayName())).create());
        ClickEvent message_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/m " + friend.getPlayer().getName());
        message.setHoverEvent(message_hover);
        message.setClickEvent(message_click);

        TextComponent join = new TextComponent(MessageUtils.color("&a➤"));
        HoverEvent join_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&aClick to connect to " + friend.getPlayer().getServer().getInfo().getName())).create());
        ClickEvent join_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f join " + friend.getPlayer().getName());
        join.setHoverEvent(join_hover);
        join.setClickEvent(join_click);

        TextComponent username = new TextComponent(MessageUtils.color(" &a" + friend.getPlayer().getDisplayName()));
        HoverEvent friend_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&7Friends since&8:&a " + this.getDate(friend.getTime()) + "\n&7Status&8:&a Online at " + friend.getPlayer().getServer().getInfo().getName())).create());
        username.setHoverEvent(friend_hover);

        return new TextComponent(this.open_bracket, remove, message, join, this.closed_bracket, username);
    }

    private TextComponent getOfflineFriendMessage(Friend friend) {
        TextComponent remove = new TextComponent(MessageUtils.color("&c✘"));
        HoverEvent remove_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&cClick to unfriend " + friend.getUsername())).create());
        ClickEvent remove_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f remove " + friend.getUsername());
        remove.setHoverEvent(remove_hover);
        remove.setClickEvent(remove_click);

        TextComponent username = new TextComponent(MessageUtils.color(" &c" + friend.getUsername()));
        HoverEvent friend_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&7Friends since&8:&a " + this.getDate(friend.getTime()) + "\n&7Status&8:&c Offline")).create());
        username.setHoverEvent(friend_hover);

        return new TextComponent(this.open_bracket, remove, this.closed_bracket, username);
    }

    private String getDate(long time) {
        return this.dateFormat.format(new Date(time));
    }

    private boolean isInteger(String potentialInteger) {
        try {
            Integer.parseInt(potentialInteger);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
