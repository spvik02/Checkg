package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import providers.DiscountCardProvider;
import providers.ProductProvider;
import providers.StockProvider;
import utils.FormatUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptTest {

    @Mock
    private ProductProvider productProvider;
    @Mock
    private DiscountCardProvider discountCardProvider;
    @Mock
    private StockProvider stockProvider;

    double productPrice1 = 1.1, productPrice2 = 2.2, discount = 10;
    int productQty1 = 2, productQty2 = 6;
    Receipt receipt;

    @BeforeEach
    public void prepareTestData(){
        receipt = new Receipt.ReceiptBuilder()
                .withCashier(17)
                .withDateTime(
                        LocalDate.of(2023, 1, 10),
                        LocalTime.of(8, 16, 59))
                .withPositions(List.of(
                        new ProductInReceipt(1,1, productQty1),
                        new ProductInReceipt(2, 2, productQty2)
                ))
                .withDiscountCard(1)
                .build();
    }


    @Test
    public void givenReceipt_whenCalculateTotal_thenSetPricesForEachPositions(){
        Optional<Stock> notOnStock = Optional.empty();
        Optional<Stock> onStock = Optional.of(new Stock(1, 5, 10, "buy > 5 to get 10% discount", List.of(2)));
        when(productProvider.getProductById(any(Integer.class)))
                .thenReturn(
                        new Product(1, "Product1Stock=false", productPrice1, true),
                        new Product(2, "Product2Stock=true", productPrice2, false)
                );
        when(stockProvider.findStock(any(Integer.class)))
                .thenReturn(notOnStock).thenReturn(onStock);
        when(discountCardProvider.getDiscountCardById(any(Integer.class)))
                .thenReturn(new DiscountCardClassic(1, discount,
                        LocalDate.of(2022, 2, 24)));

        List<ProductInReceipt> expectedPositions = List.of(
                new ProductInReceipt(1, 1, productQty1, FormatUtil.round(productPrice1*productQty1), FormatUtil.round(productPrice1*productQty1*(1-discount/100))),
                new ProductInReceipt(2, 2, productQty2, FormatUtil.round(productPrice2*productQty2),  FormatUtil.round(productPrice2*productQty2*(1-discount/100)))
        );

        receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

        //assertTrue(expectedPositions.equals(receiptValid.getPositions()));
        assertEquals(expectedPositions, receipt.getPositions());
    }

    @Test
    public void givenReceipt_whenCalculateTotal_thenSetTotalAndTotalWithDiscount(){
        Optional<Stock> notOnStock = Optional.empty();
        Optional<Stock> onStock = Optional.of(new Stock(1, 5, 10, "buy > 5 to get 10% discount", List.of(2)));
        when(productProvider.getProductById(any(Integer.class)))
                .thenReturn(
                        new Product(1, "Product1Stock=false", productPrice1, true),
                        new Product(2, "Product2Stock=true", productPrice2, false)
                );
        when(stockProvider.findStock(any(Integer.class)))
                .thenReturn(notOnStock).thenReturn(onStock);
        when(discountCardProvider.getDiscountCardById(any(Integer.class)))
                .thenReturn(new DiscountCardClassic(1, discount,
                        LocalDate.of(2022, 2, 24)));

        double expectedTotal = FormatUtil.round(productPrice1*productQty1 + productPrice2*productQty2);
        double expectedTotalWithDiscount = FormatUtil.round(
                productPrice1*productQty1*(1-discount/100)
                        +productPrice2*productQty2*(1-discount/100));

        receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

        assertEquals(expectedTotal, receipt.getTotalPrice());
        assertEquals(expectedTotalWithDiscount, receipt.getTotalPriceWithDiscount());
    }

    @Test
    public void givenReceiptWithInvalidProductId_whenCalculateTotal_thenSetPricesForValidPositions(){
        Optional<Stock> notOnStock = Optional.empty();
        Optional<Stock> onStock = Optional.of(new Stock(1, 5, 10, "buy > 5 to get 10% discount", List.of(2)));
        when(productProvider.getProductById(1))
                .thenReturn(new Product(1, "Product1Stock=false", productPrice1, true));
        when(productProvider.getProductById(2)).thenThrow(new NoSuchElementException("Product not found - " + 2));
        when(stockProvider.findStock(any(Integer.class)))
                .thenReturn(notOnStock).thenReturn(onStock);
        when(discountCardProvider.getDiscountCardById(any(Integer.class)))
                .thenReturn(new DiscountCardClassic(1, discount,
                        LocalDate.of(2022, 2, 24)));
        List<ProductInReceipt> expectedPositions = List.of(
                new ProductInReceipt(1, 1, productQty1, FormatUtil.round(productPrice1*productQty1), FormatUtil.round(productPrice1*productQty1*(1-discount/100))),
                new ProductInReceipt(2, 2, productQty2, 0,  0)
        );

        receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

        assertEquals(expectedPositions, receipt.getPositions());
    }

    @Test
    public void givenReceiptWithInvalidDiscountCardId_whenCalculateTotal_thenSetPricesWithoutDiscountCard(){
        Optional<Stock> notOnStock = Optional.empty();
        Optional<Stock> onStock = Optional.of(new Stock(1, 5, 10, "buy > 5 to get 10% discount", List.of(2)));
        when(productProvider.getProductById(any(Integer.class)))
                .thenReturn(
                        new Product(1, "Product1Stock=false", productPrice1, true),
                        new Product(2, "Product2Stock=true", productPrice2, true)
                );
        when(stockProvider.findStock(any(Integer.class)))
                .thenReturn(notOnStock).thenReturn(onStock);
        when(discountCardProvider.getDiscountCardById(1)).thenThrow(new NoSuchElementException("Discount card not found - " + 1));
        List<ProductInReceipt> expectedPositions = List.of(
                new ProductInReceipt(1, 1, productQty1, FormatUtil.round(productPrice1*productQty1), FormatUtil.round(productPrice1*productQty1)),
                new ProductInReceipt(2, 2, productQty2, FormatUtil.round(productPrice2*productQty2), FormatUtil.round(productPrice2*productQty2*(1-discount/100)))
        );

        receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

        assertEquals(expectedPositions, receipt.getPositions());
    }

    @Test
    public void givenReceiptWithInvalidDiscountCardId_whenCalculateTotal_thenSetDiscountCardToNull(){
        Optional<Stock> notOnStock = Optional.empty();
        Optional<Stock> onStock = Optional.of(new Stock(1, 5, 10, "buy > 5 to get 10% discount", List.of(2)));
        when(productProvider.getProductById(any(Integer.class)))
                .thenReturn(
                        new Product(1, "Product1Stock=false", productPrice1, true),
                        new Product(2, "Product2Stock=true", productPrice2, true)
                );
        when(stockProvider.findStock(any(Integer.class)))
                .thenReturn(notOnStock).thenReturn(onStock);
        when(discountCardProvider.getDiscountCardById(1)).thenThrow(new NoSuchElementException("Discount card not found - " + 1));

        receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

        assertNull(receipt.getDiscountCardId());
    }

    @Test
    public void givenReceipt_whenCreateReceipt_thenReturnStringBuilderWithReceipt(){
        when(productProvider.getProductById(any(Integer.class)))
                .thenReturn(
                        new Product(1, "Product1Stock=false", productPrice1, true),
                        new Product(2, "Product2Stock=true", productPrice2, false)
                );
        String expected =  "                  CASH RECEIPT\n" +
                "                    LocalShop\n" +
                "                     Address\n" +
                "                    Tel: 7717\n" +
                "CASHIER: #717                      Date: 10/01/2023\n" +
                "                                   Time: 08:16:59\n" +
                "QTY DESCRIPTION                      PRICE    TOTAL\n" +
                "2   Product1Stock=false              $0,00    $0,00\n" +
                "6   Product2Stock=true               $0,00    $0,00\n" +
                "--------------------------------------------------\n" +
                "TAXABLE TOT.                                  $0,00\n" +
                "DISCOUNT                                      $0,00\n" +
                "TOTAL                                         $0,00\n";

        StringBuilder actual = receipt.createReceipt(productProvider);

        assertEquals(expected, actual.toString());
    }

    @Test
    public void givenReceiptWithInvalidProductId_whenCreateReceipt_thenReturnStringBuilderWithReceiptWithoutInvalidPosition(){
        when(productProvider.getProductById(1))
                .thenReturn(new Product(1, "Product1Stock=false", productPrice1, true));
        when(productProvider.getProductById(2)).thenThrow(new NoSuchElementException("Product not found - " + 2));
        String expected =  "                  CASH RECEIPT\n" +
                "                    LocalShop\n" +
                "                     Address\n" +
                "                    Tel: 7717\n" +
                "CASHIER: #717                      Date: 10/01/2023\n" +
                "                                   Time: 08:16:59\n" +
                "QTY DESCRIPTION                      PRICE    TOTAL\n" +
                "2   Product1Stock=false              $0,00    $0,00\n" +
                "--------------------------------------------------\n" +
                "TAXABLE TOT.                                  $0,00\n" +
                "DISCOUNT                                      $0,00\n" +
                "TOTAL                                         $0,00\n";

        StringBuilder actual = receipt.createReceipt(productProvider);

        assertEquals(expected, actual.toString());
    }

    @Test
    public void givenReceipt_whenWriteReceipt_thenCreateFile(){
        String dateS = DateTimeFormatter.ofPattern("dd_MM_uuuu").format(receipt.getDate());
        String timeS = DateTimeFormatter.ofPattern("HH_mm_ss").format(receipt.getTime());
        String path = "src/main/resources/receipts";
        String name = "receipt-" + dateS + "-" + timeS + ".txt";
        when(productProvider.getProductById(any(Integer.class)))
                .thenReturn(
                        new Product(1, "Product1Stock=false", productPrice1, true),
                        new Product(2, "Product2Stock=true", productPrice2, false)
                );

        receipt.writeReceipt(productProvider);

        File file = new File(path + File.separator + name);
        assertTrue(file.exists());
    }

    @Test
    public void givenReceipt_whenWriteReceipt_thenWriteReceiptToFile(){
        String dateS = DateTimeFormatter.ofPattern("dd_MM_uuuu").format(receipt.getDate());
        String timeS = DateTimeFormatter.ofPattern("HH_mm_ss").format(receipt.getTime());
        String path = "src/main/resources/receipts";
        String name = "receipt-" + dateS + "-" + timeS + ".txt";
        when(productProvider.getProductById(1))
                .thenReturn(new Product(1, "Product1Stock=false", productPrice1, true));
        when(productProvider.getProductById(2))
                .thenReturn(new Product(2, "Product2Stock=true", productPrice2, false));


        receipt.writeReceipt(productProvider);

        File file = new File(path + File.separator + name);
        StringBuilder actual = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.lines().forEach((String str)->{actual.append(str).append("\n");});
        } catch (IOException e) {
            System.out.println("error: " +e.getMessage());
        }

        assertEquals(receipt.createReceipt(productProvider).toString(), actual.toString());
    }


}