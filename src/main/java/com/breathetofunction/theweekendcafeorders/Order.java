package com.breathetofunction.theweekendcafeorders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Order {
    public String time;
    public String tableNumber;
    public int grandTotal;
    public int itemsCount;
    public String orderData;
    public JSONArray items;

    public Order(String time, String tableNumber, int itemsCount, int grandTotal, String orderData) throws JSONException {
        this.time = time.substring(0,5);
        this.tableNumber = "Table "+tableNumber;
        this.grandTotal = grandTotal;
        this.itemsCount = itemsCount;
        this.orderData = orderData;

        JSONObject order = new JSONObject(orderData);
        this.items = order.getJSONArray("items");
    }

    public JSONArray getItemsJson(){
        return items;
    }

    public String getTime(){
        return time;
    }

    public String getTableNumber(){
        return tableNumber;
    }

    public int getGrandTotal(){
        return grandTotal;
    }

    public int getItemsCount(){
        return itemsCount;
    }

    public String getOrderData(){
        return orderData;
    }
}
