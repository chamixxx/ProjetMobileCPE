package com.othmanechamikhazraji.mychatcpe.task;

/**
 * Created by othmanechamikhazraji on 14/10/15.
 */

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.dd.processbutton.iml.ActionProcessButton;
import com.othmanechamikhazraji.mychatcpe.ui.Activities.MainActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * LoginTask: AsyncTask to process authentication.
 * FYI: AsyncTask takes three generic types: Params, Progress, Result
 */
public class LoginTask extends AsyncTask<String, Void, Boolean> {

    public interface LoginTaskFinishedListener {
        void onPostExecute(Boolean success);
    }

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_BASE_URL = "http://training.loicortola.com/chat-rest/2.0";
    private ActionProcessButton btn;
    private LoginTaskFinishedListener loginTaskFinishedListener;

    public LoginTask (ActionProcessButton btn, LoginTaskFinishedListener loginTaskFinishedListener) {
        this.btn = btn;
        this.loginTaskFinishedListener = loginTaskFinishedListener ;
    }

    @Override
    protected void onPreExecute() {
        btn.setProgress(1);
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

        // Webservice URL
        String urlString = API_BASE_URL + "/connect";
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
            e.printStackTrace();
        }
        if (urlConnection != null) {
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            try {
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
                urlConnection.setDoInput(true);
                urlConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if ((urlConnection != null ? urlConnection.getResponseCode() : 0) == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (IOException e) {
            Log.w(TAG, "Exception occured while logging in: " + e.getMessage());
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        loginTaskFinishedListener.onPostExecute(success);
    }
}
