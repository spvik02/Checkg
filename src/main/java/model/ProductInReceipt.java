package model;

import utils.FormatUtil;

import java.util.Objects;

public class ProductInReceipt {
    static int idGlobal = 0;
    private int id;
    private int idProduct;
    private int quantity;
    private double price;
    private double total;

    public ProductInReceipt( int idProduct, int quantity) {
//        this.id = idGlobal++;
        this.idProduct = idProduct;
        this.quantity = quantity;
    }
    public ProductInReceipt( int id, int idProduct, int quantity) {
        this.id = id;
        this.idProduct = idProduct;
        this.quantity = quantity;
    }

    public ProductInReceipt( int id, int idProduct, int quantity, double price, double total) {
        this.id = id;
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = FormatUtil.round(price);
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = FormatUtil.round(total);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductInReceipt that = (ProductInReceipt) o;
        return id == that.id && idProduct == that.idProduct && quantity == that.quantity && Double.compare(that.price, price) == 0 && Double.compare(that.total, total) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idProduct, quantity, price, total);
    }
}
