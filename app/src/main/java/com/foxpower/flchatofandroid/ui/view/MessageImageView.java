package com.foxpower.flchatofandroid.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * Created by fengli on 2018/2/24.
 */

public class MessageImageView extends ImageView {
    public MessageImageView(Context context) {
        super(context);
    }

    public MessageImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private Drawable mask;
    private static final Paint paintMask = createMaskPaint();

    private static Paint createMaskPaint() {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mask != null) {
            int width = getWidth();
            int height = getHeight();

            canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);

            if (mask != null) {
                mask.setBounds(0, 0, width, height);
                mask.draw(canvas);
            }

            {
                canvas.saveLayer(0, 0, width, height, paintMask, Canvas.ALL_SAVE_FLAG);
                super.onDraw(canvas);
                canvas.restore();
            }

            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    public void loadAsResource(final int resId, final int maskId) {
        setBlendDrawable(maskId);
        Glide.with(getContext().getApplicationContext()).load(resId).into(this);
    }

    public void loadAsPath(final String path, final int width, final int height, final int maskId, final String ext) {
        if (TextUtils.isEmpty(path)) {

            return;
        }
        setBlendDrawable(maskId);
        RequestBuilder builder;
        RequestOptions options = new RequestOptions()
                .override(width, height)
                .fitCenter();

        builder = Glide.with(getContext().getApplicationContext()).asBitmap().apply(options).load(new File(path));

        builder.into(this);
    }

    private void setBlendDrawable(int maskId) {
        mask = maskId != 0? getResources().getDrawable(maskId) : null;
    }
}
