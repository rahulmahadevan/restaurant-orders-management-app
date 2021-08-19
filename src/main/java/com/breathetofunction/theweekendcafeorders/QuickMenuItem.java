package com.breathetofunction.theweekendcafeorders;

public class QuickMenuItem {
    private int categotyid;
    private String categoryname;
    public QuickMenuItem(int categotyid,String categoryname)
    {
        this.categotyid = categotyid;
        this.categoryname = categoryname;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public int getCategotyid() {
        return categotyid;
    }
}