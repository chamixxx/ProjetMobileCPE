package com.othmanechamikhazraji.mychatcpe.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_BASE_URL = "http://training.loicortola.com/chat-rest/2.0";
    public static final String EXTRA_LOGIN = "ext_login";
    public static final String EXTRA_PASSWORD = "ext_password";

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
                loginTask = new LoginTask();
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

    /**
     * LoginTask: AsyncTask to process authentication.
     * FYI: AsyncTask takes three generic types: Params, Progress, Result
     */
    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            // Here, show progress bar
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * @param params [login, password]
         * @return true if login succeeded, false otherwise
         */
        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            String credentials = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            // Webservice URL
            String urlString = API_BASE_URL + "/connect";
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
                e.printStackTrace();
            }
            if (urlConnection != null) {
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                try {
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                if ((urlConnection != null ? urlConnection.getResponseCode() : 0) == HttpURLConnection.HTTP_OK) {
                    return true;
                }
            } catch (IOException e) {
                Log.w(TAG, "Exception occured while logging in: " + e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // Here, hide progress bar and proceed to login if OK.
            progressBar.setVisibility(View.GONE);

            // Wrong login entered
            if (!success) {
                Toast.makeText(MainActivity.this, R.string.login_error, LENGTH_LONG).show();
                return;
            }

            // Everything good!
            Toast.makeText(MainActivity.this, R.string.login_success, LENGTH_LONG).show();

            // Declare activity switch intent
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra(EXTRA_LOGIN, username.getText().toString());
            intent.putExtra(EXTRA_PASSWORD, password.getText().toString());

            // Start activity
            startActivity(intent);
            // If you don't want the current activity to be in the backstack,
            // uncomment the following line:
            // finish();
        }
    }
}
