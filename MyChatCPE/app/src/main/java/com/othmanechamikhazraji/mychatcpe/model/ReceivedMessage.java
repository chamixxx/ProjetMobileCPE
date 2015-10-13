package com.othmanechamikhazraji.mychatcpe.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by othmanechamikhazraji on 12/10/15.
 */
public class ReceivedMessage {
    private String uuid = "";
    private String login = "";
    private String message = "";
    private List<String> images;


    public ReceivedMessage(String uuid, String login, String message, List<String> imagesURL) {
        this.uuid = uuid;
        this.login = login;
        this.message = message;
        this.images = imagesURL;
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

    public List<String> getImages() {
        return images;
    }
}
