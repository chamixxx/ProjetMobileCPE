package com.othmanechamikhazraji.mychatcpe.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.Utils.CustomArrayAdapter;
import com.othmanechamikhazraji.mychatcpe.Utils.Util;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class MessageListActivity extends AppCompatActivity {

    private static final String TAG = MessageListActivity.class.getSimpleName();
    private static final String API_BASE_URL = "http://training.loicortola.com/chat-rest/2.0";
    public static final String EXTRA_LOGIN = "ext_login";
    public static final String EXTRA_PASSWORD = "ext_password";

    private ProgressBar progressBar;
    private String allMessagesString = "";
    private JSONObject allMessageJSON = null;

    private PullMessageTask pullMessageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_activity);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_message);

        final Intent intentMessageListActivity = getIntent();


        String usernameStr = intentMessageListActivity.getStringExtra(EXTRA_LOGIN);
        String passwordStr = intentMessageListActivity.getStringExtra(EXTRA_PASSWORD);


        // Cancel previous task if it is still running
        if (pullMessageTask != null && pullMessageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            pullMessageTask.cancel(true);
        }
        // Launch pullMessageTask Task
        pullMessageTask = new PullMessageTask();
        pullMessageTask.execute(usernameStr, passwordStr);

    }
    @Override
    protected void onPause() {
        if (pullMessageTask != null) {
            pullMessageTask.cancel(true);
        }
        super.onPause();
    }

    /**
     * pullMessageTask: AsyncTask to pull messages from the server authentication.
     */
    private class PullMessageTask extends AsyncTask<String, Void, Boolean> {

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

            // Webservice URL
            String urlString = API_BASE_URL + "/messages?&limit=10&offset=0";
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder result = null;
            String resultString = null;
            if (urlConnection != null) {
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
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
                allMessageJSON = Util.stringToJson(allMessagesString);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Here, hide progress bar and proceed to login if OK.
            progressBar.setVisibility(View.GONE);

            // No response from the server entered
            if (!success) {
                return;
            }

            // Everything good!
            Toast.makeText(MessageListActivity.this, R.string.messages_success, LENGTH_LONG).show();
            String[] splitMassageArray = Util.splitMessages(allMessagesString);
            List<String> messageList = Util.populateListMessages(splitMassageArray);

            CustomArrayAdapter messageAdapter = new CustomArrayAdapter(MessageListActivity.this, messageList);
            ListView listViewMessage = (ListView) findViewById(R.id.listView);
            listViewMessage.setAdapter(messageAdapter);
        }
    }
}
