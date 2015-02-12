package com.example.agagneja.androidchat;

/**
 * Created by agagneja on 1/27/2015.
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class ChatActivity extends Activity implements AdapterView.OnItemSelectedListener{
    SharedPreferences prefs;
    List<NameValuePair> params;
    EditText chat_msg;
    Button send_btn;
    Bundle bundle;
    TableLayout tab;
    Button sendMoney;
    Spinner spinner;
    String payee;
    String val;
    String cur;
    String PAYPAL_FUNDING_BODY;
    String idUser;
    JSONArray source;
    String PAYPAL_FULFILL_BODY;
    List<String> options;
    String network;
    String last_4;
    String type;
    StringBuffer sb;
    Button create_request;
    String CREATE_REQUEST_BODY;
    String REQUEST_FUNDING_URL;
    String REQUEST_FUNDING_BODY;
    String payer;
    String REQUEST_PAY_BODY;
    String REQUEST_PAY_URL;
    ArrayList<HashMap<String, String>> chats = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tab = (TableLayout)findViewById(R.id.tab);
        prefs = getSharedPreferences("Chat", 0);
        bundle = getIntent().getBundleExtra("INFO");
       Bundle bd = getIntent().getExtras();
        payee = bd.getString("name");
        payer = prefs.getString("FROM_NAME","");
        sendMoney = (Button) findViewById(R.id.sendMoney);
        create_request = (Button) findViewById(R.id.create_request);
        options = new ArrayList<String>();
       // getOptions();

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies_array,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("CURRENT_ACTIVE", bundle.getString("mobno"));
        edit.commit();
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        if(bundle.getString("name") != null){
            TableRow tr1 = new TableRow(getApplicationContext());
            tr1.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView textview = new TextView(getApplicationContext());
            textview.setTextSize(20);
            textview.setTextColor(Color.parseColor("#0B0719"));
            textview.setText(Html.fromHtml("<b>"+bundle.getString("name")+" : </b>"+bundle.getString("msg")));
            tr1.addView(textview);
            tab.addView(tr1);
        }
        chat_msg = (EditText)findViewById(R.id.chat_msg);
        send_btn = (Button)findViewById(R.id.sendbtn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow tr2 = new TableRow(getApplicationContext());
                tr2.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView textview = new TextView(getApplicationContext());
                textview.setTextSize(20);
                textview.setTextColor(Color.parseColor("#A901DB"));
                textview.setText(Html.fromHtml("<b>You : </b>" + chat_msg.getText().toString()));
                tr2.addView(textview);
                tab.addView(tr2);
                new Send().execute();
            }
        });

        sendMoney.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                val = chat_msg.getText().toString();
                //cur = currency.getText().toString();
                //cur = "USD";
                PAYPAL_FUNDING_BODY = getBodyFunding();
                new GetFundingOptions().execute();
                //PAYPAL_FULFILL_BODY = getBody(idUser);


            }
        });
        create_request.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                val = chat_msg.getText().toString();
                CREATE_REQUEST_BODY= getCreateRequestBody();

                new GetCreateRequest().execute();
                //PAYPAL_FULFILL_BODY = getBody(idUser);


            }
        });



      new LoadChat().execute();
        //added this listener


    }

    public void getOptions()
    {
        for(int i = 0; i<source.length();i++)
        {
            try
            {
                JSONObject jobj =(JSONObject) source.get(i);
                options.add(jobj.getString("instrument_type"));

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }


        }

    }

    public String getCreateRequestBody()
    {
        StringBuffer sb = new StringBuffer("{\"event_title\":\"Dinner At Taj\",\"event_time\":\"2013-01-31T04:12:02Z\",\"requests\":[{\"note\":\"Only Drinks for you\",\"amount\":{\"currency\":\"" );
        sb.append(cur);
        sb.append("\",\"value\":\"");
        sb.append(val);
        sb.append("\"},\"requestee\":{\"id\":\"");
        sb.append(payee);
        sb.append("\",\"type\":\"email\"}}]}");
        return sb.toString();

    }
    public String getBodyFunding()
    {
        StringBuffer sb = new StringBuffer("{\"amount\":{\"value\":\"");
        sb.append(val);
        sb.append("\",\"currency\":\"");
        sb.append(cur);
        sb.append("\" }, \"payee\":{ \"id\":\"");
        sb.append(payee);
        sb.append("\", \"type\":\"EMAIL\" }, \"fee\":{ \"payer\":\"PAYER\"},\"payment_type\":\"PERSONAL\"}");
        //sb.append("\" }, \"payee\":{ \"id\":\"cdayanand-pre@paypal.com\", \"type\":\"EMAIL\" }, \"fee\":{ \"payer\":\"PAYER\"},\"payment_type\":\"PERSONAL\"}");
        Log.d("Cons body", sb.toString());
        return sb.toString();


    }
    /*public void startPayment(View view)
    {
        Intent intent1 = new Intent(this,MainActivityP.class);
        Log.d("hello","hi");
        intent1.putExtra("id", payee);
        Log.d("payee",payee);
        startActivity(intent1);
    }*/
    private BroadcastReceiver onNotice= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("msg");
            String str1 = intent.getStringExtra("fromname");
            String str2 = intent.getStringExtra("fromu");
            if(str2.equals(bundle.getString("mobno"))){
                TableRow tr1 = new TableRow(getApplicationContext());
                tr1.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView textview = new TextView(getApplicationContext());
                textview.setTextSize(20);
                textview.setTextColor(Color.parseColor("#0B0719"));
                textview.setText(Html.fromHtml("<b>"+str1+" : </b>"+str));
                tr1.addView(textview);
                tab.addView(tr1);
                if(str.contains("https://"))
                {

                    String url = str.substring(str.lastIndexOf("||") + 2);
                    REQUEST_FUNDING_URL =url.concat("/funding-options");
                    REQUEST_PAY_URL = url.concat("/pay");
                    Log.d("URL FOR FUNDING REQUEST side 2",REQUEST_FUNDING_URL);
                    Log.d("URL FOR PAY REQUEST SIDE 2 ",REQUEST_PAY_URL);
                    new GetFundingRequest().execute();

                 }

                else
                {
                    Log.d("ERROR","did not find url");
                }
            }
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
        cur = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void launchActivity()
    {
        Intent intent = new Intent(this,CompletedActivity.class);
        intent.putExtra("userId", idUser);
        intent.putExtra("amount",val);
        intent.putExtra("currency",cur);
        //intent.putExtra("network",network);
        //intent.putExtra("type",type);
        //intent.putExtra("last_4",last_4);
        intent.putExtra("sources",source.toString());

        startActivity(intent);
    }


    private class Send extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("from", prefs.getString("REG_FROM","")));
            params.add(new BasicNameValuePair("fromn", prefs.getString("FROM_NAME","")));
            params.add(new BasicNameValuePair("to", bundle.getString("mobno")));
            params.add((new BasicNameValuePair("msg",chat_msg.getText().toString())));
           // JSONObject jObj = json.getJSONFromUrl("http://10.0.2.2:8080/send",params);
           // return jObj;
            String job = json.getJSONSendFromUrl("http://10.0.2.2:8080/send", params);
            return job;
        }
        @Override
        protected void onPostExecute(String json) {
            chat_msg.setText("");
            //String res = null;
            /*try {
                if(json!=null) {
                    res = json.getString("response");
                    if (res.equals("Failure")) {
                        Toast.makeText(getApplicationContext(), "The user has logged out. You cant send message anymore !", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    }
    //adding new code
    private class LoadChat extends AsyncTask<String, String, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("from", prefs.getString("REG_FROM","")));
            params.add(new BasicNameValuePair("fromn", prefs.getString("FROM_NAME","")));
            params.add(new BasicNameValuePair("to", bundle.getString("mobno")));
            JSONArray jAry = json.getJSONArray("http://10.0.2.2:8080/getchat",params);
            return jAry;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            for(int i = 0; i < json.length(); i++){
                JSONObject c = null;
                try {
                    c = json.getJSONObject(i);
                    String name = c.getString("from");
                    String msg = c.getString("message");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", name);
                    map.put("msg", msg);
                    chats.add(map);
                    TableRow tr1 = new TableRow(getApplicationContext());
                    tr1.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    TextView textview = new TextView(getApplicationContext());
                    textview.setTextSize(20);
                    textview.setTextColor(Color.parseColor("#0B0719"));
                    textview.setText(Html.fromHtml("<b>"+name+" : </b>"+msg));
                    tr1.addView(textview);
                    tab.addView(tr1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    //till here

    public String getBody(String uid)
    {
        StringBuffer sb = new StringBuffer("{\"funding_option_id\":\"");
        sb.append(uid);
        sb.append("\"");
        sb.append(", \"payer\": { \"shipping_address_id\": \"405113\" }, \"note_to_payee\": \"Hello world!\"}" );
        return sb.toString();


    }

    private class GetFundingOptions extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {
            JSONObject response = ConnectionUtils.fundingOptionsRequest(PAYPAL_FUNDING_BODY);
            if(response!=null)
            {
                return response.toString();
            }
            else {
                return null;
            }
        }

        public void onPostExecute(String result)
        {
            if(result!=null) {
                Log.d("Response:", result);
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    JSONObject jFund = jsonObj.getJSONObject("funding_options");
                    JSONArray opt = jFund.getJSONArray("options");
                    JSONObject jid = (JSONObject) opt.get(0);
                    idUser = jid.getString("id");
                    source = jid.getJSONArray("sources");
                    //JSONObject jdet = (JSONObject)source.get(0);
                    //network = jdet.getString("network");
                    //type = jdet.getString("type");
                    //last_4 = jdet.getString("last_4");

                    Log.e("ID", idUser);
                    //Log.e("network",network);
                    //Log.e("type",type);
                    //Log.e("last_4",last_4);

                   // launchActivity();
                    getOptions();
                    PAYPAL_FULFILL_BODY = getBody(idUser);
                    if(options.get(0).toString().equals("PAYMENT_CARD"));
                    {
                        try {
                            JSONObject jo = (JSONObject) source.get(0);
                            JSONObject inn = jo.getJSONObject("payment_card");
                            network = inn.getString("network");
                            type = inn.getString("type");
                            last_4 = inn.getString("last_4");
                           chat_msg.setText("Your payment source is" + network + " " + type + " " + last_4 + " ");
                            new GetFulfillmentOptions().execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                   // new GetFulfillmentOptions().execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

    }

    private class GetFulfillmentOptions extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {
            JSONObject response = ConnectionUtils.fulfillmentOptionsRequest(PAYPAL_FULFILL_BODY);
            if(response!=null)
            {
                return response.toString();
            }
            else {
                return null;
            }
        }

        public void onPostExecute(String result)
        {
            if(result!=null) {
                Log.d("Response:", result);
                //message.setText("PaymentDone!!!");
                sb = new StringBuffer(val);
                sb.append(" ");
                sb.append(cur);
                sb.append(" have been payed to cdayanand!!");
                Toast.makeText(getApplicationContext(),sb.toString(),Toast.LENGTH_SHORT).show();
                chat_msg.setText(sb.toString());
                send_btn.performClick();
               // send_btn.setPressed(false);
            }
            else
            {
                Log.d("Response:","failed");
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                chat_msg.setText("");
                //message.setText("failed");

            }

        }
    }

    private class GetCreateRequest extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {
            JSONObject response = ConnectionUtils.createRequest(CREATE_REQUEST_BODY);
            if(response!=null)
            {
                return response.toString();
            }
            else {
                return null;
            }
        }

        public void onPostExecute(String result)
        {
            if(result!=null) {
                Log.d("Response:", result);
                //message.setText("PaymentDone!!!");
                sb = new StringBuffer(val);
                sb.append(" ");
                sb.append(cur);
                sb.append(" have been requested to you");
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                chat_msg.setText(sb.toString());
                Log.d("Response",result);
                try
                    {
                        JSONObject jobj = new JSONObject(result);
                        String gid = jobj.getString("id");
                        JSONArray jary = jobj.getJSONArray("requests");
                        JSONObject job = (JSONObject)jary.get(0);
                        String rid = job.getString("id");
                        Log.d("GID",gid);
                        Log.d("RID",rid);
                        String url = getRequestFundingURL(gid, rid);
                        Log.d("FUNDING URL side 1 ",url);
                        if (url!="")
                        {
                            // chat_msg.setText(" "+payer+" has requested "+val+" "+cur+" "+"from "+ payee+"||"+FUNDING_REQUEST_URL);
                            chat_msg.setText(" "+payer +" has requested "+val+" "+cur+" "+"from "+payee+"||"+url);
                            send_btn.performClick();

                        }
                        else {
                            Log.d("Not recieved gid,uid","Error");
                        }

                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("Response:", result);


              //  send_btn.performClick();

                // send_btn.setPressed(false);
            }
            else
            {
                Log.d("Response:","failed");
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                chat_msg.setText("");
                //message.setText("failed");

            }

        }
    }
    public String getRequestFundingURL(String gid, String rid)
    {
        StringBuffer sb = new StringBuffer("https://www.stage2c7169.qa.paypal.com:12807/v1/payments/money-requests/");
        sb.append(gid);
        sb.append("/requests/");
        sb.append(rid);
        return sb.toString();
    }

    private class GetFundingRequest extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {
            REQUEST_FUNDING_BODY = "{\"fee\":{\"payer\":\"PAYEE\"},\"payment_type\":\"PURCHASE\"}";
            JSONObject response = ConnectionUtils.createPayRequest(REQUEST_FUNDING_BODY, REQUEST_FUNDING_URL);
            if(response!=null)
            {
                return response.toString();
            }
            else {
                return null;
            }
        }

        public void onPostExecute(String result)
        {
            if(result!=null) {
                Log.d("Response:", result);
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                try
                {
                    JSONObject jobj = new JSONObject(result);
                    JSONObject job = jobj.getJSONObject("funding_options");
                    JSONArray jAry = job.getJSONArray("options");
                    JSONObject jo = jAry.getJSONObject(0);
                    String funding_id = jo.getString("id");
                    StringBuffer sb = new StringBuffer("{ \"funding_option_id\" : \"");
                    sb.append(funding_id);
                    sb.append("\"}");
                    REQUEST_PAY_BODY = sb.toString();
                    Log.d("FUNDINF OPTION ID",REQUEST_PAY_BODY);
                    //chat_msg.setText(funding_id);
                    new GetPayRequest().execute();

                 }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
               // chat_msg.setText(result);
            }
            else
            {
                Log.d("Response:","failed");
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                chat_msg.setText("");
                //message.setText("failed");

            }

        }
    }
    private class GetPayRequest extends AsyncTask<Void, Void, String>
    {
        public String doInBackground(Void... params)
        {

            JSONObject response = ConnectionUtils.createPayRequest(REQUEST_PAY_BODY, REQUEST_PAY_URL);
            if(response!=null)
            {
                return response.toString();
            }
            else {
                return null;
            }
        }

        public void onPostExecute(String result)
        {
            if(result!=null) {
                Log.d("Response:", result);
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                //chat_msg.setText(result);
            }
            else
            {
                Log.d("Response:","failed");
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                chat_msg.setText("");
                //message.setText("failed");

            }

        }
    }
}



