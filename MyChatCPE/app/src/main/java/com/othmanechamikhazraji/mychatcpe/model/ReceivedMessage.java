package com.othmanechamikhazraji.mychatcpe.model;

/**
 * Created by othmanechamikhazraji on 12/10/15.
 */
public class ReceivedMessage {
    private String uuid = "";
    private String login = "";
    private String message = "";
    private String images = "";

    public ReceivedMessage(String uuid, String login, String message, String images) {
        this.uuid = uuid;
        this.login = login;
        this.message = message;
        this.images = images;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLogin() {
        return login;
    }

    public String getMessage() {
        return message;
    }

    public String getImages() {
        return images;
    }
}
