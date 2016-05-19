package com.mobile.ict.cart.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.test.MoreAsserts;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alertdialogpro.AlertDialogPro;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.Container.Product;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.adapter.CartAdapter;
import com.mobile.ict.cart.database.DBHelper;
import com.mobile.ict.cart.interfaces.DeleteProductListener;
import com.mobile.ict.cart.util.CartIconDrawable;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements DeleteProductListener {

    //ArrayList<Product> cartList;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    LinearLayout cartButtonLinearLayout;
    RelativeLayout emptyCartLinearLayout, noUpdateRelativeLayout;
    CartAdapter cartAdapter;
  //  Dialog dialog ;
    TextView cartTotal;
    DBHelper dbHelper;
    double sum=0.0;
    String response,val,orderid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Master.cartList = new ArrayList<>();
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutManager = new LinearLayoutManager(this);

        cartButtonLinearLayout = (LinearLayout) findViewById(R.id.cartButtonLinearLayout);

        emptyCartLinearLayout = (RelativeLayout) findViewById(R.id.cartEmptyRelativeLayout);
        emptyCartLinearLayout.setVisibility(View.GONE);
        noUpdateRelativeLayout = (RelativeLayout) findViewById(R.id.noUpdateRelativeLayout);
        noUpdateRelativeLayout.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.rvCart);
        recyclerView.setHasFixedSize(true);

        //recyclerView.setLayoutManager(gridLayout);
        recyclerView.setLayoutManager(layoutManager);

        cartTotal = (TextView)findViewById(R.id.tcartTotal);

        dbHelper = new DBHelper(this);
        //Master.dbHelper = new DBHelper(getApplicationContext());

        Master.cartList = dbHelper.getCartDetails(MemberDetails.getMobileNumber(), MemberDetails.getSelectedOrgAbbr());





        if(Master.cartList.isEmpty())
        {
            Master.CART_ITEM_COUNT=0;
            invalidateOptionsMenu();

            cartButtonLinearLayout.setVisibility(View.GONE);
            emptyCartLinearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);


        }

       else
        {

            emptyCartLinearLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            cartButtonLinearLayout.setVisibility(View.VISIBLE);

            //checkCart();

            Master.CART_ITEM_COUNT=Master.cartList.size();
            invalidateOptionsMenu();

            for(int i = 0; i<Master.cartList.size(); ++i )
            {
                sum=sum+Master.cartList.get(i).getTotal();
            }
            cartTotal.setText(""+String.format("%.2f",sum));

        }


        cartAdapter = new CartAdapter(this,cartTotal);
        recyclerView.setAdapter(cartAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onSupportNavigateUp() {
        saveCart();
        finish();
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void saveCart()
    {
        for(int i=0;i<Master.cartList.size();i++)
        {
            dbHelper.updateProduct(
                    String.valueOf(Master.cartList.get(i).getUnitPrice()),
                    String.valueOf(Master.cartList.get(i).getQuantity()),
                    String.valueOf(Master.cartList.get(i).getTotal()),
                    String.valueOf(Master.cartList.get(i).getName()),
                    MemberDetails.getMobileNumber(),
                    MemberDetails.getSelectedOrgAbbr(),
                    String.valueOf(Master.cartList.get(i).getID()),
                    String.valueOf(Master.cartList.get(i).getImageUrl()),
                    String.valueOf(Master.cartList.get(i).getStockQuantity()),
                    Master.cartList.get(i).getStockEnabledStatus()
            );

            for(int j = 0; j< Master.productList.size(); ++j)
            {
                if(Master.productList.get(j).getID().equals(Master.cartList.get(i).getID()))
                {
                    Master.productList.get(j).setQuantity(Master.cartList.get(i).getQuantity());
                    break;
                }
            }

        }
    }


    /*void checkCart()
    {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Master.ORG_ABBR, MemberDetails.getSelectedOrgAbbr());

            JSONArray products = new JSONArray();
            JSONObject product;
            for(int i = 0; i < Master.cartList.size(); ++i)
            {
                product = new JSONObject();
                product.put(Master.PRICE, Master.cartList.get(i).getUnitPrice());
                product.put(Master.PRODUCT_NAME, Master.cartList.get(i).getName());
                product.put(Master.ID, Master.cartList.get(i).getID());
                product.put(Master.IMAGE_URL, Master.cartList.get(i).getImageUrl());
                product.put(Master.AUDIO_URL, Master.cartList.get(i).getAudioUrl());
                product.put(Master.STOCK_QUANTITY, Master.cartList.get(i).getStockQuantity());
                products.put(product);
            }
            jsonObject.put("products", products);
            System.out.println("JSONObject" + jsonObject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/




    public void onCheckOut(View v)
    {

        if(Master.isNetworkAvailable(this))
        {
            try {
                boolean qty=false;
                JSONObject order = new JSONObject();
                order.put("orgabbr",MemberDetails.getSelectedOrgAbbr());
                order.put("groupname", "Parent Group");
                JSONArray products = new JSONArray();
                JSONObject object;

                for(int i=0;i<Master.cartList.size();i++)
                {
                    object  = new JSONObject();
                    object.put("name",Master.cartList.get(i).getName());
                    double itemTotal = Master.cartList.get(i).getQuantity();
                     if(itemTotal==0.0)
                     {
                         qty=true;
                         break;
                     }

                    object.put("quantity",Master.cartList.get(i).getQuantity());
                    products.put(object);
                }

                order.put("orderItems", products);

                System.out.println("json-------" + order.toString());

                if(!qty)
                   new PlacingOrderTask().execute(order);
                 else
                    Material.alertDialog(this, getString(R.string.alert_please_insert_quantity), "OK");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            Material.alertDialog(this, getString(R.string.toast_Please_check_internet_connection), "OK");
        }
    }

    @Override
    public void deleteProduct(final int position,final String productID) {



        AlertDialogPro.Builder builder= new AlertDialogPro.Builder(this);
        builder.setMessage(getResources().getString(R.string.alert_do_you_really_want_to_remove));
        builder.setCancelable(true);
        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        for(int j = 0; j< Master.productList.size(); ++j)
                        {
                            if(Master.productList.get(j).getID().equals(productID))
                            {
                                Master.productList.get(j).setQuantity(0);
                                break;
                            }
                        }

                        Master.cartList.remove(position);
                        cartAdapter.notifyItemRemoved(position);
                        cartAdapter.notifyDataSetChanged();
                        dialog.dismiss();


                        dbHelper.deleteProduct(MemberDetails.getMobileNumber(), productID);

                        if (Master.cartList.isEmpty()) {
                            Master.CART_ITEM_COUNT = 0;
                            cartButtonLinearLayout.setVisibility(View.GONE);

                            emptyCartLinearLayout.setVisibility(View.VISIBLE);

                            recyclerView.setVisibility(View.GONE);

                        } else {
                            Master.CART_ITEM_COUNT--;

                            sum = 0.0;
                            for (int i = 0; i < Master.cartList.size(); ++i) {
                                sum = sum + Master.cartList.get(i).getTotal();
                            }

                            cartTotal.setText("" + String.format("%.2f",sum));

                        }
                        invalidateOptionsMenu();
                    }
                });
        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sum = 0.0;
                        for (int i = 0; i < Master.cartList.size(); ++i) {
                            sum = sum + Master.cartList.get(i).getTotal();
                        }

                        cartTotal.setText("" + String.format("%.2f",sum));
                    }
                }
        );
        AlertDialogPro alert11 = builder.create();
        alert11.show();


    }


    @Override
    protected void onResume() {
        super.onResume();

    }



    private class PlacingOrderTask extends AsyncTask<JSONObject,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(CartActivity.this, getString(R.string.pd_sending_data_to_server), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            GetJSON getJson = new GetJSON();
            response = getJson.getJSONFromUrl(Master.getPlacingOrderURL(),params[0],"POST",true,MemberDetails.getEmail(),MemberDetails.getPassword());
            System.out.println("--------detail response--------"+response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            Material.circularProgressDialog.dismiss();
            if(s.equals("exception"))
            {
                Material.alertDialog(CartActivity.this, getString(R.string.alert_cannot_connect_to_the_server), "OK");
            }
            else {
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(s);
                    val=jsonObj.getString("status");

                    if(val.equals("Success"))
                    {
                        Master.CART_ITEM_COUNT=0;
                        invalidateOptionsMenu();
                        orderid=jsonObj.getString("orderId");

                        dbHelper.deleteCart(MemberDetails.getMobileNumber());

                        Intent i = new Intent(CartActivity.this,OrderSubmitActivity.class);
                        i.putExtra("orderid",orderid);
                        startActivity(i);
                        finish();

                    }else  if(val.equals("Failure"))
                    {
                        String error[] = {jsonObj.getString("error")};

                        Toast.makeText(CartActivity.this, getResources().getString(R.string.label_toast_sorry_we_are_unable_to_process_order_items)+error[0] + getResources().getString(R.string.label_toast_out_of_stock), Toast.LENGTH_SHORT).show();


                    }
                    else

                        Toast.makeText(CartActivity.this, R.string.label_toast_Sorry_we_were_unable_to_process_your_request_Please_try_again_later, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /*private class VerifyCartTask extends AsyncTask<JSONObject,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(CartActivity.this, getString(R.string.pd_verifying_cart_details), false);
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            GetJSON getJson = new GetJSON();
            response = getJson.getJSONFromUrl(Master.verifyCartURL(), params[0], "POST", true,
                    MemberDetails.getEmail(), MemberDetails.getPassword());
            System.out.println("--------cart verify response--------"+response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            Material.circularProgressDialog.dismiss();
            if(s.equals("exception"))
            {
                //Material.alertDialog(CartActivity.this, getString(R.string.alert_cannot_connect_to_the_server), "OK");
                recyclerView.setVisibility(View.GONE);
                noUpdateRelativeLayout.setVisibility(View.VISIBLE);
            }
            else {
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(s);

                    JSONArray jsonArray = jsonObj.getJSONArray("products");

                    JSONObject tempObj;

                    for(int i = 0; i < jsonArray.length(); ++i)
                    {
                        tempObj = jsonArray.getJSONObject(i);
                        if(tempObj.get("changed").equals("1"))
                        {
                            for(int j = 0; j < Master.cartList.size(); ++j)
                            {
                                if(tempObj.get(Master.ID).equals(Master.cartList.get(j).getID()))
                                {
                                    Master.cartList.get(j).setName(tempObj.getString(Master.PRODUCT_NAME));
                                    Master.cartList.get(j).setUnitPrice(Double.parseDouble(tempObj.getString(Master.PRICE)));
                                    Master.cartList.get(j).setAudioUrl(tempObj.getString(Master.AUDIO_URL));
                                    Master.cartList.get(j).setImageUrl(tempObj.getString(Master.IMAGE_URL));
                                    Master.cartList.get(j).setStockQuantity(Integer.parseInt(tempObj.getString(Master.STOCK_QUANTITY)));

                                    String total = String.format("%.2f",Double.parseDouble(tempObj.getString(Master.PRICE))*Master.cartList.get(j).getQuantity());

                                    Master.cartList.get(j).setTotal(Double.parseDouble(total));

                                    dbHelper.updateProduct(
                                            String.valueOf( Master.cartList.get(j).getUnitPrice()),
                                            String.valueOf( Master.cartList.get(j).getQuantity()),
                                            String.valueOf( Master.cartList.get(j).getTotal()),
                                            String.valueOf( Master.cartList.get(j).getName()),
                                            MemberDetails.getMobileNumber(),
                                            MemberDetails.getSelectedOrgAbbr(),
                                            String.valueOf( Master.cartList.get(j).getID()),
                                            String.valueOf( Master.cartList.get(j).getImageUrl())
                                    );


                                }
                            }
                        }

                        else
                        {

                            for(int j = 0; j < Master.cartList.size(); ++j)
                            {
                                if(tempObj.get(Master.ID).equals(Master.cartList.get(j).getID()))
                                {
                                    if(tempObj.getBoolean(Master.STOCK_MANAGEMENT_STATUS))
                                    {
                                        if(Integer.parseInt(tempObj.getString(Master.STOCK_QUANTITY)) < Master.cartList.get(j).getStockQuantity())

                                            Master.cartList.get(j).setName(Master.cartList.get(j).getName() + "\n" + "OUT OF STOCK");

                                    }


                                    cartAdapter.notifyItemChanged(j);

                                }


                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }*/



}
