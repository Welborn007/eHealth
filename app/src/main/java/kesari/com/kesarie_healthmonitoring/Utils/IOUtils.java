package kesari.com.kesarie_healthmonitoring.Utils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.nispok.snackbar.listeners.ActionSwipeListener;

import org.json.JSONObject;

import java.util.Map;

import kesari.com.kesarie_healthmonitoring.MyApplication;

/**
 * Created by kesari on 13/04/17.
 */

public class IOUtils {


    // Volley String Get Request
    public void getGETStringRequest(final Context context, String url, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        Log.i("url", url);
        // custom dialog

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response.toString());

                callback.onSuccess(response);

                //dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
                //dialog.dismiss();

                failureCallback.onFailure("");

               /* try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);


                }catch (Exception e)
                {
                    //Log.d("Error", e.getMessage());
                }*/
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(stringRequest, "");
    }

    // Volley String Get Request
    public void getGETStringRequestFilter(final Context context, String url, final VolleyCallback callback, final VolleyFailureCallback volleyFailureCallback) {

        Log.i("url", url);
        // custom dialog

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response.toString());

                callback.onSuccess(response);

                //dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
                //dialog.dismiss();

                try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                    volleyFailureCallback.onFailure("");

                }catch (Exception e)
                {
                    //Log.d("Error", e.getMessage());
                }
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(stringRequest, "");
    }

    public interface VolleyFailureCallback {
        void onFailure(String result);
    }

    // Volley String Get Request with Header
    public void getGETStringRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders , final VolleyCallback callback) {

        //RequestQueue queue = Volley.newRequestQueue(this);
        Log.i("url", url);


        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //Log.d("ERROR","error => "+error.toString());

                        try{
                            String json = null;
                            NetworkResponse response = error.networkResponse;
                            json = new String(response.data);
                            Log.d("Error", json);

                        }catch (Exception e)
                        {
                            //Log.d("Error", e.getMessage());
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(postRequest, "");
    }

    // Volley String Delete Request with Header
    public void getDeleteStringRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders , final VolleyCallback callback) {

        //RequestQueue queue = Volley.newRequestQueue(this);
        Log.i("url", url);

        StringRequest postRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //Log.d("ERROR","error => "+error.toString());

                        try{
                            String json = null;
                            NetworkResponse response = error.networkResponse;
                            json = new String(response.data);
                            Log.d("Error", json);


                        }catch (Exception e)
                        {
                            //Log.d("Error", e.getMessage());
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(postRequest, "");
    }

    // Volley String POST Request with Header
    public void getPOSTStringRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders , final VolleyCallback callback) {

        //RequestQueue queue = Volley.newRequestQueue(this);

        Log.i("url", url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //Log.d("ERROR","error => "+error.toString());

                        try{
                            String json = null;
                            NetworkResponse response = error.networkResponse;
                            json = new String(response.data);
                            Log.d("Error", json);


                        }catch (Exception e)
                        {
                            //Log.d("Error", e.getMessage());
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(postRequest, "");
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    //Volley JSON Object Post Request
    public void sendJSONObjectRequest(final Context context, String url, JSONObject jsonObject, final VolleyCallback callback) {

        Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());

                try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                }catch (Exception e)
                {
                    //Log.d("Error", e.getMessage());
                }
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    //Volley JSON Object Post Request for Dialog
    public void sendJSONObjectRequestHeaderDialog(final Context context, final ViewGroup viewGroup, String url, final Map<String, String> paramsHeaders, JSONObject jsonObject, final VolleyCallback callback) {

        Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());

                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());

                try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                }catch (Exception e)
                {
                    //Log.d("Error", e.getMessage());
                    FireToast.customSnackbarDialog(context, "Oops Something Went Wrong!!", "", viewGroup, new ActionSwipeListener() {
                        @Override
                        public void onSwipeToDismiss() {
                            viewGroup.setVisibility(View.GONE);
                        }
                    });
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };;

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    //Volley JSON Object Post Request
    public void sendJSONObjectRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, JSONObject jsonObject, final VolleyCallback callback) {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        //dialog.dismiss();
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());
                //dialog.dismiss();

                try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                }catch (Exception e)
                {
                    //Log.d("Error", e.getMessage());
                    FireToast.customSnackbar(context, "Oops Something Went Wrong!!", "");
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };;

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    //Volley JSON Object Put Request
    public void sendJSONObjectPutRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, JSONObject jsonObject, final VolleyCallback callback) {

        Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());


                try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                }catch (Exception e)
                {
                    //Log.d("Error", e.getMessage());
                    FireToast.customSnackbar(context, "Oops Something Went Wrong!!", "");
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };;

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    //Volley JSON Object Put Request
    public void sendJSONObjectPutRequest(final Context context, String url, JSONObject jsonObject, final VolleyCallback callback) {

        Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());


                try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                }catch (Exception e)
                {
                    //Log.d("Error", e.getMessage());
                    FireToast.customSnackbar(context, "Oops Something Went Wrong!!", "");
                }
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }


    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
