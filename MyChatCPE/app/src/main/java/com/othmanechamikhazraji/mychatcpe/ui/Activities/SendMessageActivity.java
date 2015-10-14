package com.othmanechamikhazraji.mychatcpe.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.Utils.Util;

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

import static android.widget.Toast.LENGTH_LONG;

public class SendMessageActivity extends AppCompatActivity {

    public static final String EXTRA_LOGIN = "ext_login";
    public static final String EXTRA_PASSWORD = "ext_password";
    public static final String API_BASE_URL = "http://training.loicortola.com/chat-rest/2.0";
    private static final String TAG = MessageListActivity.class.getSimpleName();

    private String[] imageUrls = new String[3];
    private Button sendMessageBtn;
    private EditText messageEditText;
    private EditText image1UrlEditText;
    private EditText image2UrlEditText;
    private EditText image3UrlEditText;
    private SendMessageTask sendMessageTask;
    private ProgressBar progressBar;
    private String bodyToSend;
    private String ResponseMessageString = "";
    private JSONObject ResponseMessageJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        sendMessageBtn = (Button) findViewById(R.id.sendMsgBtn);
        messageEditText = (EditText) findViewById(R.id.messageText);
        image1UrlEditText = (EditText) findViewById(R.id.imageURL1);
        image2UrlEditText = (EditText) findViewById(R.id.imageURL2);
        image3UrlEditText = (EditText) findViewById(R.id.imageURL3);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_message);

        SharedPreferences sharedPreferences = this.getSharedPreferences
                ("authentication", Context.MODE_PRIVATE);
        final String usernameStr = sharedPreferences.getString(EXTRA_LOGIN, "");
        final String passwordStr = sharedPreferences.getString(EXTRA_PASSWORD, "");

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bodyToSend = messageEditText.getText().toString();
                imageUrls[0] = "https://pbs.twimg.com/profile_images/631535425333518336/D-i_GqpT.jpg";
                imageUrls[1] = "https://lunaextrema.files.wordpress.com/2011/11/gnfn.png";
                imageUrls[2] = "https://upload.wikimedia.org/wikipedia/en/8/89/Brood_War_box_art_(StarCraft).jpg";

                // Cancel previous task if it is still running
                if (sendMessageTask != null && sendMessageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    sendMessageTask.cancel(true);
                }
                // Launch sendMessageTask
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
                            bodyToSend, SendMessageActivity.this, TAG);
                    if(messageJson == null) {
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
                }
                finally {
                    resultString = result != null ? result.toString() : null;
                    urlConnection.disconnect();
                }
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                ResponseMessageString = resultString;
                ResponseMessageJSON = Util.stringToJson(ResponseMessageString);
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
            // Everything good!

            Toast.makeText(SendMessageActivity.this, Util.getMessageContent(ResponseMessageJSON), LENGTH_LONG).show();
        }
    }

    /*private JSONObject JsonObjectDrawable(Bitmap bitmap) {
        JSONObject jsonImageObject = new JSONObject();
        Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        String encodedImage = Util.encodeImageBase64(bitmap, format);
        try {
            jsonImageObject.put("mimeType", "image/png");
            jsonImageObject.put("data", encodedImage);
            return jsonImageObject;
        }
        catch (JSONException e) {
            Log.w(TAG, "Exception occurred while transfering IMG URL to bitmap in: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }*/
}
