package kesari.com.kesarie_healthmonitoring;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by kesari on 05/11/16.
 */
public class MyApplication extends Application
{

    private static MyApplication mInstance;
    private RequestQueue requestQueue;
    public final static String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }

    public <T> void addRequestToQueue(Request<T> requestQueue, String tag) {
        requestQueue.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(requestQueue);


    }

    public <T> void addRequestToQueue(Request<T> requestQueue) {
        requestQueue.setTag(TAG);
        getRequestQueue().add(requestQueue);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

}
