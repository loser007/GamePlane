package com.loser007.gameplane.game;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.loser007.gameplane.R;

/**
 * Created by dbSven on 2018/6/28.
 */
public class AutoPlayView extends View {


    Drawable mDrawableTop, mDrawableDown,mDrawableNoSee;
    int drawableWidth, drawableHeight;
    int mScreenH,mScreenW;
    ValueAnimator valueAnimator;
    int duration=2000;

    public AutoPlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mScreenH = getScreenHeight(getContext());
        mScreenW = getScreenWidth(getContext());
        mDrawableTop = getResources().getDrawable(R.drawable.bg);
        drawableWidth = getScreenWidth(getContext());
        drawableHeight = mScreenH/2;
        mDrawableTop.setBounds(0, 0, drawableWidth, drawableHeight);

        mDrawableDown = getResources().getDrawable(R.drawable.bg);
        mDrawableDown.setBounds(0, drawableHeight, drawableWidth, mScreenH);

        mDrawableNoSee = getResources().getDrawable(R.drawable.bg);
        mDrawableNoSee.setBounds(0, drawableHeight * 2, drawableWidth, drawableHeight * 3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wdMode = MeasureSpec.getMode(widthMeasureSpec);
        int hdMode = MeasureSpec.getMode(heightMeasureSpec);
        //测量布局大小，默认为屏幕的宽，图片的高
        if (wdMode == MeasureSpec.EXACTLY && hdMode == MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int wd = MeasureSpec.makeMeasureSpec(getScreenWidth(getContext()), MeasureSpec.EXACTLY);
            int hd = MeasureSpec.makeMeasureSpec(drawableHeight, MeasureSpec.EXACTLY);
            setMeasuredDimension(wd, hd);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //思路：左边一张图，右边隐藏一张图，不停的scrollto
        mDrawableTop.draw(canvas);
        mDrawableDown.draw(canvas);
        mDrawableNoSee.draw(canvas);
    }


    public void startPlay() {
        //一张图片的宽的移动距离，即可视觉上达到不停的在滚动
        valueAnimator = ValueAnimator.ofInt(0, drawableHeight);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                scrollTo(0, value);
            }
        });
        valueAnimator.start();
    }

    public void setSudu(int duration){
        this.duration = duration;
    }

    public void stopPlay() {
        if (valueAnimator != null && valueAnimator.isRunning() && valueAnimator.isStarted()) {
            valueAnimator.cancel();
        }
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}