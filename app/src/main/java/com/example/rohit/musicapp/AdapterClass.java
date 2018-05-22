package com.example.rohit.musicapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class AdapterClass extends ArrayAdapter<SongObject> {
    private Context mContext;
    private int mResource;
    private ArrayList<SongObject> mList;

    AdapterClass(Context context, int resource, ArrayList<SongObject> objects) {
        super(context, resource, objects);
        mContext =context;
        mResource =resource;
        mList =objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View mView=null;

        //Initializing view which will point to layout file list_item
        mView= LayoutInflater.from(mContext).inflate(mResource,parent,false);

        //Initializing TextView
        TextView fileName=(TextView)mView.findViewById(R.id.textSong);

        SongObject sdOb= mList.get(position);
        //Setting the Icon and FileName
        fileName.setText(sdOb.getmFileName());

        return mView;
    }
}
