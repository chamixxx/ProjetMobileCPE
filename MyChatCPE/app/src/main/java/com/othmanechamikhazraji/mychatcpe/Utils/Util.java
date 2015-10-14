package com.othmanechamikhazraji.mychatcpe.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.othmanechamikhazraji.mychatcpe.model.Attachment;
import com.othmanechamikhazraji.mychatcpe.model.MessageModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by othmanechamikhazraji on 09/10/15.
 */
public class Util {
    public static JSONObject stringToJson (String stringToConvert) {
        try {
            return new JSONObject(stringToConvert);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray stringToJsonArray (String stringToConvert) {
        try {
            return new JSONArray(stringToConvert);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<MessageModel> makeMessageList (JSONArray allMessageJSON) {
        List<MessageModel> receivedMessageList = new ArrayList<>();
        Gson gson = new Gson();

        for (int i=0; i<allMessageJSON.length(); i++) {
            try {
                JSONObject currentMessageJson = allMessageJSON.getJSONObject(i);
                MessageModel receivedMessage = gson.fromJson(currentMessageJson.toString(), MessageModel.class);
                receivedMessageList.add(receivedMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return receivedMessageList;
    }

    public static String getUUID() {
        UUID idOne = UUID.randomUUID();
        return idOne.toString();
    }

    public static String getMessageContent(JSONObject response) {
        try {
            return response.get("message").toString();
        }
        catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String encodeImageBase64(Bitmap image, Bitmap.CompressFormat format) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(format, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap.CompressFormat getImageFormat(String urlStr) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if(urlStr.endsWith(".png")){
            format = Bitmap.CompressFormat.PNG;
        }
        return format;
    }

    public static String createJSONMessage(String usernameStr, String uuidStr,
                                     String[] imageUrls, String bodyToSend,
                                           Context context, String TAG) {
        List<Attachment> attachments = new ArrayList<>();
        for(int i =0; i<3; i++) {
            if (imageUrls[i].equals("")) {
                continue;
            }
            String mimeType;
            String data;

            Bitmap bitmapImage = getBitmapFromURL(imageUrls[i], context, TAG);
            if (bitmapImage == null) {
                continue;
            }
            Bitmap.CompressFormat format = Util.getImageFormat(imageUrls[i]);
            data = Util.encodeImageBase64(bitmapImage, format);
            if(format == Bitmap.CompressFormat.JPEG) {
                mimeType = "image/jpeg";
            }
            else {
                mimeType = "image/png";
            }
            Attachment currentAttachment = new Attachment(mimeType,data);
            attachments.add(currentAttachment);
        }
        MessageModel messageToSend = new MessageModel(uuidStr, usernameStr, bodyToSend, null, attachments);
        Gson gson = new Gson();
        String messageJsonToSend = gson.toJson(messageToSend);
        Log.d("JSON DEBUG", messageJsonToSend);
        return messageJsonToSend;
    }

    public static Bitmap getBitmapFromURL(String urlStr, Context context, String TAG) {
        Bitmap myBitmap;
        try {
            myBitmap = Picasso.with(context).load(urlStr).get();
            return myBitmap;
        } catch (IOException e) {
            Log.w(TAG, "Exception occurred while downloading IMG URL to bitmap in: " + e.getMessage());
            return null;
        }
    }
}
