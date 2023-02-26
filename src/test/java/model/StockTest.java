package model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class StockTest {

    private Stock stock = new Stock(1, 5, 10, "description", List.of(1, 4, 5));

    @Nested
    class IsProductOnStock{
        @Test
        void checkIsProductOnStockShouldReturnTrue() {
            int productId = 4;

            boolean actual = stock.isProductOnStock(productId);

            assertThat(actual).isTrue();
        }

        @Test
        void checkIsProductOnStockShouldReturnFalse(){
            int productId = 3;

            boolean actual = stock.isProductOnStock(productId);

            assertThat(actual).isFalse();
        }

        @ParameterizedTest
        @CsvSource ({"4,true", "3,false"})
        void checkIsProductOnStockShouldReturnExpected(int productId, boolean expected){
            var actual = stock.isProductOnStock(productId);
            assertThat(actual).isEqualTo(expected);
        }
    }
}
