
package com.mobile.ict.cart.Container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONObject;


public class Order {

    String memberNumber;
    int order_id;
    int group_id;
    String group_name;
    int order_ref_num;
    String timeStamp;
    String status;
    double totalBill;
    double quantity=0.0;

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    double stockQuantity;

    String stockEnabledStatus;


    TreeMap<String,String[]> itemList;
    HashSet<String> set;


    public Order(JSONObject order,int pos){

        try {
            this.totalBill=0.0;
         //   this.memberNumber=memberNumber;
            this.order_id=order.getInt("orderId");
            this.timeStamp=order.getString("registeredTime");
            this.stockEnabledStatus=order.getString("stockManagement");
            JSONArray orderItems=order.getJSONArray("orderItems");
            this.itemList=new TreeMap<String, String[]>();
            this.set= new HashSet<>();
            for(int i=0;i<orderItems.length();i++){
                JSONObject item=(JSONObject)orderItems.get(i);
                String name=item.getString("productname");
                String[] temp=new String[3];
                temp[0]=item.getString("unitrate");
                quantity=Double.parseDouble(item.getString("quantity"));
                if(!set.contains(name))
                {
                    set.add(name);
                    temp[1]=item.getString("quantity");

                }
                else
                {
                    temp[1]=String.valueOf(Double.parseDouble(this.itemList.get(pos+name)[1])+Double.parseDouble(item.getString("quantity")));
                }

                temp[2]=Double.parseDouble(item.getString("stockquantity"))+"";
                this.itemList.put(pos+name,temp);

                this.totalBill=this.totalBill+(Double.parseDouble(String.format("%.2f",(Double.parseDouble(temp[0])*quantity))));



            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    public String getStockEnabledStatus() {
        return stockEnabledStatus;
    }

    public void setStockEnabledStatus(String stockEnabledStatus) {
        this.stockEnabledStatus = stockEnabledStatus;
    }


    public double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }


    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getOrder_ref_num() {
        return order_ref_num;
    }

    public void setOrder_ref_num(int order_ref_num) {
        this.order_ref_num = order_ref_num;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItems() {
        String res="";
        for(String itemName:itemList.keySet()){
            res+= itemName + "-" + itemList.get(itemName)[1] + "\n";
        }
        return res;
    }
    public ArrayList<String[]> getItemsList(int pos) {
        ArrayList<String[]> itemlist = new ArrayList<>();
        String[] res = new String[5] ;

        int posLen = (""+pos).length();


        for (String itemName:itemList.keySet()){

            res = new String[5] ;
            String name =itemName.substring(posLen);
            res[0]=name;
            res[1]=itemList.get(itemName)[0];
            res[2]=itemList.get(itemName)[1];

            double rate=Double.parseDouble(itemList.get(itemName)[0]);
            double quantity=Double.parseDouble(itemList.get(itemName)[1]);
            //  double total = quantity * rate;
            double total = Double.parseDouble(String.format("%.2f",(quantity*rate)));

            res[3]= ""+total;
            res[4]=itemList.get(itemName)[2];
            itemlist.add(res);
            res=null;
        }

        return itemlist;
    }


    public TreeMap<String,String[]> getItemList() {
        return itemList;
    }
}
