package com.demo.music.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import androidx.viewpager.widget.ViewPager;


public class KKViewPager extends ViewPager implements ViewPager.PageTransformer {
    public static final String TAG = "KKViewPager";
    private float MAX_SCALE;
    private boolean animationEnabled;
    private boolean fadeEnabled;
    private float fadeFactor;
    private int mPageMargin;

    public KKViewPager(Context context) {
        this(context, null);
    }

    public KKViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.MAX_SCALE = 0.0f;
        this.animationEnabled = true;
        this.fadeEnabled = true;
        this.fadeFactor = 0.5f;
        setClipChildren(false);
        setClipToPadding(false);
        setOverScrollMode(2);
        setPageTransformer(false, this);
        setOffscreenPageLimit(3);
        this.mPageMargin = dp2px(context.getResources(), 40);
        int i = this.mPageMargin;
        setPadding(i, i, i, i);
    }

    public int dp2px(Resources resources, int i) {
        return (int) TypedValue.applyDimension(1, i, resources.getDisplayMetrics());
    }

    public void setAnimationEnabled(boolean z) {
        this.animationEnabled = z;
    }

    public void setFadeEnabled(boolean z) {
        this.fadeEnabled = z;
    }

    public void setFadeFactor(float f) {
        this.fadeFactor = f;
    }

    @Override 
    public void setPageMargin(int i) {
        this.mPageMargin = i;
        int i2 = this.mPageMargin;
        setPadding(i2, i2, i2, i2);
    }

    @Override 
    public void transformPage(View view, float f) {
        int i = this.mPageMargin;
        if (i > 0 && this.animationEnabled) {
            view.setPadding(i / 3, i / 3, i / 3, i / 3);
            if (this.MAX_SCALE == 0.0f && f > 0.0f && f < 1.0f) {
                this.MAX_SCALE = f;
            }
            float f2 = f - this.MAX_SCALE;
            float abs = Math.abs(f2);
            if (f2 <= -1.0f || f2 >= 1.0f) {
                if (this.fadeEnabled) {
                    view.setAlpha(this.fadeFactor);
                }
            } else if (f2 == 0.0f) {
                view.setScaleX(this.MAX_SCALE + 1.0f);
                view.setScaleY(this.MAX_SCALE + 1.0f);
                view.setAlpha(1.0f);
            } else {
                float f3 = 1.0f - abs;
                view.setScaleX((this.MAX_SCALE * f3) + 1.0f);
                view.setScaleY((this.MAX_SCALE * f3) + 1.0f);
                if (this.fadeEnabled) {
                    view.setAlpha(Math.max(this.fadeFactor, f3));
                }
            }
        }
    }
}
