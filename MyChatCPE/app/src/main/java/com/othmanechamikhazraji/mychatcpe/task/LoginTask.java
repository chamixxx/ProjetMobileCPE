package com.othmanechamikhazraji.mychatcpe.task;

/**
 * Created by othmanechamikhazraji on 14/10/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.ui.activities.MainActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.widget.Toast.LENGTH_LONG;

/**
 * LoginTask: AsyncTask to process authentication.
 * FYI: AsyncTask takes three generic types: Params, Progress, Result
 */
public class LoginTask extends AsyncTask<String, Void, Boolean> {

    public interface LoginTaskFinishedListener {
        void onPostExecute();
    }

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_BASE_URL = "http://training.loicortola.com/chat-rest/2.0";
    private static final String EXTRA_LOGIN = "ext_login";
    private static final String EXTRA_PASSWORD = "ext_password";
    private ProgressBar progressBar;
    private Context context;
    private LoginTaskFinishedListener loginTaskFinishedListener;


    public LoginTask (ProgressBar progressBar, Context context) {
        this.progressBar = progressBar;
        this.context = context;
        this.loginTaskFinishedListener = (LoginTaskFinishedListener) context;
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
                // Saving login password in sharedPreferences to avoid using extra each time we change
                // activities
                SharedPreferences sharedPreferences =context.getSharedPreferences
                        ("authentication", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(EXTRA_LOGIN,username);
                editor.putString(EXTRA_PASSWORD,password);
                editor.apply();
                return true;
            }
        } catch (IOException e) {
            Log.w(TAG, "Exception occured while logging in: " + e.getMessage());
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        // Here, hide progress bar and proceed to login if OK.
        progressBar.setVisibility(View.GONE);

        // Wrong login entered
        if (!success) {
            Toast.makeText(context, R.string.login_error, LENGTH_LONG).show();
            return;
        }
        loginTaskFinishedListener.onPostExecute();
    }
}
