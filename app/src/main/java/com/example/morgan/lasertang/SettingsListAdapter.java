package com.example.morgan.lasertang;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

public class SettingsListAdapter extends ArrayAdapter<String> {

    String LOG = "SettingsListAdapter_LOG";

    private Activity context;
    private String[] title;
    private String[] subtitle;
    private Integer[] icon;
    private Boolean[] cB;

    public SettingsListAdapter(Activity context, String[] title, String[] subtitle, Integer[] imgid, Boolean[] checkBox) {
        super(context, R.layout.settings_item, title);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.title = title;
        this.subtitle = subtitle;
        this.icon = imgid;
        this.cB = checkBox;
    }

    public SettingsListAdapter(Activity context, String[] title, Integer[] imgid) {
        super(context, R.layout.settings_item, title);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.title = title;
        this.icon = imgid;
    }

    public SettingsListAdapter(Activity context, String[] title, String[] subtitle, Boolean[] checkBox) {
        super(context, R.layout.settings_item, title);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.title = title;
        this.subtitle = subtitle;
        this.cB = checkBox;
        this.icon = null;
    }

    public void setData( String[] title, String[] subtitle, Integer[] imgid) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon=imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.settings_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
        TextView txtComment = (TextView) rowView.findViewById(R.id.subtitle);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
        Space space = (Space) rowView.findViewById(R.id.space);

        Log.d(LOG, "title[" + position + "] = " + title[position]);
        txtTitle.setText(title[position]);
        if(!subtitle[position].equals("")){
            space.setVisibility(View.GONE);
        }
        txtComment.setText(subtitle[position]);
        if (!cB[position]) {
            checkBox.setVisibility(View.GONE);
        }
        if (icon == null) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setImageResource(icon[position]);
        }
        System.out.print(position);
//        System.out.print(imgid[position]);
        return rowView;

    };
}
