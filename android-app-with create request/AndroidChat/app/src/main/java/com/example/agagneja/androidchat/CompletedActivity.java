package com.example.agagneja.androidchat;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CompletedActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    EditText message;
    EditText name;
    String PAYPAL_FULFILL_BODY;
    String id;
    String val;
    String cur;
    String network;
    String last_4;
    String type;
    Spinner spinner2 ;
    JSONArray source;
    List<String> options;
    String instrument_type;
    //Button pay_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);
        message = (EditText)findViewById(R.id.message);
        name = (EditText) findViewById(R.id.name);
        // Button pay_button = (Button)findViewById(R.id.payButton);
        Bundle bd = getIntent().getExtras();
        id = bd.getString("userId");
        val = bd.getString("amount");
        cur = bd.getString("currency");


        options = new ArrayList<String>();

        String src = bd.getString("sources");
        try
        {
            source = new JSONArray(src);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        getOptions();

        spinner2 = (Spinner) findViewById(R.id.option_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(this);

        PAYPAL_FULFILL_BODY = getBody(id);







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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
        instrument_type = parent.getItemAtPosition(position).toString();
        if(instrument_type.equals("PAYMENT_CARD")) {
            try {
                JSONObject jo = (JSONObject) source.get(0);
                JSONObject inn = jo.getJSONObject("payment_card");
                network = inn.getString("network");
                type = inn.getString("type");
                last_4 = inn.getString("last_4");
                message.setText("Your payment source is" + network + " " + type + " " + last_4 + " ");
                new GetFulfillmentOptions().execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




    }

    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_completed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public String getBody(String uid)
    {
        StringBuffer sb = new StringBuffer("{\"funding_option_id\":\"");
        sb.append(uid);
        sb.append("\"");
        sb.append(", \"payer\": { \"shipping_address_id\": \"405113\" }, \"note_to_payee\": \"Hello world!\"}" );
        return sb.toString();


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
                message.setText("PaymentDone!!!");
                StringBuffer sb = new StringBuffer(val);
                sb.append(" ");
                sb.append(cur);
                sb.append(" have been payed to cdayanand!!");

                name.setText(sb.toString());
            }
            else
            {
                Log.d("Response:","failed");
                message.setText("failed");

            }

        }
    }

}
