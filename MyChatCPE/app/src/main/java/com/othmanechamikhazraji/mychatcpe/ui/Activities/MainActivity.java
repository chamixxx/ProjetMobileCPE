package com.othmanechamikhazraji.mychatcpe.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.task.LoginTask;
import com.othmanechamikhazraji.mychatcpe.task.LoginTask.LoginTaskFinishedListener;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity implements LoginTaskFinishedListener {

    private static final String EXTRA_LOGIN = "ext_login";
    private static final String EXTRA_PASSWORD = "ext_password";

    private EditText username;
    private EditText password;
    private Button resetBtn;
    private Button submitBtn;
    private ProgressBar progressBar;
    private LoginTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Retrieve views from XML
        resetBtn = (Button) findViewById(R.id.reset_button);
        submitBtn = (Button) findViewById(R.id.submit_button);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Set on click listener on reset button
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset username text to empty
                username.setText("");
                password.setText("");
                // Create Toast message (context, string resource, length)
                Toast message = Toast.makeText(MainActivity.this, R.string.form_reset, LENGTH_LONG);
                // Show Toast message
                message.show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();
                // Cancel previous task if it is still running
                if (loginTask != null && loginTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    loginTask.cancel(true);
                }
                // Launch Login Task
                loginTask = new LoginTask(progressBar, MainActivity.this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostExecute(Boolean success) {
        // Wrong login entered
        if (!success) {
            Toast.makeText(this, R.string.login_error, LENGTH_LONG).show();
            return;
        }
        // Saving login password in sharedPreferences to avoid using extra each time we change
        // activities
        SharedPreferences sharedPreferences = this.getSharedPreferences
                ("authentication", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EXTRA_LOGIN, username.getText().toString());
        editor.putString(EXTRA_PASSWORD, password.getText().toString());
        editor.apply();

        // Everything good!
        Toast.makeText(this, R.string.login_success, LENGTH_LONG).show();
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }
}
