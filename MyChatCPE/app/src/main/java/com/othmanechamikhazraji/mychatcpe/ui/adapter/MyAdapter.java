package com.othmanechamikhazraji.mychatcpe.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.othmanechamikhazraji.mychatcpe.R;
import com.othmanechamikhazraji.mychatcpe.model.MessageModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by othmanechamikhazraji on 13/10/15.
 */
public class MyAdapter extends RecyclerView.Adapter {
    private List<MessageModel> values;
    private Picasso picasso;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView profilePic;
        private TextView login;
        private TextView message;
        private LinearLayout images;

        public MyViewHolder(View view) {
            super(view);
            login = (TextView) view.findViewById(R.id.list_item_login);
            message = (TextView) view.findViewById(R.id.list_item_message);
            images = (LinearLayout) view.findViewById(R.id.list_item_images);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<MessageModel> myDataSet, Picasso picasso, Context context) {
        this.picasso = picasso;
        values = myDataSet;
        this.context = context;
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

        ((MyViewHolder) holder).images.setVisibility(View.GONE);
        ((MyViewHolder) holder).images.removeAllViews();
        ((MyViewHolder)holder).login.setText(values.get(position).getLogin());
        ((MyViewHolder)holder).message.setText(values.get(position).getMessage());

        if (values.get(position).getImages() != null) {
            if (values.get(position).getImages().size() != 0) {
                for (int j=0; j<values.get(position).getImages().size(); j++) {
                    ImageView imageView = new ImageView(context);
                    picasso.load(values.get(position).getImages().get(j)).resize(200,200).into(imageView);
                    ((MyViewHolder) holder).images.addView(imageView);
                    ((MyViewHolder) holder).images.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }
}
