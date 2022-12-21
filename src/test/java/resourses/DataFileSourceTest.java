package resourses;

import model.DiscountCard;
import model.DiscountCardClassic;
import model.Product;
import org.junit.jupiter.api.Test;

import java.io.DataInput;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DataFileSourceTest {

    @Test
    void givenString_whenMakeProduct(){
        String lineToParse = "1; milk file; 2.8; true";
        Product expected = new Product(
                1,
                "milk file",
                2.8,
                true
        );

        Product actual = DataFileSource.makeProduct(lineToParse);

        assertEquals(expected, actual);
    }

    @Test
    void givenString_whenMakeCard(){
        String lineToParse = "1; 10; 19/12/2017";
        DiscountCard expected = new DiscountCardClassic(
                1,
                10,
                LocalDate.of(2017,12, 19)
        );

        DiscountCard actual = DataFileSource.makeCard(lineToParse);

        assertEquals(expected, actual);
    }

    @Test
    void givenInvalidString_whenMakeCard(){
        String lineToParse = "1; ten; 19/12/2017";

        assertThrows(IllegalArgumentException.class, ()->{DataFileSource.makeCard(lineToParse);});
    }

}