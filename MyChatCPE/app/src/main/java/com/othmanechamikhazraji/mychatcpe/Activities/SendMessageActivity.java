package com.othmanechamikhazraji.mychatcpe.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.othmanechamikhazraji.mychatcpe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class SendMessageActivity extends AppCompatActivity {

    public static final String EXTRA_LOGIN = "ext_login";
    public static final String EXTRA_PASSWORD = "ext_password";
    public static final String API_BASE_URL = "http://training.loicortola.com/chat-rest/2.0";
    private static final String TAG = MessageListActivity.class.getSimpleName();
    private Button sendMessageBtn;
    private EditText messageEditText;
    private SendMessageTask sendMessageTask;
    private ProgressBar progressBar;
    private String messageToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        final Intent intentSendMessageActivity = getIntent();
        sendMessageBtn = (Button) findViewById(R.id.sendMsgBtn);
        messageEditText = (EditText) findViewById(R.id.messageText);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_message);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = intentSendMessageActivity.getStringExtra(EXTRA_LOGIN);
                String passwordStr = intentSendMessageActivity.getStringExtra(EXTRA_PASSWORD);
                messageToSend = messageEditText.getText().toString();

                // Cancel previous task if it is still running
                if (sendMessageTask != null && sendMessageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    sendMessageTask.cancel(true);
                }
                // Launch sendMessageTask Task
                sendMessageTask = new SendMessageTask();
                sendMessageTask.execute(usernameStr, passwordStr);

            }
        });

    }

    @Override
    protected void onPause() {
        if (sendMessageTask != null) {
            sendMessageTask.cancel(true);
        }
        super.onPause();
    }

    private class SendMessageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            // Here, show progress bar
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            //Parameters
            String username = params[0];
            String password = params[1];

            int responseCode = 0;
            String uuidStr = getUUID();
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

                    //Create JSONObject here
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("uuid", uuidStr);
                    jsonParam.put("login", username);
                    jsonParam.put("message", messageToSend);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                    outputStreamWriter.write(jsonParam.toString());
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                    responseCode = urlConnection.getResponseCode();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    urlConnection.disconnect();
                }
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Here, hide progress bar and proceed to login if OK.
            progressBar.setVisibility(View.GONE);

            if (!success) {
                return;
            }
        }
    }


    private String getUUID() {
        UUID idOne = UUID.randomUUID();
        return idOne.toString();
    }



}
