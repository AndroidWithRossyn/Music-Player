package com.demo.music.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;


public class VerticalSeekBarWrapper extends FrameLayout {
    private static int toRotationAngleToDegrees(int i) {
        if (i != 90) {
            return i != 270 ? 0 : -90;
        }
        return 90;
    }

    public VerticalSeekBarWrapper(Context context) {
        this(context, null, 0);
    }

    public VerticalSeekBarWrapper(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VerticalSeekBarWrapper(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override 
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        if (useViewRotation()) {
            onSizeChangedUseViewRotation(i, i2, i3, i4);
        } else {
            onSizeChangedTraditionalRotation(i, i2, i3, i4);
        }
    }

    private void onSizeChangedTraditionalRotation(int i, int i2, int i3, int i4) {
        VerticalSeekBar childSeekBar = getChildSeekBar();
        if (childSeekBar != null) {
            LayoutParams layoutParams = (LayoutParams) childSeekBar.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = i2;
            childSeekBar.setLayoutParams(layoutParams);
            childSeekBar.measure(0, 0);
            int measuredWidth = childSeekBar.getMeasuredWidth();
            childSeekBar.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(i2, 1073741824));
            layoutParams.gravity = 53;
            layoutParams.leftMargin = (i - measuredWidth) / 2;
            childSeekBar.setLayoutParams(layoutParams);
        }
        super.onSizeChanged(i, i2, i3, i4);
    }

    private void onSizeChangedUseViewRotation(int i, int i2, int i3, int i4) {
        VerticalSeekBar childSeekBar = getChildSeekBar();
        if (childSeekBar != null) {
            childSeekBar.measure(MeasureSpec.makeMeasureSpec(i2, 1073741824), MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE));
        }
        applyViewRotation(i, i2);
        super.onSizeChanged(i, i2, i3, i4);
    }

    @Override 
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        VerticalSeekBar childSeekBar = getChildSeekBar();
        int mode = MeasureSpec.getMode(i);
        if (childSeekBar == null || mode == 1073741824) {
            super.onMeasure(i, i2);
            return;
        }
        if (useViewRotation()) {
            childSeekBar.measure(i2, i);
            i3 = childSeekBar.getMeasuredHeight();
            i4 = childSeekBar.getMeasuredWidth();
        } else {
            childSeekBar.measure(i, i2);
            i3 = childSeekBar.getMeasuredWidth();
            i4 = childSeekBar.getMeasuredHeight();
        }
        setMeasuredDimension(ViewCompat.resolveSizeAndState(i3, i, 0), ViewCompat.resolveSizeAndState(i4, i2, 0));
    }

    
    public void applyViewRotation() {
        applyViewRotation(getWidth(), getHeight());
    }

    private void applyViewRotation(int i, int i2) {
        VerticalSeekBar childSeekBar = getChildSeekBar();
        if (childSeekBar != null) {
            ViewGroup.LayoutParams layoutParams = childSeekBar.getLayoutParams();
            int rotationAngle = childSeekBar.getRotationAngle();
            int paddingTop = childSeekBar.getPaddingTop();
            int paddingBottom = childSeekBar.getPaddingBottom();
            layoutParams.width = i2;
            layoutParams.height = -2;
            childSeekBar.setLayoutParams(layoutParams);
            ViewCompat.setRotation(childSeekBar, toRotationAngleToDegrees(rotationAngle));
            ViewCompat.setTranslationX(childSeekBar, (-(i2 - i)) * 0.5f);
            ViewCompat.setTranslationY(childSeekBar, Math.max(0.0f, (i2 - Math.max(i, (paddingTop + paddingBottom) + (childSeekBar.getThumbOffset() * 2))) * 0.5f));
        }
    }

    private VerticalSeekBar getChildSeekBar() {
        View childAt = getChildCount() > 0 ? getChildAt(0) : null;
        if (childAt instanceof VerticalSeekBar) {
            return (VerticalSeekBar) childAt;
        }
        return null;
    }

    private boolean useViewRotation() {
        VerticalSeekBar childSeekBar = getChildSeekBar();
        if (childSeekBar != null) {
            return childSeekBar.useViewRotation();
        }
        return false;
    }
}
