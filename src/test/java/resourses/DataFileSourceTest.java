package resourses;

import model.DiscountCard;
import model.DiscountCardClassic;
import model.Product;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DataFileSourceTest {

    @Nested
    class ReadData{
        @Test
        void checkReadDataDoesNotThrowAnyExceptionIfFileDoesNotExist() {
            assertThatCode(()-> DataFileSource.readData("incorrectFileName", line -> line))
                    .doesNotThrowAnyException();
        }
        @Test
        void checkReadDataShouldReturnEmptyListIfFileDoesNotExist() {
            List<String> actualList = DataFileSource.readData("incorrectFileName", line -> line);

            assertThat(actualList).isEmpty();
        }
    }

    @Nested
    class MakeProduct{
        @Test
        void checkMakeProductShouldReturnProduct(){
            String lineToParse = "1; milk file; 2.8; true";
            Product expectedProduct = new Product(
                    1,
                    "milk file",
                    2.8,
                    true
            );

            Product actualProduct = DataFileSource.makeProduct(lineToParse);

            assertThat(actualProduct).isEqualTo(expectedProduct);
        }
        @Test
        void checkMakeProductWithInvalidStringShouldThrowIllegalArgumentException(){
            String lineToParse = "one; milk file; 2.8; true";

            assertThatThrownBy(()->DataFileSource.makeProduct(lineToParse))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class MakeCard{
        @Test
        void checkMakeCardShouldReturnDiscountCard(){
            String lineToParse = "1; 10; 19/12/2017";
            DiscountCard expectedCard = new DiscountCardClassic(
                    1,
                    10,
                    LocalDate.of(2017,12, 19)
            );

            DiscountCard actualCard = DataFileSource.makeCard(lineToParse);

            assertThat(actualCard).isEqualTo(expectedCard);
        }
        @Test
        void checkMakeCardWithInvalidStringShouldThrowIllegalArgumentException(){
            String lineToParse = "1; ten; 19/12/2017";

            assertThatThrownBy(()->DataFileSource.makeCard(lineToParse))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
