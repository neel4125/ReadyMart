package com.codecoy.ecommerce.adminmodule.model;

public class SubCategoreyModel {

    private String main_cat_name,name,image,docid;

    public SubCategoreyModel(String main_cat_name, String name, String image, String docid) {
        this.main_cat_name = main_cat_name;
        this.name = name;
        this.image = image;
        this.docid = docid;
    }

    public String getMain_cat_name() {
        return main_cat_name;
    }

    public void setMain_cat_name(String main_cat_name) {
        this.main_cat_name = main_cat_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }
}
