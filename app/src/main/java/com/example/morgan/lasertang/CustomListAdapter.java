package com.example.morgan.lasertang;

/**
 * Created by v.denisov on 20.04.16.
 */
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<String> {

    StoreContainer container;

    private Activity context;
    private List<String> itemname;
    private List<String> comment;
    private List<String> link;
    private List<String> imgid;
    private List<String> tabs = new ArrayList<String>();
    static String LOG = "STORE_ACTIVITY_LOG";

    public CustomListAdapter(Activity context, List<String> itemname, List<String> comment,
                             List<String> imgid, List<String> link) {
        super(context, R.layout.list_item, itemname);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.itemname = itemname;
        this.comment = comment;
        this.imgid = imgid;
        this.link = link;
        this.tabs.add("Ходовая");
        this.tabs.add("Броня");
        this.tabs.add("Орудия");
    }

    public void setDataSet(StoreContainer container){
        this.container = container;
    }

    public void setData( Integer _case) {
        _case = _case - 1;
        try {
            List<List<String>> lists = container.getData(this.tabs.get(_case));
            this.itemname = lists.get(0);
            this.imgid = lists.get(1);
            this.comment = lists.get(2);
            this.link = lists.get(3);
        }
        catch (NullPointerException e) {
        }
    }

    public View getView(final int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.store_item_text);
        TextView txtComment = (TextView) rowView.findViewById(R.id.store_item_comment);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.store_item_img);

        final String p = link.get(position);

        txtTitle.setText(itemname.get(position));
        txtComment.setText(comment.get(position));
        if (!TextUtils.isEmpty(imgid.get(position))) {
            Picasso.with(context).load(imgid.get(position)).into(imageView);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(p));
                context.startActivity(intent);
            }
        });


        //new DownloadImageTask(( ImageView ) imageView).execute(imgid.get(position));
        return rowView;

    };

    public String get_link(Integer position){
        return this.comment.get(position);
    }

    public int getCount() {
        return this.itemname.size();
    }
}
