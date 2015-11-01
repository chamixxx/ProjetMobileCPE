package com.othmanechamikhazraji.mychatcpe.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.dd.processbutton.iml.ActionProcessButton;
import com.othmanechamikhazraji.mychatcpe.Utils.Util;
import com.othmanechamikhazraji.mychatcpe.model.Attachment;
import com.othmanechamikhazraji.mychatcpe.ui.Activities.MessageListActivity;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by othmanechamikhazraji on 14/10/15.
 */

public class SendMessageTask extends AsyncTask<String, Void, Boolean> {

    public interface SendMessageTaskFinishedListener {
        void onPostExecute(Boolean success, JSONObject responseMessageJSON);
    }

    public static final String API_BASE_URL = "http://training.loicortola.com/chat-rest/2.0";
    private static final String TAG = MessageListActivity.class.getSimpleName();
    private String[] imageUrls;
    private String bodyToSend;
    private ActionProcessButton sendMessageBtn;
    private Context context;
    private JSONObject responseMessageJSON;
    private List<Attachment> imageToSend;
    private SendMessageTaskFinishedListener sendMessageTaskFinishedListener;

    public SendMessageTask(String[] imageUrls, String bodyToSend, List<Attachment> imageToSend, ActionProcessButton sendMessageBtn, Context context) {
        this.imageUrls = imageUrls;
        this.bodyToSend = bodyToSend;
        this.sendMessageBtn = sendMessageBtn;
        this.context = context;
        this.imageToSend = imageToSend;
        this.sendMessageTaskFinishedListener = (SendMessageTaskFinishedListener) context;
    }

    @Override
    protected void onPreExecute() {
        sendMessageBtn.setProgress(1);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        //Parameters
        String username = params[0];
        String password = params[1];
        StringBuilder result = null;
        String resultString = null;

        int responseCode = 0;
        String uuidStr = Util.getUUID();
        String credentials = username + ":" + password;
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        // Webservice URL
        String urlString = API_BASE_URL + "/messages";
        URL url = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) (url != null ? url.openConnection() : null);
        } catch (IOException e) {
            Log.w(TAG, "Exception occurred while getting messages in: " + e.getMessage());
        }

        if (urlConnection != null) {
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);

            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                String messageJson = Util.createJSONMessage(username, uuidStr, imageUrls,
                        bodyToSend, imageToSend, context, TAG);
                if (messageJson == null) {
                    return false;
                }
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                outputStreamWriter.write(messageJson);
                outputStreamWriter.flush();
                outputStreamWriter.close();
                responseCode = urlConnection.getResponseCode();

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                resultString = result != null ? result.toString() : null;
                urlConnection.disconnect();
            }
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            responseMessageJSON = Util.stringToJson(resultString);
            return true;
        }
        String responseMessageString = resultString;
        responseMessageJSON = Util.stringToJson(responseMessageString);
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        // Here, hide progress bar and proceed to login if OK.
        sendMessageTaskFinishedListener.onPostExecute(success, responseMessageJSON);
    }
}

