package com.othmanechamikhazraji.mychatcpe.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.Utils.DividerItemDecoration;
import com.othmanechamikhazraji.mychatcpe.Utils.MyAdapter;
import com.othmanechamikhazraji.mychatcpe.Utils.Util;
import com.othmanechamikhazraji.mychatcpe.model.ReceivedMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class MessageListActivity extends AppCompatActivity {

    private static final String TAG = MessageListActivity.class.getSimpleName();
    private static final String API_BASE_URL = "http://training.loicortola.com/chat-rest/2.0";
    public static final String EXTRA_LOGIN = "ext_login";
    public static final String EXTRA_PASSWORD = "ext_password";

    private RecyclerView messageRecyclerView;
    private RecyclerView.Adapter messageAdapter;
    private RecyclerView.LayoutManager messageLayoutManager;

    private ProgressBar progressBar;
    private String allMessagesString = "";
    private JSONArray allMessageJSON = null;
    private List<ReceivedMessage> receivedMessageList;

    private PullMessageTask pullMessageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_activity);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_message);

        final Intent intentMessageListActivity = getIntent();

        String usernameStr = intentMessageListActivity.getStringExtra(EXTRA_LOGIN);
        String passwordStr = intentMessageListActivity.getStringExtra(EXTRA_PASSWORD);

        receivedMessageList = new ArrayList<>();

        messageRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        messageLayoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(messageLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST);
        messageRecyclerView.addItemDecoration(itemDecoration);

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

            StringBuilder result = null;
            String resultString = null;

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

            // No response from the server entered
            if (!success) {
                return;
            }

            // Everything good!
            Toast.makeText(MessageListActivity.this, R.string.messages_success, LENGTH_LONG).show();

            for (int i=0; i<allMessageJSON.length(); i++) {
                try {
                    JSONObject currentMessageJson = allMessageJSON.getJSONObject(i);
                    String currentUuid = currentMessageJson.getString("uuid");
                    String currentLogin = currentMessageJson.getString("login");
                    String currentMessage = currentMessageJson.getString("message");
                    ReceivedMessage receivedMessage = new ReceivedMessage(currentUuid, currentLogin, currentMessage, "");
                    receivedMessageList.add(receivedMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            messageAdapter = new MyAdapter(receivedMessageList);
            messageRecyclerView.setAdapter(messageAdapter);
        }
    }
}
