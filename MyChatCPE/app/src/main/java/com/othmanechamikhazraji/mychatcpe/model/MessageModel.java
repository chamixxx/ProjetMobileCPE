package com.othmanechamikhazraji.mychatcpe.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by othmanechamikhazraji on 12/10/15.
 */
public class MessageModel {
    private String uuid = "";
    private String login = "";
    private String message = "";
    private List<String> images;
    private List<Attachment> attachments;


    public MessageModel(String uuid, String login, String message, List<String> imagesURL,
                        List<Attachment> attachments) {
        this.uuid = uuid;
        this.login = login;
        this.message = message;
        this.images = imagesURL;
        this.attachments = attachments;
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
