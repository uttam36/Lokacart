package com.mobile.ict.cart.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.Container.Organisations;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.activity.DashboardActivity;
import com.mobile.ict.cart.activity.DetailsFormActivity;
import com.mobile.ict.cart.adapter.OrganisationAdapter;
import com.mobile.ict.cart.database.DBHelper;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.JSONDataHelper;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;
import com.mobile.ict.cart.util.SharedPreferenceConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vish on 21/3/16.
 */
public class ChangeOrganisationFragment extends Fragment {

    View changeOrganisationFragmentView;
    Button bChangeOrganisation, bLeaveOrganisation;
    TextView tOrganisationZero;
    //public static ArrayList<Organisations> organisationList;
    String JSON;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mCurrentLayoutManagerType;
    DBHelper dbHelper;
    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        changeOrganisationFragmentView = inflater.inflate(R.layout.fragment_change_organisation, container, false);
        getActivity().setTitle(R.string.title_fragment_change_organisation);

        setHasOptionsMenu(true);

        dbHelper = new DBHelper(getActivity());
        mRecyclerView = (RecyclerView) changeOrganisationFragmentView.findViewById(R.id.organisationRecyclerView);
        tOrganisationZero = (TextView) changeOrganisationFragmentView.findViewById(R.id.tOrganisationZero);
        bChangeOrganisation = (Button) changeOrganisationFragmentView.findViewById(R.id.bChangeOrganisation);
        bLeaveOrganisation = (Button) changeOrganisationFragmentView.findViewById(R.id.bLeaveOrganisation);

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

        Organisations.organisationList = new ArrayList<Organisations>();
        Organisations.organisationList = JSONDataHelper.getOrganisationListFromJson(getActivity(), SharedPreferenceConnector
                .readString(getActivity().getApplicationContext(), Master.LOGIN_JSON, Master.DEFAULT_LOGIN_JSON));

        /*if(SharedPreferenceConnector.readString(getActivity(), Master.SELECTED_ORG_NAME, Master.DEFAULT_ORG_NAME)
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
        }*/
        String org[] = new String[2];
        org = dbHelper.getSelectedOrg(MemberDetails.getMobileNumber());

        /*if(org[0].equals("null"))
        {
            Master.dbHelper.setSelectedOrg(MemberDetails.getMobileNumber(),
                    Organisations.organisationList.get(0).getName(),
                    Organisations.organisationList.get(0).getOrgabbr());
        }
        else
        {
            */
            for(int i = 0; i < Organisations.organisationList.size(); ++i)
            {
                if(Organisations.organisationList.get(i).getName()
                        .equals(org[1]))
                {
                    Organisations.organisationList.get(i).setIsChecked(true);
                    break;
                }
            }
      //  }


        return changeOrganisationFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(Organisations.organisationList.size() == 0)
        {
            mRecyclerView.setVisibility(View.GONE);
            tOrganisationZero.setVisibility(View.VISIBLE);
            bChangeOrganisation.setVisibility(View.GONE);
            bLeaveOrganisation.setVisibility(View.GONE);
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

            bChangeOrganisation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < Organisations.organisationList.size(); ++i) {
                        if (Organisations.organisationList.get(i).getIsChecked()) {
                            /*SharedPreferenceConnector.writeString(getActivity().getApplicationContext(), Master.SELECTED_ORG_NAME,
                                    Organisations.organisationList.get(i).getName());
                            SharedPreferenceConnector.writeString(getActivity().getApplicationContext(), Master.SELECTED_ORG_ABBR,
                                    Organisations.organisationList.get(i).getOrgabbr());*/

                            dbHelper.setSelectedOrg(MemberDetails.getMobileNumber(),
                                    Organisations.organisationList.get(i).getName(),
                                    Organisations.organisationList.get(i).getOrgabbr());

                            MemberDetails.setSelectedOrgAbbr(Organisations.organisationList.get(i).getOrgabbr());
                            MemberDetails.setSelectedOrgName(Organisations.organisationList.get(i).getName());

                            Master.CART_ITEM_COUNT = dbHelper.getCartItemsCount(MemberDetails.getMobileNumber(),
                                    MemberDetails.getSelectedOrgAbbr());

                            getActivity().invalidateOptionsMenu();

                            Toast.makeText(getActivity(), R.string.toast_organisation_changed_successfully, Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }
            });

            bLeaveOrganisation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialogPro.Builder builder = new AlertDialogPro.Builder(getActivity());
                    builder.setCancelable(false);
                    builder.setMessage(R.string.alert_Do_you_want_terminate_this_organization_memebership);
                    builder.setTitle(Organisations.organisationList.get(OrganisationAdapter.getLastCheckedPos()).getName());

                    builder.setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put(Master.ORG_ABBR, Organisations.organisationList.get(OrganisationAdapter.getLastCheckedPos()).getOrgabbr());
                                jsonObject.put(Master.MOBILENUMBER, "91" + MemberDetails.getMobileNumber());

                                new LeaveOrganisationTask(OrganisationAdapter.getLastCheckedPos()).execute(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
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

    class LeaveOrganisationTask extends AsyncTask<JSONObject, String, String>
    {
        int position;
        LeaveOrganisationTask(int position)
        {
            this.position = position;
        }
        @Override
        protected void onPreExecute() {
            Material.circularProgressDialog(getActivity(), getString(R.string.pd_terminating_your_membership), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            Master.getJSON = new GetJSON();
            System.out.println("URL: " + Master.getLeaveOrganisationURL());
            System.out.println("PARAMS" + params[0]);
            Master.response = Master.getJSON.getJSONFromUrl(Master.getLeaveOrganisationURL(), params[0], "POST", true,
                    MemberDetails.getEmail(), MemberDetails.getPassword());
            System.out.println(Master.response);
            return Master.response;
        }

        @Override
        protected void onPostExecute(String s) {
            Material.circularProgressDialog.dismiss();
            try {
                Master.responseObject = new JSONObject(s);
                if(Master.response.equals("exception"))
                {
                    Toast.makeText(getActivity(), R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
                }
                else if(Master.responseObject.getString(Master.RESPONSE).equals("Membership removed")
                        || Master.responseObject.getString(Master.RESPONSE).equals("error"))
                {
                    Toast.makeText(getActivity(), R.string.toast_Membership_terminated_successfully, Toast.LENGTH_LONG).show();


                    String org[] = new String[2];
                    org = dbHelper.getSelectedOrg(MemberDetails.getMobileNumber());

                    if(org[1].equals(Organisations.organisationList.get(position).getName()))
                    {
                        Organisations.organisationList.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        Organisations.organisationList.get(0).setIsChecked(true);
                        mAdapter.notifyItemChanged(0);

                        dbHelper.setSelectedOrg(MemberDetails.getMobileNumber(),
                                Organisations.organisationList.get(0).getName(),
                                Organisations.organisationList.get(0).getOrgabbr());
                    }
                    else
                    {
                        Organisations.organisationList.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        Organisations.organisationList.get(0).setIsChecked(true);
                        mAdapter.notifyItemChanged(0);
                    }


                    /*SharedPreferenceConnector.writeString(getActivity(), Master.SELECTED_ORG_NAME, Organisations.organisationList.get(0).getName());
                    SharedPreferenceConnector.writeString(getActivity(), Master.SELECTED_ORG_ABBR, Organisations.organisationList.get(0).getOrgabbr());*/


                }
                else if(Master.responseObject.getString(Master.RESPONSE).equals("Cannot be deleted"))
                {
                    Toast.makeText(getActivity(), R.string.toast_Membership_cannot_be_terminated, Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Toast.makeText(getActivity(), R.string.alert_something_went_wrong, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
            }
            else
            {
                    SharedPreferenceConnector.writeString(getActivity().getApplicationContext(), Master.LOGIN_JSON, response);
            }
        }
    }
}
