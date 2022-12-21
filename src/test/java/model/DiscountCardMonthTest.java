package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class DiscountCardMonthTest {
    DiscountCardMonth card = new DiscountCardMonth(new DiscountCardClassic(
            1,
            10,
            LocalDate.now().minusMonths(8)
    ));


    @Test
    void givenCardMonth8_whenGetNumOfMonthsSinceRegistrationDate_thenReturnNumOfMonthSinceRegistrationDate(){
        assertEquals(8, card.getNumOfMonthsSinceRegistrationDate());
    }


    @Test
    void givenCardDiscount10Month8_whenGetPercentageOfDiscount_thenReturnSumOfDiscounts() {
        var expected = 10+8*0.1;

        assertEquals(expected, card.getPercentageOfDiscount());
    }
}