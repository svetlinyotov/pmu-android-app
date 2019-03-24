package com.snsdevelop.tusofia.sem6.pmu.Utils;


import android.app.Activity;
import android.content.Context;

import com.snsdevelop.tusofia.sem6.pmu.R;

public class AlertDialog {

    private final android.app.AlertDialog.Builder builder;

    public AlertDialog(Context ctx) {
        builder = new android.app.AlertDialog.Builder(ctx, android.R.style.Theme_Material_Dialog_Alert);
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

}
