package com.othmanechamikhazraji.mychatcpe.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.task.LoginTask;
import com.othmanechamikhazraji.mychatcpe.task.LoginTask.LoginTaskFinishedListener;


public class MainActivity extends AppCompatActivity implements LoginTaskFinishedListener {

    private static final String EXTRA_LOGIN = "ext_login";
    private static final String EXTRA_PASSWORD = "ext_password";

    private EditText username;
    private EditText password;
    private ActionProcessButton submitBtn;
    private LoginTask loginTask;
    private ImageView logoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Retrieve views from XML
        submitBtn = (ActionProcessButton) findViewById(R.id.submit_button);
        submitBtn.setMode(ActionProcessButton.Mode.ENDLESS);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        logoView = (ImageView) findViewById(R.id.logo);


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submitBtn.setProgress(0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();
                // Cancel previous task if it is still running
                if (loginTask != null && loginTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    loginTask.cancel(true);
                }
                submitBtn.setEnabled(false);
                username.setEnabled(false);
                password.setEnabled(false);
                // Launch Login Task
                loginTask = new LoginTask(submitBtn, MainActivity.this);
                loginTask.execute(usernameStr, passwordStr);
            }
        });
    }

    @Override
    protected void onPause() {
        if (loginTask != null) {
            loginTask.cancel(true);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        submitBtn.setProgress(0);
        submitBtn.setEnabled(true);
        username.setEnabled(true);
        password.setEnabled(true);
    }

    @Override
    public void onPostExecute(Boolean success) {
        // Wrong login entered
        if (!success) {
            submitBtn.setProgress(-1);
            submitBtn.setEnabled(true);
            username.setEnabled(true);
            password.setEnabled(true);
            return;
        }
        submitBtn.setProgress(100);
        // Saving login password in sharedPreferences to avoid using extra each time we change
        // activities
        SharedPreferences sharedPreferences = this.getSharedPreferences
                ("authentication", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EXTRA_LOGIN, username.getText().toString());
        editor.putString(EXTRA_PASSWORD, password.getText().toString());
        editor.apply();

        // Everything good!
        Intent intent = new Intent(this, DashboardActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, logoView, "logoTransition");
        startActivity(intent, options.toBundle());
    }
}
