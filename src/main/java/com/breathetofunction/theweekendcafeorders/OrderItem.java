package com.breathetofunction.theweekendcafeorders;

public class OrderItem {
    public String item;
    public String price;
    public String quant;

    public OrderItem(String item, String quant, String price){
        this.item = item;
        this.price = price;
        this.quant = quant;
    }

    public String getItem(){
        return item;
    }

    public String getPrice(){
        return price;
    }

    public String getQuant(){
        return quant;
    }
}
