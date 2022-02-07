package net.revelationmc.friends.bungee.commands.subcommands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;
import net.revelationmc.friends.bungee.model.user.User;
import net.revelationmc.friends.bungee.model.user.UserSetting;
import net.revelationmc.friends.bungee.utils.MessageUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SettingsCmd implements SubCommand {
    private static final TextComponent CLOSED_BRACKET = new TextComponent(MessageUtils.color("&8]"));
    private static final TextComponent OPEN_BRACKET = new TextComponent(MessageUtils.color("&8["));

    private final RevelationFriendsPlugin plugin;

    public SettingsCmd(RevelationFriendsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String label, String... args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) sender;
        final User user = this.plugin.getUserManager().getOrLoad(player.getUniqueId()).join();

        if (args.length == 1) {
            this.sendSettingsMessage(player, user);
            return;
        }

        try {
            final UserSetting setting = UserSetting.valueOf(args[1].toUpperCase());

            if (args.length == 2) {
                MessageUtils.message(player, "&cPlease specify a value. (true/false)");
                return;
            }

            user.getSettings().set(setting, Boolean.parseBoolean(args[2]));

            this.plugin.getStorage().saveUser(user)
                    .whenComplete((v, err) -> {
                        if (err != null) {
                            MessageUtils.message(player, "&cA fatal error occurred while saving your settings! Please contact an administrator.");
                            return;
                        }
                        MessageUtils.message(player, "&aSetting updated.");
                        this.sendSettingsMessage(player, user);
                    });
        } catch (IllegalArgumentException ignored) {
            MessageUtils.message(player, "Invalid setting! Try: (" + Arrays.stream(UserSetting.values()).map(Enum::name).collect(Collectors.joining(", ")) + ")");
        }

    }

    private void sendSettingsMessage(ProxiedPlayer player, User user) {
        MessageUtils.message(player, "&8&l&m------------------------------------------");
        for (UserSetting setting : UserSetting.values()) {
            MessageUtils.message(player, this.getSettingMessage(user, setting, setting.getDescription()));
        }
        MessageUtils.message(player, "&8&l&m------------------------------------------");
    }

    private TextComponent getSettingMessage(User user, UserSetting setting, String description) {
        final TextComponent disable = new TextComponent(MessageUtils.color("&c&l\u2717 "));
        final HoverEvent disable_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&cClick to disable this setting.")).create());
        final ClickEvent disable_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f settings " + setting + " false");
        disable.setHoverEvent(disable_hover);
        disable.setClickEvent(disable_click);

        final TextComponent enable = new TextComponent(MessageUtils.color("&a&l\u2713"));
        final HoverEvent enable_hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.color("&aClick to enable this setting.")).create());
        final ClickEvent enable_click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f settings " + setting + " true");
        enable.setHoverEvent(enable_hover);
        enable.setClickEvent(enable_click);

        final TextComponent message = new TextComponent(MessageUtils.color((user.getSettings().get(setting) ? " &a" : " &c") + description));

        return new TextComponent(OPEN_BRACKET, disable, enable, CLOSED_BRACKET, message);
    }
}
