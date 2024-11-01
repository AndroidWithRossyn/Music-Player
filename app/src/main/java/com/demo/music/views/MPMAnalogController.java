package com.demo.music.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.demo.music.R;

import com.demo.music.fragments.MPMEqualizerFragment;


public class MPMAnalogController extends View {
    public String angle;
    public Paint circlePaint;
    public Paint circlePaint2;
    public ImageView circleone;
    public float currdeg;
    public float deg = 3.0f;
    public float downdeg;
    public String label;
    public int lineColor;
    public Paint linePaint;
    public onProgressChangedListener mListener;
    public float midx;
    public float midy;
    public int progressColor;
    public Paint textPaint;

    
    public interface onProgressChangedListener {
        void onProgressChanged(int i);
    }

    public void setOnProgressChangedListener(onProgressChangedListener onprogresschangedlistener) {
        this.mListener = onprogresschangedlistener;
    }

    public MPMAnalogController(Context context) {
        super(context);
        init();
    }

    public MPMAnalogController(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public MPMAnalogController(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    void init() {
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.parseColor("#000000"));
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen._12ssp));
        this.textPaint.setFakeBoldText(true);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.circlePaint = new Paint();
        this.circlePaint.setColor(Color.parseColor("#d9d9d9"));
        this.circlePaint.setStyle(Paint.Style.FILL);
        this.circlePaint2 = new Paint();
        this.circlePaint2.setStyle(Paint.Style.FILL);
        this.linePaint = new Paint();
        this.linePaint.setColor(MPMEqualizerFragment.themeColor);
        this.linePaint.setStrokeWidth(7.0f);
        this.angle = "0.0";
        this.label = "Label";
    }

    @Override 
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.midx = canvas.getWidth() / 2;
        this.midy = canvas.getHeight() / 2;
        int min = (int) (Math.min(this.midx, this.midy) * 0.90625f);
        float max = Math.max(3.0f, this.deg);
        float min2 = Math.min(this.deg, 21.0f);
        for (int i = (int) max; i < 22; i++) {
            float f = this.midx;
            double d = (1.0d - (i / 24.0f)) * 6.283185307179586d;
            Math.sin(d);
            float f2 = this.midy;
            Math.cos(d);
            this.circlePaint.setColor(Color.parseColor("#111111"));
        }
        int i2 = 3;
        while (true) {
            float f3 = i2;
            if (f3 <= min2) {
                float f4 = this.midx;
                double d2 = (1.0d - (f3 / 24.0f)) * 6.283185307179586d;
                Math.sin(d2);
                float f5 = this.midy;
                Math.cos(d2);
                i2++;
            } else {
                float f6 = min;
                double d3 = 0.4f * f6;
                double d4 = (1.0d - (this.deg / 24.0f)) * 6.283185307179586d;
                float sin = ((float) (Math.sin(d4) * d3)) + this.midx;
                float cos = this.midy + ((float) (d3 * Math.cos(d4)));
                float f7 = 0.6f * f6;
                double d5 = f7;
                float sin2 = this.midx + ((float) (Math.sin(d4) * d5));
                float f8 = this.midy;
                this.circlePaint.setColor(Color.parseColor("#f6f6f6"));
                canvas.drawCircle(this.midx, this.midy, f6 * 0.8666667f, this.circlePaint);
                this.circlePaint.setColor(Color.parseColor("#d9d9d9"));
                canvas.drawCircle(this.midx, this.midy, f7, this.circlePaint);
                canvas.drawText(this.label, this.midx, this.midy + ((float) (min * 1.15d)), this.textPaint);
                canvas.drawLine(sin, cos, sin2, ((float) (d5 * Math.cos(d4))) + f8, this.linePaint);
                return;
            }
        }
    }

    @Override 
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mListener.onProgressChanged((int) (this.deg - 2.0f));
        if (motionEvent.getAction() == 0) {
            this.downdeg = (float) ((Math.atan2(motionEvent.getY() - this.midy, motionEvent.getX() - this.midx) * 180.0d) / 3.141592653589793d);
            this.downdeg -= 90.0f;
            float f = this.downdeg;
            if (f < 0.0f) {
                this.downdeg = f + 360.0f;
            }
            this.downdeg = (float) Math.floor(this.downdeg / 15.0f);
            return true;
        } else if (motionEvent.getAction() != 2) {
            return motionEvent.getAction() == 1 || super.onTouchEvent(motionEvent);
        } else {
            this.currdeg = (float) ((Math.atan2(motionEvent.getY() - this.midy, motionEvent.getX() - this.midx) * 180.0d) / 3.141592653589793d);
            this.currdeg -= 90.0f;
            float f2 = this.currdeg;
            if (f2 < 0.0f) {
                this.currdeg = f2 + 360.0f;
            }
            this.currdeg = (float) Math.floor(this.currdeg / 15.0f);
            if (this.currdeg == 0.0f && this.downdeg == 23.0f) {
                this.deg += 1.0f;
                if (this.deg > 21.0f) {
                    this.deg = 21.0f;
                }
                this.downdeg = this.currdeg;
            } else if (this.currdeg == 23.0f && this.downdeg == 0.0f) {
                this.deg -= 1.0f;
                if (this.deg < 3.0f) {
                    this.deg = 3.0f;
                }
                this.downdeg = this.currdeg;
            } else {
                this.deg += this.currdeg - this.downdeg;
                if (this.deg > 21.0f) {
                    this.deg = 21.0f;
                }
                if (this.deg < 3.0f) {
                    this.deg = 3.0f;
                }
                this.downdeg = this.currdeg;
            }
            this.angle = String.valueOf(this.deg);
            invalidate();
            return true;
        }
    }

    public int getProgress() {
        return (int) (this.deg - 2.0f);
    }

    public void setProgress(int i) {
        this.deg = i + 2;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String str) {
        this.label = str;
    }

    public int getLineColor() {
        return this.lineColor;
    }

    public void setLineColor(int i) {
        this.lineColor = i;
    }

    public int getProgressColor() {
        return this.progressColor;
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
    }
}
