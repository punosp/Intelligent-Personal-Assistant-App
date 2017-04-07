package everytasc.nineleaps.com.hackaton.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import everytasc.nineleaps.com.hackaton.MyApplication;

/**
 * Created by BURNI on 15/12/15.
 */
public class VolleySingeltonUniv {
    private static VolleySingeltonUniv mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    boolean secure_request = false;

    private String Log_TAG = "VolleySingelton";

    private VolleySingeltonUniv() {
        mCtx = MyApplication.getAppContext();
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleySingeltonUniv getInstance() {
        if (mInstance == null) {
            mInstance = new VolleySingeltonUniv();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        //req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);

    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }


    //------------------- PUBLIC METHOD FOR ACTIVITIES TO USE NETWORK  ----------

    public void getJsonStringRequest(String url){

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, response.toString());
                if(volleyGetStringReqListener!=null){
                    volleyGetStringReqListener.onResponse(response);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                if(volleyGetStringReqListener!=null){
                    volleyGetStringReqListener.onErrorResponse(error);
                }
            }
        });

        getRequestQueue().add(strReq);
    }

    public void getJsonObjectRequest(String url){

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(volleyGetJSONObjReqListener!=null){
                            volleyGetJSONObjReqListener.onResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                if(volleyGetJSONObjReqListener!=null){
                    volleyGetJSONObjReqListener.onErrorResponse(error);
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = getSecureHeader();
                return params;
            }

        };
    }

    public void getJsonArrayRequest(String url){
        //String url = Constants.MainURL + "?barcode=" + barcode;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                if (volleyGetJSONArrayReqListener != null) {
                    volleyGetJSONArrayReqListener.onResponse(jsonArray);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyGetJSONArrayReqListener != null) {
                    volleyGetJSONArrayReqListener.onErrorResponse(volleyError);
                }
            }
        });

        getRequestQueue().add(jsonArrayRequest);
    }

    public void getSecureJsonArrayRequest(String url){

        //String url = Constants.MainURL + "?barcode=" + barcode;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {

                try{
                    String status = String.valueOf(response.get("status"));
                    if(status.equalsIgnoreCase("success")){
                        JSONArray jsonArrayData = (JSONArray) response.get("data");
                        if (volleyGetJSONArrayReqListener != null) {
                            volleyGetJSONArrayReqListener.onResponse(jsonArrayData);
                        }

                    }else if (status.equals("error")){
                        String message = String.valueOf(response.get("message"));
                        Log.e(Log_TAG, "Error in request/no header: "+message);
                    }else{
                        //DO SOMETHING
                    }
                }catch(Exception e){
                    Log.e(Log_TAG, "Error/exception in JSON response: "+ String.valueOf(e));
                }


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                if (volleyGetJSONArrayReqListener != null) {
                    volleyGetJSONArrayReqListener.onErrorResponse(error);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = getSecureHeader();
                return headers;
            }
        };
        getRequestQueue().add(jsonObjectRequest);
    }

//-------------- POST METHODS -------------------------------

    public void postJsonStringRequest(String url,final HashMap<String ,String> dataParam){

        Log.i("DataParam",dataParam.toString());

        StringRequest strReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String str) {
                        //Log.d(Log_TAG, str);
                        if(volleyPostStringReqListener!=null){
                            volleyPostStringReqListener.onResponse(str);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(Log_TAG, "Error: " + error.getMessage());
                if(volleyPostStringReqListener!=null){
                    volleyPostStringReqListener.onErrorResponse(error);
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = dataParam;
                Log.i("TAG",String.valueOf(params));
                return params;
            }

            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = getSecureHeader();
                return headers;
            }*/
        };

        getRequestQueue().add(strReq);

    }

    public void postJsonObjectRequest(String url , final HashMap<String, String> param){

        JSONObject obj = new JSONObject(param);
        //String url = Constants.PostURL;
        Log.d("URL",url);
        Log.i("OBJECT",obj.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,url, obj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(Log_TAG, response.toString());
                        if(volleyPostJSONObjReqListener!=null){
                            volleyPostJSONObjReqListener.onResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(Log_TAG, "Error: " + error.getMessage());
                if(volleyPostJSONObjReqListener!=null){
                    volleyPostJSONObjReqListener.onErrorResponse(error);
                }
            }
        });
        getRequestQueue().add(jsonObjReq);
    }

    public void postJsonArrayRequest(String url , final HashMap<String, String> param){

        JsonArrayRequest req = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, response.toString());
                        if(volleyPostJSONArrayReqListener!=null){
                            volleyPostJSONArrayReqListener.onResponse_VolleyPostJSONArrayReqListener(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                if(volleyPostJSONArrayReqListener!=null){
                    volleyPostJSONArrayReqListener.onErrorResponse_VolleyPostJSONArrayReqListener(error);
                }
            }
        });
    }


    //---------------------------------------------------------------------------------------------


    private HashMap getSecureHeader(){

        HashMap<String,String> headers = new HashMap();
        long ts = System.currentTimeMillis()/1000;
        String timeStamp = String.valueOf(ts);
        Log.w(Log_TAG,"X-Time Stamp:"+ timeStamp);
        headers.put("X-Timestamp", timeStamp);
        String signature ="";//sha256(Constants.signature.concat(timeStamp));
        Log.w(Log_TAG, "X-Signature: " + signature);
        headers.put("X-Signature", signature);
        return headers;
    }

    //-------------------------------------- Hashing Algorithm -------------------------

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }


    //######################################### INTERFACE ##########################################

    public interface VolleyGetStringReqListener{
        void onResponse(String jsonString);
        void onErrorResponse(VolleyError volleyError);
    }

    public interface VolleyGetJSONObjReqListener{
        void onResponse(JSONObject jsonObject);
        void onErrorResponse(VolleyError volleyError);
    }

    public interface VolleyGetJSONArrayReqListener{
        void onResponse(JSONArray jsonArray);
        void onErrorResponse(VolleyError volleyError);
    }

    public interface VolleyPostStringReqListener{
        void onResponse(String jsonString);
        void onErrorResponse(VolleyError volleyError);
    }

    public interface VolleyPostJSONObjReqListener{
        void onResponse(JSONObject jsonObject);
        void onErrorResponse(VolleyError volleyError);
    }

    public interface VolleyPostJSONArrayReqListener{
        void onResponse_VolleyPostJSONArrayReqListener(JSONArray jsonArray);
        void onErrorResponse_VolleyPostJSONArrayReqListener(VolleyError volleyError);
    }

    private VolleyGetStringReqListener volleyGetStringReqListener;
    private VolleyGetJSONObjReqListener volleyGetJSONObjReqListener;
    private VolleyGetJSONArrayReqListener volleyGetJSONArrayReqListener;

    private VolleyPostStringReqListener volleyPostStringReqListener;
    private VolleyPostJSONObjReqListener volleyPostJSONObjReqListener;
    private VolleyPostJSONArrayReqListener volleyPostJSONArrayReqListener;

    //-------------------------------------

    public void setVolleyGetStringReqListener(VolleyGetStringReqListener volleyGetStringReqListener){
        this.volleyGetStringReqListener = volleyGetStringReqListener;
    }

    public void setVolleyGetJSONObjReqListener(VolleyGetJSONObjReqListener volleyGetJSONObjReqListener){
        this.volleyGetJSONObjReqListener = volleyGetJSONObjReqListener;
    }

    public void setVolleyGetJSONArrayReqListener(VolleyGetJSONArrayReqListener volleyGetJSONArrayReqListener){
        this.volleyGetJSONArrayReqListener = volleyGetJSONArrayReqListener;
    }

    public void setVolleyPostStringReqListener (VolleyPostStringReqListener volleyPostStringReqListener){
        this.volleyPostStringReqListener = volleyPostStringReqListener;
    }

    public void setVolleyPostJSONObjReqListener (VolleyPostJSONObjReqListener volleyPostJSONObjReqListener){
        this.volleyPostJSONObjReqListener = volleyPostJSONObjReqListener;
    }

    public void setVolleyPostJSONArrayReqListener (VolleyPostJSONArrayReqListener volleyPostJSONArrayReqListener){
        this.volleyPostJSONArrayReqListener = volleyPostJSONArrayReqListener;
    }

}
