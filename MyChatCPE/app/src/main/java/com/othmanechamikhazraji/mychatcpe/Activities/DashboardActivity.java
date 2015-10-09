package com.othmanechamikhazraji.mychatcpe.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;

import static android.widget.Toast.LENGTH_LONG;

public class DashboardActivity extends AppCompatActivity {

    private Button sendBtn;
    private Button messagesBtn;

    private static final String TAG = DashboardActivity.class.getSimpleName();
    private static final String API_BASE_URL = "http://formation-android-esaip.herokuapp.com";
    public static final String EXTRA_LOGIN = "ext_login";
    public static final String EXTRA_PASSWORD = "ext_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        Intent intent = getIntent();
        final String message = new StringBuilder("user : ")
                .append(intent.getStringExtra(EXTRA_LOGIN))
                .append(" password : ")
                .append(intent.getStringExtra(EXTRA_PASSWORD))
                .toString();

        sendBtn = (Button) findViewById(R.id.sendBtn);
        messagesBtn = (Button) findViewById(R.id.messagesBtn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Toast message (context, string resource, length)
                String message2 = new StringBuilder("test send message : ")
                        .append(message)
                        .toString();
                Toast message = Toast.makeText(DashboardActivity.this, message2, LENGTH_LONG);
                // Show Toast message
                message.show();

                // Declare activity switch intent
                //Intent intent = new Intent(MainActivity.this, SendActivity.class);
                //intent.putExtra(EXTRA_LOGIN, username.getText().toString());

                // Start activity
                //startActivity(intent);
            }
        });

        messagesBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Create Toast message (context, string resource, length)
                String message3 = new StringBuilder("test liste message : ")
                        .append(message)
                        .toString();
                Toast message = Toast.makeText(DashboardActivity.this, message3, LENGTH_LONG);
                // Show Toast message
                message.show();
                // Declare activity switch intent
                //Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                //intent.putExtra(EXTRA_LOGIN, username.getText().toString());

                // Start activity
                //startActivity(intent);
            }
        });


    }

}
