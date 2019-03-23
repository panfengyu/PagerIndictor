package fy.learn.com.pagerindictor.adapter;

import android.view.View;
import android.view.ViewGroup;

public interface BaseAdapter {

    int getCount();

    int getItemId(int position);

    Object getItem(int position) ;

    View getView(int position, View convertView, ViewGroup parent);

    View getSpaceView();

}
