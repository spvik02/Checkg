package model;

import providers.DiscountCardProvider;
import providers.ProductProvider;
import providers.StockProvider;
import utils.FormatUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class Receipt {
    private int id;
    private List<ProductInReceipt> positions;
    private Integer discountCardId;
    private double totalPrice;
    private double totalPriceWithDiscount;
    private final int cashier;
    private final LocalDate date;
    private final LocalTime time;

    private Receipt(ReceiptBuilder builder){
        id = builder.id;
        positions = builder.positions;
        discountCardId = builder.discountCardId;
        cashier = builder.cashier;
        date = builder.date;
        time = builder.time;
    }

    public static class ReceiptBuilder{
        private int id;
        private List<ProductInReceipt> positions;
        private Integer discountCardId;
        private int cashier;
        private LocalDate date;
        private LocalTime time;

        public ReceiptBuilder(){}
        public ReceiptBuilder withCashier(int idCashier){
            cashier = 717;
            return this;
        }
        public ReceiptBuilder withDateTime(LocalDate date, LocalTime time){
            this.date = date;
            this.time = time;
            return this;
        }
        public ReceiptBuilder withDiscountCard(int id){
            this.discountCardId = id;
            return this;
        }
        public ReceiptBuilder withPositions(List<ProductInReceipt> positions){
            this.positions = positions;
            return this;
        }
        public Receipt build(){
            return new Receipt(this);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ProductInReceipt> getPositions() {
        return positions;
    }

    public void setPositions(List<ProductInReceipt> positions) {
        this.positions = positions;
    }

    public Integer getDiscountCardId() {
        return discountCardId;
    }

    public void setDiscountCardId(Integer discountCardId) {
        this.discountCardId = discountCardId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getTotalPriceWithDiscount() {
        return totalPriceWithDiscount;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = FormatUtil.round(totalPrice);
    }

    public void setTotalPriceWithDiscount(double totalPriceWithDiscount) {
        this.totalPriceWithDiscount = FormatUtil.round(totalPriceWithDiscount);
    }

    public int getCashier() {
        return cashier;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void calculateTotal(ProductProvider productProvider, StockProvider stockProvider, DiscountCardProvider discountCardProvider){
        double totalPriceWithDiscount = 0;
        double totalPrice = 0;

        for(ProductInReceipt position : positions){
            //высчитывается начальная стоимость позиции
            double positionPrice;
            Product product;

            try{
                product = productProvider.getProductById(position.getIdProduct());
                positionPrice = product.getPrice()
                        * position.getQuantity();
            }catch (Exception e){
                System.out.println(e.getMessage() + " Product will not be counted.");
                position.setTotal(0);
                position.setPrice(0);
                continue;
            }
            position.setPrice(positionPrice);
            totalPrice += positionPrice;

            //расчет скидки по акции по количеству
            Optional<Stock> stock = stockProvider.findStock(position.getIdProduct());
            if(stock.isPresent() && position.getQuantity()>=stock.get().getQuantity())
                positionPrice -= positionPrice*stock.get().getSale()/100;
                //расчет скидки по скидочной карте
                //скидки не суммируются
                //если скидочная карты была предъявлена и скидка распространяется на товар
            else if (discountCardId!=null && product.isAtDiscount()) {
                try {
                    positionPrice -= positionPrice * discountCardProvider.getDiscountCardById(discountCardId)
                            .getPercentageOfDiscount()/100;
                } catch (Exception e) {
                    discountCardId = null;
                    System.out.println("The discount card is not registered. The discount will not be calculated. " + e.getMessage());
                }
            }

            positionPrice = FormatUtil.round(positionPrice);
            position.setTotal(positionPrice);
            //добавляем высчитанную стоимость в стоимость всего чека
            totalPriceWithDiscount +=positionPrice;
        }
        setTotalPrice(totalPrice);
        setTotalPriceWithDiscount(totalPriceWithDiscount);
    }
}
