package com.mobile.ict.cart.Container;

import com.mobile.ict.cart.Container.Product;

import java.util.ArrayList;

/**
 * Created by vish on 15/4/16.
 */
public class ProductType {

    public String name;
    public ArrayList<Product> productItems = new ArrayList<>();

    public ProductType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
