package com.othmanechamikhazraji.mychatcpe.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.Utils.CustomArrayAdapter;
import com.othmanechamikhazraji.mychatcpe.Utils.Util;

import java.util.List;

public class MessageListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_activity);

        String allMessages = "excilys:Bonjour !;truc:coucou;truc:bijou;truc:machin;";
        String[] splitMassageArray = Util.splitMessages(allMessages);
        List<String> messageList = Util.populateListMessages(splitMassageArray);

        CustomArrayAdapter messageAdapter = new CustomArrayAdapter(this,messageList);
        ListView listViewMessage = (ListView) findViewById(R.id.listView);
        listViewMessage.setAdapter(messageAdapter);
    }
}
