package com.codecoy.ecommerce.usermodule.model;

public class MyCartModel {
    String productimage,productname,productprice,productqty,productunit,docid;

    public MyCartModel(String productimage, String productname, String productprice, String productqty, String productunit, String docid) {
        this.productimage = productimage;
        this.productname = productname;
        this.productprice = productprice;
        this.productqty = productqty;
        this.productunit = productunit;
        this.docid = docid;
    }

    public String getProductimage() {
        return productimage;
    }

    public void setProductimage(String productimage) {
        this.productimage = productimage;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductprice() {
        return productprice;
    }

    public void setProductprice(String productprice) {
        this.productprice = productprice;
    }

    public String getProductqty() {
        return productqty;
    }

    public void setProductqty(String productqty) {
        this.productqty = productqty;
    }

    public String getProductunit() {
        return productunit;
    }

    public void setProductunit(String productunit) {
        this.productunit = productunit;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }
}
