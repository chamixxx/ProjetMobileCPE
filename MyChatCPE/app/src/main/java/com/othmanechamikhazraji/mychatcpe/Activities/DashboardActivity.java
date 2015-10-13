package com.othmanechamikhazraji.mychatcpe.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.othmanechamikhazraji.mychatcpe.R;

public class DashboardActivity extends AppCompatActivity {

    private Button sendBtn;
    private Button messageListeBtn;

    private static final String TAG = DashboardActivity.class.getSimpleName();
    public static final String EXTRA_LOGIN = "ext_login";
    public static final String EXTRA_PASSWORD = "ext_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final Intent intentDashboardActivity = getIntent();

        sendBtn = (Button) findViewById(R.id.sendBtn);
        messageListeBtn = (Button) findViewById(R.id.messagesBtn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  // Declare activity switch intent
                Intent intentSendMessageActivity = new Intent(DashboardActivity.this, SendMessageActivity.class);
                intentSendMessageActivity.putExtra(EXTRA_LOGIN, intentDashboardActivity.getStringExtra(EXTRA_LOGIN));
                intentSendMessageActivity.putExtra(EXTRA_PASSWORD, intentDashboardActivity.getStringArrayExtra(EXTRA_PASSWORD));

                // Start activity
                startActivity(intentSendMessageActivity);*/
            }
        });

        messageListeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Declare activity switch intent
                Intent intentMessagesListActivity = new Intent(DashboardActivity.this, MessageListActivity.class);
                intentMessagesListActivity.putExtra(EXTRA_LOGIN, intentDashboardActivity.getStringExtra(EXTRA_LOGIN));
                intentMessagesListActivity.putExtra(EXTRA_PASSWORD, intentDashboardActivity.getStringExtra(EXTRA_PASSWORD));

                // Start activity
                startActivity(intentMessagesListActivity);
            }
        });


    }

}
