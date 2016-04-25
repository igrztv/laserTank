package com.example.morgan.lasertang;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsListAdapter extends ArrayAdapter<String> {

    private Activity context;
    private String[] title;
    private String[] subtitle;
    private Integer[] icon;
    // add checkbox

    public SettingsListAdapter(Activity context, String[] title, String[] subtitle, Integer[] imgid) {
        super(context, R.layout.list_item, title);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.title = title;
        this.subtitle = subtitle;
        this.icon = imgid;
    }

    public SettingsListAdapter(Activity context, String[] title, Integer[] imgid) {
        super(context, R.layout.list_item, title);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.title = title;
        this.icon = imgid;
    }

    public SettingsListAdapter(Activity context, String[] title, String[] subtitle) {
        super(context, R.layout.list_item, title);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.title = title;
        this.subtitle = subtitle;
    }

    public void setData( String[] title, String[] subtitle, Integer[] imgid) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon=imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
        TextView txtComment = (TextView) rowView.findViewById(R.id.subtitle);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        txtTitle.setText(title[position]);
        txtComment.setText(subtitle[position]);
        System.out.print(position);
//        System.out.print(imgid[position]);
        imageView.setImageResource(icon[position]);
        return rowView;

    };
}
