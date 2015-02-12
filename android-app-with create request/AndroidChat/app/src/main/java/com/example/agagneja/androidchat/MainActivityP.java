package com.example.agagneja.androidchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivityP extends ActionBarActivity implements AdapterView.OnItemSelectedListener
 {

    EditText amount;
    //EditText currency;
    Button proceed;
    Spinner spinner;
    String val;
    String cur;
    String PAYPAL_FUNDING_BODY;
    String idUser;
    String network;
    String type;
    String last_4;
    JSONArray source;
    //String PAYPAL_FULFILL_BODY;
    String nameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_p);
        Bundle bd = getIntent().getExtras();
        nameId = bd.getString("id");
        spinner = (Spinner)findViewById(R.id.spinner);
        proceed = (Button)findViewById(R.id.proceed);
        // currency = (EditText)findViewById(R.id.currency);
        amount = (EditText)findViewById(R.id.amount);

       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies_array,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        proceed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                val = amount.getText().toString();
                //cur = currency.getText().toString();
                //cur = "USD";
                PAYPAL_FUNDING_BODY = getBodyFunding();
                new GetFundingOptions().execute();
                //PAYPAL_FULFILL_BODY = getBody(idUser);



            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
        cur = parent.getItemAtPosition(position).toString();

    }

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

    public String getBodyFunding()
    {
        StringBuffer sb = new StringBuffer("{\"amount\":{\"value\":\"");
        sb.append(val);
        sb.append("\",\"currency\":\"");
        sb.append(cur);
        sb.append("\" }, \"payee\":{ \"id\":\"");
        sb.append(nameId);
        sb.append("\", \"type\":\"EMAIL\" }, \"fee\":{ \"payer\":\"PAYER\"},\"payment_type\":\"PERSONAL\"}");
        //sb.append("\" }, \"payee\":{ \"id\":\"cdayanand-pre@paypal.com\", \"type\":\"EMAIL\" }, \"fee\":{ \"payer\":\"PAYER\"},\"payment_type\":\"PERSONAL\"}");
        Log.d("Cons body", sb.toString());
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
                    launchActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }


}