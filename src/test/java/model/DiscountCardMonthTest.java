package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountCardMonthTest {
    private static Stream<Arguments> provideCardsWithDifferentRegistrationDate() {
        LocalDate currentDate = LocalDate.now();
        LocalDate currentDateMinus8 = LocalDate.now().minusMonths(8);
        return Stream.of(
                //карта зарегистрированная сегодня
                Arguments.of(0, new DiscountCardMonth(
                        new DiscountCardClassic(
                                1,
                                10,
                                currentDate
                        ))),
                //карта зарегистрированная 15 числа 8 месяцев назад
                Arguments.of(8, new DiscountCardMonth(
                        new DiscountCardClassic(
                                1,
                                10,
                                currentDateMinus8.withDayOfMonth(15)
                ))),
                //карта зарегистрированная 1 числа 8 месяцев назад
                Arguments.of(8, new DiscountCardMonth(
                        new DiscountCardClassic(
                                1,
                                10,
                                currentDateMinus8.withDayOfMonth(1)
                        ))),
                //карта зарегистрированная в последний день месяца 8 месяцев назад
                Arguments.of(8, new DiscountCardMonth(
                        new DiscountCardClassic(
                                1,
                                10,
                                currentDateMinus8.withDayOfMonth(currentDateMinus8.lengthOfMonth())
                        ))),
                //карта зарегистрированная в последний день месяца 9 месяцев назад
                Arguments.of(9, new DiscountCardMonth(
                        new DiscountCardClassic(
                                1,
                                10,
                                currentDateMinus8.withDayOfMonth(currentDateMinus8.lengthOfMonth())
                                        .minusMonths(1)
                        )))
        );
    }

    @Test
    void CheckGetPercentageOfDiscountShouldReturnCorrectPercentageOfDiscount() {
        DiscountCardMonth card = new DiscountCardMonth(new DiscountCardClassic(
                1,
                10,
                LocalDate.now().minusMonths(8)
        ));
        double expectedPercentageOfDiscount = 10+8*0.1;

        assertThat(card.getPercentageOfDiscount()).isEqualTo(expectedPercentageOfDiscount);
    }

    @ParameterizedTest
    @MethodSource("provideCardsWithDifferentRegistrationDate")
    void checkGetNumOfMonthsSinceRegistrationDateShouldReturnExpectedNumOfMonths(int expected, DiscountCardMonth card){
        assertThat(card.getNumOfMonthsSinceRegistrationDate()).isEqualTo(expected);
    }
}
