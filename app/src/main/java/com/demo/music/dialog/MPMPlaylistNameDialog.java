package com.demo.music.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.demo.music.R;

import com.demo.music.interfaces.MPMPlaylistDialogListener;


public class MPMPlaylistNameDialog {
    View cancel;
    Context context;
    Dialog dialog;
    MPMPlaylistDialogListener dialogListener;
    View done;
    EditText editText;

    public MPMPlaylistNameDialog(final Context context, String str, final MPMPlaylistDialogListener mPMPlaylistDialogListener) {
        this.context = context;
        this.dialogListener = mPMPlaylistDialogListener;
        this.dialog = new Dialog(context, R.style.AlertDialogCustom);
        this.dialog.setCancelable(true);
        this.dialog.setCanceledOnTouchOutside(true);
        this.dialog.setContentView(R.layout.dialog_playlist);
        this.dialog.getWindow().setBackgroundDrawableResource(17170445);
        this.dialog.getWindow().getAttributes().dimAmount = 0.7f;
        this.dialog.getWindow().addFlags(2);
        this.editText = (EditText) this.dialog.findViewById(R.id.name);
        this.done = this.dialog.findViewById(R.id.create);
        this.cancel = this.dialog.findViewById(R.id.cancel);
        if (!TextUtils.isEmpty(str)) {
            this.editText.setText(str);
        }
        this.done.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                if (!TextUtils.isEmpty(MPMPlaylistNameDialog.this.editText.getText())) {
                    mPMPlaylistDialogListener.onSaveClicked(MPMPlaylistNameDialog.this.editText.getText().toString());
                } else {
                    Toast.makeText(context, "Enter name for playlist!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.cancel.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                mPMPlaylistDialogListener.onCancelClicked();
            }
        });
    }

    public void show() {
        this.dialog.show();
    }

    public void dismiss() {
        this.dialog.dismiss();
    }

    public boolean isShowing() {
        return this.dialog.isShowing();
    }
}
