package com.mobile.ict.cart.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Toast;

import com.mobile.ict.cart.R;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;
import com.mobile.ict.cart.Container.MemberDetails;

import org.json.JSONException;
import org.json.JSONObject;

public class EnquiryFormActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener{

    EditText eName, eEmail, eMobileNumber, eMessage;
    Button bSave, bCancel;
    public static Boolean backPress;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backPress = false;
        setContentView(R.layout.activity_enquiry_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eName = (EditText) findViewById(R.id.eEnquiryName);
        eName.setOnFocusChangeListener(this);

        eEmail = (EditText) findViewById(R.id.eEnquiryEmail);
        eEmail.setOnFocusChangeListener(this);

        eMobileNumber = (EditText) findViewById(R.id.eEnquiryMobileNumber);
        eMobileNumber.setOnFocusChangeListener(this);

        eMessage = (EditText) findViewById(R.id.eEnquiryMessage);
        eMessage.setScroller(new Scroller(EnquiryFormActivity.this));
        eMessage.setVerticalScrollBarEnabled(true);
        eMessage.setMovementMethod(new ScrollingMovementMethod());
        eMessage.setOnFocusChangeListener(this);

        bSave = (Button) findViewById(R.id.bEnquirySave);
        bSave.setOnClickListener(this);

        bCancel = (Button) findViewById(R.id.bEnquiryCancel);
        bCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        backPress = false;
        switch (v.getId())
        {
            case R.id.bEnquirySave:
                save();
                break;

            case R.id.bEnquiryCancel:
                finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(backPress)
            finish();
        else
        {
            backPress = true;
            Log.e("Enq form", "in else in onbackpressed");
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.enquiryCoordinatorLayout);
            Snackbar.make(coordinatorLayout, R.string.snackbar_press_back_once_more_to_exit, Snackbar.LENGTH_LONG).show();
        }
    }

    void save()
    {
        if(eName.getText().toString().trim().equals("")
                ||eEmail.getText().toString().trim().equals("")
                ||eMessage.getText().toString().trim().equals(""))
        {
            if(eName.getText().toString().trim().equals(""))
            {
                //Toast.makeText(this, R.string.toast_please_enter_name, Toast.LENGTH_SHORT).show();
                eName.setError(getString(R.string.error_required));
            }
            if(eEmail.getText().toString().trim().equals(""))
            {
               // Toast.makeText(this, R.string.toast_please_enter_email, Toast.LENGTH_SHORT).show();
                eEmail.setError(getString(R.string.error_required));
            }
            if(eMessage.getText().toString().trim().equals(""))
            {
                //Toast.makeText(this, R.string.toast_please_enter_message, Toast.LENGTH_SHORT).show();
                eMessage.setError(getString(R.string.error_required));
            }
        }
        else
        {
            JSONObject enquiryObject = new JSONObject();
            try
            {
                enquiryObject.put(Master.EMAIL, eEmail.getText().toString().trim());
                enquiryObject.put("name", eName.getText().toString().trim());
                enquiryObject.put("message", eMessage.getText().toString().trim());
                if(!eMobileNumber.getText().toString().trim().equals(""))
                    enquiryObject.put(Master.MOBILENUMBER, eMobileNumber.getText().toString().trim());
                new EnquiryTask().execute(enquiryObject);
            }
            catch (JSONException e)
            {
                Toast.makeText(EnquiryFormActivity.this, R.string.alert_something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.e("Enq form", "in onFocusChange");
        if(hasFocus)
            backPress = false;
    }


    private class EnquiryTask extends AsyncTask<JSONObject, String, String>
    {
        @Override
        protected void onPreExecute() {
            Material.circularProgressDialog(EnquiryFormActivity.this, getString(R.string.pd_sending_data_to_server), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            GetJSON getJSON = new GetJSON();
            Master.response = getJSON.getJSONFromUrl(Master.getEnquiryFormURL(), params[0], "POST", true,
                    MemberDetails.getEmail(), MemberDetails.getPassword());
            System.out.println("Enquiry form response: " + Master.response);
            return Master.response;
        }

        @Override
        protected void onPostExecute(String s) {
            Material.circularProgressDialog.dismiss();
            try
            {
                Master.responseObject = new JSONObject(s);
                if (Master.responseObject.get("response").equals("Enquiry sent"))
                {
                    Toast.makeText(EnquiryFormActivity.this, getString(R.string.toast_the_enquiry_has_been_submitted), Toast.LENGTH_LONG).show();
                    finish();
                }
                else if(Master.responseObject.get("response").equals("Failed to send enquiry"))
                {
                    Toast.makeText(EnquiryFormActivity.this, getString(R.string.toast_unable_to_record_your_enquiry), Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(EnquiryFormActivity.this, getString(R.string.toast_we_are_facing_some_technical_problems), Toast.LENGTH_LONG).show();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}
