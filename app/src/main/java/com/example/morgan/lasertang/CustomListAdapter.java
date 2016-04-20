package com.example.morgan.lasertang;

/**
 * Created by v.denisov on 20.04.16.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {

    private Activity context;
    private String[] itemname;
    private String[] comment;
    private Integer[] imgid;


    public CustomListAdapter(Activity context, String[] itemname, String[] comment, Integer[] imgid) {
        super(context, R.layout.list_item, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.comment = comment;
        this.imgid=imgid;
    }

    public void setData( String[] itemname, String[] comment, Integer[] imgid) {
        this.itemname=itemname;
        this.comment = comment;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.store_item_text);
        TextView txtComment = (TextView) rowView.findViewById(R.id.store_item_comment);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.store_item_img);

        txtTitle.setText(itemname[position]);
        txtComment.setText(comment[position]);
        System.out.print(position);
//        System.out.print(imgid[position]);
        imageView.setImageResource(imgid[position]);
        return rowView;

    };
}
