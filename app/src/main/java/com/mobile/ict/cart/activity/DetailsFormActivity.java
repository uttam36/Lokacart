package com.mobile.ict.cart.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Toast;

import com.mobile.ict.cart.R;
import com.mobile.ict.cart.database.DBHelper;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.util.SharedPreferenceConnector;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsFormActivity extends AppCompatActivity implements View.OnFocusChangeListener{

    String response;
    EditText eFirstName,eLastName, eAddress, ePincode, eEmail;
    Button bSave;
    RelativeLayout relativeLayout;
    Boolean backPress;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_form);
        setTitle(R.string.title_activity_details);
dbHelper = new DBHelper(this);
        backPress = false;

        relativeLayout = (RelativeLayout) findViewById(R.id.detailsRelativeLayout);

        response = getIntent().getExtras().getString(Master.LOGIN_JSON);
          System.out.println("detailsssss----------" + response);

        eFirstName = (EditText) findViewById(R.id.eFirstName);
        eFirstName.setOnFocusChangeListener(this);

        eLastName = (EditText) findViewById(R.id.eLastName);
        eLastName.setOnFocusChangeListener(this);

        eAddress = (EditText) findViewById(R.id.eAddress);
        eAddress.setScroller(new Scroller(DetailsFormActivity.this));
        eAddress.setVerticalScrollBarEnabled(true);
        eAddress.setMovementMethod(new ScrollingMovementMethod());
        eLastName.setOnFocusChangeListener(this);

        ePincode = (EditText) findViewById(R.id.ePincode);
        eLastName.setOnFocusChangeListener(this);

        eEmail = (EditText) findViewById(R.id.eEmail);
        eEmail.setEnabled(false);
        eLastName.setOnFocusChangeListener(this);

        bSave = (Button) findViewById(R.id.bDetailsSave);
        bSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                backPress = false;
                JSONObject obj = new JSONObject();
                if(eFirstName.getText().toString().trim().equals("")
                        && eLastName.getText().toString().trim().equals("")
                        && eAddress.getText().toString().trim().equals("")
                        && ePincode.getText().toString().trim().equals(""))
                {
                    Toast.makeText(DetailsFormActivity.this, R.string.toast_please_fill_all_the_details, Toast.LENGTH_SHORT).show();
                }
                else if(ePincode.getText().toString().trim().length() != 6)
                {
                    Toast.makeText(DetailsFormActivity.this, R.string.toast_please_enter_a_valid_six_digit_pincode, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try {
                        obj.put(Master.FNAME,eFirstName.getText().toString().trim());
                        obj.put(Master.LNAME, eLastName.getText().toString().trim());
                        obj.put(Master.ADDRESS, eAddress.getText().toString().trim());
                        obj.put(Master.MOBILENUMBER, "91" + MemberDetails.getMobileNumber());
                        obj.put(Master.PINCODE,ePincode.getText().toString().trim());
                        obj.put(Master.EMAIL,eEmail.getText().toString().trim());

                         System.out.println("-----------------json string------"+obj.toString());

                        new SendDetailsTask().execute(obj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        try
        {
            JSONObject obj = new JSONObject(response);

            eEmail.setText(obj.getString(Master.EMAIL));
            if(obj.has(Master.ADDRESS))
                eAddress.setText(obj.getString(Master.ADDRESS));
            if(obj.has(Master.PINCODE))
                ePincode.setText(obj.getString(Master.PINCODE));
            if(obj.has(Master.FNAME))
                eFirstName.setText(obj.getString(Master.FNAME));
            if(obj.has(Master.LNAME))
                eLastName.setText(obj.getString(Master.LNAME));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {

        if(backPress)
            finish();
        else
        {
            backPress = true;
            Snackbar.make(relativeLayout, R.string.snackbar_press_back_once_more_to_exit, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
            backPress = true;
    }

    private class SendDetailsTask extends AsyncTask<JSONObject,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(DetailsFormActivity.this, getString(R.string.pd_sending_data_to_server), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            GetJSON getJson = new GetJSON();
            response = getJson.getJSONFromUrl(Master.getDetailsFormURL(),params[0],"POST",false,null,null);
            System.out.println("--------detail response--------"+response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            Material.circularProgressDialog.dismiss();
            try {
                JSONObject res = new JSONObject(s);

                if(res.getString("response").equals("success"))
                {
                    MemberDetails.setFname(eFirstName.getText().toString());
                    MemberDetails.setLname(eLastName.getText().toString());
                    MemberDetails.setAddress(eAddress.getText().toString());
                    MemberDetails.setPincode(ePincode.getText().toString());

                    /*SharedPreferenceConnector.writeString(getApplicationContext(), Master.FNAME, eFirstName.getText().toString());
                    SharedPreferenceConnector.writeString(getApplicationContext(), Master.LNAME, eLastName.getText().toString());
                    SharedPreferenceConnector.writeString(getApplicationContext(), Master.ADDRESS, eAddress.getText().toString());
                    SharedPreferenceConnector.writeString(getApplicationContext(), Master.PINCODE, ePincode.getText().toString());
                    SharedPreferenceConnector.writeString(getApplicationContext(), Master.EMAIL, eEmail.getText().toString());
                    SharedPreferenceConnector.writeString(getApplicationContext(), Master.MOBILENUMBER, MemberDetails.getMobileNumber());
                    SharedPreferenceConnector.writeString(getApplicationContext(), Master.PASSWORD, MemberDetails.getPassword());
                    SharedPreferenceConnector.writeBoolean(getApplicationContext(), Master.LOGIN, true);
*/
                    dbHelper.addProfile();


                    startActivityForResult(new Intent(DetailsFormActivity.this, DashboardActivity.class), 0);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),R.string.alert_something_went_wrong, Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                Log.e("Details form", "Inside catch of post execute details form");
                e.printStackTrace();
            }
        }
    }
}
