package com.othmanechamikhazraji.mychatcpe.ui.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.Utils.Util;
import com.othmanechamikhazraji.mychatcpe.model.Attachment;
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
    private ActionProcessButton sendMessageBtn;
    private Button addUrlBtn;
    private EditText messageEditText;
    private EditText image1UrlEditText;
    private SendMessageTask sendMessageTask;
    private String bodyToSend;
    private LinearLayout imageLayout;
    private LinearLayout previewLayout;
    private List<Attachment> attachmentList;
    private List<ImageDrawable> imageDrawableList;
    private View.OnClickListener onImageClick;
    private float px;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        Resources r = getResources();
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());

        attachmentList = new ArrayList<>();
        sendMessageBtn = (ActionProcessButton) findViewById(R.id.sendMsgBtn);
        sendMessageBtn.setMode(ActionProcessButton.Mode.ENDLESS);

        addUrlBtn = (Button) findViewById(R.id.addUrl);
        messageEditText = (EditText) findViewById(R.id.messageText);
        image1UrlEditText = (EditText) findViewById(R.id.imageURL1);

        SharedPreferences sharedPreferences = this.getSharedPreferences
                ("authentication", Context.MODE_PRIVATE);
        final String usernameStr = sharedPreferences.getString(EXTRA_LOGIN, "");
        final String passwordStr = sharedPreferences.getString(EXTRA_PASSWORD, "");

        addUrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image1UrlEditText.setText("http://mathiasfranck.free.fr/images//point.jpg");
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bodyToSend = messageEditText.getText().toString();
                imageUrls[0] = image1UrlEditText.getText().toString();

                // Cancel previous task if it is still running
                if (sendMessageTask != null && sendMessageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    sendMessageTask.cancel(true);
                }
                sendMessageBtn.setEnabled(false);
                messageEditText.setEnabled(false);
                image1UrlEditText.setEnabled(false);
                // Launch sendMessageTask
                sendMessageTask = new SendMessageTask(imageUrls, bodyToSend, attachmentList, sendMessageBtn, SendMessageActivity.this);
                sendMessageTask.execute(usernameStr, passwordStr);
            }
        });

        imageDrawableList = Util.getListImageDrawables(Util.getIDRessourcesAsIntegers());
        imageLayout = (LinearLayout) findViewById(R.id.imageLayout);
        previewLayout = (LinearLayout) findViewById(R.id.previewLayout);
        onImageClick = new View.OnClickListener() {
            public void onClick(View v) {
                int tag = (int) v.getTag();
                ImageDrawable imageCurrent = imageDrawableList.get(tag);
                Bitmap bitmapImageCurrent = BitmapFactory.decodeResource(SendMessageActivity.this.getResources(), imageCurrent.getRessourceId());
                Attachment attachment = new Attachment("image/png", Util.encodeImageBase64(bitmapImageCurrent,Bitmap.CompressFormat.PNG));
                attachmentList.add(attachment);

                imageLayout.removeView(v);
                v.getLayoutParams().height = (int) (30*px);
                v.getLayoutParams().width = (int) (30*px);
                v.requestLayout();
                previewLayout.addView(v);
            }
        };
        displayStickers(imageDrawableList);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendMessageBtn.setProgress(0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        messageEditText.addTextChangedListener(textWatcher);
        image1UrlEditText.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onPause() {
        if (sendMessageTask != null) {
            sendMessageTask.cancel(true);
        }
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostExecute(Boolean success, JSONObject responseMessageJSON) {
        sendMessageBtn.setEnabled(true);
        messageEditText.setEnabled(true);
        image1UrlEditText.setEnabled(true);

        if (!success) {
            sendMessageBtn.setProgress(-1);
            Toast.makeText(this, Util.getMessageContent(responseMessageJSON), LENGTH_LONG).show();
            return;
        }
        // Everything good!
        sendMessageBtn.setProgress(100);
    }



    private void displayStickers (List<ImageDrawable> imageDrawableList) {
        int i = 0;
        for (ImageDrawable image : imageDrawableList) {
            ImageView imageView = new ImageView(SendMessageActivity.this);
            imageView.setImageResource(image.getRessourceId());
            imageView.setTag(i);
            imageView.setOnClickListener(onImageClick);
            imageLayout.addView(imageView);
            imageView.getLayoutParams().height = (int) (70*px);
            imageView.getLayoutParams().width = (int) (70*px);
            imageView.requestLayout();
            i++;
        }
    }
}
