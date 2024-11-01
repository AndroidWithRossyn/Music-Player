package com.demo.music.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.SeekBar;

import androidx.core.view.ViewCompat;

import com.demo.music.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class VerticalSeekBar extends SeekBar {
    public static final int ROTATION_ANGLE_CW_270 = 270;
    public static final int ROTATION_ANGLE_CW_90 = 90;
    private boolean mIsDragging;
    private Method mMethodSetProgress;
    private int mRotationAngle = 90;
    private Drawable mThumb_;

    private static boolean isValidRotationAngle(int i) {
        return i == 90 || i == 270;
    }

    public VerticalSeekBar(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context, attributeSet, 0, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context, attributeSet, i, 0);
    }

    private void initialize(Context context, AttributeSet attributeSet, int i, int i2) {
        ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.VerticalSeekBar, i, i2);
            int integer = obtainStyledAttributes.getInteger(R.styleable.VerticalSeekBar_seekBarRotation, 0);
            if (isValidRotationAngle(integer)) {
                this.mRotationAngle = integer;
            }
            obtainStyledAttributes.recycle();
        }
    }

    @Override 
    public void setThumb(Drawable drawable) {
        this.mThumb_ = drawable;
        super.setThumb(drawable);
    }

    @Override 
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (useViewRotation()) {
            return onTouchEventUseViewRotation(motionEvent);
        }
        return onTouchEventTraditionalRotation(motionEvent);
    }

    private boolean onTouchEventTraditionalRotation(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        switch (motionEvent.getAction()) {
            case 0:
                setPressed(true);
                onStartTrackingTouch();
                trackTouchEvent(motionEvent);
                attemptClaimDrag(true);
                invalidate();
                break;
            case 1:
                if (this.mIsDragging) {
                    trackTouchEvent(motionEvent);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    onStartTrackingTouch();
                    trackTouchEvent(motionEvent);
                    onStopTrackingTouch();
                    attemptClaimDrag(false);
                }
                invalidate();
                break;
            case 2:
                if (this.mIsDragging) {
                    trackTouchEvent(motionEvent);
                    break;
                }
                break;
            case 3:
                if (this.mIsDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate();
                break;
        }
        return true;
    }

    private boolean onTouchEventUseViewRotation(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                attemptClaimDrag(true);
                break;
            case 1:
                attemptClaimDrag(false);
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

    private void trackTouchEvent(MotionEvent motionEvent) {
        int paddingLeft = super.getPaddingLeft();
        int paddingRight = super.getPaddingRight();
        int height = getHeight() - paddingLeft;
        int i = height - paddingRight;
        int y = (int) motionEvent.getY();
        int i2 = this.mRotationAngle;
        float f = 0.0f;
        float f2 = i2 != 90 ? i2 != 270 ? 0.0f : height - y : y - paddingLeft;
        if (f2 >= 0.0f && i != 0) {
            float f3 = i;
            f = f2 > f3 ? 1.0f : f2 / f3;
        }
        setProgress((int) (f * getMax()), true);
    }

    private void attemptClaimDrag(boolean z) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(z);
        }
    }

    private void onStartTrackingTouch() {
        this.mIsDragging = true;
    }

    private void onStopTrackingTouch() {
        this.mIsDragging = false;
    }

    @Override 
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int i2;
        if (isEnabled()) {
            boolean z = false;
            switch (i) {
                case 19:
                    i2 = this.mRotationAngle == 270 ? 1 : -1;
                    z = true;
                    break;
                case 20:
                    i2 = this.mRotationAngle == 90 ? 1 : -1;
                    z = true;
                    break;
                case 21:
                case 22:
                    i2 = 0;
                    z = true;
                    break;
                default:
                    i2 = 0;
                    break;
            }
            if (z) {
                int keyProgressIncrement = getKeyProgressIncrement();
                int progress = getProgress() + (i2 * keyProgressIncrement);
                if (progress >= 0 && progress <= getMax()) {
                    setProgress(progress - keyProgressIncrement, true);
                }
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override 
    public synchronized void setProgress(int i) {
        super.setProgress(i);
        if (!useViewRotation()) {
            refreshThumb();
        }
    }

    @Override 
    public synchronized void setProgress(int i, boolean z) {
        if (this.mMethodSetProgress == null) {
            try {
                Method method = getClass().getMethod("setProgress", Integer.TYPE, Boolean.TYPE);
                method.setAccessible(true);
                this.mMethodSetProgress = method;
            } catch (NoSuchMethodException unused) {
            }
        }
        if (this.mMethodSetProgress != null) {
            try {
                this.mMethodSetProgress.invoke(this, Integer.valueOf(i), Boolean.valueOf(z));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException unused2) {
            }
        } else {
            super.setProgress(i);
        }
        refreshThumb();
    }

    @Override 
    protected synchronized void onMeasure(int i, int i2) {
        if (useViewRotation()) {
            super.onMeasure(i, i2);
        } else {
            super.onMeasure(i2, i);
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (!isInEditMode() || layoutParams == null || layoutParams.height < 0) {
                setMeasuredDimension(super.getMeasuredHeight(), super.getMeasuredWidth());
            } else {
                setMeasuredDimension(super.getMeasuredHeight(), layoutParams.height);
            }
        }
    }

    @Override 
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        if (useViewRotation()) {
            super.onSizeChanged(i, i2, i3, i4);
        } else {
            super.onSizeChanged(i2, i, i4, i3);
        }
    }

    @Override 
    protected synchronized void onDraw(Canvas canvas) {
        if (!useViewRotation()) {
            int i = this.mRotationAngle;
            if (i == 90) {
                canvas.rotate(90.0f);
                canvas.translate(0.0f, -super.getWidth());
            } else if (i == 270) {
                canvas.rotate(-90.0f);
                canvas.translate(-super.getHeight(), 0.0f);
            }
        }
        super.onDraw(canvas);
    }

    public int getRotationAngle() {
        return this.mRotationAngle;
    }

    public void setRotationAngle(int i) {
        if (!isValidRotationAngle(i)) {
            throw new IllegalArgumentException("Invalid angle specified :" + i);
        } else if (this.mRotationAngle != i) {
            this.mRotationAngle = i;
            if (useViewRotation()) {
                VerticalSeekBarWrapper wrapper = getWrapper();
                if (wrapper != null) {
                    wrapper.applyViewRotation();
                    return;
                }
                return;
            }
            requestLayout();
        }
    }

    private void refreshThumb() {
        onSizeChanged(super.getWidth(), super.getHeight(), 0, 0);
    }

    
    public boolean useViewRotation() {
        return (Build.VERSION.SDK_INT >= 11) && !isInEditMode();
    }

    private VerticalSeekBarWrapper getWrapper() {
        ViewParent parent = getParent();
        if (parent instanceof VerticalSeekBarWrapper) {
            return (VerticalSeekBarWrapper) parent;
        }
        return null;
    }
}
