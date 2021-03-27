package com.nitish.gamershub;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Gridview_adapter extends BaseAdapter {
    ArrayList title_list, image_list;
    Context context;

    public Gridview_adapter(Context context, ArrayList mtitle_list, ArrayList mimage_list) {
        this.context = context;
        this.image_list = mimage_list;
        this.title_list = mtitle_list;
    }

    @Override
    public int getCount() {
        return title_list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView title = view.findViewById(R.id.list_text);

        title.setTextColor(Color.BLACK);
        ImageView imageView = view.findViewById(R.id.category_img);

        imageView.setImageResource((Integer) image_list.get(i));
        title.setText((CharSequence) title_list.get(i));
        return view;
    }

}
