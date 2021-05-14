package com.codecoy.ecommerce.usermodule.model;

public class MainCategoryMOdel {
   private String name,image,docid;

    public MainCategoryMOdel(String name, String image, String docid) {
        this.name = name;
        this.image = image;
        this.docid = docid;
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
