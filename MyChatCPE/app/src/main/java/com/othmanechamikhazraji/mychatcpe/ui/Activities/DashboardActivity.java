package com.othmanechamikhazraji.mychatcpe.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.othmanechamikhazraji.mychatcpe.R;

public class DashboardActivity extends AppCompatActivity {

    private Button sendBtn;
    private Button messageListeBtn;
    private ImageView logoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sendBtn = (Button) findViewById(R.id.sendBtn);
        messageListeBtn = (Button) findViewById(R.id.messagesBtn);
        logoView = (ImageView) findViewById(R.id.logo2);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Declare activity switch intent
                Intent intentSendMessageActivity = new Intent(DashboardActivity.this, SendMessageActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(DashboardActivity.this, logoView, "logoTransition");
                startActivity(intentSendMessageActivity, options.toBundle());
            }
        });

        messageListeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Declare activity switch intent
                Intent intentMessagesListActivity = new Intent(DashboardActivity.this, MessageListActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(DashboardActivity.this, logoView, "logoTransition");
                startActivity(intentMessagesListActivity, options.toBundle());
            }
        });
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
}
