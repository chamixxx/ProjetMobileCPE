package com.othmanechamikhazraji.mychatcpe.model;

/**
 * Created by vincentbeauvieux on 14/10/2015.
 */
public class ImageDrawable {
    private String mimeType;
    private int ressourceId;

    public ImageDrawable(String mimeType, int ressourceId) {
        this.mimeType = mimeType;
        this.ressourceId = ressourceId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getRessourceId() {
        return ressourceId;
    }
}
