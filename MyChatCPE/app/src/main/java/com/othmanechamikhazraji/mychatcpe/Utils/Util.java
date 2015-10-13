package com.othmanechamikhazraji.mychatcpe.Utils;

import org.json.JSONArray;
import org.json.JSONException;

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
}
