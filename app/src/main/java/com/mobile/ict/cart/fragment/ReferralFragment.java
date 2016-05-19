package com.mobile.ict.cart.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.Container.Organisations;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.activity.DashboardActivity;
import com.mobile.ict.cart.adapter.OrganisationAdapter;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.JSONDataHelper;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;
import com.mobile.ict.cart.util.SharedPreferenceConnector;
import com.mobile.ict.cart.util.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vish on 21/3/16.
 */
public class ReferralFragment extends Fragment {

    View referralFragmentView;
    EditText eEmail, eMobileNumber;
    TextView tOrganisationZero;
    Button bSendReferral;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mCurrentLayoutManagerType;
    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    String refEmail, refMobileNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        referralFragmentView = inflater.inflate(R.layout.fragment_referral, container, false);
        getActivity().setTitle(R.string.title_fragment_referral);

       // DashboardActivity.item.setVisible(false);

        mRecyclerView = (RecyclerView) referralFragmentView.findViewById(R.id.referralRecyclerView);
        eMobileNumber = (EditText) referralFragmentView.findViewById(R.id.eReferralMobileNumber);
        eEmail = (EditText) referralFragmentView.findViewById(R.id.eReferralEmail);
        bSendReferral = (Button) referralFragmentView.findViewById(R.id.bSendReferral);
        tOrganisationZero = (TextView) referralFragmentView.findViewById(R.id.tOrganisationZero);

        Organisations.organisationList = new ArrayList<Organisations>();

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

        Organisations.organisationList = JSONDataHelper.getOrganisationListFromJson(getActivity(), SharedPreferenceConnector
                .readString(getActivity().getApplicationContext(), Master.LOGIN_JSON, Master.DEFAULT_LOGIN_JSON));

        if(SharedPreferenceConnector.readString(getActivity(), Master.SELECTED_ORG_NAME, Master.DEFAULT_ORG_NAME)
                .equals(Master.DEFAULT_ORG_NAME))
        {
            Organisations.organisationList.get(0).setIsChecked(true);
            SharedPreferenceConnector.writeString(getActivity(), Master.SELECTED_ORG_NAME, Organisations.organisationList.get(0).getName());
            SharedPreferenceConnector.writeString(getActivity(), Master.SELECTED_ORG_ABBR, Organisations.organisationList.get(0).getOrgabbr());
        }
        else
        {
            for(int i = 0; i < Organisations.organisationList.size(); ++i)
            {
                if(Organisations.organisationList.get(i).getName()
                        .equals(SharedPreferenceConnector.readString(getActivity(), Master.SELECTED_ORG_NAME, Master.DEFAULT_ORG_NAME)))
                {
                    Organisations.organisationList.get(i).setIsChecked(true);
                    break;
                }
            }
        }

        return referralFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(Organisations.organisationList.size() == 0)
        {
            mRecyclerView.setVisibility(View.GONE);
            tOrganisationZero.setVisibility(View.VISIBLE);
            eEmail.setVisibility(View.GONE);
            eMobileNumber.setVisibility(View.GONE);
            bSendReferral.setVisibility(View.GONE);
        }
        else
        {
            mRecyclerView.setHasFixedSize(true);
            System.out.println(Organisations.organisationList);
            mAdapter = new OrganisationAdapter(getActivity());
            mRecyclerView.setAdapter(mAdapter);

            // mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, Master.savedToProcessedKey, PendingOrderFragment.this));

            mLayoutManager = new LinearLayoutManager(getActivity());
            mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
            if (savedInstanceState != null) {
                mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                        .getSerializable(KEY_LAYOUT_MANAGER);
            }
            setRecyclerViewLayoutManager(mCurrentLayoutManagerType);



            bSendReferral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialogPro.Builder builder = new AlertDialogPro.Builder(getActivity());
                    builder.setCancelable(false);
                    builder.setMessage(R.string.alert_Do_you_want_to_refer_the_person);
                    builder.setTitle(Organisations.organisationList.get(OrganisationAdapter.getLastCheckedPos()).getName());

                    builder.setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            refMobileNumber = eMobileNumber.getText().toString().trim();
                            refEmail = eEmail.getText().toString().trim();

                            if (refMobileNumber.equals("") || refEmail.equals("")) {
                                Toast.makeText(getActivity(), R.string.toast_all_fileds_are_mandatory, Toast.LENGTH_LONG).show();
                            } else if (!Validation.isValidEmail(refEmail)) {

                            } else if (!Validation.isValidMobileNumber(refMobileNumber)) {

                            } else {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put(Master.MOBILENUMBER, "91" + refMobileNumber);
                                    jsonObject.put(Master.EMAIL, refEmail);
                                    jsonObject.put(Master.REFER_ORG_ABBR, Organisations.organisationList
                                            .get(OrganisationAdapter.getLastCheckedPos()).getOrgabbr());
                                    jsonObject.put(Master.REFER_EMAIL, MemberDetails.getEmail());

                                    new SendReferralTask().execute(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                    builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType)
        {
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;

            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    public class SendReferralTask extends AsyncTask<JSONObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(getActivity(), getString(R.string.pd_fetching_data_from_server), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {

            GetJSON getJson = new GetJSON();
            System.out.println("PARAMS: " + params[0]);
            System.out.println("URL: " + Master.getSendReferralURL());
            Master.response = getJson.getJSONFromUrl(Master.getSendReferralURL(), params[0], "POST", true
                    , MemberDetails.getEmail(), MemberDetails.getPassword());
            System.out.println(Master.response);
            return Master.response;
        }

        @Override
        protected void onPostExecute(String response) {
            Material.circularProgressDialog.dismiss();
            System.out.println("------------" + response);
            if (response.equals("exception")) {
                Toast.makeText(getActivity(), R.string.alert_cannot_connect_to_the_server, Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    Master.responseObject = new JSONObject(Master.response);
                    if(Master.responseObject.get(Master.RESPONSE).equals("success"))
                    {
                        eMobileNumber.setText("");
                        eEmail.setText("");
                        Toast.makeText(getActivity(), R.string.toast_member_referred_successfully, Toast.LENGTH_LONG).show();
                    }
                    else if (Master.responseObject.get(Master.RESPONSE).equals("Already a member"))
                    {
                        Material.alertDialog(getActivity(), getString(R.string.toast_already_a_member), "OK");
                        //Toast.makeText(getActivity(), R.string.toast_already_a_member, Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("Referral frag", "");
                    Toast.makeText(getActivity(), R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public class GetOrganisationsTask extends AsyncTask<JSONObject, String, String> {

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
            }
            else
            {
                SharedPreferenceConnector.writeString(getActivity().getApplicationContext(), Master.LOGIN_JSON, response);
            }
        }
    }
}
