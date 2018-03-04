package com.foxpower.flchatofandroid.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.util.other.FLLog;
import com.foxpower.flchatofandroid.util.other.FLUtil;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fengli on 2018/2/7.
 */

public class NavigationBar extends LinearLayout {

    private View root = null;

    private String title;

    private boolean hasBack;

    private Context mContext;

    @BindView(R.id.navigation_title)
    TextView titleTextView;

    @BindView(R.id.navigation_back)
    ImageView backIcon;



    public NavigationBar(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }


    private void initView(final Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);

        root = inflater.inflate(R.layout.navigation_bar, this, true);
        ButterKnife.bind(this);


    }


    public void setTitle(String title) {

        this.title = title;
        titleTextView.setText(title);
    }

    public void setHasBack(boolean hasBack) {
        this.hasBack = hasBack;
        if (hasBack){
            backIcon.setVisibility(VISIBLE);
        } else {
            backIcon.setVisibility(GONE);
        }
    }

    public void setBackListener(OnClickListener listener) {

        backIcon.setOnClickListener(listener);
    }

    public <T> View addLeft(T item, final clickCallBack callBack) {

        backIcon.setVisibility(GONE);

        ViewGroup back = findViewById(R.id.navigation_left_layout);
        View view = null;
        if (item instanceof String) {

            TextView textView = new TextView(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setText((String) item);
            textView.setTextColor(Color.WHITE);
            view = textView;
            back.addView(textView);


            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callBack != null)callBack.itemClick();
                }
            });
        }
        return view;
    }

    public <T> View addRight(T item, final clickCallBack callBack) {

        ViewGroup back = findViewById(R.id.navigation_right_layout);
        View view = null;
        if (item instanceof String) {

            TextView textView = new TextView(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setText((String) item);
            textView.setTextColor(Color.WHITE);
            view = textView;
            back.addView(textView);


            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callBack != null)callBack.itemClick();
                }
            });
        }
        return view;
    }

    public interface clickCallBack {

        public void itemClick();
    }
}
