package com.othmanechamikhazraji.mychatcpe.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.othmanechamikhazraji.mychatcpe.R;

import java.util.List;

/**
 * Created by othmanechamikhazraji on 09/10/15.
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public CustomArrayAdapter(Context context, List<String> values) {
        super(context, R.layout.simple_list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.simple_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.list_item);
        textView.setText(values.get(position));
        return rowView;
    }
}
