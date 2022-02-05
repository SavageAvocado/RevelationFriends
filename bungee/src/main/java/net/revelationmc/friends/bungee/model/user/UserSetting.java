package net.revelationmc.friends.bungee.model.user;

public enum UserSetting {
    SERVER_CHANGE_MESSAGES("Receive messages when friends change servers.", false),
    QUIT_MESSAGES("Receive messages when friends leave the server.", true),
    JOIN_MESSAGES("Receive messages when friends join the server.", true),
    ALLOW_FRIEND_REQUESTS("Allow players to send you friend requests.", true);

    private final String description;
    private final boolean defaultValue;

    UserSetting(String description, boolean defaultValue) {
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getDefault() {
        return this.defaultValue;
    }
}
