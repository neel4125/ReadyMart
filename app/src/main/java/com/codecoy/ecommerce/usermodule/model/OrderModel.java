package com.codecoy.ecommerce.usermodule.model;

import java.util.ArrayList;

public class OrderModel {
    private String name,email,phoneno,ordertotal,userid,paymentvia;
    private ArrayList<MyCartModel> chartItems;

    public OrderModel(String name, String email, String phoneno, String ordertotal, String userid, String paymentvia, ArrayList<MyCartModel> chartItems) {
        this.name = name;
        this.email = email;
        this.phoneno = phoneno;
        this.ordertotal = ordertotal;
        this.userid = userid;
        this.paymentvia = paymentvia;
        this.chartItems = chartItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getOrdertotal() {
        return ordertotal;
    }

    public void setOrdertotal(String ordertotal) {
        this.ordertotal = ordertotal;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPaymentvia() {
        return paymentvia;
    }

    public void setPaymentvia(String paymentvia) {
        this.paymentvia = paymentvia;
    }

    public ArrayList<MyCartModel> getChartItems() {
        return chartItems;
    }

    public void setChartItems(ArrayList<MyCartModel> chartItems) {
        this.chartItems = chartItems;
    }
}
