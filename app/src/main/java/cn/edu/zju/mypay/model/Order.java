package cn.edu.zju.mypay.model;

/**
 * Created by Austin on 2018/4/20.
 */

public class Order {
    private String name;
    private double price;
    private String orderTime;

    public Order(String name, double price, String orderTime){
        this.name = name;
        this.price = price;
        this.orderTime = orderTime;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==this)return true;

        return obj instanceof Order &&
                this.name.equals(((Order)obj).name) &&
                this.price ==  ((Order)obj).price &&
                this.orderTime == ((Order)obj).orderTime;
    }
}
