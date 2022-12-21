package model;

import java.util.List;

public class Stock { //promotion?
    private int id;
    private int quantity;
    private int sale;
    private List<Integer> products;
    private String description;

    public Stock(int id, int quantity, int sale, String description, List<Integer> products) {
        this.id = id;
        this.quantity = quantity;
        this.sale = sale;
        this.description = description;
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isProductOnStock(int id){
        return products.contains(id);
    }
}
