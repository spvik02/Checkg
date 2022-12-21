package model;

import java.util.Objects;

public class Product {
    private int id;
    private String name;
    private double price;
    private boolean isAtDiscount;   //скидка по скидочной карте
    //private boolean isOnStock;      //скидка при покупке от 5 штук


    public Product(int id, String name, double price, boolean isAtDiscount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isAtDiscount = isAtDiscount;
    }

    public boolean isAtDiscount() {
        return isAtDiscount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Double.compare(product.price, price) == 0 && isAtDiscount == product.isAtDiscount && name.equals(product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, isAtDiscount);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", isAtDiscount=" + isAtDiscount +
                '}';
    }
}
