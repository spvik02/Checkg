package model;

import java.time.LocalDate;

public interface DiscountCard {

    double getPercentageOfDiscount();
    int getId();
    LocalDate getDateOfRegistration();
}
