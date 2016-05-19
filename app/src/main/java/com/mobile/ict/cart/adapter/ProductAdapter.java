package com.mobile.ict.cart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mobile.ict.cart.Container.MemberDetails;
import com.mobile.ict.cart.Container.Product;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.activity.ProductDetailActivity;
import com.mobile.ict.cart.database.DBHelper;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;

import java.util.ArrayList;

/**
 * Created by vish on 17/4/16.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.DataObjectHolder> {
    
    private Context context;
    static DBHelper dbHelper;

    public ProductAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
    {
        TextView tProductName, tPrice, tAvailable, tQuantity;
        ImageView ivProduct, ivBuy;

        RelativeLayout rl;

        public DataObjectHolder(final View itemView, final Context context)
        {
            super(itemView);
            tProductName = (TextView) itemView.findViewById(R.id.tProductName);
            tPrice = (TextView) itemView.findViewById(R.id.tPrice);
            tAvailable = (TextView) itemView.findViewById(R.id.tAvailable);
            ivProduct = (ImageView) itemView.findViewById(R.id.ivProduct);
            ivBuy = (ImageView) itemView.findViewById(R.id.ivBuy);
            tQuantity = (TextView) itemView.findViewById(R.id.tQuantity);
            rl = (RelativeLayout) itemView.findViewById(R.id.contentRL);


            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Master.isProductClicked = true;
                    Intent i = new Intent(context, ProductDetailActivity.class);
                    i.putExtra("position", getAdapterPosition());
                    context.startActivity(i);
                }
            });

            ivBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(Master.productList.get(getAdapterPosition()).getStockEnabledStatus().equals("true")
                            && Master.productList.get(getAdapterPosition()).getQuantity() >= Master.productList.get(getAdapterPosition()).getStockQuantity())
                    {
                        Material.alertDialog(context, context.getString(R.string.alert_no_more_stock_available), "OK");
                    }
                    else
                    {
                        dbHelper = new DBHelper(context.getApplicationContext());
                        int qty = dbHelper.addProduct(
                                Master.productList.get(getAdapterPosition()).getUnitPrice() + "",
                                Master.productList.get(getAdapterPosition()).getUnitPrice() + "",
                                Master.productList.get(getAdapterPosition()).getName(),
                                MemberDetails.getMobileNumber(),
                                MemberDetails.getSelectedOrgAbbr(),
                                Master.productList.get(getAdapterPosition()).getID(),
                                Master.productList.get(getAdapterPosition()).getImageUrl(),
                                Master.productList.get(getAdapterPosition()).getStockQuantity() + "",
                                Master.productList.get(getAdapterPosition()).getStockEnabledStatus());

                        ((Activity)context).invalidateOptionsMenu();

                        tQuantity.setText(qty + "");

                        Master.productList.get(getAdapterPosition()).setQuantity(qty);

                    }

                    /*Toast.makeText(context,
                            Master.productList.get(getAdapterPosition()).getName() + " " + context.getString(R.string.toast_product_added_to_cart),
                            Toast.LENGTH_LONG).show();*/
                }
            });

        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_product, null);
        DataObjectHolder rcv = new DataObjectHolder(cardView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.tProductName.setText(Master.productList.get(position).getName());
        holder.tPrice.setText("\u20B9" + Master.productList.get(position).getUnitPrice());
        Log.e("Product Adapter", "Price: " + Master.productList.get(position).getUnitPrice());
        if(Master.productList.get(position).getStockEnabledStatus().equals("true"))
        {
            if(Master.productList.get(position).getStockQuantity() == 0.0)
            {
                holder.tAvailable.setText(R.string.textview_out_of_stock);
                holder.tAvailable.setTextColor(context.getResources().getColor(R.color.red));
                holder.ivBuy.setVisibility(View.GONE);
                holder.tQuantity.setText("");
            }
            else
            {
                Log.e("Prod adapter", Master.productList.get(position).getName() + " : " + Master.productList.get(position).getQuantity());
                holder.tQuantity.setText(Master.productList.get(position).getQuantity() + "");

                holder.tAvailable.setText(R.string.textview_available);
                holder.tAvailable.setTextColor(context.getResources().getColor(R.color.green));
                holder.ivBuy.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            Log.e("Prod adapter", Master.productList.get(position).getName() + " : " + Master.productList.get(position).getQuantity());
            holder.tQuantity.setText(Master.productList.get(position).getQuantity() + "");

            //holder.tAvailable.setText("Disabled");
            holder.tAvailable.setText(R.string.textview_available);
            holder.tAvailable.setTextColor(context.getResources().getColor(R.color.green));
            holder.ivBuy.setVisibility(View.VISIBLE);
        }

        /*if(Master.productList.get(position).getImageUrl() == "null")
        {
            Glide.with(context)
                    .load(Master.productList.get(position).getImageUrl())
                    .placeholder(R.drawable.placeholder_products)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivProduct);
        }
        else*/
        {
            Glide.with(context)
                    .load(Master.productList.get(position).getImageUrl())
                    .placeholder(R.drawable.placeholder_products)
                    .centerCrop()
                    .into(holder.ivProduct);
        }
    }

    @Override
    public int getItemCount() {
        return Master.productList.size();
    }
}