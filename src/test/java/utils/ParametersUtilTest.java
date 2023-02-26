package utils;

import model.ProductInReceipt;
import model.Receipt;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ParametersUtilTest {

    private Receipt receipt;

    @BeforeEach
    void setUp() {
        receipt = new Receipt.ReceiptBuilder()
                .withCashier(17)
                .withDateTime(
                        LocalDate.of(2023, 1, 10),
                        LocalTime.of(8, 16, 59))
                .build();

    }

    @Nested
    class ParseParameters{
        @Test
        @DisplayName("parseParameters with valid arg pairs should set positions correctly")
        void checkParseParametersShouldSetPositions() {
            String[] args = new String[]{"1-2", "2-5", "3-5"};
            List<ProductInReceipt> expectedPositions = List.of(
                    new ProductInReceipt( 1, 2),
                    new ProductInReceipt(2, 5),
                    new ProductInReceipt(3, 5)
            );

            ParametersUtil.parseParameters(args, receipt);

            assertThat(receipt).hasFieldOrPropertyWithValue("positions", expectedPositions);
        }

        @Test
        @DisplayName("parseParameters with invalid arg pair format should set valid positions correctly and skip invalid")
        void checkParseParametersWithInvalidParameterFormatShouldSetPositions(){
            String[] args = new String[]{"Str-2", "2-5", "3-5", "Card-1"};
            List<ProductInReceipt> expectedPositions = List.of(
                    new ProductInReceipt( 2, 5),
                    new ProductInReceipt(3, 5)
            );
            ParametersUtil.parseParameters(args, receipt);

            assertThat(receipt).hasFieldOrPropertyWithValue("positions", expectedPositions);
        }

        @Test
        @DisplayName("parseParameters with valid Card-Id arg pair should set discountCardId")
        void checkParseParametersShouldSetDiscountCardId(){
            String[] args = new String[]{"1-2", "2-5", "3-5", "Card-1"};

            ParametersUtil.parseParameters(args, receipt);

            assertThat(receipt).hasFieldOrPropertyWithValue("discountCardId", 1);
        }

        //the correctness of the discount card is checked when the receipt is calculated
        @Test
        @DisplayName("parseParameters with a non-existing discountCardId should set discountCardId")
        void checkParseParametersWithInvalidCardIdShouldSetDiscountCardId(){
            String[] args = new String[]{"1-2", "2-5", "3-5", "Card-0"};

            ParametersUtil.parseParameters(args, receipt);

            assertThat(receipt).hasFieldOrPropertyWithValue("discountCardId", 0);
        }

        @Test
        @DisplayName("parseParameters with a incorrect discountCardId should throw NumberFormatException")
        void checkParseParametersWithInvalidCardIdShouldThrowNumberFormatException(){
            String[] args = new String[]{"1-2", "2-5", "3-5", "Card-str"};

            assertThatExceptionOfType(NumberFormatException.class)
                    .isThrownBy(() -> ParametersUtil.parseParameters(args, receipt));
        }
    }

    @Nested
    class ReadParametersFromFile{

        @Test
        @DisplayName("readParametersFromFile with valid fileName should return String[] parameters")
        void checkReadParametersFromFileWithValidFileNameShouldReturnParameters(){
            String name = "parameters1.txt";
            String[] expectedArgs = new String[]{"1-2", "2-5", "3-5", "4-2", "5-5", "6-2", "Card-2"};

            String[] actualArgs = ParametersUtil.readParametersFromFile(name);

            assertThat(actualArgs).isEqualTo(expectedArgs);
        }

        @Test
        @DisplayName("readParametersFromFile with invalid fileName should return null")
        void checkReadParametersFromFileWithInvalidFileNameShouldReturnNull(){
            String name = "parameters4.txt";

            String[] actualArgs = ParametersUtil.readParametersFromFile(name);

            assertThat(actualArgs).isNull();
        }

        @Test
        @DisplayName("readParametersFromFile with empty file should return null")
        void checkReadParametersFromFileWithEmptyFileShouldReturnNull(){
            String name = "parameters3.txt";

            String[] actualArgs = ParametersUtil.readParametersFromFile(name);

            assertThat(actualArgs).isNull();
        }
    }
}
