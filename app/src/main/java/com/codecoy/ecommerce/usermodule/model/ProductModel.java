package com.codecoy.ecommerce.usermodule.model;

import java.util.ArrayList;

public class ProductModel {

    private String maincat, subcat, product_name, product_desc, product_wholesale_price, product_unit, producta_thumbnail, docid;

    private ArrayList<String> productImages;

    public ProductModel(String maincat, String subcat, String product_name, String product_desc, String product_wholesale_price, String product_unit, String producta_thumbnail, String docid, ArrayList<String> productImages) {
        this.maincat = maincat;
        this.subcat = subcat;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.product_wholesale_price = product_wholesale_price;
        this.product_unit = product_unit;
        this.producta_thumbnail = producta_thumbnail;
        this.docid = docid;
        this.productImages = productImages;
    }

    public String getMaincat() {
        return maincat;
    }

    public void setMaincat(String maincat) {
        this.maincat = maincat;
    }

    public String getSubcat() {
        return subcat;
    }

    public void setSubcat(String subcat) {
        this.subcat = subcat;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getProduct_wholesale_price() {
        return product_wholesale_price;
    }

    public void setProduct_wholesale_price(String product_wholesale_price) {
        this.product_wholesale_price = product_wholesale_price;
    }

    public String getProduct_unit() {
        return product_unit;
    }

    public void setProduct_unit(String product_unit) {
        this.product_unit = product_unit;
    }

    public String getProducta_thumbnail() {
        return producta_thumbnail;
    }

    public void setProducta_thumbnail(String producta_thumbnail) {
        this.producta_thumbnail = producta_thumbnail;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public ArrayList<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(ArrayList<String> productImages) {
        this.productImages = productImages;
    }
}