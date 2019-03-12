package bg.tusofia.sem6.pmu.myapplication.Utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Toast {

    public static void make(Context ctx, String data) {
        android.widget.Toast toast = android.widget.Toast.makeText(ctx, data, android.widget.Toast.LENGTH_LONG);
        LinearLayout layout = (LinearLayout) toast.getView();
        if (layout.getChildCount() > 0) {
            TextView tv = (TextView) layout.getChildAt(0);
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        }
        toast.show();
    }

}

