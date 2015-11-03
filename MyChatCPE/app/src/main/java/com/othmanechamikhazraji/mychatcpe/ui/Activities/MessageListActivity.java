package com.othmanechamikhazraji.mychatcpe.ui.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.othmanechamikhazraji.mychatcpe.R;
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
import java.util.Collections;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class MessageListActivity extends AppCompatActivity implements PullMessageTask.PullMessageFinishedListener {

    public static final String EXTRA_LOGIN = "ext_login";
    public static final String EXTRA_PASSWORD = "ext_password";

    private RecyclerView messageRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter messageAdapter;
    private RecyclerView.LayoutManager messageLayoutManager;
    private ActionBar actionBar;

    private List<MessageModel> receivedMessageList;
    private Picasso picasso;
    private String login;

    private PullMessageTask pullMessageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_activity);

        SharedPreferences sharedPreferences = this.getSharedPreferences
                ("authentication", Context.MODE_PRIVATE);

        final String usernameStr = sharedPreferences.getString(EXTRA_LOGIN, "");
        final String passwordStr = sharedPreferences.getString(EXTRA_PASSWORD, "");
        login = usernameStr;

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

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (pullMessageTask != null && pullMessageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    pullMessageTask.cancel(true);
                }
                pullMessageTask = new PullMessageTask(MessageListActivity.this);
                pullMessageTask.execute(usernameStr, passwordStr);
            }
        });

        messageRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        messageLayoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(messageLayoutManager);


        // Cancel previous task if it is still running
        if (pullMessageTask != null && pullMessageTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            pullMessageTask.cancel(true);
        }
        // Launch pullMessageTask Task
        pullMessageTask = new PullMessageTask(this);
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
    public void onPostExecute(Boolean success, JSONArray allMessageJSON) {
        // No response from the server entered
        if (!success) {
            return;
        }
        // Everything good!
        Toast.makeText(MessageListActivity.this, R.string.messages_success, LENGTH_LONG).show();
        receivedMessageList = Util.makeMessageList(allMessageJSON);
        Collections.reverse(receivedMessageList);
        messageAdapter = new MyAdapter(receivedMessageList, picasso, login, this);
        messageRecyclerView.setAdapter(messageAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
