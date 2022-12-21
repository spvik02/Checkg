package model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public class DiscountCardMonth extends DiscountCardDecorator{

    public DiscountCardMonth(DiscountCard baseCard) {
        super(baseCard);
    }

    @Override
    public double getPercentageOfDiscount() {
        //добавляет скидку 0,1*количество месяцев с даты регистрации карты
        var discount = super.getPercentageOfDiscount() * (1 + 0.01*getNumOfMonthsSinceRegistrationDate());
        return discount;
    }

    long getNumOfMonthsSinceRegistrationDate(){
        return ChronoUnit.MONTHS.between(
                YearMonth.from(super.getDateOfRegistration()),
                YearMonth.from(LocalDate.now())
        );
    }
}
