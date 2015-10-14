package com.othmanechamikhazraji.mychatcpe.task;

/**
 * Created by othmanechamikhazraji on 14/10/15.
 */

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.Utils.Util;
import com.othmanechamikhazraji.mychatcpe.ui.Activities.MessageListActivity;
import com.othmanechamikhazraji.mychatcpe.ui.adapter.MyAdapter;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.widget.Toast.LENGTH_LONG;

/**
 * pullMessageTask: AsyncTask to pull messages from the server authentication.
 */
public class PullMessageTask extends AsyncTask<String, Void, Boolean> {

    public interface PullMessageFinishedListener {
        void onPostExecute(Boolean success, JSONArray allMessageJSON);
    }

    private ProgressBar progressBar;
    private static final String TAG = MessageListActivity.class.getSimpleName();
    private static final String API_BASE_URL = "http://training.loicortola.com/chat-rest/2.0";
    private String allMessagesString;
    private JSONArray allMessageJSON = null;
    private PullMessageFinishedListener pullMessageFinishedListener;


    public PullMessageTask(ProgressBar progressBar, PullMessageFinishedListener pullMessageFinishedListener) {
        this.progressBar = progressBar;
        this.pullMessageFinishedListener = pullMessageFinishedListener;
    }

    @Override
    protected void onPreExecute() {
        // Here, show progress bar
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * @param params [login, password]
     * @return true if login succeeded, false otherwise
     */
    @Override
    protected Boolean doInBackground(String... params) {
        String username = params[0];
        String password = params[1];

        String credentials = username + ":" + password;
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        int responseCode = 0;

        StringBuilder result = null;
        String resultString = null;

        // Webservice URL
        String urlString = API_BASE_URL + "/messages?&limit=100&offset=0";
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
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                responseCode = urlConnection.getResponseCode();

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                Log.w(TAG, "Exception occurred while getting messages in: " + e.getMessage());
            } finally {
                resultString = result != null ? result.toString() : null;
                urlConnection.disconnect();
            }
        }
        if (responseCode == HttpURLConnection.HTTP_OK) {
            allMessagesString = resultString;
            allMessageJSON = Util.stringToJsonArray(allMessagesString);
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        // Here, hide progress bar and proceed to login if OK.
        progressBar.setVisibility(View.GONE);
        pullMessageFinishedListener.onPostExecute(success, allMessageJSON);
    }
}
