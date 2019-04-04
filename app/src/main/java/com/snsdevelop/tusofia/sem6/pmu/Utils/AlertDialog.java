package com.snsdevelop.tusofia.sem6.pmu.Utils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.snsdevelop.tusofia.sem6.pmu.R;

public class AlertDialog {

    private final android.app.AlertDialog.Builder builder;

    public AlertDialog(Context ctx) {
        builder = new android.app.AlertDialog.Builder(ctx, R.style.DialogTheme);
    }

    public android.app.AlertDialog.Builder getBuilder() {
        return builder;
    }

    public static void showErrorWithInternetConnection(Context context) {
        showError(context, context.getResources().getString(R.string.modal_internet_error_title), context.getResources().getString(R.string.modal_internet_error_description));
    }

    public static void showError(Context context, String title, String message) {
        ((Activity) context).runOnUiThread(() ->
                new AlertDialog(context).getBuilder()
                        .setTitle(title)
                        .setMessage(message)
                        .setNegativeButton(android.R.string.ok, (dialog, which) -> dialog.cancel())
                        .show());

    }

    public static void styled(Context context, android.app.AlertDialog a) {
        if (a.getWindow() != null) {
            a.getWindow().setLayout(600, 300);

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.paper1);

            Drawable drawable = new BitmapDrawable(Bitmap.createScaledBitmap(bitmap, 600, 300, false));

            a.getWindow().setBackgroundDrawable(drawable);
            a.getWindow().setTitleColor(context.getColor(android.R.color.black));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 0, 20, 20);
            a.setOnShowListener(arg -> {
                Button buttonPositive = a.getButton(DialogInterface.BUTTON_POSITIVE);
                if (buttonPositive != null) {
                    buttonPositive.setLayoutParams(layoutParams);
                    buttonPositive.setBackground(context.getDrawable(R.drawable.button_gray_default));
                }
                Button buttonNeutral = a.getButton(DialogInterface.BUTTON_NEUTRAL);
                if (buttonNeutral != null) {
                    buttonNeutral.setLayoutParams(layoutParams);
                    buttonNeutral.setBackground(context.getDrawable(R.drawable.button_gray_default));
                }
                Button buttonNegative = a.getButton(DialogInterface.BUTTON_NEGATIVE);
                if (buttonNeutral != null) {
                    buttonNegative.setLayoutParams(layoutParams);
                    buttonNegative.setBackground(context.getDrawable(R.drawable.button_gray_default));
                }
            });
        }

        a.show();
    }

}
