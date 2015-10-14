package com.othmanechamikhazraji.mychatcpe.ui.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.Utils.Util;
import com.othmanechamikhazraji.mychatcpe.model.ImageDrawable;
import com.othmanechamikhazraji.mychatcpe.task.SendMessageTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class SendMessageActivity extends AppCompatActivity implements SendMessageTask.SendMessageTaskFinishedListener {

    public static final String EXTRA_LOGIN = "ext_login";
    public static final String EXTRA_PASSWORD = "ext_password";

    private String[] imageUrls = new String[3];
    private Button sendMessageBtn;
    private EditText messageEditText;
    private EditText image1UrlEditText;
    private SendMessageTask sendMessageTask;
    private ProgressBar progressBar;
    private String bodyToSend;
    private LinearLayout imageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        sendMessageBtn = (Button) findViewById(R.id.sendMsgBtn);
        messageEditText = (EditText) findViewById(R.id.messageText);
        image1UrlEditText = (EditText) findViewById(R.id.imageURL1);
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

                // Cancel previous task if it is still running
                if (sendMessageTask != null && sendMessageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    sendMessageTask.cancel(true);
                }
                // Launch sendMessageTask
                sendMessageTask = new SendMessageTask(imageUrls, progressBar, bodyToSend, SendMessageActivity.this);
                sendMessageTask.execute(usernameStr, passwordStr);
            }
        });

        List<ImageDrawable> imageDrawableList = Util.getListImageDrawables(Util.getIDRessourcesAsIntegers());
        imageLayout = (LinearLayout) findViewById(R.id.imageLayout);
        CreateImageView(imageDrawableList);
    }

    @Override
    protected void onPause() {
        if (sendMessageTask != null) {
            sendMessageTask.cancel(true);
        }
        super.onPause();
    }

    @Override
    public void onPostExecute(Boolean success, JSONObject responseMessageJSON) {
        if (!success) {
            Toast.makeText(this, Util.getMessageContent(responseMessageJSON), LENGTH_LONG).show();
            return;
        }
        // Everything good!
        Toast.makeText(this, Util.getMessageContent(responseMessageJSON), LENGTH_LONG).show();
    }

    private void CreateImageView(List<ImageDrawable> imageDrawableList) {
        for (ImageDrawable image : imageDrawableList) {
            ImageView imageView = new ImageView(SendMessageActivity.this);
            imageView.setImageResource(image.getRessourceId());
            imageLayout.addView(imageView);
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
