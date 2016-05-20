package com.mobile.ict.cart.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.Container.Order;
import com.mobile.ict.cart.Container.PlacedOrder;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.adapter.OrganisationAdapter;
import com.mobile.ict.cart.adapter.PlacedOrderAdapter;
import com.mobile.ict.cart.interfaces.DeletePlacedOrderInterface;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;
import com.mobile.ict.cart.util.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by vish on 21/3/16.
 */
public class PlacedOrderFragment extends Fragment implements DeletePlacedOrderInterface{

    View placedOrderFragmentView;
    ArrayList<JSONObject> orderObjects;
    List<PlacedOrder> placedOrders,orders;
    PlacedOrder placedOrder;
    Dialog dialog;
    AlertDialogPro feedbackDialog;
    RecyclerView mRecyclerView;
    PlacedOrderAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mCurrentLayoutManagerType;
   // Dialog feedbackDialog ;
    Window window;
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    Point size;
    WindowManager windowManager;
    Display display;
    int width,height,optionIndex=-1;
    EditText otherReason;
    String options="",comments,otherReasonText="";
    String stockEnabledStatus="false";
    HashMap<String,Double> hmap = new HashMap<>();
    int pos,orderId,lastpos=0;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        placedOrderFragmentView = inflater.inflate(R.layout.fragment_placed_order, container, false);
        mRecyclerView = (RecyclerView) placedOrderFragmentView.findViewById(R.id.rvPlacedOrder);
        getActivity().setTitle(R.string.title_fragment_placed_order);
        new FetchOrders().execute();
        return placedOrderFragmentView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        if (savedInstanceState != null) {
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }



        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
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




    public List<PlacedOrder> getOrders() {

        orders = new ArrayList<>();

        int count=0;


        if(orderObjects.size()!=0)

        {
            for (JSONObject entry : orderObjects) {
                  Order order = new Order(entry, count);

                placedOrder = new PlacedOrder(order, Arrays.asList(order.getItemsList(count)));
                orders.add(placedOrder);
                count++;
            }
        }
        else
        {


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.alert_No_Cancel_Orders_Found)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss()
                            ;
                        }
                    });

            dialog = builder.show();
        }

        return orders;
    }


    @Override
    public void deletePlacedOrder()
   // public void deletePlacedOrder(int orderId,int position)
    {
        //orderID=orderId;

        System.out.println("deleting order------"+"pos------------"+pos+"-----------orderId-------"+orderId);
       // pos=position;
        openFeedBackDialog(orderId);
    }



    public void openFeedBackDialog( final int orderId)
    {

        //feedbackDialog = new Dialog(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View Layout = inflater.inflate(R.layout.cancel_placed_order_feedback, (ViewGroup)placedOrderFragmentView. findViewById(R.id.cancel_order_feedback), false);
       // feedbackDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // feedbackDialog.setContentView(Layout);

        final AlertDialogPro.Builder builder = new AlertDialogPro.Builder(getActivity());
        builder.setView(Layout);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        feedbackDialog = builder.create();

        feedbackDialog.show();


      //  Button feedbackOk = (Button)Layout.findViewById(R.id.feedbackOk);
      //  Button feedbackClose = (Button)Layout.findViewById(R.id.feedbackClose);
        final ListView lv = (ListView)Layout.findViewById(R.id.lv1);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        otherReason =(EditText)Layout.findViewById(R.id.comments);
        otherReason.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(!s.equals(""))
                    otherReasonText=s.toString();
                else
                    otherReasonText="";
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        final ArrayList<HashMap<String, Object>> m_data = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("option",getResources().getString(R.string.label_cancelorder_feedback_form_option1));
        m_data.add(map1);

        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("option", getResources().getString(R.string.label_cancelorder_feedback_form_option2));// no small text of this item!
        m_data.add(map2);

        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("option", getResources().getString(R.string.label_cancelorder_feedback_form_option3));
        m_data.add(map3);

        HashMap<String, Object> map4 = new HashMap<String, Object>();
        map4.put("option", getResources().getString(R.string.label_cancelorder_feedback_form_option4));
        m_data.add(map4);

        HashMap<String, Object> map5 = new HashMap<String, Object>();
        map5.put("option", getResources().getString(R.string.label_cancelorder_feedback_form_option5));
        m_data.add(map5);

        for (HashMap<String, Object> m :m_data)
            m.put("checked", false);

      /*  if(dataFragment.getFeedBackDialogId()==1 && optionIndex!=-1)
        {
            m_data.get(optionIndex).put("checked", true);
            options=m_data.get(optionIndex).get("option").toString();
            if(m_data.get(optionIndex).get("option").equals(getResources().getString(R.string.label_cancelorder_feedback_form_option5)))
            {
                otherReason.setVisibility(View.VISIBLE);

                if(!otherReasonText.equals(""))
                    otherReason.setText(otherReasonText);

            }
        }*/

        final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                m_data,
                R.layout.cancel_placed_order_feedback_item,
                new String[] {"option","checked"},
                new int[] {R.id.options, R.id.rb_choice});

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data, String textRepresentation) {

                view.setVisibility(View.VISIBLE);
                return false;
            }

        });


        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
                RadioButton rb = (RadioButton) v.findViewById(R.id.rb_choice);

                if (m_data.get(arg2).get("option").equals(getResources().getString(R.string.label_cancelorder_feedback_form_option5))) {
                    otherReason.setVisibility(View.VISIBLE);
                } else {
                    if (otherReason.isShown()) {
                        otherReason.setVisibility(View.GONE);
                        otherReason.setError(null);

                    }

                }

                options = m_data.get(arg2).get("option").toString();

                optionIndex = arg2;

                if (!rb.isChecked()) {
                    for (HashMap<String, Object> m : m_data)
                        m.put("checked", false);
                    m_data.get(arg2).put("checked", true);
                    adapter.notifyDataSetChanged();
                }
            }
        });




/*

        window =feedbackDialog.getWindow();

        size = new Point();
        windowManager = getActivity().getWindowManager();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            windowManager.getDefaultDisplay().getSize(size);

            width = size.x;
            height = size.y;
        }else{
            display = windowManager.getDefaultDisplay();
            width = display.getWidth();
            height = display.getHeight();
        }



        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
        {
            lp.width=(int)(width/1.125);
            lp.height = (int)(height/1.5);


        } else
        {
            lp.width=width/2;
            lp.height = (int)(height/1.25);


        }

        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        feedbackDialog.setCancelable(false);
*/

        feedbackDialog.getButton(AlertDialogPro.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (options.equals("")) {
                    Toast.makeText(getActivity(),R.string.label_toast_cancel_order_feedback_form, Toast.LENGTH_SHORT).show();

                } else {
                    if(options.equals(getResources().getString(R.string.label_cancelorder_feedback_form_option5)))
                    {
                        if(!otherReason.getText().toString().equals(""))
                        {

                            comments=otherReason.getText().toString().replaceAll("\\n", " ");
                            otherReasonText=comments;
                            feedbackDialog.dismiss();
                            new CancelOrder().execute(String.valueOf(orderId));



                        }
                        else
                        {
                            Validation.hasText(getActivity(), otherReason);

                        }
                    }
                    else
                    {
                        comments=options;

                        feedbackDialog.dismiss();
                        new CancelOrder().execute(String.valueOf(orderId));



                    }
                }

                options="";


            }
        });

        feedbackDialog.getButton(AlertDialogPro.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherReasonText = "";
                feedbackDialog.dismiss();
            }
        });
        /*feedbackOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(options.equals(""))
                {
                    Toast.makeText(getActivity(),R.string.label_toast_cancel_order_feedback_form, Toast.LENGTH_SHORT).show();

                }
                else
                {
                    if(options.equals(getResources().getString(R.string.label_cancelorder_feedback_form_option5)))
                    {
                        if(!otherReason.getText().toString().equals(""))
                        {

                            comments=otherReason.getText().toString().replaceAll("\\n", " ");
                            otherReasonText=comments;
                            feedbackDialog.dismiss();
                            new CancelOrder().execute(String.valueOf(orderId));



                        }
                        else
                        {
                            Validation.hasText(getActivity(), otherReason);

                        }
                    }
                    else
                    {
                        comments=options;

                        feedbackDialog.dismiss();
                        new CancelOrder().execute(String.valueOf(orderId));



                    }
                }

                options="";


            }
        });

        feedbackClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherReasonText="";
                feedbackDialog.dismiss();
            }
        });*/








    }


    @SuppressLint("NewApi")
    public class FetchOrders extends AsyncTask<String,String, String>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(getActivity(), getString(R.string.pd_fetching_orders), false);

        }

        @Override
        protected String doInBackground(String... params) {



                Master.getJSON = new GetJSON();

              String url = "http://ruralict.cse.iitb.ac.in/RuralIvrs/api/orders/search/getOrdersForMember?format=binary&status=saved&abbr=Test2&phonenumber=919827755129&projection=default";
              System.out.println("URL: " + url);
              Master.response = Master.getJSON.getJSONFromUrl(url, null, "GET", true, "chetan.jaiswal411@gmail.com", "qwer");
              System.out.println(Master.response);
              return Master.response;

        }

        @Override
        protected void onPostExecute(String response) {

            Material.circularProgressDialog.dismiss();

            if(response.equals("exception"))
            {
                Material.alertDialog(getActivity(), getString(R.string.alert_cannot_connect_to_the_server), "OK");

            }
            else
            {
                try {


                    JSONObject orderList = new JSONObject(response);
                    JSONArray ordersArray = orderList.getJSONObject("_embedded").getJSONArray("orders");
                    orderObjects=new ArrayList<JSONObject>();

                    for(int i=0;i<ordersArray.length();i++)
                    {
                        orderObjects.add((JSONObject)ordersArray.get(i));

                    }


                    placedOrders = getOrders();
                    mAdapter= new PlacedOrderAdapter(getActivity(),placedOrders,PlacedOrderFragment.this);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {

                        @Override
                        public void onListItemExpanded(int position) {

                            if(lastpos!=position)
                            {
                                mAdapter.collapseParent(lastpos);
                            }
                            lastpos=position;
                            pos=position;
                            orderId=placedOrders.get(position).getOrder().getOrder_id();
                        }

                        @Override
                        public void onListItemCollapsed(int position) {

                        }
                    });


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                    Material.alertDialog(getActivity(), getString(R.string.alert_No_Cancel_Orders_Found), "OK");


                }




            }

        }



    }



    public class CancelOrder extends AsyncTask<String, String, String>{


        String val;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Material.circularProgressDialog(getActivity(), getString(R.string.pd_fetching_orders), false);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            String url="http://ruralict.cse.iitb.ac.in/RuralIvrs/api/orders/update/"+params[0];


            JSONObject obj = new JSONObject();
            try {
                obj.put("status","cancelled");
                obj.put("comments","");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("URL: " + url);
            Master.response = Master.getJSON.getJSONFromUrl(url,obj, "POST", true, "chetan.jaiswal411@gmail.com", "qwer");
            System.out.println(Master.response);
            return Master.response;





        }

        protected void onPostExecute(String response1) {

            System.out.println("response-------------------------"+response1);

            Material.circularProgressDialog.dismiss();

            if(response1.equals("exception"))
            {
                Material.alertDialog(getActivity(), getString(R.string.alert_cannot_connect_to_the_server), "OK");
            }
            else {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(response1);
                    val=jsonObj.getString("status");

                    if(val.equals("Success"))
                    {

                        Toast.makeText(getActivity(),R.string.label_toast_Your_order_has_been_cancelled_successfully, Toast.LENGTH_SHORT).show();

                        System.out.println("removing pos-------------" + pos);
                        mAdapter.collapseParent(pos);
                        placedOrders.remove(pos);
                        mAdapter.notifyParentItemRemoved(pos);
                        mAdapter.notifyDataSetChanged();


                    }

                    else

                        Toast.makeText(getActivity(),R.string.label_toast_Sorry_we_were_unable_to_process_your_request_Please_try_again_later, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }

    }




}
