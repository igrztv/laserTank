package com.example.morgan.lasertang;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by v.denisov on 15.05.16.
 */
public class json_loader extends AsyncTask<String, Void, String> {

    CustomListAdapter adapter;

    public json_loader(CustomListAdapter adapter ) {
        this.adapter = adapter;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(urls[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                return out.toString();
            }
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
        return "";
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {

        JSONObject dataJsonObj = null;


        StoreContainer container = new StoreContainer();

        try {
            dataJsonObj = new JSONObject(result);


            for(int i = 0; i < dataJsonObj.names().length(); i++) {

                List<List<String>> item_set = new ArrayList<List<String>>();

                for(int counter = 0; counter < 4; counter++){
                    item_set.add(new ArrayList<String>());
                }

                String key = dataJsonObj.names().getString(i);
                JSONArray items = dataJsonObj.getJSONArray(key);

                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);

                    String img = item.getString("img");
                    String name = item.getString("name");
                    String link = item.getString("link");
                    String comment = item.getString("comment");
                    item_set.get(0).add(name);
                    item_set.get(1).add(img);
                    item_set.get(2).add(comment);
                    item_set.get(3).add(link);
                }
                container.setData(key, item_set);
            }

            adapter.setDataSet(container);
            adapter.setData(1);
            adapter.notifyDataSetChanged();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }
}
