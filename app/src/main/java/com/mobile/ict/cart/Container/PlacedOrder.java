package com.mobile.ict.cart.Container;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 3/5/16.
 */
public class PlacedOrder implements ParentListItem {



    Order order;
     List<ArrayList<String[]>> items;

    public PlacedOrder(Order order, List<ArrayList<String[]>> items) {
        this.order = order;
        this.items = items;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public List<?> getChildItemList() {
        return items;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
