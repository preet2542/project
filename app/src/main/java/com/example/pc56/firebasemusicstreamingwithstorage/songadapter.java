package com.example.pc56.firebasemusicstreamingwithstorage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pc56 on 22-05-2017.
 */

public class songadapter extends BaseAdapter {
    Context context;
    ArrayList<SongDetail> mylist;
    public songadapter(Context context, ArrayList<SongDetail> mylist) {
        this.context = context;
this.mylist=mylist;
    }

    @Override
    public int getCount() {
        return mylist.size();
    }

    @Override
    public Object getItem(int position) {
        return mylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
          LayoutInflater mInflater= LayoutInflater.from(context);
        convertView = mInflater.inflate(R.layout.child, parent, false);
        TextView t=(TextView)convertView.findViewById(R.id.sontitle) ;
        t.setText(mylist.get(position).getName());
        return convertView;
    }

}
