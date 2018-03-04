package com.foxpower.flchatofandroid.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.util.other.FLUtil;

import java.util.List;

/**
 * Created by fengli on 2018/3/1.
 */

public class EmotionGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> emotionNames;
    private int itemWidth;

    public EmotionGridViewAdapter(Context context, List<String> emotionNames, int itemWidth){
        this.context = context;
        this.emotionNames = emotionNames;
        this.itemWidth = itemWidth;
    }

    @Override
    public int getCount() {
        return emotionNames.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        return emotionNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if (i == getCount() - 1) {
            ImageView imageView = new ImageView(context);
            imageView.setPadding(itemWidth/8, itemWidth/8, itemWidth/8, itemWidth/8);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(itemWidth, itemWidth);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.compose_emotion_delete);
            return imageView;
        } else {
            TextView textView = new TextView(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(itemWidth, itemWidth);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(0xff000000);
            textView.setTextSize(20);
            String emotionName = emotionNames.get(i);
            textView.setText(emotionName);
            return textView;
        }

    }
}
