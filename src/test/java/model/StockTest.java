package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {
    Stock stock = new Stock(1, 5, 10, "description", List.of(1, 4, 5));

    @Test
    void givenId_whenFindProductInStockByProductId_thenReturnTrue() {
        //given
        int productId = 4;

        //when
        var provided = stock.isProductOnStock(productId);

        //then
        assertTrue(provided);
    }

    @Test
    void givenId_whenNotFoundProductInStockByProductId_thenReturnFalse(){
        //given
        int productId = 3;

        //when
        var provided = stock.isProductOnStock(productId);

        //then
        assertFalse(provided);
    }
}