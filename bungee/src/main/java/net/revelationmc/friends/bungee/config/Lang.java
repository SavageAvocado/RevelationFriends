package net.revelationmc.friends.bungee.config;

import com.google.gson.JsonParseException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public enum Lang {
    // Command stuff.
    INVALID_ARGUMENTS("invalid_arguments"),
    NO_PERMISSION("no_permission"),
    UNKNOWN_PLAYER("unknown_player"),
    RELOAD_SUCCESS("reload_success"),
    RELOAD_FAIL("reload_fail"),

    COMMAND_LIST_HEADER("command_list_header"),
    COMMAND_LIST_ITEM("command_list_item"),
    COMMAND_LIST_FOOTER("command_list_footer"),
    HELP("help"),

    // Request messages.
    REQUEST_NOTIFICATION("request_notification"),
    REQUEST_LIST_HEADER("request_list_header"),
    REQUEST_LIST_EMPTY("request_list_empty"),
    REQUEST_LIST_ITEM("request_list_item"),
    REQUEST_LIST_FOOTER("request_list_footer"),
    REQUESTS_NOT_ALLOWED("requests_not_allowed"),
    REQUEST_ALREADY_SENT("request_already_sent"),
    REQUEST_NOT_FOUND("request_not_found"),
    REQUEST_RECEIVER("request_receiver"),
    REQUEST_SENDER("request_sender"),
    REQUEST_ACCEPT_RECEIVER("request_accept_receiver"),
    REQUEST_ACCEPT_SENDER("request_accept_sender"),
    REQUEST_DENY_RECEIVER("request_deny_receiver"),
    REQUEST_DENY_SENDER("request_deny_sender"),

    // Friend management messages.
    FRIEND_LIST_INVALID_PAGE("friend_list_invalid_page"),
    FRIEND_LIST_HEADER("friend_list_header"),
    FRIEND_LIST_EMPTY("friend_list_empty"),
    FRIEND_LIST_ITEM_ONLINE("friend_list_item_online"),
    FRIEND_LIST_ITEM_OFFLINE("friend_list_item_offline"),
    FRIEND_LIST_FOOTER("friend_list_footer"),
    FRIEND_ALREADY_EXISTS("friend_already_exists"),
    FRIEND_NOT_FOUND("friend_not_found"),
    FRIEND_SELF("friend_self"),
    FRIEND_LIST_FULL_SELF("friend_list_full_self"),
    FRIEND_LIST_FULL_OTHER("friend_list_full_other"),
    FRIEND_REMOVE_REMOVER("friend_remove_remover"),
    FRIEND_REMOVE_ALL_REMOVER("friend_remove_all_remover"),
    FRIEND_REMOVE_REMOVED("friend_remove_removed"),

    // Settings messages.
    SETTING_LIST_HEADER("setting_list_header"),
    SETTING_LIST_ITEM_ENABLED("setting_list_item_enabled"),
    SETTING_LIST_ITEM_DISABLED("setting_list_item_disabled"),
    SETTING_LIST_FOOTER("setting_list_footer"),
    SETTING_NOT_BOOLEAN("setting_not_boolean"),
    SETTING_UNKNOWN("setting_unknown");

    private static final ConfigurationProvider YAML_PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private static Configuration s_Configuration;

    public static void loadOrCreate(Path path, String fileName) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectories(path.getFileName());
            try (final InputStream inputStream = Lang.class.getClassLoader().getResourceAsStream(fileName)) {
                if (inputStream == null) {
                    throw new RuntimeException("Failed to create default lang file! InputStream null.");
                }
                Files.copy(inputStream, path);
            }
        }
        try (final BufferedReader reader = Files.newBufferedReader(path)) {
            s_Configuration = YAML_PROVIDER.load(reader);
        }
    }

    private final String key;

    Lang(String key) {
        this.key = key;
    }

    public void send(CommandSender sender, Placeholder... placeholders) {
        String message = s_Configuration.getString(this.key);
        if (message == null || message.isBlank()) {
            return; // Nothing to send. The message is null, or empty.
        }

        for (Placeholder placeholder : placeholders) { // Replace all placeholders before parsing the message.
            message = message.replace(placeholder.key, placeholder.value);
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        BaseComponent[] components;
        try {
            components = ComponentSerializer.parse(message);
        } catch (JsonParseException ignored) { // Message is either not a json message, or the formatting is incorrect. Just send it as text.
            components = new ComponentBuilder(message).create();
        }
        sender.sendMessage(components);
    }

    public record Placeholder(String key, String value) {
    }
}
