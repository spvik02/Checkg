package utils;

import model.ProductInReceipt;
import model.Receipt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParametersUtilTest {

    private Receipt receipt;

    @BeforeEach
    void setUp(){
        receipt = new Receipt.ReceiptBuilder()
                .withCashier(17)
                .withDateTime(
                        LocalDate.of(2023, 1, 10),
                        LocalTime.of(8, 16, 59))
                .build();
    }

    @Test
    void givenValidArgPairs_whenParseParameters_thenSetReceiptPositions(){
        String[] args = new String[]{"1-2", "2-5", "3-5"};
        List<ProductInReceipt> expected = List.of(
                new ProductInReceipt( 1, 2),
                new ProductInReceipt(2, 5),
                new ProductInReceipt(3, 5)
        );

        ParametersUtil.parseParameters(args, receipt);

        assertEquals(expected, receipt.getPositions());
    }

    @Test
    void givenValidArgPairsAndDiscountCard_whenParseParameters_thenSetDiscountCard(){
        String[] args = new String[]{"1-2", "2-5", "3-5", "Card-1"};
        List<ProductInReceipt> expected = List.of(
                new ProductInReceipt(0, 1, 2),
                new ProductInReceipt(1, 2, 5),
                new ProductInReceipt(2, 3, 5)
        );

        ParametersUtil.parseParameters(args, receipt);

        assertEquals(1, receipt.getDiscountCardId());
    }

    @Test
    void givenInvalidArgPairs_whenParseParameters_thenSetDiscountCard(){
        String[] args = new String[]{"Str-2", "2-5", "3-5", "Card-1"};
        List<ProductInReceipt> expected = List.of(
                new ProductInReceipt( 2, 5),
                new ProductInReceipt(3, 5)
        );

        ParametersUtil.parseParameters(args, receipt);

        assertEquals(expected, receipt.getPositions());
    }

    @Test
    void givenValidFileName_whenReadParametersFromFile_thenReturnParameters(){
        String name = "parameters1.txt";
        String[] expected = new String[]{"1-2", "2-5", "3-5", "4-2", "5-5", "6-2", "Card-2"};

        String[] actual = ParametersUtil.readParametersFromFile(name);

        assertArrayEquals(expected, actual);
    }

    @Test
    void givenInvalidFileName_whenReadParametersFromFile_thenReturnNull(){
        String name = "parameters4.txt";

        String[] actual = ParametersUtil.readParametersFromFile(name);

        assertArrayEquals(null, actual);
    }

    @Test
    void givenEmptyFile_whenReadParametersFromFile_thenReturnNull(){
        String name = "parameters3.txt";

        String[] actual = ParametersUtil.readParametersFromFile(name);

        assertArrayEquals(null, actual);
    }


}