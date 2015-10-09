package com.othmanechamikhazraji.mychatcpe.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.Utils.CustomArrayAdapter;
import com.othmanechamikhazraji.mychatcpe.Utils.Util;

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
    private static final String API_BASE_URL = "http://formation-android-esaip.herokuapp.com";

    private ProgressBar progressBar;
    private String allMessages = "";

    private PullMessageTask pullMessageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_activity);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_message);

        String usernameStr = "byche";
        String passwordStr = "byche";

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
            int statusCode = 0;
            // Webservice URL
            String urlString = API_BASE_URL + "/messages/" + username + "/" + password;
            URL url = null;

            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;


            try {
                urlConnection = (HttpURLConnection) (url != null ? url.openConnection() : null);
                statusCode = urlConnection != null ? urlConnection.getResponseCode() : 0;
            } catch (IOException e) {
                Log.w(TAG, "Exception occurred while getting messages in: " + e.getMessage());
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

            if (statusCode == HttpURLConnection.HTTP_OK) {
                allMessages = resultString;
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
            Toast.makeText(MessageListActivity.this, R.string.login_success, LENGTH_LONG).show();
            String[] splitMassageArray = Util.splitMessages(allMessages);
            List<String> messageList = Util.populateListMessages(splitMassageArray);

            CustomArrayAdapter messageAdapter = new CustomArrayAdapter(MessageListActivity.this, messageList);
            ListView listViewMessage = (ListView) findViewById(R.id.listView);
            listViewMessage.setAdapter(messageAdapter);
        }
    }
}
