package com.mobile.ict.cart.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mobile.ict.cart.Container.Organisations;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.database.DBHelper;
import com.mobile.ict.cart.util.JSONDataHelper;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.SharedPreferenceConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener{

    GetOTPTask anp;
    OTPCountDownTimer countDownTimer;
    AddPasswordTask addPasswordTask;

    Dialog dialog;

    String number, pwd, response, pwd1, pwd2, otp_check;
    long session;
    int forgotOtpDialogId;
    JSONObject resObj;

    Boolean login;
    EditText eMobileNumber, ePassword, eOTP, ePass1, ePass2;
    Button bSignIn, bForgotPassword, bContactUs, bOTP, bConfirm, bCancel;
    CheckBox chkBox;

    public static Boolean backPress = false;

    LinearLayout linearLayout;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // Master.dbHelper = new DBHelper(getApplicationContext());
        dbHelper = new DBHelper(this);

        //login = SharedPreferenceConnector.readBoolean(getApplicationContext(), Master.LOGIN, false);
        backPress = false;
        if (dbHelper.getSignedInProfile())
        /*if(true)*/
        {
            //Master.getMemberDetails(getApplicationContext());
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }
        else
        {
            setContentView(R.layout.activity_login);

            dbHelper = new DBHelper(getApplicationContext());

            eMobileNumber = (EditText) findViewById(R.id.eMobileNumber);
            eMobileNumber.setOnFocusChangeListener(this);

            ePassword = (EditText) findViewById(R.id.ePassword);
            ePassword.setOnFocusChangeListener(this);
            ePassword.setLongClickable(false);

            chkBox = (CheckBox) findViewById(R.id.chkSignIn);

            //fetching from local database
            if(SharedPreferenceConnector.readBoolean(getApplicationContext(), Master.SIGNCHK, Master.DEFAULT_SIGNCHK).equals(true))
            {

                /*eMobileNumber.setText(SharedPreferenceConnector.readString(getApplicationContext(), Master.MOBILENUMBER, Master.DEFAULT_MOBILENUMBER));
                ePassword.setText(SharedPreferenceConnector.readString(getApplicationContext(), Master.PASSWORD, Master.DEFAULT_PASSWORD));*/

                eMobileNumber.setText(MemberDetails.getMobileNumber());
                ePassword.setText(MemberDetails.getPassword());
                chkBox.setChecked(true);
            }
            else
                chkBox.setChecked(false);

            bSignIn = (Button) findViewById(R.id.bSignIn);
            bSignIn.setOnClickListener(this);

            bForgotPassword= (Button) findViewById(R.id.bForgotPassword);
            bForgotPassword.setOnClickListener(this);

            bContactUs = (Button) findViewById(R.id.bContactUs);
            bContactUs.setOnClickListener(this);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        backPress = false;
    }

    @Override
    public void onClick(View v) {
        backPress = false;
        switch (v.getId())
        {
            case R.id.bSignIn:
                number = eMobileNumber.getText().toString().trim();
                pwd = ePassword.getText().toString().trim();
                if(Master.isNetworkAvailable(LoginActivity.this))
                    login();
                else
                    Toast.makeText(LoginActivity.this, R.string.toast_Please_check_internet_connection, Toast.LENGTH_LONG).show();
                break;

            case R.id.bForgotPassword:
                number = eMobileNumber.getText().toString().trim();
                forgot();
                break;

            case R.id.bContactUs:
                startActivity(new Intent(LoginActivity.this, EnquiryFormActivity.class));
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
            backPress = true;
    }

    @Override
    public void onBackPressed() {
        if(backPress)
            finish();
        else
        {
            backPress = true;
            Log.e("Login Act", "in else in onbackpress");
            linearLayout = (LinearLayout) findViewById(R.id.firstPageLayout);
            Snackbar.make(linearLayout, R.string.snackbar_press_back_once_more_to_exit, Snackbar.LENGTH_LONG).show();
        }
    }

    private void login()
    {
        Log.e("Vish", "Sign up clicked");

        if(number.equals("") && pwd.equals(""))
        {
            Toast.makeText(LoginActivity.this,R.string.toast_Please_enter_a_mobile_number_and_a_password, Toast.LENGTH_SHORT).show();
        }
        else if(number.equals(""))
        {
            Toast.makeText(LoginActivity.this,R.string.toast_Please_enter_mobile_number, Toast.LENGTH_SHORT).show();
        }
        else if(pwd.equals(""))
        {
            Toast.makeText(LoginActivity.this, R.string.toast_Please_enter_password, Toast.LENGTH_SHORT).show();
        }
        else if(number.length()!= 10)
        {
            Toast.makeText(getApplicationContext(), R.string.toast_enter_valid_number, Toast.LENGTH_SHORT).show();
        }
        else if(Master.isNetworkAvailable(LoginActivity.this))
        {
            JSONObject obj = new JSONObject();
            try
            {
                obj.put(Master.MOBILENUMBER,"91"+number);
                obj.put(Master.PASSWORD,pwd);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            new LoginVerification(LoginActivity.this).execute(obj);
        }
        else
        {
            Toast.makeText(getApplicationContext(),R.string.toast_Please_check_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void forgot()
    {
        Log.e("Vish", "Forgot clicked");

        number = eMobileNumber.getText().toString();

        if(!number.equals(""))
        {

            if(Master.isNetworkAvailable(LoginActivity.this))
            {

                dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.new_password_box);
                dialog.setTitle(R.string.dialog_member_verification);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);

                eOTP = (EditText) dialog.findViewById(R.id.eOTP);
                eOTP.setVisibility(View.GONE);

                ePass1 = (EditText) dialog.findViewById(R.id.ePass1);
                ePass1.setVisibility(View.GONE);

                ePass2 = (EditText) dialog.findViewById(R.id.ePass2);
                ePass2.setVisibility(View.GONE);

                bConfirm = (Button) dialog.findViewById(R.id.bConfirm);
                bConfirm.setVisibility(View.GONE);

                bOTP = (Button) dialog.findViewById(R.id.bOTP);
                bCancel = (Button) dialog.findViewById(R.id.bCancel);

                bCancel.setOnClickListener(new View.OnClickListener()
                                           {
                                               @Override
                                               public void onClick(View v)
                                               {
                                                   dialog.dismiss();
                                               }
                                           }
                );

                bOTP.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                JSONObject obj = new JSONObject();
                                                try
                                                {
                                                    obj.put("phonenumber","91"+number);
                                                    anp = new GetOTPTask(LoginActivity.this);
                                                    anp.execute(obj);
                                                }
                                                catch (JSONException e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                );

                bConfirm.setOnClickListener(new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View v)
                                                {

                                                    long session2 = System.currentTimeMillis();
                                                    if(session2<session)
                                                    {
                                                        if(eOTP.getText().toString().equals(otp_check))
                                                        {
                                                            countDownTimer.cancel();
                                                            pwd1 = ePass1.getText().toString();
                                                            pwd2 = ePass2.getText().toString();

                                                            if(!pwd1.equals(""))
                                                            {
                                                                if(pwd1.equals(pwd2)){
                                                                    try
                                                                    {
                                                                        JSONObject addPasswordDB= new JSONObject();
                                                                        addPasswordDB.put("phonenumber","91"+number);
                                                                        addPasswordDB.put("password", pwd1);
                                                                        if(Master.isNetworkAvailable(LoginActivity.this))
                                                                        {
                                                                            addPasswordTask = new AddPasswordTask(LoginActivity.this, pwd1);
                                                                            addPasswordTask.execute(addPasswordDB);
                                                                        }
                                                                        else
                                                                        {
                                                                            Toast.makeText(getApplicationContext(), R.string.toast_Please_check_internet_connection, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                    catch(JSONException e)
                                                                    {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    ePass1.setText("");
                                                                    ePass2.setText("");
                                                                    Toast.makeText(getApplicationContext(),R.string.toast_Passwords_do_not_match, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            else
                                                            {
                                                                ePass1.setText("");
                                                                ePass2.setText("");
                                                                Toast.makeText(getApplicationContext(),R.string.toast_Please_enter_password, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                        else
                                                        {
                                                            eOTP.setHint(R.string.hint_enter_otp_received);
                                                            Toast.makeText(getApplicationContext(), R.string.toast_Please_enter_correct_OTP, Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                    else
                                                    {
                                                        eOTP.setText("");
                                                        eOTP.setHint(R.string.hint_enter_otp_received);
                                                        eOTP.setEnabled(false);

                                                        ePass1.setText("");
                                                        ePass1.setHint(R.string.hint_enter_new_password);
                                                        ePass1.setEnabled(false);

                                                        ePass2.setText("");
                                                        ePass2.setHint(R.string.hint_reenter_new_password);
                                                        ePass2.setEnabled(false);

                                                        bOTP.setEnabled(true);
                                                        bConfirm.setVisibility(View.GONE);
                                                        bConfirm.setEnabled(false);
                                                        bCancel.setVisibility(View.VISIBLE);
                                                        bOTP.setVisibility(View.VISIBLE);

                                                        Toast.makeText(getApplicationContext(),R.string.toast_otp_expired, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                );
                dialog.show();
            }
            else
            {
                Toast.makeText(this,R.string.toast_Please_check_internet_connection,Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this,R.string.toast_Please_enter_mobile_number,Toast.LENGTH_SHORT).show();
        }
    }



    public class LoginVerification extends AsyncTask<JSONObject, String, String> {

        ProgressDialog pd;
        Context context;

        public LoginVerification(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(LoginActivity.this, getString(R.string.pd_logging_you_in), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {

            GetJSON getJson = new GetJSON();
            System.out.println(params[0]);
            Master.response = "null";
            response = getJson.getJSONFromUrl(Master.getLoginURL(),params[0],"POST",false,null,null);
            System.out.println(Master.response);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {

            Material.circularProgressDialog.dismiss();

            System.out.println("------------"+response);

            if (response.equals("exception"))
            {
                Material.alertDialog(LoginActivity.this, getString(R.string.alert_cannot_connect_to_the_server), "OK");
            }
            else
            {
                try
                {
                    resObj = new JSONObject(response);
                    if (resObj.get("Authentication").toString().equals("success")) {

                        MemberDetails.setMobileNumber(number);
                        MemberDetails.setPassword(pwd);
                        MemberDetails.setEmail(resObj.getString(Master.EMAIL));
                        Log.e("Email", MemberDetails.getEmail());

                        JSONArray formFieldsArray = resObj.getJSONArray("formFields");
                        if (formFieldsArray.length() == 0)
                        {
                            if(chkBox.isChecked())
                            {
                                SharedPreferenceConnector.writeBoolean(getApplicationContext(), Master.SIGNCHK, true);
                            }
                            else
                            {
                                SharedPreferenceConnector.writeBoolean(getApplicationContext(), Master.SIGNCHK, Master.DEFAULT_SIGNCHK);
                            }

                            /*SharedPreferenceConnector.writeString(getApplicationContext(), Master.PASSWORD, pwd);
                            SharedPreferenceConnector.writeString(getApplicationContext(), Master.MOBILENUMBER, number);
                            SharedPreferenceConnector.writeString(getApplicationContext(), Master.FNAME, resObj.getString(Master.FNAME));
                            SharedPreferenceConnector.writeString(getApplicationContext(), Master.LNAME, resObj.getString(Master.LNAME));
                            SharedPreferenceConnector.writeString(getApplicationContext(), Master.EMAIL, resObj.getString(Master.EMAIL));
                            SharedPreferenceConnector.writeString(getApplicationContext(), Master.ADDRESS, resObj.getString(Master.ADDRESS));
                            SharedPreferenceConnector.writeString(getApplicationContext(), Master.PINCODE, resObj.getString(Master.PINCODE));
                            SharedPreferenceConnector.writeBoolean(getApplicationContext(), Master.LOGIN, true);*/

                            SharedPreferenceConnector.writeString(getApplicationContext(), Master.LOGIN_JSON, response);

                            MemberDetails.setFname(resObj.getString(Master.FNAME));
                            MemberDetails.setLname(resObj.getString(Master.LNAME));
                            MemberDetails.setAddress(resObj.getString(Master.ADDRESS));
                            MemberDetails.setPincode(resObj.getString(Master.PINCODE));

                            if(!dbHelper.isMobileNumberPresent(number))
                            {
                                System.out.println("------number not present------");
                                ArrayList<Organisations> org = JSONDataHelper.getOrganisationListFromJson(LoginActivity.this, response);
                                MemberDetails.selectedOrgAbbr = org.get(0).getOrgabbr();
                                MemberDetails.selectedOrgName = org.get(0).getName();

                                dbHelper.addProfile();
                            }
                            else
                            {
                                System.out.println("------setting login status true------");
                                dbHelper.setLogin(number);
                            }

                            startActivityForResult(new Intent(LoginActivity.this, DashboardActivity.class), 0);
                            finish();
                        }
                        else
                        {
                            System.out.println("------intent------"+response);

                            ArrayList<Organisations> org = JSONDataHelper.getOrganisationListFromJson(LoginActivity.this, response);
                            MemberDetails.selectedOrgAbbr = org.get(0).getOrgabbr();
                            MemberDetails.selectedOrgName = org.get(0).getName();

                            Intent detailsFormIntent = new Intent(LoginActivity.this,DetailsFormActivity.class);
                            detailsFormIntent.putExtra(Master.LOGIN_JSON, response);
                            startActivity(detailsFormIntent);
                            finish();
                        }
                    }
                    else
                    {
                        if(resObj.get("registered").toString().equals("false"))
                        {
                            Toast.makeText(getApplicationContext(),R.string.toast_You_are_not_a_registered_member, Toast.LENGTH_SHORT).show();
                            eMobileNumber.setText("");
                            ePassword.setText("");
                        }
                        else
                        {
                            if(resObj.get("Authentication").equals("failure"))
                            {
                                Toast.makeText(getApplicationContext(), R.string.toast_incorrect_password, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), R.string.alert_something_went_wrong, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Log.e("Login", "in catch in LoginTask");
                    Toast.makeText(getApplicationContext(), R.string.alert_something_went_wrong, Toast.LENGTH_SHORT).show();
                }


            }
        }
    }


    public class GetOTPTask extends AsyncTask<JSONObject, String, String> {

        Context context;

        public GetOTPTask(Context context) {
            // TODO Auto-generated constructor stub
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(LoginActivity.this, getString(R.string.pd_please_wait), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {

            String url = Master.getForgotPasswordURL();
            GetJSON getJson = new GetJSON();
            response = getJson.getJSONFromUrl(url,params[0],"POST",false,null,null);
            return response;
        }

        @Override
        protected void onPostExecute(String response2) {

            Material.circularProgressDialog.dismiss();

            if(response2.equals("exception"))
            {
                Material.alertDialog(LoginActivity.this, getString(R.string.alert_cannot_connect_to_the_server), "OK");
            }
            else
            {

                try {
                    JSONObject obj = new JSONObject(response2);

                    otp_check = obj.getString("otp");

                    if(otp_check.equals("null")){
                        Toast.makeText(getApplicationContext(), R.string.toast_You_are_not_a_registered_member, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),R.string.toast_OTP_sent_through_sms,Toast.LENGTH_SHORT).show();
                        dialog.setTitle(R.string.dialog_set_new_password);
                        eOTP.setVisibility(View.VISIBLE);
                        eOTP.setEnabled(true);
                        ePass1.setVisibility(View.VISIBLE);
                        ePass2.setVisibility(View.VISIBLE);
                        ePass1.setEnabled(true);
                        ePass2.setEnabled(true);
                        bOTP.setVisibility(View.GONE);
                        bConfirm.setVisibility(View.VISIBLE);
                        bConfirm.setEnabled(true);
                        session = System.currentTimeMillis();
                        forgotOtpDialogId=2;
                        countDownTimer = new OTPCountDownTimer(600000, 1000);
                        countDownTimer.start();
                        session = session + 600000;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public class  AddPasswordTask extends AsyncTask<JSONObject, String, String> {

        Context context;
        String password;

        public AddPasswordTask(Context context, String password) {
            // TODO Auto-generated constructor stub
            this.password = password;
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(LoginActivity.this, getString(R.string.pd_please_wait), false);

        }

        @Override
        protected String doInBackground(JSONObject... params) {

            String url = Master.getChangePasswordURL();
            GetJSON getJson = new GetJSON();
            response = getJson.getJSONFromUrl(url,params[0],"POST",false,null,null);
            return response;
        }


        @Override
        protected void onPostExecute(String response3) {

            //Master.pd.dismiss();
            Material.circularProgressDialog.dismiss();

            if(response3.equals("exception"))
            {
                Material.alertDialog(LoginActivity.this, getString(R.string.alert_cannot_connect_to_the_server), "OK");
            }
            else
            {
                try {

                    JSONObject obj = new JSONObject(response3);
                    String msg = obj.getString("Status");

                    if(msg.equals("Success")) {
                        Toast.makeText(getApplicationContext(),R.string.toast_Password_successfully_changed, Toast.LENGTH_SHORT).show();
                        dbHelper.changePassword(MemberDetails.getMobileNumber(), password);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),R.string.alert_something_went_wrong, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    dialog.dismiss();
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }



    public class OTPCountDownTimer extends CountDownTimer
    {

        public OTPCountDownTimer(long startTime, long interval)
        {
            super(startTime, interval);
        }

        @Override
        public void onFinish()
        {
            eOTP.setText("");
            eOTP.setHint(R.string.hint_enter_otp_received);
            eOTP.setEnabled(false);
            bCancel.setEnabled(true);
            bCancel.setVisibility(View.VISIBLE);
            bOTP.setEnabled(true);
            bOTP.setVisibility(View.VISIBLE);

            ePass1.setText("");
            ePass1.setHint(R.string.hint_enter_new_password);
            ePass1.setEnabled(true);

            ePass2.setText("");
            ePass2.setHint(R.string.hint_reenter_new_password);
            ePass2.setEnabled(true);

            ePass1.setVisibility(View.VISIBLE);
            ePass2.setVisibility(View.VISIBLE);

            bConfirm.setEnabled(false);
            bConfirm.setVisibility(View.GONE);

            Toast.makeText(getApplicationContext(),R.string.toast_otp_expired, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onTick(long millisUntilFinished)
        {
        }
    }
}
