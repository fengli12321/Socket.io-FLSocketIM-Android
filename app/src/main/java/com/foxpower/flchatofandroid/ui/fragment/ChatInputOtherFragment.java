package com.foxpower.flchatofandroid.ui.fragment;

import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.common.BaseFragment;

import butterknife.OnClick;

/**
 * Created by fengli on 2018/3/1.
 */

public class ChatInputOtherFragment extends BaseFragment {

    private AddItemClickCallBack clickCallBack;

    @OnClick(R.id.msg_add_photo)
    void addPhotoClick(){
        if (clickCallBack != null) {

            clickCallBack.clickItemIndex(0);
        }
    }

    @OnClick(R.id.msg_add_camera)
    void addCameraClick() {
        if (clickCallBack != null) {

            clickCallBack.clickItemIndex(1);
        }
    }

    @OnClick(R.id.msg_add_location)
    void addLocationClick() {
        if (clickCallBack != null) {

            clickCallBack.clickItemIndex(2);
        }
    }

    @OnClick(R.id.msg_add_video)
    void addVideoClick(){
        if (clickCallBack != null) {

            clickCallBack.clickItemIndex(3);
        }
    }

    public void setClickCallBack(AddItemClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    @Override
    protected int getLayoutId() {

        return R.layout.fragment_chat_add;
    }

    public interface AddItemClickCallBack{

        public void clickItemIndex(int index);
    }
}
