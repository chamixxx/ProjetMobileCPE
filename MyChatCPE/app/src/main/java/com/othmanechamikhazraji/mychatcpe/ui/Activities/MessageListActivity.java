package com.othmanechamikhazraji.mychatcpe.ui.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.Utils.DividerItemDecoration;
import com.othmanechamikhazraji.mychatcpe.Utils.Util;
import com.othmanechamikhazraji.mychatcpe.model.MessageModel;
import com.othmanechamikhazraji.mychatcpe.task.PullMessageTask;
import com.othmanechamikhazraji.mychatcpe.ui.adapter.MyAdapter;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class MessageListActivity extends AppCompatActivity implements PullMessageTask.PullMessageFinishedListener {

    public static final String EXTRA_LOGIN = "ext_login";
    public static final String EXTRA_PASSWORD = "ext_password";

    private RecyclerView messageRecyclerView;
    private RecyclerView.Adapter messageAdapter;
    private RecyclerView.LayoutManager messageLayoutManager;

    private ProgressBar progressBar;
    private List<MessageModel> receivedMessageList;
    private Picasso picasso;

    private PullMessageTask pullMessageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_activity);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_message);

        SharedPreferences sharedPreferences = this.getSharedPreferences
                ("authentication", Context.MODE_PRIVATE);

        final String usernameStr = sharedPreferences.getString(EXTRA_LOGIN, "");
        final String passwordStr = sharedPreferences.getString(EXTRA_PASSWORD, "");

        //Setup basic auth for picasso
        OkHttpClient picassoClient = new OkHttpClient();
        picassoClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String credentials = usernameStr + ":" + passwordStr;
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Basic " + base64EncodedCredentials)
                        .build();
                return chain.proceed(newRequest);
            }
        });
        picasso = new Picasso.Builder(this).downloader(new OkHttpDownloader(picassoClient)).build();

        receivedMessageList = new ArrayList<>();

        messageRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        messageLayoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(messageLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST);
        messageRecyclerView.addItemDecoration(itemDecoration);

        // Cancel previous task if it is still running
        if (pullMessageTask != null && pullMessageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            pullMessageTask.cancel(true);
        }
        // Launch pullMessageTask Task
        pullMessageTask = new PullMessageTask(progressBar, this);
        pullMessageTask.execute(usernameStr, passwordStr);
    }

    @Override
    protected void onPause() {
        if (pullMessageTask != null) {
            pullMessageTask.cancel(true);
        }
        super.onPause();
    }

    @Override
    public void onPostExecute(Boolean success, JSONArray allMessageJSON) {
        // No response from the server entered
        if (!success) {
            return;
        }
        // Everything good!
        Toast.makeText(MessageListActivity.this, R.string.messages_success, LENGTH_LONG).show();
        receivedMessageList = Util.makeMessageList(allMessageJSON);
        messageAdapter = new MyAdapter(receivedMessageList, picasso, this);
        messageRecyclerView.setAdapter(messageAdapter);
    }
}
