package com.demo.music.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;


@Metadata(bv = {1, 0, 3}, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0002\b\n\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u0007\b\u0016¢\u0006\u0002\u0010\u0004B\u0017\b\u0016\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ%\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00028\u00002\u0006\u0010\u0014\u001a\u00020\u0015H\u0016¢\u0006\u0002\u0010\u0016J5\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00028\u00002\u0006\u0010\u0019\u001a\u00020\u00022\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001bH\u0016¢\u0006\u0002\u0010\u001dJE\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00028\u00002\u0006\u0010\u0019\u001a\u00020\u00022\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020!H\u0016¢\u0006\u0002\u0010&J=\u0010'\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00028\u00002\u0006\u0010(\u001a\u00020\u00022\u0006\u0010\u0019\u001a\u00020\u00022\u0006\u0010)\u001a\u00020!2\u0006\u0010%\u001a\u00020!H\u0016¢\u0006\u0002\u0010*J-\u0010+\u001a\u00020\u001f2\u0006\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00028\u00002\u0006\u0010\u0019\u001a\u00020\u00022\u0006\u0010%\u001a\u00020!H\u0016¢\u0006\u0002\u0010,J%\u0010-\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00028\u00002\u0006\u0010\u0014\u001a\u00020\u0015H\u0016¢\u0006\u0002\u0010\u0016R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f¨\u0006."}, d2 = {"Lcomm/mucply/aladioply/utils/LockableBottomSheetBehavior;", "V", "Landroid/view/View;", "Lcom/google/android/material/bottomsheet/BottomSheetBehavior;", "()V", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "swipeEnabled", "", "getSwipeEnabled", "()Z", "setSwipeEnabled", "(Z)V", "onInterceptTouchEvent", "parent", "Landroidx/coordinatorlayout/widget/CoordinatorLayout;", "child", NotificationCompat.CATEGORY_EVENT, "Landroid/view/MotionEvent;", "(Landroidx/coordinatorlayout/widget/CoordinatorLayout;Landroid/view/View;Landroid/view/MotionEvent;)Z", "onNestedPreFling", "coordinatorLayout", "target", "velocityX", "", "velocityY", "(Landroidx/coordinatorlayout/widget/CoordinatorLayout;Landroid/view/View;Landroid/view/View;FF)Z", "onNestedPreScroll", "", "dx", "", "dy", "consumed", "", "type", "(Landroidx/coordinatorlayout/widget/CoordinatorLayout;Landroid/view/View;Landroid/view/View;II[II)V", "onStartNestedScroll", "directTargetChild", "axes", "(Landroidx/coordinatorlayout/widget/CoordinatorLayout;Landroid/view/View;Landroid/view/View;Landroid/view/View;II)Z", "onStopNestedScroll", "(Landroidx/coordinatorlayout/widget/CoordinatorLayout;Landroid/view/View;Landroid/view/View;I)V", "onTouchEvent", "app_release"}, k = 1, mv = {1, 1, 16})

public final class LockableBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {
    private boolean swipeEnabled = true;

    public LockableBottomSheetBehavior() {
    }

    
    public LockableBottomSheetBehavior(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(attrs, "attrs");
    }

    public final boolean getSwipeEnabled() {
        return this.swipeEnabled;
    }

    public final void setSwipeEnabled(boolean z) {
        this.swipeEnabled = z;
    }

    @Override 
    public boolean onInterceptTouchEvent(@NotNull CoordinatorLayout parent, @NotNull V child, @NotNull MotionEvent event) {
        Intrinsics.checkParameterIsNotNull(parent, "parent");
        Intrinsics.checkParameterIsNotNull(child, "child");
        Intrinsics.checkParameterIsNotNull(event, "event");
        if (this.swipeEnabled) {
            return super.onInterceptTouchEvent(parent, child, event);
        }
        return false;
    }

    @Override 
    public boolean onTouchEvent(@NotNull CoordinatorLayout parent, @NotNull V child, @NotNull MotionEvent event) {
        Intrinsics.checkParameterIsNotNull(parent, "parent");
        Intrinsics.checkParameterIsNotNull(child, "child");
        Intrinsics.checkParameterIsNotNull(event, "event");
        if (this.swipeEnabled) {
            return super.onTouchEvent(parent, child, event);
        }
        return false;
    }

    @Override 
    public boolean onStartNestedScroll(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View directTargetChild, @NotNull View target, int i, int i2) {
        Intrinsics.checkParameterIsNotNull(coordinatorLayout, "coordinatorLayout");
        Intrinsics.checkParameterIsNotNull(child, "child");
        Intrinsics.checkParameterIsNotNull(directTargetChild, "directTargetChild");
        Intrinsics.checkParameterIsNotNull(target, "target");
        if (this.swipeEnabled) {
            return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, i, i2);
        }
        return false;
    }

    @Override 
    public void onNestedPreScroll(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View target, int i, int i2, @NotNull int[] consumed, int i3) {
        Intrinsics.checkParameterIsNotNull(coordinatorLayout, "coordinatorLayout");
        Intrinsics.checkParameterIsNotNull(child, "child");
        Intrinsics.checkParameterIsNotNull(target, "target");
        Intrinsics.checkParameterIsNotNull(consumed, "consumed");
        if (this.swipeEnabled) {
            super.onNestedPreScroll(coordinatorLayout, child, target, i, i2, consumed, i3);
        }
    }

    @Override 
    public void onStopNestedScroll(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View target, int i) {
        Intrinsics.checkParameterIsNotNull(coordinatorLayout, "coordinatorLayout");
        Intrinsics.checkParameterIsNotNull(child, "child");
        Intrinsics.checkParameterIsNotNull(target, "target");
        if (this.swipeEnabled) {
            super.onStopNestedScroll(coordinatorLayout, child, target, i);
        }
    }

    @Override 
    public boolean onNestedPreFling(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View target, float f, float f2) {
        Intrinsics.checkParameterIsNotNull(coordinatorLayout, "coordinatorLayout");
        Intrinsics.checkParameterIsNotNull(child, "child");
        Intrinsics.checkParameterIsNotNull(target, "target");
        if (this.swipeEnabled) {
            return super.onNestedPreFling(coordinatorLayout, child, target, f, f2);
        }
        return false;
    }
}
