package com.othmanechamikhazraji.mychatcpe.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by othmanechamikhazraji on 09/10/15.
 */
public class Util {
    public static String[] splitMessages (String allMessages) {
        String[] splitMessageArray;
        splitMessageArray = allMessages.split(";");
        return splitMessageArray;
    }

    public static List<String> populateListMessages (String[] splitMessageArray) {
        List<String> messageList = new ArrayList<String>(Arrays.asList(splitMessageArray));
        return messageList;
    }
}
