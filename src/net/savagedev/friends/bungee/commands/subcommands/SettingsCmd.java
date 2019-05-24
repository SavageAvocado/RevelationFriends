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

@SuppressWarnings("Duplicates")
public class SettingsCmd extends SubCommand {
    private TextComponent closed_bracket;
    private TextComponent open_bracket;

    public SettingsCmd(RevelationFriends plugin) {
        super(plugin);
        this.init();
    }

    private void init() {
        this.closed_bracket = new TextComponent(MessageUtils.color("&8]"));
        this.open_bracket = new TextComponent(MessageUtils.color("&8["));
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer user = (ProxiedPlayer) sender;

        if (args.length == 1) {
            MessageUtils.message(user, "&8&l&m------------------------------------------");
            MessageUtils.message(user, this.getSettingMessage(user, User.Setting.REQUESTS, "Friend requests."));
            MessageUtils.message(user, this.getSettingMessage(user, User.Setting.JOIN_MESSAGES, "Join messages."));
            MessageUtils.message(user, this.getSettingMessage(user, User.Setting.LEAVE_MESSAGES, "Leave messages"));
            MessageUtils.message(user, this.getSettingMessage(user, User.Setting.SERVER_SWITCH_MESSAGES, "Switch server messages."));
            MessageUtils.message(user, "&8&l&m------------------------------------------");
            return;
        }

        User.Setting setting = User.Setting.valueOf(args[1].toUpperCase());

        if (args.length == 2) {
            MessageUtils.message(user, "&cPlease specify a value. (true/false)");
            return;
        }

        boolean value = Boolean.valueOf(args[2]);

        this.editSetting(user, setting, value);
        MessageUtils.message(user, "&aSetting updated.");
    }

    private TextComponent getSettingMessage(ProxiedPlayer user, User.Setting setting, String description) {
        boolean value = this.plugin().getUserManager().get(user.getUniqueId()).getSetting(setting);

        TextComponent disable = new TextComponent(MessageUtils.color("&c✘ "));
        HoverEvent disable_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&cClick to disable this setting.")).create());
        ClickEvent disable_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f settings " + setting + " false");
        disable.setHoverEvent(disable_hover);
        disable.setClickEvent(disable_click);

        TextComponent enable = new TextComponent(MessageUtils.color("&a✔"));
        HoverEvent enable_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&aClick to enable this setting.")).create());
        ClickEvent enable_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f settings " + setting + " true");
        enable.setHoverEvent(enable_hover);
        enable.setClickEvent(enable_click);

        TextComponent message = new TextComponent(MessageUtils.color((value ? " &a" : " &c") + description));

        return new TextComponent(this.open_bracket, disable, enable, this.closed_bracket, message);
    }

    private void editSetting(ProxiedPlayer user, User.Setting setting, boolean value) {
        this.plugin().getUserManager().get(user.getUniqueId()).editSetting(setting, value);
        this.plugin().getUserManager().save(user.getUniqueId());

        MessageUtils.message(user, "&8&l&m------------------------------------------");
        MessageUtils.message(user, this.getSettingMessage(user, User.Setting.REQUESTS, "Friend requests."));
        MessageUtils.message(user, this.getSettingMessage(user, User.Setting.JOIN_MESSAGES, "Join messages."));
        MessageUtils.message(user, this.getSettingMessage(user, User.Setting.LEAVE_MESSAGES, "Leave messages"));
        MessageUtils.message(user, this.getSettingMessage(user, User.Setting.SERVER_SWITCH_MESSAGES, "Switch server messages."));
        MessageUtils.message(user, "&8&l&m------------------------------------------");
    }
}
