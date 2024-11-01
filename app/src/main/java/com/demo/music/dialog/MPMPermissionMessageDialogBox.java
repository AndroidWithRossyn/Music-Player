package com.demo.music.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.demo.music.R;


public class MPMPermissionMessageDialogBox extends Dialog {
    private onDialogClickListener mOnDialogClickListener;
    private String message;
    public TextView txtCancel;
    public TextView txtGrantPermissionClick;
    private TextView txtPermissionMessage;

    
    public interface onDialogClickListener {
        void onCancelClick();

        void onPermissionClick();
    }

    public MPMPermissionMessageDialogBox(@NonNull Context context, @NonNull String str) {
        super(context);
        this.message = str;
    }

    @Override 
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.permission_message_dialog);
        this.txtPermissionMessage = (TextView) findViewById(R.id.txt_permission_message);
        this.txtGrantPermissionClick = (TextView) findViewById(R.id.txt_grant_permission_click);
        this.txtCancel = (TextView) findViewById(R.id.txt_cancel);
        this.txtPermissionMessage.setText(this.message);
        this.txtGrantPermissionClick.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                if (MPMPermissionMessageDialogBox.this.mOnDialogClickListener != null) {
                    MPMPermissionMessageDialogBox.this.mOnDialogClickListener.onPermissionClick();
                }
            }
        });
        this.txtCancel.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                if (MPMPermissionMessageDialogBox.this.mOnDialogClickListener != null) {
                    MPMPermissionMessageDialogBox.this.mOnDialogClickListener.onCancelClick();
                }
            }
        });
    }

    public void setOnDialogClickListener(onDialogClickListener ondialogclicklistener) {
        this.mOnDialogClickListener = ondialogclicklistener;
    }
}
