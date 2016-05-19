package com.mobile.ict.cart.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.Container.Organisations;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.JSONDataHelper;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;
import com.mobile.ict.cart.util.SharedPreferenceConnector;

/**
 * Created by vish on 21/3/16.
 */

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Siddharthsingh on 06-05-2016.
 */
public class FeedbackFragment extends Fragment {


    final static String Lokacart_Tag = "lokacart";
    int position =0;
    ArrayAdapter<String> adapter;
    List<String> list;
    EditText feedbackEditText;
    TextView charRemainingTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);


        //set title in actionbar
        getActivity().setTitle("Feedback");

        charRemainingTV = (TextView)view.findViewById(R.id.char_remaining_TV);
        Button submit = (Button)view.findViewById(R.id.submit_button);
        Spinner spinner = (Spinner) view.findViewById(R.id.planets_spinner);
        feedbackEditText = (EditText)view.findViewById(R.id.feedback_edittext);

        list  = new ArrayList<String>();
        list.add("Loading Organisation List...");


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
            submit.setEnabled(false);

        }



        feedbackEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    charRemainingTV.setText((500 - s.length()) + "characters remaining");
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Message = feedbackEditText.getText().toString();

                //check if the feedbcak edit text has any text
                if(Message.trim().equals("")
                        ||Message.trim().isEmpty()){

                    Toast.makeText(getActivity(),"Please enter your feedback before submitting",Toast.LENGTH_SHORT).show();

                }else if(Message.equals("Loading Organisation List...")||Message.equals("Error loading organisations")){

                    Toast.makeText(getActivity(),"There was a problem loading organisations. Please check your internet connection.",Toast.LENGTH_SHORT).show();


                }else{
                    String Orgabbr;
                    /*as Lokacart is not an organisation, its value will not be in Organisation.OrganisationList
                    so setting organisation manually */
                    if(list.get(position).equals(Lokacart_Tag)){
                        Orgabbr = "lcart";

                    }else {
                        Orgabbr = Organisations.organisationList.get(position).getOrgabbr();
                    }

                    try
                    {
                        JSONObject obj = new JSONObject();
                        obj.put(Master.MOBILENUMBER,"91"+ MemberDetails.getMobileNumber());
                        obj.put(Master.DEFAULT_ORG_ABBR, Orgabbr);
                        obj.put(Master.feedbackText, Message);

                        new SendFeedbackTask().execute(obj);
                    }
                    catch (JSONException e)
                    {

                        e.printStackTrace();
                    }


                }


            }
        });



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                position = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        //setBackground was added in API level 16 so use setBackgroundDrawable for versions before that
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.edittextshape,null));
            feedbackEditText.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.edittextshape,null));

        } else {
            spinner.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.edittextshape,null));
            feedbackEditText.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.edittextshape,null));

        }




        // Create an ArrayAdapter using the string array and a textview that will be used in the spinner
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item , list);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_cart);
        item.setVisible(false);
    }

    public class SendFeedbackTask extends AsyncTask<JSONObject, String, String> {

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
            Master.response = Master.getJSON.getJSONFromUrl(Master.getSendCommentURL(), params[0], "POST", true,
                    MemberDetails.getEmail(), MemberDetails.getPassword());
            System.out.println("Send Feedback Response "+Master.response);
            Log.d("Send Feedback Response " , Master.response);
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
                Toast.makeText(getActivity() , "Thanks for your feedback!" , Toast.LENGTH_SHORT).show();
                feedbackEditText.setText("");
                charRemainingTV.setText("500 characters remaining");
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
                list.add("Error Loading Organisations");
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
                    list.add(Lokacart_Tag);
                    adapter.notifyDataSetChanged();
                }else{

                    list.add("Error loading organisations");
                    adapter.notifyDataSetChanged();

                }



            }
        }
    }
}