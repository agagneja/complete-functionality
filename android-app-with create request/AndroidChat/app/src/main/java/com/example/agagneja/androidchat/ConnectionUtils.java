package com.example.agagneja.androidchat;

/**
 * Created by agagneja on 1/28/2015.
 */

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;

public class ConnectionUtils {

    private static final String API_URL = "https://www.stage2c7161.qa.paypal.com:12450/v1/payments/personal-payments/funding-options";
    private static final String X_PAYPAL_SECURITY_CONTEXT = "{\"scopes\":[\"*\"],\"subjects\":[{\"subject\":{\"id\":\"0\",\"auth_state\":\"LOGGEDIN\",\"account_number\":\"1453948006982829156\",\"auth_claims\":[\"USERNAME\",\"PASSWORD\"]}}],\"actor\":{\"id\":\"0\",\"account_number\":\"1453948006982829156\",\"auth_claims\":[\"USERNAME\",\"PASSWORD\"]}, \"global_session_id\": \"3ilsi34jsi32300Zsdkk23sdlfjlkjsd\"}";
   private static final String  PAYPAL_BODY = "{\"amount\":{\"value\":\"5\",\"currency\":\"USD\" }, \"payee\":{ \"id\":\"cdayanand-pre@paypal.com\", \"type\":\"EMAIL\" }, \"fee\":{ \"payer\":\"PAYER\"},\"payment_type\":\"PERSONAL\"}";
    private final static HttpClient httpClient = getNewHttpClient();
    private static final String CREATE_REQUEST_URL ="https://stage2c7169.qa.paypal.com:12807/v1/payments/money-requests";
    private static final String CREATE_REQUEST_SECURITY = "{\"actor\":{\"account_number\":\"1502059720681642427\",\"id\":\"28\",\"auth_claims\":[\"USERNAME\",\"PASSWORD\"]},\"scopes\":[ \"*\"],\"subjects\":[{\"subject\":{\"account_number\":\"1502059720681642427\",\"auth_claims\":[ \"USERNAME\",\"PASSWORD\"],\"auth_state\":\"LOGGEDIN\"}}]}";
    //added
    private static final String API_URL_FULFILL= "https://www.stage2c7161.qa.paypal.com:12450/v1/payments/personal-payments";
    private static final String REQUEST_FUNDING_SECURITY=" {\"scopes\":[\"*\"],\"subjects\":[{\"subject\":{\"id\":\"0\",\"auth_state\":\"LOGGEDIN\",\"account_number\":\"1541647125402729793\",\"auth_claims\":[\"USERNAME\",\"PASSWORD\"]}}],\"actor\":{\"id\":\"0\",\"account_number\":\"1541647125402729793\",\"auth_claims\":[\"USERNAME\",\"PASSWORD\"]}, \"global_session_id\": \"3ilsi34jsi32300Zsdkk23sdlfjlkjsd\"}";
   // private static final String  PAYPAL_BODY_FULFILL = "{\"funding_option_id\": \"Rf-8vSvKBTCbp1Yup3-k58xE-bdEl8zKcvnYDyhj22pJaciWmsLaPwNwUkkh-Lo4oAT_P0Eh8z9mrkwnk6ox6I10jdm\", \"payer\": { \"shipping_address_id\": \"405113\" }, \"note_to_payee\": \"Hello world!\" }";

    public static HttpClient getNewHttpClient() {
    try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);

        } catch (Exception e) {
            return new DefaultHttpClient();
        }

    }

    public static JSONObject fundingOptionsRequest(String bodyFundingRequest) {
        String postPath = API_URL;
        HttpPost httpPost = new HttpPost(postPath);
        Log.d("PATH", postPath);
        HttpResponse httpResponse;
        JSONObject response = null;
        try {
            if(bodyFundingRequest.equals(PAYPAL_BODY))
            {
                Log.d("They are equal", "EQUAL");
            }
            else
            {
                Log.d("not equal","not equal");
                Log.d("made by me",bodyFundingRequest);
                Log.d("made by em",PAYPAL_BODY);

            }
            httpPost.setEntity(new StringEntity(bodyFundingRequest));
            Log.d("BODY_Connection",bodyFundingRequest);
            httpPost.setHeader("X-PAYPAL-SECURITY-CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            Log.d("SECURITY_CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {

            try {
                response = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            try {
                Log.e("FAILED RESPONSE", EntityUtils.toString(httpResponse.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return response;
    }
    //added
    public static JSONObject fulfillmentOptionsRequest(String uid) {
        String postPath = API_URL_FULFILL;
        HttpPost httpPost = new HttpPost(postPath);
        Log.d("PATH", postPath);
        HttpResponse httpResponse;
        JSONObject response = null;
        try {
            httpPost.setEntity(new StringEntity(uid));
            Log.d("BODY",uid);
            httpPost.setHeader("X-PAYPAL-SECURITY-CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            Log.d("SECURITY_CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {

            try {
                response = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            try {
                Log.e("FAILED RESPONSE", EntityUtils.toString(httpResponse.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return response;
    }

    public static JSONObject createRequest(String uid) {
        String postPath = CREATE_REQUEST_URL;
        HttpPost httpPost = new HttpPost(postPath);
        Log.d("PATH", postPath);
        HttpResponse httpResponse;
        JSONObject response = null;
        try {
            httpPost.setEntity(new StringEntity(uid));
            Log.d("BODY",uid);
            httpPost.setHeader("X-PAYPAL-SECURITY-CONTEXT", CREATE_REQUEST_SECURITY);
            Log.d("SECURITY_CONTEXT", CREATE_REQUEST_SECURITY);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {

            try {
                response = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            try {
                Log.e("FAILED RESPONSE", EntityUtils.toString(httpResponse.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return response;
    }

    public static JSONObject createFundingRequest(String uid,String url) {
        String postPath = url;
        HttpPost httpPost = new HttpPost(postPath);
        Log.d("PATH", postPath);
        HttpResponse httpResponse;
        JSONObject response = null;
        try {
            httpPost.setEntity(new StringEntity(uid));
            Log.d("BODY",uid);
            httpPost.setHeader("X-PAYPAL-SECURITY-CONTEXT", REQUEST_FUNDING_SECURITY);
            Log.d("SECURITY_CONTEXT", REQUEST_FUNDING_SECURITY);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {

            try {
                response = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            try {
                Log.e("FAILED RESPONSE", EntityUtils.toString(httpResponse.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return response;
    }

    public static JSONObject createPayRequest(String uid,String url) {
        String postPath = url;
        HttpPost httpPost = new HttpPost(postPath);
        Log.d("PATH", postPath);
        HttpResponse httpResponse;
        JSONObject response = null;
        try {
            httpPost.setEntity(new StringEntity(uid));
            Log.d("BODY",uid);
            httpPost.setHeader("X-PAYPAL-SECURITY-CONTEXT", REQUEST_FUNDING_SECURITY);
            Log.d("SECURITY_CONTEXT", REQUEST_FUNDING_SECURITY);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {

            try {
                response = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            try {
                Log.e("FAILED RESPONSE", EntityUtils.toString(httpResponse.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return response;
    }

}
