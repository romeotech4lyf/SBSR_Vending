package com.tech4lyf.sbsrvending;

public class Product {
    private String id;
    private String name;
    private String url;
    private String price;
    private String count;



    public Product(String id, String name, String url, String price, String count) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.price = price;
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String geturl() {
        return url;
    }

    public void seturl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
