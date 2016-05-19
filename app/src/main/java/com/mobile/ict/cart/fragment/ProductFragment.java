package com.mobile.ict.cart.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.Container.Product;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.adapter.CartAdapter;
import com.mobile.ict.cart.adapter.ProductAdapter;
import com.mobile.ict.cart.database.DBHelper;
import com.mobile.ict.cart.util.GetJSON;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;
import com.mobile.ict.cart.Container.ProductType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by vish on 21/3/16.
 */
public class ProductFragment extends Fragment {

    View productFragmentView;
    RelativeLayout relativeLayout;
    Drawer result;
    ProductType productType;
    Product product;
    //ArrayList<ProductType> productTypes ;
    Map <String,String> itemsMap = new HashMap<String, String>();
    Map <String,String> stockQuantityMap = new HashMap<String, String>();
     String stockEnabledStatus="false";
    RecyclerView recyclerView;
    DrawerBuilder drawerBuilder;
    private GridLayoutManager gridLayout;
    ProductAdapter swapAdapter;
    StaggeredGridLayoutManager layoutManager;
    DBHelper dbHelper;
    ImageView ivNoProduct, ivNoData;
    TextView tNoProduct, tNoData;

    //ArrayList<Product> cartList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        if(!Master.isNetworkAvailable(getActivity()))
        {
            productFragmentView = inflater.inflate(R.layout.no_internet_layout, container, false);

            /*productFragmentView = inflater.inflate(R.layout.fragment_products, container, false);

            dbHelper = new DBHelper(getActivity());

            //getActivity().item.setVisible(true);

            getActivity().setTitle(R.string.title_fragment_product);
            relativeLayout = (RelativeLayout) productFragmentView.findViewById(R.id.relativeLayout);
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

            recyclerView = (RecyclerView) productFragmentView.findViewById(R.id.rvProduct);
            recyclerView.setHasFixedSize(true);


            recyclerView.setLayoutManager(layoutManager);

            Master.productList = new ArrayList<>();
            Master.productList.add(0,new Product("name", 10, 0, 100, "true", "null", "null", "142"));
            ProductAdapter rcAdapter = new ProductAdapter(getActivity());
            recyclerView.swapAdapter(rcAdapter, false);

            cartList = new ArrayList<>();*/
        }
        else {

            Master.isProductClicked = false;
            productFragmentView = inflater.inflate(R.layout.fragment_products, container, false);

            dbHelper = new DBHelper(getActivity());

            //getActivity().item.setVisible(true);

            getActivity().setTitle(R.string.title_fragment_product);
            relativeLayout = (RelativeLayout) productFragmentView.findViewById(R.id.relativeLayout);

            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

            recyclerView = (RecyclerView) productFragmentView.findViewById(R.id.rvProduct);
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setMotionEventSplittingEnabled(false);

            //recyclerView.setEnabled(false);
            ivNoProduct = (ImageView) productFragmentView.findViewById(R.id.ivNoProduct);
            tNoProduct = (TextView) productFragmentView.findViewById(R.id.tNoProduct);

            ivNoProduct.setVisibility(View.GONE);
            tNoProduct.setVisibility(View.GONE);

            ivNoData = (ImageView) productFragmentView.findViewById(R.id.ivNoProduct);
            tNoData = (TextView) productFragmentView.findViewById(R.id.tNoProduct);

            ivNoData.setVisibility(View.GONE);
            tNoData.setVisibility(View.GONE);

            drawerBuilder = new DrawerBuilder();

            Master.cartList = new ArrayList<>();


            new GetProductsTask(savedInstanceState).execute();
        }

        return productFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*Master.productList = productTypes.get(selectedProductTypePosition).productItems;
        Master.cartList = dbHelper.getCartDetails(MemberDetails.getMobileNumber(), MemberDetails.getSelectedOrgAbbr());
        updateProductList();*/

        Master.isProductClicked = false;
        Log.e("Product frag", "in OnResume");

        if(recyclerView != null)
        {
            ProductAdapter rcAdapter = new ProductAdapter(getActivity());
            recyclerView.swapAdapter(rcAdapter, false);
        }
    }

    public class GetProductsTask extends AsyncTask<Void, String, String> {

        Bundle savedInstanceState;

        public GetProductsTask(Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Material.circularProgressDialog(getActivity(), getString(R.string.pd_fetching_products), false);
        }

        @Override
        protected String doInBackground(Void... params) {

            Master.getJSON = new GetJSON();
            String url = Master.getProductsURL(dbHelper.getSelectedOrg(MemberDetails.getMobileNumber())[0]);
            System.out.println("URL: " + url);
            Master.response = Master.getJSON.getJSONFromUrl(url, null, "GET", true,MemberDetails.getEmail(), MemberDetails.getPassword());
            System.out.println(Master.response);
            return Master.response;
        }

        @Override
        protected void onPostExecute(String response)
        {
            Material.circularProgressDialog.dismiss();

            if(!response.equals("exception"))
            {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.length() > 0)
                    {
                        Master.productTypeList = new ArrayList<>();

                        Iterator<String> keysItr = object.keys();
                        while(keysItr.hasNext())
                        {
                            String key = keysItr.next();

                            productType = new ProductType(key);
                            Object category = object.get(key);
                            System.out.println("Product type: " + productType.getName());
                            if(category instanceof JSONArray)
                            {
                                for(int i=0; i<((JSONArray)category).length();i++)
                                {
                                    if(((JSONArray)category).getJSONObject(i).has("imageUrl") && ((JSONArray)category).getJSONObject(i).has("audioUrl"))
                                    {
                                        if(((JSONArray)category).getJSONObject(i).getString("imageUrl") == null && ((JSONArray)category).getJSONObject(i).getString("audioUrl") == null)
                                        {
                                            product =new Product(((JSONArray)category).getJSONObject(i).getString("name"),
                                                    Double.parseDouble(((JSONArray)category).getJSONObject(i).getString("unitRate")),
                                                    0.0,
                                                    Integer.parseInt(((JSONArray)category).getJSONObject(i).getString("quantity")),
                                                    ((JSONArray)category).getJSONObject(i).getString("stockManagement"), "null", "null",
                                                    ((JSONArray)category).getJSONObject(i).getString("id"));
                                        }
                                        else if(((JSONArray)category).getJSONObject(i).getString("imageUrl") == null)
                                        {
                                            product=new Product(((JSONArray)category).getJSONObject(i).getString("name"),
                                                    Double.parseDouble(((JSONArray)category).getJSONObject(i).getString("unitRate")),
                                                    0.0,
                                                    Integer.parseInt(((JSONArray)category).getJSONObject(i).getString("quantity")),
                                                    ((JSONArray)category).getJSONObject(i).getString("stockManagement"),
                                                    "null",
                                                    ((JSONArray)category).getJSONObject(i).getString("audioUrl"),
                                                    ((JSONArray)category).getJSONObject(i).getString("id"));
                                        }
                                        else if (((JSONArray)category).getJSONObject(i).getString("audioUrl") == null)
                                        {
                                            product=new Product(((JSONArray)category).getJSONObject(i).getString("name"),
                                                    Double.parseDouble(((JSONArray)category).getJSONObject(i).getString("unitRate")),
                                                    0.0,
                                                    Integer.parseInt(((JSONArray)category).getJSONObject(i).getString("quantity")),
                                                    ((JSONArray)category).getJSONObject(i).getString("stockManagement"),
                                                    ((JSONArray)category).getJSONObject(i).getString("imageUrl"),
                                                    "null",
                                                    ((JSONArray)category).getJSONObject(i).getString("id"));
                                        }
                                        else
                                        {
                                            product=new Product(((JSONArray)category).getJSONObject(i).getString("name"),
                                                    Double.parseDouble(((JSONArray)category).getJSONObject(i).getString("unitRate")),
                                                    0.0,
                                                    Integer.parseInt(((JSONArray)category).getJSONObject(i).getString("quantity")),
                                                    ((JSONArray)category).getJSONObject(i).getString("stockManagement"),
                                                    ((JSONArray)category).getJSONObject(i).getString("imageUrl"),
                                                    ((JSONArray)category).getJSONObject(i).getString("audioUrl"),
                                                    ((JSONArray)category).getJSONObject(i).getString("id"));
                                        }
                                    }
                                    else if(((JSONArray)category).getJSONObject(i).has("imageUrl"))
                                    {
                                        if(((JSONArray)category).getJSONObject(i).getString("imageUrl") == null)
                                        {
                                            product=new Product(((JSONArray)category).getJSONObject(i).getString("name"),
                                                    Double.parseDouble(((JSONArray)category).getJSONObject(i).getString("unitRate")),
                                                    0.0,
                                                    Integer.parseInt(((JSONArray)category).getJSONObject(i).getString("quantity")),
                                                    ((JSONArray)category).getJSONObject(i).getString("stockManagement"),
                                                    "null",
                                                    "null",
                                                    ((JSONArray)category).getJSONObject(i).getString("id"));
                                        }
                                        else
                                        {
                                            product=new Product(((JSONArray)category).getJSONObject(i).getString("name"),
                                                    Double.parseDouble(((JSONArray)category).getJSONObject(i).getString("unitRate")),
                                                    0.0,
                                                    Integer.parseInt(((JSONArray)category).getJSONObject(i).getString("quantity")),
                                                    ((JSONArray)category).getJSONObject(i).getString("stockManagement"),
                                                    ((JSONArray)category).getJSONObject(i).getString("imageUrl"),
                                                    "null",
                                                    ((JSONArray)category).getJSONObject(i).getString("id"));
                                        }
                                    }
                                    else if(((JSONArray)category).getJSONObject(i).has("audioUrl"))
                                    {
                                        if(((JSONArray)category).getJSONObject(i).getString("audioUrl") == null)
                                        {
                                            product=new Product(((JSONArray)category).getJSONObject(i).getString("name"),
                                                    Double.parseDouble(((JSONArray)category).getJSONObject(i).getString("unitRate")),
                                                    0.0,
                                                    Integer.parseInt(((JSONArray)category).getJSONObject(i).getString("quantity")),
                                                    ((JSONArray)category).getJSONObject(i).getString("stockManagement"),
                                                    "null",
                                                    "null",
                                                    ((JSONArray)category).getJSONObject(i).getString("id"));
                                        }
                                        else
                                        {
                                            product=new Product(((JSONArray)category).getJSONObject(i).getString("name"),
                                                    Double.parseDouble(((JSONArray)category).getJSONObject(i).getString("unitRate")),
                                                    0.0,
                                                    Integer.parseInt(((JSONArray)category).getJSONObject(i).getString("quantity")),
                                                    ((JSONArray)category).getJSONObject(i).getString("stockManagement"),
                                                    "null",
                                                    ((JSONArray)category).getJSONObject(i).getString("audioUrl"),
                                                    ((JSONArray)category).getJSONObject(i).getString("id"));
                                        }
                                    }
                                    else
                                    {
                                        product=new Product(((JSONArray)category).getJSONObject(i).getString("name"),
                                                Double.parseDouble(((JSONArray)category).getJSONObject(i).getString("unitRate")),
                                                0.0,
                                                Integer.parseInt(((JSONArray)category).getJSONObject(i).getString("quantity")),
                                                ((JSONArray)category).getJSONObject(i).getString("stockManagement"),
                                                "null",
                                                "null",
                                                ((JSONArray)category).getJSONObject(i).getString("id"));
                                    }

                                    stockEnabledStatus = ((JSONArray)category).getJSONObject(i).getString("stockManagement");
                                    productType.productItems.add(product);
                                }
                            }

                            if(productType.productItems.size() > 0)
                            {
                                drawerBuilder.addDrawerItems(new PrimaryDrawerItem().withName(productType.getName()));
                                Log.e("Product frg", productType.getName() + " added to drawer");
                                Master.productTypeList.add(productType);
                            }

                        }

                        //setting up the navigation drawer
                        System.out.println("setting up the navigation drawer");

                        result = drawerBuilder
                                .withActivity(getActivity())
                                .withRootView(relativeLayout)
                                .withDisplayBelowStatusBar(false)
                                .withSavedInstance(savedInstanceState)
                                .withDrawerGravity(Gravity.END)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                                        drawerItemClick(position);
                                        //selectedProductTypePosition = position;
                                        return true;
                                    }
                                })
                                .buildForFragment();

                        result.getDrawerLayout().setFitsSystemWindows(true);
                        result.getSlider().setFitsSystemWindows(true);

                        Master.productList = Master.productTypeList.get(0).productItems;
                        Master.cartList = dbHelper.getCartDetails(MemberDetails.getMobileNumber(), MemberDetails.getSelectedOrgAbbr());

                        updateCart();
                        updateProductList();

                        ProductAdapter rcAdapter = new ProductAdapter(getActivity());
                        recyclerView.setAdapter(rcAdapter);

                        getActivity().setTitle(Master.productTypeList.get(0).getName());
                    }
                    else
                    {
                        //Material.alertDialog(getActivity(), getString(R.string.alert_no_products), "OK");
                        recyclerView.setVisibility(View.GONE);
                        ivNoProduct.setVisibility(View.VISIBLE);
                        tNoProduct.setVisibility(View.VISIBLE);
                    }
                }

                catch (JSONException e)
                {
                    Log.e("Product frag", "Inside catch. " + e.getMessage());
                }
            }
            else
            {
                // if exception

                recyclerView.setVisibility(View.GONE);
                ivNoData.setVisibility(View.VISIBLE);
                tNoData.setVisibility(View.VISIBLE);
            }

        }
    }

    public void drawerItemClick(int position)
    {
        result.closeDrawer();
        Log.e("Product frag", "Drawer clicked: " + position);

        Master.productList = Master.productTypeList.get(position).productItems;

        updateProductList();

        swapAdapter = new ProductAdapter(getActivity());
        recyclerView.swapAdapter(swapAdapter, false);
        getActivity().setTitle(Master.productTypeList.get(position).getName());
    }

    public void updateProductList()
    {
        for(int i = 0; i < Master.cartList.size(); ++i)
        {
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


    public void updateCart()
    {
        for(int i = 0; i < Master.productTypeList.size(); ++i)
        {
            for (int j = 0; j < Master.productTypeList.get(i).productItems.size(); ++j)
            {

                for(int z=0; z<Master.cartList.size();z++)
                {
                    if(Master.cartList.get(z).getID()==Master.productTypeList.get(i).productItems.get(j).getID())
                    {
                        Master.cartList.get(z).setName(Master.productTypeList.get(i).productItems.get(j).getName());
                        Master.cartList.get(z).setUnitPrice(Master.productTypeList.get(i).productItems.get(j).getUnitPrice());
                        Master.cartList.get(z).setImageUrl(Master.productTypeList.get(i).productItems.get(j).getImageUrl());
                        Master.cartList.get(z).setStockQuantity(Master.productTypeList.get(i).productItems.get(j).getStockQuantity());
                        Master.cartList.get(z).setStockEnabledStatus(Master.productTypeList.get(i).productItems.get(j).getStockEnabledStatus());

                        String total = String.format("%.2f",Master.productTypeList.get(i).productItems.get(j).getUnitPrice()*Master.cartList.get(z).getQuantity());

                        Master.cartList.get(z).setTotal(Double.parseDouble(total));

                        dbHelper.updateProduct(
                                String.valueOf( Master.cartList.get(z).getUnitPrice()),
                                String.valueOf( Master.cartList.get(z).getQuantity()),
                                String.valueOf( Master.cartList.get(z).getTotal()),
                                String.valueOf( Master.cartList.get(z).getName()),
                                MemberDetails.getMobileNumber(),
                                MemberDetails.getSelectedOrgAbbr(),
                                String.valueOf( Master.cartList.get(z).getID()),
                                String.valueOf( Master.cartList.get(z).getImageUrl()),
                                String.valueOf(Master.cartList.get(z).getStockQuantity()),
                                Master.cartList.get(z).getStockEnabledStatus()
                        );

                    }
                }

            }
        }
    }


}
