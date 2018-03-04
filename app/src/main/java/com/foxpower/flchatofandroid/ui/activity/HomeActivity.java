package com.foxpower.flchatofandroid.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.common.BaseActivity;
import com.foxpower.flchatofandroid.db.DbManager;
import com.foxpower.flchatofandroid.db.dbObject.ConversationDbObject;
import com.foxpower.flchatofandroid.ui.adapter.HomePagerAdapter;
import com.foxpower.flchatofandroid.ui.fragment.HomeContactFrament;
import com.foxpower.flchatofandroid.ui.fragment.HomeMeFragment;
import com.foxpower.flchatofandroid.ui.fragment.HomeMessageFragment;
import com.foxpower.flchatofandroid.util.other.FLLog;
import com.foxpower.flchatofandroid.util.other.FLUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmAsyncTask;

public class HomeActivity extends BaseActivity {


    @BindView(R.id.home_view_pager)
    ViewPager viewPager;

    @BindView(R.id.tab_message_image)
    ImageView messageImage;

    @BindView(R.id.tab_message_text)
    TextView messageText;

    @BindView(R.id.tab_contact_image)
    ImageView contactImage;

    @BindView(R.id.tab_contact_text)
    TextView contactText;

    @BindView(R.id.tab_me_image)
    ImageView meImage;

    @BindView(R.id.tab_me_text)
    TextView meText;

    @OnClick(R.id.llmessage)
    void messageClick(){

        tabItemClick(0);
    }

    @OnClick(R.id.llcontact)
    void  contactClick(){

        tabItemClick(1);
    }

    @OnClick(R.id.llme)
    void meClick(){
        tabItemClick(2);
    }


    List<Fragment> fragments = new ArrayList<>();
    List<ImageView> imageViews = new ArrayList<>();
    List<TextView> textViews = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        super.initView();


        setTitle("FoxChat");
        hasBack(false);

        imageViews.add(messageImage);
        imageViews.add(contactImage);
        imageViews.add(meImage);

        textViews.add(messageText);
        textViews.add(contactText);
        textViews.add(meText);

        fragments.add(new HomeMessageFragment());
        fragments.add(new HomeContactFrament());
        fragments.add(new HomeMeFragment());

        tabItemClick(0);

        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                viewPagerSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void viewPagerSelected(int position){

        tabItemClick(position);

    }

    private void tabItemClick(int position) {

        String [] titles = {"FoxChat", "联系人", "我"};
        setTitle(titles[position]);
        viewPager.setCurrentItem(position, false);
        for(ImageView imageView : imageViews) {

            imageView.setSelected(false);
        }
        for (TextView textView : textViews) {
            textView.setSelected(false);
        }

        ImageView seletedImage = imageViews.get(position);
        seletedImage.setSelected(true);

        TextView seletedTextView = textViews.get(position);
        seletedTextView.setSelected(true);
    }
}
