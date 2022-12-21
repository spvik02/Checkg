package model;

import java.time.LocalDate;

public class DiscountCardDecorator implements DiscountCard {
    private DiscountCard baseCard;

    DiscountCardDecorator(DiscountCard baseCard){
        this.baseCard = baseCard;
    }

    @Override
    public double getPercentageOfDiscount() {
        return baseCard.getPercentageOfDiscount();
    }

    @Override
    public int getId() {
        return baseCard.getId();
    }

    @Override
    public LocalDate getDateOfRegistration() {
        return baseCard.getDateOfRegistration();
    }
}
