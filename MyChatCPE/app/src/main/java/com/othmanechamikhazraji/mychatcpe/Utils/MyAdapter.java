package com.othmanechamikhazraji.mychatcpe.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.model.ReceivedMessage;

import java.util.List;

/**
 * Created by othmanechamikhazraji on 13/10/15.
 */
public class MyAdapter extends RecyclerView.Adapter {
    private List<ReceivedMessage> values;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView login;
        public TextView message;
        public MyViewHolder(View view) {
            super(view);
            login = (TextView) view.findViewById(R.id.list_item_login);
            message = (TextView) view.findViewById(R.id.list_item_message);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<ReceivedMessage> myDataSet) {
        values = myDataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((MyViewHolder)holder).login.setText(values.get(position).getLogin());
        ((MyViewHolder)holder).message.setText(values.get(position).getMessage());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }
}
