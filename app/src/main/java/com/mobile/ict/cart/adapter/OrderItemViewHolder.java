package com.mobile.ict.cart.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.interfaces.DeletePlacedOrderInterface;


import java.util.ArrayList;


public class OrderItemViewHolder extends ChildViewHolder {

   // private TextView mIngredientTextView;
    ListView itemListView;
    ScrollView sv;
    Context ctx;
    ImageView edit,delete;
    DeletePlacedOrderInterface callback;

    public OrderItemViewHolder(Context context, View itemView,DeletePlacedOrderInterface deletePlacedOrderInterface)
   // public OrderItemViewHolder(Context context, View itemView,DeletePlacedOrderInterface deletePlacedOrderInterface,final int orderId, final int pos)
    {
        super(itemView);
        ctx = context;
        callback=deletePlacedOrderInterface;
        itemListView = (ListView)itemView.findViewById(R.id.listView);
        edit=(ImageView)itemView.findViewById(R.id.edit);
        delete=(ImageView)itemView.findViewById(R.id.delete);
       /* sv = (ScrollView)itemView.findViewById(R.id.lv);
        sv.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {
                //Log.v(TAG,”CHILD TOUCH”);
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });*/
        itemListView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                //Log.v(TAG,”CHILD TOUCH”);
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

      /*  delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.deletePlacedOrder(orderId,pos);
            }
        });*/

        //mIngredientTextView = (TextView) itemView.findViewById(R.id.ingredient_textview);



    }

    public void bind(ArrayList<String[]> list, final int orderId,final int pos)
   // public void bind(ArrayList<String[]> list)
    {
        //mIngredientTextView.setText(ingredient.getName());
        /*for(int i=0;i<list.size();i++)
        {
            System.out.println(list.get(i).getName());
        }*/


        itemListView.setAdapter(new DataAdapter(ctx,list));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           // callback.deletePlacedOrder(orderId,pos);
                callback.deletePlacedOrder();
            }
        });
    }


    public class DataAdapter extends BaseAdapter
    {
        public ArrayList<String[]> list;
        Context context;

        public DataAdapter(Context ctx, ArrayList<String[]> list) {
            super();
            context = ctx;
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        private class ViewHolder {
            TextView productName,unitPrice,quantity,total;


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            LayoutInflater inflater =  ((Activity)context).getLayoutInflater();

            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.placedorder_list_item, null);
                holder = new ViewHolder();
                holder.productName = (TextView) convertView.findViewById(R.id.placed_order_quantityName);
               // holder.unitPrice = (TextView) convertView.findViewById(R.id.placed_order_group_quantityBasePrice);
                holder.quantity = (TextView) convertView.findViewById(R.id.placed_order_quantity);
                holder.total = (TextView) convertView.findViewById(R.id.placed_order_quantityTotalPrice);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            String[] list = (String[]) getItem(position);

            holder.productName.setText(list[0]);
            holder.quantity.setText(list[2]);
           // holder.unitPrice.setText('\u20B9' + list[1]);
            holder.total.setText(list[3]);

        /*    Ingredient ingredient = (Ingredient)getItem(position);
            System.out.println("----------"+ingredient.getName());
            holder.mIngredientTextView.setText(ingredient.getName());*/
         /*   HashMap map = list.get(position);
            holder.titel.setText(map.get(TITEL).toString());
            holder.artist.setText(map.get(INTERPRET).toString());
            holder.duration.setText(map.get(DAUER).toString());*/


            return convertView;
        }

    }
}
