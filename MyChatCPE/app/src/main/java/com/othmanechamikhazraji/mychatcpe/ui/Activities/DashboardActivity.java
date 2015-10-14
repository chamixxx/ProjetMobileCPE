package com.othmanechamikhazraji.mychatcpe.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.othmanechamikhazraji.mychatcpe.R;

public class DashboardActivity extends AppCompatActivity {

    private Button sendBtn;
    private Button messageListeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sendBtn = (Button) findViewById(R.id.sendBtn);
        messageListeBtn = (Button) findViewById(R.id.messagesBtn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Declare activity switch intent
                Intent intentSendMessageActivity = new Intent(DashboardActivity.this, SendMessageActivity.class);
                // Start activity
                startActivity(intentSendMessageActivity);
            }
        });

        messageListeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Declare activity switch intent
                Intent intentMessagesListActivity = new Intent(DashboardActivity.this, MessageListActivity.class);
                // Start activity
                startActivity(intentMessagesListActivity);
            }
        });
    }
}
