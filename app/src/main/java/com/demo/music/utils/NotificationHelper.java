package com.demo.music.utils;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import java.lang.reflect.Method;


public final class NotificationHelper {
    private static final Method MAKE_CONTENT_VIEW_METHOD;

    static {
        Method method = null;
        try {
            method = NotificationCompat.Builder.class.getDeclaredMethod("makeContentView", new Class[0]);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        MAKE_CONTENT_VIEW_METHOD = method;
    }

    private NotificationHelper() {
    }

    public static Notification buildWithBackgroundResource(Context context, NotificationCompat.Builder builder, @DrawableRes int i) {
        if (MAKE_CONTENT_VIEW_METHOD == null) {
            return buildNotification(builder);
        }
        RemoteViews obtainRemoteViews = obtainRemoteViews(builder);
        Notification buildNotification = buildNotification(builder);
        if (obtainRemoteViews != null) {
            obtainRemoteViews.setInt(LayoutInflater.from(context).inflate(obtainRemoteViews.getLayoutId(), (ViewGroup) null).getId(), "setBackgroundResource", i);
        }
        return buildNotification;
    }

    public static Notification buildWithBackgroundColor(Context context, NotificationCompat.Builder builder, @ColorInt int i) {
        if (MAKE_CONTENT_VIEW_METHOD == null) {
            return buildNotification(builder);
        }
        RemoteViews obtainRemoteViews = obtainRemoteViews(builder);
        Notification buildNotification = buildNotification(builder);
        if (obtainRemoteViews != null) {
            View inflate = LayoutInflater.from(context).inflate(obtainRemoteViews.getLayoutId(), (ViewGroup) null);
            obtainRemoteViews.setInt(inflate.getId(), "setBackgroundColor", i);
            applyTextColorToRemoteViews(obtainRemoteViews, inflate, ((((((double) Color.red(i)) * 0.299d) + (((double) Color.green(i)) * 0.587d)) + (((double) Color.blue(i)) * 0.114d)) > 186.0d ? 1 : ((((((double) Color.red(i)) * 0.299d) + (((double) Color.green(i)) * 0.587d)) + (((double) Color.blue(i)) * 0.114d)) == 186.0d ? 0 : -1)) > 0 ? ViewCompat.MEASURED_STATE_MASK : -1);
        }
        return buildNotification;
    }

    private static RemoteViews obtainRemoteViews(NotificationCompat.Builder builder) {
        try {
            RemoteViews remoteViews = (RemoteViews) MAKE_CONTENT_VIEW_METHOD.invoke(builder, new Object[0]);
            builder.setContent(remoteViews);
            return remoteViews;
        } catch (Throwable unused) {
            return null;
        }
    }

    private static Notification buildNotification(NotificationCompat.Builder builder) {
        if (Build.VERSION.SDK_INT >= 16) {
            return builder.build();
        }
        return builder.getNotification();
    }

    private static void applyTextColorToRemoteViews(RemoteViews remoteViews, View view, int i) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                applyTextColorToRemoteViews(remoteViews, viewGroup.getChildAt(i2), i);
            }
        } else if (view instanceof TextView) {
            remoteViews.setTextColor(view.getId(), i);
        }
    }
}
