package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    Product product = new Product(
            1, "GoodMood", 100, false
    );

    @Test
    void givenProduct_whenCheckEquals_thenReturnThrue(){
        Product givenProduct = new Product(
                1, "GoodMood", 100, false
        );

        var provided = givenProduct.equals(product);

        assertTrue(provided);
    }

}
