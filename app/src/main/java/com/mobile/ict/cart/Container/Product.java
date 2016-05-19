package com.mobile.ict.cart.Container;

/**
 * Created by vish on 15/4/16.
 */
import org.json.JSONException;
import org.json.JSONObject;


public class Product {

    private String audioUrl;
    private double unitPrice;
    private double total;
    private int quantity;
    private String stockEnabledStatus;
    private int stockQuantity;
    private String name;
    private String imageUrl;
    private String ID;
    private String orgAbbr;

    public String getOrgAbbr() {
        return orgAbbr;
    }

    public void setOrgAbbr(String orgAbbr) {
        this.orgAbbr = orgAbbr;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStockEnabledStatus() {
        return stockEnabledStatus;
    }

    public void setStockEnabledStatus(String stockEnabledStatus) {
        this.stockEnabledStatus = stockEnabledStatus;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product(String name, double unitPrice, double total, int stockQuantity, String stockEnabledStatus, String imageUrl, String audioUrl, String ID) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.total = total;
        this.quantity = (int) (total/unitPrice);
        this.stockQuantity = stockQuantity;
        this.stockEnabledStatus = stockEnabledStatus;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.ID = ID;
    }

    public Product(String name, double unitPrice, double total) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.total = total;
        this.quantity = (int) (total/unitPrice);
    }

    public Product(JSONObject orderItem) throws JSONException {
        this.name = orderItem.getString("name");
        this.unitPrice = Double.parseDouble(orderItem.getString("rate"));
        this.quantity = Integer.parseInt(orderItem.getString("quantiity"));
        this.total = this.quantity * this.unitPrice;
    }

    public Product()
    {

    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
