package com.mobile.ict.cart.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.mobile.ict.cart.Container.Product;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.interfaces.DeleteProductListener;
import com.mobile.ict.cart.util.Master;
import com.mobile.ict.cart.util.Material;

import java.util.ArrayList;

/**
 * Created by vish on 22/4/16.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.DataObjectHolder> {
    //public static ArrayList<Product> itemList = new ArrayList<>();
    private Context context;
    DataObjectHolder rcv;
    TextView cartTotal;
    double sum=0.0;
    DeleteProductListener deleteProductListener;


    public CartAdapter(Context context, TextView cartTotal) {
        this.context = context;
        this.cartTotal=cartTotal;
        this.deleteProductListener =(DeleteProductListener)context;

       // sum=Double.parseDouble(cartTotal.getText().toString().trim());
        System.out.println("total sum---------------------"+cartTotal.getText().toString().trim());

    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
    {
        TextView tProductName, tPrice, tAvailable, tItemTotal;
        ImageButton bDelete, bPlus, bMinus;
        EditText eQuantity;
        ImageView ivProduct;

        MyCustomEditTextListener myCustomEditTextListener;

        public DataObjectHolder(final View itemView, final Context context,MyCustomEditTextListener myCustomEditTextListener)
        {
            super(itemView);

            ivProduct = (ImageView) itemView.findViewById(R.id.ivProduct);

            tProductName = (TextView) itemView.findViewById(R.id.tCartProductName);
            tPrice = (TextView) itemView.findViewById(R.id.tPrice);
            tItemTotal = (TextView) itemView.findViewById(R.id.tItemTotal);

            bDelete = (ImageButton) itemView.findViewById(R.id.bDelete);
            bPlus = (ImageButton) itemView.findViewById(R.id.bPlus);
            bMinus = (ImageButton) itemView.findViewById(R.id.bMinus);

            eQuantity = (EditText) itemView.findViewById(R.id.eQuantity);
            this.myCustomEditTextListener = myCustomEditTextListener;



         /*   eQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus)
                        System.out.println("Focus removed from " + getAdapterPosition());
                }
            });
*/



          /*  bDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Delete clicked at " + getAdapterPosition());



                }
            });

            bPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    System.out.println("Plus clicked at " + getAdapterPosition());
                }
            });

            bMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Minus clicked at " + getAdapterPosition());
                }
            });*/
        }


    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_cart, parent, false);

       rcv = new DataObjectHolder(cardView, context,new MyCustomEditTextListener());

        return rcv;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {


        holder.eQuantity.addTextChangedListener(holder.myCustomEditTextListener);
        holder.myCustomEditTextListener.updatePosition(position, holder.tItemTotal, holder.eQuantity, cartTotal);
        holder.tProductName.setText("" + Master.cartList.get(position).getName());
        holder.tPrice.setText("\u20B9" + Master.cartList.get(position).getUnitPrice());
        holder.tItemTotal.setText("\u20B9" + Master.cartList.get(position).getTotal());

        if(Master.cartList.get(position).getStockEnabledStatus().equals("true"))
            if(Master.cartList.get(position).getStockQuantity() < Master.cartList.get(position).getQuantity())
                Master.cartList.get(position).setQuantity(Master.cartList.get(position).getStockQuantity());

        holder.eQuantity.setText("" + Master.cartList.get(position).getQuantity());


        if(Master.cartList.get(position).getQuantity() <= 1)
        {
            Log.e("Cart adapter", "quant <= 1");
            holder.bMinus.setEnabled(false);
            holder.bMinus.setColorFilter(context.getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_IN);
        }

        holder.bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sqty = holder.eQuantity.getText().toString().trim();
                int dqty = Integer.parseInt(sqty);

                if(Master.cartList.get(position).getStockEnabledStatus().equals("true")
                        && dqty >= Master.cartList.get(position).getStockQuantity())
                {
                    Material.alertDialog(context, context.getString(R.string.alert_no_more_stock_available), "OK");
                    holder.bPlus.setEnabled(false);
                }
                else
                {
                    dqty++;
                    holder.bPlus.setEnabled(true);
                    System.out.println("adding-------" + Master.cartList.get(position).getName() + "--------" + dqty);

                    holder.eQuantity.setText("" + dqty);
                    holder.bMinus.setEnabled(true);
                    holder.bMinus.setColorFilter(context.getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                }
            }
        });

        holder.bMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.bPlus.setEnabled(true);
                String sqty = holder.eQuantity.getText().toString().trim();

                int dqty = Integer.parseInt(sqty);

                if(dqty > 2)
                    holder.eQuantity.setText("" + --dqty);

                else if(dqty == 2)
                {
                    Log.e("Cart adapter", "in ocClick quant <= 1");
                    holder.eQuantity.setText("" + --dqty);
                    holder.bMinus.setColorFilter(context.getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_IN);
                    holder.bMinus.setEnabled(false);

                }

            }
        });


        holder.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("position------------"+position+"------------id----------"+Master.cartList.get(position).getID());
                deleteProductListener.deleteProduct(position,Master.cartList.get(position).getID());
            }
        });


       /* holder.eQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {

                if (!hasFocus) {
                    System.out.println("Focus removed from------------------ ");

                    if (holder.eQuantity.getText().toString().equals("")) {
                        System.out.println("-----------Focus removed from----------------");
                        holder.eQuantity.setText("1.0");
                       // holder.bMinus.setEnabled(true);
                       // holder.bPlus.setEnabled(true);
                   }
                }
               *//* else
                {
                    System.out.println("Focus on--------------------------");
                    if (holder.eQuantity.getText().toString().trim().equals("")) {
                        System.out.println("-----------Focus on----------------");
                        holder.bMinus.setEnabled(false);
                        holder.bPlus.setEnabled(false);
                    }
                }*//*

            }
        });
*/





        if(Master.cartList.get(position).getImageUrl() == "null")
        {
            Glide.with(context)
                    .load(Master.cartList.get(position).getImageUrl())
                    .placeholder(R.drawable.placeholder_products)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivProduct);
        }
        else
        {
            Glide.with(context)
                    .load(Master.cartList.get(position).getImageUrl())
                    .placeholder(R.drawable.placeholder_products)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivProduct);
        }


    }

    @Override
    public int getItemCount() {
        return Master.cartList.size();
    }



    public static ArrayList<Product> getList()
    {
        for(int i=0;i<Master.cartList.size();i++)
        {
            System.out.println("---------------------");
            System.out.println("Name: " + Master.cartList.get(i).getName());
            System.out.println("Qty: " + Master.cartList.get(i).getQuantity());
            System.out.println("price: " + Master.cartList.get(i).getUnitPrice());
            System.out.println("total: " + Master.cartList.get(i).getTotal());
            System.out.println("---------------------");
        }

        return  Master.cartList;
    }



    private class MyCustomEditTextListener implements TextWatcher {
        private int position;
        TextView itotal;
        EditText eqty;
        TextView cartTotal;

        public void updatePosition(int position,TextView textView,EditText editText,TextView cartTotal) {
            this.position = position;
            itotal=textView;
            eqty=editText;
            this.cartTotal=cartTotal;
        }



        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {
            /*if(!charSequence.toString().equals(""))
            {
                String total;
                try {
                    Double.parseDouble(charSequence.toString());
                    total = String.format("%.2f", Master.cartList.get(position).getUnitPrice() * Double.parseDouble(charSequence.toString()));
                    if(total.equals("0.0"))eqty.setText("1.0");

                }
                catch (Exception e) {

                }

            }*/

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (!editable.toString().equals("")) {
                String total;
                try {

                    total= String.format("%.2f",Master.cartList.get(position).getUnitPrice()*Integer.parseInt(editable.toString()));


                    Master.cartList.get(position).setQuantity(Integer.parseInt(editable.toString()));
                    Master.cartList.get(position).setTotal(Double.parseDouble(total));
                    itotal.setText("\u20B9" + Master.cartList.get(position).getTotal());

                    System.out.println(Master.cartList.get(position).getName() + "---------------------" + total);

                    sum=0.0;
                    for(int i=0;i<Master.cartList.size();i++)
                    {
                        sum=sum+Master.cartList.get(i).getTotal();
                        System.out.println("updating---------------------"+sum);
                    }

                    cartTotal.setText("" + String.format("%.2f",sum));

                    /*if(Integer.parseInt(editable.toString().trim()) > Master.cartList.get(position).getStockQuantity())
                        eqty.setText(Master.cartList.get(position).getStockQuantity() + "");*/

                }
                catch (NumberFormatException e) {
                    //Toast.makeText(context,"Please enter valid quantity", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                eqty.setText("1");
            }

        }
    }

}