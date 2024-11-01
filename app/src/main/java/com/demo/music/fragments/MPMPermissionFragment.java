package com.demo.music.fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.demo.music.R;

import com.demo.music.dialog.MPMPermissionMessageDialogBox;


public class MPMPermissionFragment extends DialogFragment {
    private Context context;
    private OnPermissionResult mOnPermissionResult;
    private String permissionMessage1;
    private String permissionMessage2;
    private String permissionMessage3;
    private String[] permissionString;
    private final int PERMISSION_REQUEST_CODE = 1;
    private boolean permissionGranted = false;
    private final int REQUEST_PERMISSION_SETTING = 2;
    private boolean variable1 = false;
    private boolean variable2 = false;

    
    public interface OnPermissionResult {
        void onFail();

        void onSuccess();
    }

    private void showMessageDialogBefourRequest(String str) {
        final MPMPermissionMessageDialogBox mPMPermissionMessageDialogBox = new MPMPermissionMessageDialogBox(this.context, str);
        mPMPermissionMessageDialogBox.setCancelable(false);
        mPMPermissionMessageDialogBox.getWindow().getDecorView().setBackgroundResource(17170445);
        mPMPermissionMessageDialogBox.getWindow().setDimAmount(0.2f);
        mPMPermissionMessageDialogBox.setOnDialogClickListener(new MPMPermissionMessageDialogBox.onDialogClickListener() { 
            @Override
            
            public void onPermissionClick() {
                MPMPermissionFragment.this.RequestPermission();
                mPMPermissionMessageDialogBox.cancel();
            }

            @Override
            
            public void onCancelClick() {
                if (MPMPermissionFragment.this.mOnPermissionResult != null) {
                    MPMPermissionFragment.this.mOnPermissionResult.onFail();
                }
                mPMPermissionMessageDialogBox.cancel();
                MPMPermissionFragment.this.dismiss();
            }
        });
        mPMPermissionMessageDialogBox.show();
    }

    private void showMessageDialogAfterPermission(String str) {
        final MPMPermissionMessageDialogBox mPMPermissionMessageDialogBox = new MPMPermissionMessageDialogBox(this.context, str);
        mPMPermissionMessageDialogBox.setCancelable(false);
        mPMPermissionMessageDialogBox.getWindow().getDecorView().setBackgroundResource(17170445);
        mPMPermissionMessageDialogBox.getWindow().setDimAmount(0.2f);
        mPMPermissionMessageDialogBox.setOnDialogClickListener(new MPMPermissionMessageDialogBox.onDialogClickListener() { 
            @Override
            
            public void onPermissionClick() {
                MPMPermissionFragment.this.RequestPermission();
                mPMPermissionMessageDialogBox.cancel();
            }

            @Override
            
            public void onCancelClick() {
                if (MPMPermissionFragment.this.mOnPermissionResult != null) {
                    MPMPermissionFragment.this.mOnPermissionResult.onFail();
                }
                mPMPermissionMessageDialogBox.cancel();
                MPMPermissionFragment.this.dismiss();
            }
        });
        mPMPermissionMessageDialogBox.show();
    }

    private void showMessageDialogIfNeverAskChecked(String str) {
        final MPMPermissionMessageDialogBox mPMPermissionMessageDialogBox = new MPMPermissionMessageDialogBox(this.context, str);
        mPMPermissionMessageDialogBox.setCancelable(false);
        mPMPermissionMessageDialogBox.getWindow().getDecorView().setBackgroundResource(17170445);
        mPMPermissionMessageDialogBox.getWindow().setDimAmount(0.2f);
        mPMPermissionMessageDialogBox.setOnDialogClickListener(new MPMPermissionMessageDialogBox.onDialogClickListener() { 
            @Override
            
            public void onPermissionClick() {
                mPMPermissionMessageDialogBox.cancel();
                MPMPermissionFragment.this.dismiss();
            }

            @Override
            
            public void onCancelClick() {
                if (MPMPermissionFragment.this.mOnPermissionResult != null) {
                    MPMPermissionFragment.this.mOnPermissionResult.onFail();
                }
                mPMPermissionMessageDialogBox.cancel();
                MPMPermissionFragment.this.dismiss();
            }
        });
        mPMPermissionMessageDialogBox.show();
        mPMPermissionMessageDialogBox.txtGrantPermissionClick.setText("Ok");
        mPMPermissionMessageDialogBox.txtCancel.setVisibility(View.GONE);
    }

    @Override 
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.permission_fragment, viewGroup, false);
        this.context = inflate.getContext();
        return inflate;
    }

    @Override 
    public void onStart() {
        super.onStart();
        performAfterPermission();
    }

    private void performAfterPermission() {
        if (CheckPermission()) {
            OnPermissionResult onPermissionResult = this.mOnPermissionResult;
            if (onPermissionResult != null) {
                onPermissionResult.onSuccess();
                dismiss();
                return;
            }
            return;
        }
        RequestPermission();
    }

    private boolean CheckPermission() {
        String[] strArr = this.permissionString;
        if (strArr != null) {
            for (String str : strArr) {
                if (ContextCompat.checkSelfPermission(this.context, str) != 0) {
                    return false;
                }
            }
            return true;
        }
        dismiss();
        return true;
    }

    
    public void RequestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(this.permissionString, 1);
        }
    }

    @Override 
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 1) {
            this.permissionGranted = true;
            for (int i2 : iArr) {
                if (i2 != 0) {
                    this.permissionGranted = false;
                }
            }
            if (this.permissionGranted) {
                OnPermissionResult onPermissionResult = this.mOnPermissionResult;
                if (onPermissionResult != null) {
                    onPermissionResult.onSuccess();
                    dismiss();
                }
            } else if (Build.VERSION.SDK_INT >= 23) {
                this.variable2 = false;
                this.variable1 = false;
                String[] strArr2 = this.permissionString;
                for (String str : strArr2) {
                    if (shouldShowRequestPermissionRationale(str)) {
                        this.variable1 = true;
                    } else if (ContextCompat.checkSelfPermission(this.context, str) != 0) {
                        this.variable2 = true;
                    }
                }
                boolean z = this.variable1;
                if (z) {
                    showMessageDialogAfterPermission(this.permissionMessage1);
                } else if (!z && this.variable2) {
                    showMessageDialogIfNeverAskChecked(this.permissionMessage2);
                }
            }
        }
    }

    @Override 
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (CheckPermission()) {
            OnPermissionResult onPermissionResult = this.mOnPermissionResult;
            if (onPermissionResult != null) {
                onPermissionResult.onSuccess();
                return;
            }
            return;
        }
        OnPermissionResult onPermissionResult2 = this.mOnPermissionResult;
        if (onPermissionResult2 != null) {
            onPermissionResult2.onFail();
        }
    }

    public MPMPermissionFragment CheckForPermission(@NonNull String[] strArr, @NonNull String str, @NonNull String str2, @NonNull String str3, @NonNull OnPermissionResult onPermissionResult) {
        this.mOnPermissionResult = onPermissionResult;
        this.permissionString = strArr;
        this.permissionMessage1 = str;
        if (str2 == null) {
            this.permissionMessage2 = "Grant the Permissions Inside Your MPMApp setting.";
        } else {
            this.permissionMessage2 = str2;
        }
        this.permissionMessage3 = str3;
        return this;
    }

    public MPMPermissionFragment CheckForPermission(@NonNull String[] strArr, @NonNull String str, @NonNull String str2, @NonNull OnPermissionResult onPermissionResult) {
        this.mOnPermissionResult = onPermissionResult;
        this.permissionString = strArr;
        this.permissionMessage1 = str;
        if (str2 == null) {
            this.permissionMessage2 = "Grant the Permissions Inside Your MPMApp setting.";
        } else {
            this.permissionMessage2 = str2;
        }
        this.permissionMessage2 = str2;
        this.permissionMessage3 = str;
        return this;
    }

    public MPMPermissionFragment CheckForPermission(@NonNull String[] strArr, @NonNull String str, @NonNull OnPermissionResult onPermissionResult) {
        this.mOnPermissionResult = onPermissionResult;
        this.permissionString = strArr;
        this.permissionMessage1 = str;
        this.permissionMessage2 = "Grant the Permissions Inside Your MPMApp setting.";
        this.permissionMessage3 = str;
        return this;
    }

    @Override 
    public void show(FragmentManager fragmentManager, String str) {
        super.show(fragmentManager, str);
    }
}
