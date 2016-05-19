package com.mobile.ict.cart.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.Container.Organisations;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.JSONDataHelper;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;
import com.mobile.ict.cart.util.SharedPreferenceConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siddharthsingh on 06-05-2016.
 */

public class ReferralFragmentSS extends Fragment {

    EditText phoneNumberNew;
    EditText emailNew;
    Spinner spinner;
    Button refer;
    ArrayAdapter<String> adapter;
    List<String> list;
    int position =0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_referral_fragment_s,container , false);
        phoneNumberNew = (EditText)view.findViewById(R.id.phonenumber_et);
        emailNew = (EditText)view.findViewById(R.id.email_et);
        refer = (Button)view.findViewById(R.id.refer_button);
        spinner = (Spinner)view.findViewById(R.id.referra_org_spinner);

        list  = new ArrayList<String>();
        list.add("Loading Organisation List...");


        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item , list);
        spinner.setAdapter(adapter);


        if(Master.isNetworkAvailable(getActivity())){

            try
            {
                JSONObject obj = new JSONObject();
                obj.put(Master.MOBILENUMBER,"91"+ MemberDetails.getMobileNumber());
                obj.put(Master.PASSWORD, MemberDetails.getPassword());
                new GetOrganisationsTask().execute(obj);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }else{

            Toast.makeText(getActivity(),"Internet not available" , Toast.LENGTH_SHORT).show();
            refer.setEnabled(false);

        }



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                position = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if the feedbcak edit text has any text
                if(phoneNumberNew.getText().toString().trim().equals("")
                        ||phoneNumberNew.getText().toString().trim().isEmpty()){

                    Toast.makeText(getActivity(),"Please enter your feedback before submitting",Toast.LENGTH_SHORT).show();

                }else{
                    String Orgabbr;

                    Orgabbr = Organisations.organisationList.get(position).getOrgabbr();

                    String phoneNumber = phoneNumberNew.getText().toString();
                    String email = emailNew.getText().toString();

                    try
                    {
                        JSONObject obj = new JSONObject();
                        obj.put("email",email);
                        obj.put(Master.DEFAULT_ORG_ABBR, Orgabbr);
                        obj.put("phonenumber", phoneNumber);
                        obj.put("refemail",MemberDetails.email);

                        new SendReferalTask().execute(obj);
                    }
                    catch (JSONException e)
                    {

                        e.printStackTrace();
                    }


                }


            }
        });


        return view;
    }



    public class SendReferalTask extends AsyncTask<JSONObject, String, String> {

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(getActivity(), getString(R.string.pd_fetching_data_from_server), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {

            GetJSON getJson = new GetJSON();
            System.out.println(params[0]);
            Master.response = Master.getJSON.getJSONFromUrl(Master.getSendReferURL(), params[0], "POST", true,
                    MemberDetails.getEmail(), MemberDetails.getPassword());
            System.out.println("Send Feedback Response "+Master.response);
            Log.d("Send referral Response " , Master.response);
            //can't call Toast as it displays on UI from background thread
            // Toast.makeText(getContext(),Master.response,Toast.LENGTH_SHORT).show();
            return Master.response;

        }

        @Override
        protected void onPostExecute(String response) {
            Material.circularProgressDialog.dismiss();
            System.out.println("------------" + response);
            if (response.equals("exception")) {
                Toast.makeText(getActivity() , "Error while submiting your feedback, please try again." , Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(getActivity() , response , Toast.LENGTH_SHORT).show();

                SharedPreferenceConnector.writeString(getActivity().getApplicationContext(), Master.LOGIN_JSON, response);
            }
        }
    }



    public class GetOrganisationsTask extends AsyncTask<JSONObject, String, String> {

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(getActivity(), getString(R.string.pd_fetching_data_from_server), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {

            GetJSON getJson = new GetJSON();
            System.out.println(params[0]);
            Master.response = getJson.getJSONFromUrl(Master.getLoginURL(), params[0], "POST", false, null, null);
            return Master.response;
        }

        @Override
        protected void onPostExecute(String response) {
            Material.circularProgressDialog.dismiss();
            System.out.println("------------" + response);
            if (response.equals("exception")) {
                //clear all previous values in the list and show the error
                list.clear();
                list.add("Error Loading Organisations list");
                adapter.notifyDataSetChanged();
            }
            else
            {

                SharedPreferenceConnector.writeString(getActivity().getApplicationContext(), Master.LOGIN_JSON, response);
                Organisations.organisationList = new ArrayList<Organisations>();
                Organisations.organisationList = JSONDataHelper.getOrganisationListFromJson(getActivity(), SharedPreferenceConnector
                        .readString(getActivity().getApplicationContext(), Master.LOGIN_JSON, Master.DEFAULT_LOGIN_JSON));
                list.clear();

                if(Organisations.organisationList.size()>0){
                    for (int i = 0; i < Organisations.organisationList.size(); ++i) {

                        list.add(Organisations.organisationList.get(i).getName());

                    }

                    adapter.notifyDataSetChanged();
                }else{

                    list.add("Error loading organisations");
                    adapter.notifyDataSetChanged();

                }



            }
        }
    }
}
