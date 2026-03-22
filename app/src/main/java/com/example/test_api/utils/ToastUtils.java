package com.example.test_api.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.test_api.R;

public class ToastUtils {

    public static void show(Context context, String message, boolean isSuccess) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_custom, null);

        ImageView icon = layout.findViewById(R.id.toast_icon);
        TextView text = layout.findViewById(R.id.toast_text);

        icon.setImageResource(isSuccess ? R.drawable.ic_toast_icon : R.drawable.ic_error_outline);
        text.setText(message);

        Toast toast = new Toast(context);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public static void success(Context context, String message) {
        show(context, message, true);
    }

    public static void error(Context context, String message) {
        show(context, message, false);
    }
}