package com.mobile.ict.cart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.mobile.ict.cart.Container.PlacedOrder;
import com.mobile.ict.cart.R;
import com.mobile.ict.cart.interfaces.DeletePlacedOrderInterface;


import java.util.ArrayList;
import java.util.List;

public class PlacedOrderAdapter extends ExpandableRecyclerAdapter<OrderViewHolder,OrderItemViewHolder> {

    private LayoutInflater mInflator;
    Context context;
    PlacedOrder placedOrder;
    int pos;
    DeletePlacedOrderInterface deletePlacedOrderInterface;


    public PlacedOrderAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList,DeletePlacedOrderInterface deletePlacedOrderInterface) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
        this.context=context;
        this.deletePlacedOrderInterface=deletePlacedOrderInterface;
    }

    @Override
    public OrderViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View orderView = mInflator.inflate(R.layout.placedorder_card_view, parentViewGroup, false);
        return new OrderViewHolder(orderView);
    }

    @Override
    public OrderItemViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View orderItemView = mInflator.inflate(R.layout.placedorder_list, childViewGroup, false);
     //   return new OrderItemViewHolder(context,orderItemView,deletePlacedOrderInterface,placedOrder.getOrder().getOrder_id(),pos);
       return new OrderItemViewHolder(context,orderItemView,deletePlacedOrderInterface);
    }

    @Override
    public void onBindParentViewHolder(OrderViewHolder recipeViewHolder, int position, ParentListItem parentListItem) {
         placedOrder = (PlacedOrder) parentListItem;
         pos= position;
         recipeViewHolder.bind(placedOrder);

        /*recipeViewHolder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/

    }

    @Override
    public void onBindChildViewHolder(OrderItemViewHolder ingredientViewHolder, int position, Object childListItem) {
        //Ingredient ingredient = (Ingredient) childListItem;
        ingredientViewHolder.bind((ArrayList<String[]>)childListItem,placedOrder.getOrder().getOrder_id(),pos);
      //  ingredientViewHolder.bind(ingredient);
       // ingredientViewHolder.bind((ArrayList<String[]>)childListItem);
    }
}
