package net.revelationmc.friends.bungee.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public final class MessageUtils {
    private MessageUtils() {
        throw new UnsupportedOperationException();
    }

    public static void message(CommandSender user, String message) {
        message(user, new TextComponent(color(message)));
    }

    public static void message(CommandSender user, TextComponent textComponent) {
        user.sendMessage(textComponent);
    }

    public static void message(CommandSender user, List<String> messages) {
        for (String message : messages) {
            message(user, message);
        }
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
