package com.othmanechamikhazraji.mychatcpe.model;

/**
 * Created by othmanechamikhazraji on 14/10/15.
 */
public class Attachment {
    private String mimeType = "";
    private String data = "";

    public Attachment(String mimeType, String data) {
        this.mimeType = mimeType;
        this.data = data;
    }
}
