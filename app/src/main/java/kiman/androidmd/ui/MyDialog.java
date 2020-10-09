package kiman.androidmd.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;

import kiman.androidmd.R;

class MyDialog extends Dialog{
    public MyDialog(@NonNull Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                setContentView(R.layout.view_pager);
    }
}