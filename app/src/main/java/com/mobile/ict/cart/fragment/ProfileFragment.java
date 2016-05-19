package com.mobile.ict.cart.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import com.alertdialogpro.AlertDialogPro;
import com.alertdialogpro.ProgressDialogPro;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.activity.DashboardActivity;
import com.mobile.ict.cart.database.DBHelper;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.util.SharedPreferenceConnector;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Member;

/**
 * Created by vish on 21/3/16.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{

    View profileFragmentView, editMobileNumberDialogView;
    EditText eFName, eLName, eAddress, ePincode, eMobileNumber, eOtp;
    CardView cardView2;
    ImageView ivEdit, ivDone, ivMobileNumberDone;
    TextView tMobileNumber, tName, tEmail, tAddress, tPincode;
    String localFName, localLName, localAddress, localPincode, localMobileNumber;
    AlertDialogPro changeMobileNumberAlert;
    OTPCountDownTimer countDownTimer;
    String otp_check;
    long session;
    DBHelper dbHelper;
    Button bPositive, bNegative, bNeutral;

    boolean isVisible = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        profileFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle(R.string.title_fragment_profile);

        Master.getMemberDetails(getActivity().getApplicationContext());

        init();
        assign();

        return profileFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Master.getMemberDetails(getActivity().getApplicationContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Master.getMemberDetails(getActivity().getApplicationContext());
        System.out.println(MemberDetails.getFname() + "---------" + MemberDetails.getLname() + "---------" + MemberDetails.getAddress() + "---------" + MemberDetails.getPincode());

    }

    void init()
    {
        dbHelper = new DBHelper(getActivity());

        cardView2 = (CardView) profileFragmentView.findViewById(R.id.card_view2);
        cardView2.setVisibility(View.GONE);

        eFName = (EditText) profileFragmentView.findViewById(R.id.eEditProfileFName);
        eLName = (EditText) profileFragmentView.findViewById(R.id.eEditProfileLName);
        eMobileNumber = (EditText) profileFragmentView.findViewById(R.id.eEditProfileMobileNumber);
        ePincode = (EditText) profileFragmentView.findViewById(R.id.eEditProfilePincode);
        eAddress = (EditText) profileFragmentView.findViewById(R.id.eEditProfileAddress);
        eAddress.setScroller(new Scroller(getActivity()));
        eAddress.setVerticalScrollBarEnabled(true);
        eAddress.setMovementMethod(new ScrollingMovementMethod());

        tName = (TextView) profileFragmentView.findViewById(R.id.tProfileName);
        tEmail = (TextView) profileFragmentView.findViewById(R.id.tProfileEmail);
        tAddress = (TextView) profileFragmentView.findViewById(R.id.tProfileAddress);
        tPincode = (TextView) profileFragmentView.findViewById(R.id.tProfilePincode);
        tMobileNumber = (TextView) profileFragmentView.findViewById(R.id.tProfileMobileNumber);

        ivEdit = (ImageView) profileFragmentView.findViewById(R.id.ivEdit);
        ivEdit.setOnClickListener(this);

        ivDone = (ImageView) profileFragmentView.findViewById(R.id.ivDone);
        ivDone.setOnClickListener(this);

        ivMobileNumberDone = (ImageView) profileFragmentView.findViewById(R.id.ivMobileNumberDone);
        ivMobileNumberDone.setOnClickListener(this);
    }

    void assign()
    {
        isVisible = false;

        tName.setText(MemberDetails.getFname() + " " + MemberDetails.getLname());
        tEmail.setText(MemberDetails.getEmail());
        tMobileNumber.setText(MemberDetails.getMobileNumber());
        tAddress.setText(MemberDetails.getAddress());
        tPincode.setText(MemberDetails.getPincode());

        eFName.setText(MemberDetails.getFname());
        eLName.setText(MemberDetails.getLname());
        eMobileNumber.setText(MemberDetails.getMobileNumber());
        eAddress.setText(MemberDetails.getAddress());
        ePincode.setText(MemberDetails.getPincode());

        localFName = MemberDetails.getFname();
        localLName = MemberDetails.getLname();
        localAddress = MemberDetails.getAddress();
        localPincode = MemberDetails.getPincode();
        localMobileNumber = MemberDetails.getMobileNumber();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ivEdit:
                if(isVisible)
                {
                    isVisible = false;
                    cardView2.setVisibility(View.GONE);
                }
                else
                {
                    isVisible = true;
                    cardView2.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.ivDone:
                save();
                break;

            case R.id.ivMobileNumberDone:
                if(eMobileNumber.getText().toString().trim().length() != 10)
                    Toast.makeText(getActivity(), R.string.toast_enter_valid_number, Toast.LENGTH_SHORT).show();
                else if(localMobileNumber.equals(eMobileNumber.getText().toString().trim()))
                    Toast.makeText(getActivity(), R.string.toast_no_changes_to_save, Toast.LENGTH_SHORT).show();
                else
                {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("phonenumber_old","91"+ MemberDetails.getMobileNumber());
                        obj.put("phonenumber_new","91" +eMobileNumber.getText().toString().trim());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new MobileNumberVerifyTask(2).execute(obj);
                }
                break;
        }
    }

    void save()
    {
        if(eFName.getText().toString().trim().equals(""))
            eFName.setError(getString(R.string.error_required));
        else if(eLName.getText().toString().trim().equals(""))
            eLName.setError(getString(R.string.error_required));
        else if(eAddress.getText().toString().trim().equals(""))
            eAddress.setError(getString(R.string.error_required));
        else if(ePincode.getText().toString().trim().equals(""))
            ePincode.setError(getString(R.string.error_required));
        else if(ePincode.getText().toString().trim().length() != 6)
            Toast.makeText(getActivity(), R.string.toast_please_enter_a_valid_six_digit_pincode, Toast.LENGTH_LONG).show();
        else if (eFName.getText().toString().trim().equals(localFName) && eLName.getText().toString().trim().equals(localLName)
                && eAddress.getText().toString().trim().equals(localAddress) && ePincode.getText().toString().trim().equals(localPincode))
        {
            Toast.makeText(getActivity(), R.string.toast_no_changes_to_save, Toast.LENGTH_LONG).show();
        }
        else
        {
            JSONObject jsonObject = new JSONObject();
            try
            {
                jsonObject.put(Master.FNAME, eFName.getText().toString().trim());
                jsonObject.put(Master.LNAME, eLName.getText().toString().trim());
                jsonObject.put(Master.ADDRESS, eAddress.getText().toString().trim());
                jsonObject.put(Master.PINCODE, ePincode.getText().toString().trim());
                jsonObject.put(Master.MOBILENUMBER, "91" + MemberDetails.getMobileNumber());

                new EditDetailsTask().execute(jsonObject);
            }
            catch (JSONException e)
            {
                Toast.makeText(getActivity(), R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
            }
        }
    }

    void editMobileNumber()
    {
        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(getActivity());
        builder.setCancelable(false);
        editMobileNumberDialogView = getActivity().getLayoutInflater().inflate(R.layout.change_mobile_number_box, null);
        builder.setView(editMobileNumberDialogView);
        builder.setTitle(R.string.title_dialog_change_mobile_number);
        builder.setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNeutralButton(R.string.button_get_OTP, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        eOtp = (EditText) editMobileNumberDialogView.findViewById(R.id.eOtp1);
        changeMobileNumberAlert = builder.create();
        changeMobileNumberAlert.show();

        bPositive = changeMobileNumberAlert.getButton(AlertDialogPro.BUTTON_POSITIVE);
        bNegative = changeMobileNumberAlert.getButton(AlertDialogPro.BUTTON_NEGATIVE);
        bNeutral = changeMobileNumberAlert.getButton(AlertDialogPro.BUTTON_NEUTRAL);
        bNeutral.setVisibility(View.GONE);

        bPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long session2 = System.currentTimeMillis();

                if(session2<session)
                {
                    if(eOtp.getText().toString().equals(otp_check))
                    {
                        changeMobileNumberAlert.dismiss();
                        countDownTimer.cancel();

                        JSONObject jsonObject = new JSONObject();
                        try
                        {
                            jsonObject.put("phonenumber_old", "91" + MemberDetails.getMobileNumber());
                            jsonObject.put("phonenumber_new", "91"+eMobileNumber.getText().toString().trim());

                            System.out.println("editing mobile number-------------"+jsonObject.toString());

                            new EditMobileNumberTask(getActivity(), MemberDetails.getMobileNumber(),
                                    eMobileNumber.getText().toString().trim()).execute(jsonObject);
                        }
                        catch (JSONException e)
                        {
                            Toast.makeText(getActivity(), R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
                        }

                    }
                    else
                    {
                        eOtp.setHint(R.string.hint_enter_otp_received);
                        Toast.makeText(getActivity(),R.string.toast_Please_enter_correct_OTP, Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    eOtp.setText("");
                    Toast.makeText(getActivity(),R.string.toast_Sorry_your_OTP_expires___Try_to_get_new_OTP, Toast.LENGTH_LONG).show();
                }
            }
        });


        bNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMobileNumberAlert.dismiss();
            }
        });

        bNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject obj = new JSONObject();
                try {
                    obj.put("phonenumber_old","91"+ MemberDetails.getMobileNumber());
                    obj.put("phonenumber_new","91" +eMobileNumber.getText().toString().trim());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new MobileNumberVerifyTask(1).execute(obj);
            }
        });

    }


    //------------------------------ change mobile number task ----------------------------------



    public class MobileNumberVerifyTask extends AsyncTask<JSONObject, String, String> {

        int category;

        public MobileNumberVerifyTask(int cat)
        {
            category = cat;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(getActivity(), getString(R.string.pd_sending_data_to_server), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            Master.getJSON = new GetJSON();
            Master.response = Master.getJSON.getJSONFromUrl(Master.getNumberVerifyURL(), params[0], "POST", true,
                    MemberDetails.getEmail(), MemberDetails.getPassword());
            System.out.println(Master.response);
            return Master.response;
        }

        @Override
        protected void onPostExecute(String s) {
            Material.circularProgressDialog.dismiss();
            try
            {
                Master.responseObject = new JSONObject(s);
                String otp_val =  Master.responseObject.getString("otp");
                String text =  Master.responseObject.getString("text");
                if(!otp_val.equals("null")&&!text.equals("null"))
                {
                    Toast.makeText(getActivity(),R.string.toast_OTP_has_sent_to_your_mobile_sms,Toast.LENGTH_LONG).show();

                    otp_check = otp_val;
                    session = System.currentTimeMillis();
                    countDownTimer = new OTPCountDownTimer(60000, 1000);
                    countDownTimer.start();

                    session = session+60000; // 1 minute

                    if(category == 2)
                        editMobileNumber();
                    else
                    {
                        bNeutral.setVisibility(View.GONE);
                        bPositive.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                        if(otp_val.equals("null")&&text.equals("Phone number entered already exists."))
                        {
                            Toast.makeText(getActivity(),R.string.toast_validation_Mobile_Number_exists, Toast.LENGTH_LONG).show();
                        }
                }
            }
            catch (JSONException e)
            {
                Toast.makeText(getActivity(), R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
            }

        }
    }






    public class EditMobileNumberTask extends AsyncTask<JSONObject, String, String> {

        Context context;
        String oldNum, newNum;

        public EditMobileNumberTask(Context context, String oldNum, String newNum) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.newNum = newNum;
            this.oldNum = oldNum;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(getActivity(), getString(R.string.pd_sending_data_to_server), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            Master.getJSON = new GetJSON();
            Master.response = Master.getJSON.getJSONFromUrl(Master.getChangeNumberURL(), params[0], "POST", true,
                    MemberDetails.getEmail(), MemberDetails.getPassword());
            System.out.println(Master.response);
            return Master.response;
        }

        @Override
        protected void onPostExecute(String s) {

            Material.circularProgressDialog.dismiss();
            try
            {
                Master.responseObject = new JSONObject(s);

                if(Master.responseObject.getString(Master.STATUS).equals("success"))
                {
                    Toast.makeText(getActivity(),R.string.toast_Your_mobile_number_upadted_successfully,Toast.LENGTH_LONG).show();

                    dbHelper.changeMobileNumber(oldNum, newNum);
                    tMobileNumber.setText(newNum);
                    MemberDetails.setMobileNumber(newNum);
                    DashboardActivity.updateNavHeader();

                }
                else if(Master.responseObject.getString(Master.STATUS).equals("user phone number not present"))
                {
                    Toast.makeText(getActivity(),R.string.toast_Mobile_Number_does_not_exists,Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getActivity(), R.string.alert_cannot_connect_to_the_server, Toast.LENGTH_LONG).show();
                }
            }
            catch (JSONException e)
            {
                Toast.makeText(getActivity(), R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
            }

        }
    }

//----------------------- OTP Count Down Timer------------------------------------------------------

    public class OTPCountDownTimer extends CountDownTimer
    {

        public OTPCountDownTimer(long startTime, long interval)
        {
            super(startTime, interval);
        }

        @Override
        public void onFinish()
        {
            if(editMobileNumberDialogView != null)
            {
                eOtp.setText("");

                bPositive.setVisibility(View.GONE);
                bNeutral.setVisibility(View.VISIBLE);

                Toast.makeText(getActivity(),R.string.toast_Sorry_your_OTP_expires___Try_to_get_new_OTP, Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onTick(long millisUntilFinished)
        {
        }
    }


    class EditDetailsTask extends AsyncTask<JSONObject, String, String>
    {
        @Override
        protected void onPreExecute() {
            Material.circularProgressDialog(getActivity(), getString(R.string.pd_sending_data_to_server), true);
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            Master.getJSON = new GetJSON();
            System.out.println("URL: " + Master.getEditProfileURL());
            System.out.println("JSON: " + params[0]);
            Master.response = Master.getJSON.getJSONFromUrl(Master.getEditProfileURL(), params[0], "POST", true,
                    MemberDetails.getEmail(), MemberDetails.getPassword());
            System.out.println(Master.response);
            return Master.response;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Master.responseObject = new JSONObject(s);
            }
            catch (Exception e)
            {
                Log.e("Prof frag", "JSON prob");
            }
            try {
                if(Master.responseObject.getString(Master.RESPONSE).equals("Success"))
                {
                    Toast.makeText(getActivity(), R.string.toast_profile_details_updated_successfully, Toast.LENGTH_LONG).show();

                    MemberDetails.setFname(eFName.getText().toString().trim());
                    MemberDetails.setLname(eLName.getText().toString().trim());
                    MemberDetails.setAddress(eAddress.getText().toString().trim());
                    MemberDetails.setPincode(ePincode.getText().toString().trim());

                    System.out.println("edit task------------" + MemberDetails.getFname() + "---------" + MemberDetails.getLname() + "---------" + MemberDetails.getAddress() + "---------" + MemberDetails.getPincode());

                    dbHelper.updateProfile(MemberDetails.getMobileNumber());

                    assign();
                    DashboardActivity.updateNavHeader();
                }
                else
                {
                    Toast.makeText(getActivity(), R.string.alert_cannot_connect_to_the_server, Toast.LENGTH_LONG).show();
                }
            }
            catch (JSONException e)
            {
                Toast.makeText(getActivity(), R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
            }
            Material.circularProgressDialog.dismiss();
        }
    }
}
