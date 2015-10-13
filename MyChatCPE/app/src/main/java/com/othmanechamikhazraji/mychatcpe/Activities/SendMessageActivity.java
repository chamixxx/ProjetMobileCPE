package com.othmanechamikhazraji.mychatcpe.Activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

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
    private String messageToSend;
    private String ResponseMessageString = "";
    private JSONObject ResponseMessageJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        final Intent intentSendMessageActivity = getIntent();
        sendMessageBtn = (Button) findViewById(R.id.sendMsgBtn);
        messageEditText = (EditText) findViewById(R.id.messageText);
        image1UrlEditText = (EditText) findViewById(R.id.imageURL1);
        image2UrlEditText = (EditText) findViewById(R.id.imageURL2);
        image3UrlEditText = (EditText) findViewById(R.id.imageURL3);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_message);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = intentSendMessageActivity.getStringExtra(EXTRA_LOGIN);
                String passwordStr = intentSendMessageActivity.getStringExtra(EXTRA_PASSWORD);

                messageToSend = messageEditText.getText().toString();
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

                    JSONObject jsonParam = createJSONMessage(username,uuidStr);
                    if(jsonParam == null) {
                        jsonParam.put("fail","fail");
                    }
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                    outputStreamWriter.write(jsonParam.toString());
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


                } catch (IOException | JSONException e) {
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

            Toast.makeText(SendMessageActivity.this, getMessageContent(ResponseMessageJSON), LENGTH_LONG).show();
        }
    }

    private String getUUID() {
        UUID idOne = UUID.randomUUID();
        return idOne.toString();
    }

    private String getMessageContent(JSONObject response) {
        try {
            String message = response.get("message").toString();
            return message;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String encodeImage(Bitmap image, Bitmap.CompressFormat format) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(format, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
        return imageEncoded;
    }

    private Bitmap getBitmapURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            Log.w(TAG, "Exception occurred while transfering IMG URL to bitmap in: " + e.getMessage());
            return null;
        }
    }

    private Bitmap.CompressFormat getImageFormat(String urlStr) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if(urlStr.endsWith(".png")){
            format = Bitmap.CompressFormat.PNG;
        }
        return format;
    }

    private JSONObject createJSONMessage(String usernameStr, String uuidStr) {
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("uuid", uuidStr);
            jsonParam.put("login", usernameStr);
            jsonParam.put("message", messageToSend);

            //Images from coded URLs
            JSONArray jsonImageArray = new JSONArray();
            for(int i =0; i<3; i++){
                if(imageUrls[i] == "") {
                    continue;
                }
                JSONObject jsonImageObject = new JSONObject();
                Bitmap bitmapImage = getBitmapURL(imageUrls[i]);
                Bitmap.CompressFormat format = getImageFormat(imageUrls[i]);
                String encodedImage = encodeImage(bitmapImage, format);
                if(format == Bitmap.CompressFormat.JPEG) {
                    jsonImageObject.put("mimeType","image/jpeg");
                }
                else {
                    jsonImageObject.put("mimeType","image/png");
                }
                jsonImageObject.put("data",encodedImage);
                jsonImageArray.put(jsonImageObject);
            }

            //Final array of image added to JSON
            jsonParam.put("attachments",jsonImageArray);
            return jsonParam;
        }
        catch (JSONException e) {
            Log.w(TAG, "Exception occurred while transfering IMG URL to bitmap in: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
