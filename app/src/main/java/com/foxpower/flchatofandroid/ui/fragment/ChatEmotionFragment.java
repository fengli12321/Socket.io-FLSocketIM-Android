package com.foxpower.flchatofandroid.ui.fragment;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.common.BaseFragment;
import com.foxpower.flchatofandroid.ui.adapter.EmotionGridViewAdapter;
import com.foxpower.flchatofandroid.ui.adapter.EmotionPagerAdapter;
import com.foxpower.flchatofandroid.ui.view.IndicatorView;
import com.foxpower.flchatofandroid.util.other.EmotionUtil;
import com.foxpower.flchatofandroid.util.other.FLUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by fengli on 2018/3/1.
 */

public class ChatEmotionFragment extends BaseFragment {

    @BindView(R.id.fragment_chat_vp)
    ViewPager fragmentChatVp;
    @BindView(R.id.fragment_chat_group)
    IndicatorView fragmentChatGroup;
    private View rootView;
    private EmotionPagerAdapter emotionPagerAdapter;
    private EmotionClickCallBack clickCallBack;

    private int chatEmotionHeight;



    public int getChatEmotionHeight() {
        return chatEmotionHeight;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_emotion;
    }

    @Override
    protected void initView() {
        super.initView();
        initWidget();
    }

    public void setClickCallBack(EmotionClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    private void initWidget() {
        fragmentChatVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int oldPagerPos = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fragmentChatGroup.playByStartPointToNext(oldPagerPos, position);
                oldPagerPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initEmotion();
    }

    private void initEmotion() {

        int screenWidth = ScreenUtils.getScreenWidth();
        int spacing = FLUtil.dip2px(mContext, 12);
        int itemWidth = (screenWidth - spacing*8)/7;
        int gvHeight = itemWidth * 3 + spacing * 6;

        chatEmotionHeight = gvHeight + FLUtil.dip2px(mContext, 30);

        List<GridView> emotionViews = new ArrayList<>();
        List<String> emotionNames = new ArrayList<>();
        for (String emotion : EmotionUtil.emotions) {
            emotionNames.add(emotion);
            if (emotionNames.size() == 23) {
                GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
                emotionViews.add(gv);
                emotionNames = new ArrayList<>();
            }
        }

        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
            emotionViews.add(gv);
        }

        fragmentChatGroup.initIndicator(emotionViews.size());

        emotionPagerAdapter = new EmotionPagerAdapter(emotionViews);
        fragmentChatVp.setAdapter(emotionPagerAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
        fragmentChatVp.setLayoutParams(params);
    }

    private GridView createEmotionGridView(final List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {

        GridView gv = new GridView(mContext);
        gv.setNumColumns(8);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding*2);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);

        EmotionGridViewAdapter adapter = new EmotionGridViewAdapter(mContext, emotionNames, itemWidth);
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (clickCallBack == null) {
                    return;
                }
                if (view instanceof ImageView) {
                    clickCallBack.onClickDelete();
                } else {
                    String emotion = emotionNames.get(i);
                    clickCallBack.onClickEmotion(emotion);
                }



            }
        });

        return gv;
    }


    public interface EmotionClickCallBack {

        public void onClickEmotion(String emotion);
        public void onClickDelete();
    }
}
