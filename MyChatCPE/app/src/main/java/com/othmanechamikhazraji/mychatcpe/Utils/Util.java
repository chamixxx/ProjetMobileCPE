package com.othmanechamikhazraji.mychatcpe.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.othmanechamikhazraji.mychatcpe.model.ReceivedMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by othmanechamikhazraji on 09/10/15.
 */
public class Util {
    public static JSONArray stringToJsonArray (String stringToConvert) {
        try {
            return new JSONArray(stringToConvert);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ReceivedMessage> makeMessageList (JSONArray allMessageJSON) {
        List<ReceivedMessage> receivedMessageList = new ArrayList<>();
        Gson gson = new Gson();

        for (int i=0; i<allMessageJSON.length(); i++) {
            try {
                JSONObject currentMessageJson = allMessageJSON.getJSONObject(i);
                ReceivedMessage receivedMessage = gson.fromJson(currentMessageJson.toString(), ReceivedMessage.class);
                receivedMessageList.add(receivedMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return receivedMessageList;
    }
}
