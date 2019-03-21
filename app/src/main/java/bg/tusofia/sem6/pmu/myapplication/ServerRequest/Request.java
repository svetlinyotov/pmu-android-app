package bg.tusofia.sem6.pmu.myapplication.ServerRequest;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class Request {

    private final RequestQueue queue;
    private final List<Object> tags = new ArrayList<>();

    public Request(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void send(StringRequest stringRequest) {
        queue.add(stringRequest);
        tags.add(stringRequest.getTag());
    }

    public void stop() {
        for (Object tag : tags) {
            queue.cancelAll(tag);
        }
    }
}
