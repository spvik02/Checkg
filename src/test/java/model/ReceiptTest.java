package model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import providers.DiscountCardProvider;
import providers.ProductProvider;
import providers.StockProvider;
import utils.FormatUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptTest {
    @Mock
    private ProductProvider productProvider;
    @Mock
    private DiscountCardProvider discountCardProvider;
    @Mock
    private StockProvider stockProvider;
    private final double productPrice1 = 1.1, productPrice2 = 2.2, discount = 10;
    private final int productQty1 = 2, productQty2 = 6;
    private Receipt receipt;
    private final DiscountCard discountCard = new DiscountCardClassic(1, discount,
            LocalDate.of(2022, 2, 18));
    private Optional<Stock> notOnStock = Optional.empty();
    private Optional<Stock> onStock = Optional.of(
            new Stock(1, 5, 10, "buy > 5 to get 10% discount", List.of(2, 4)));
    private final List<Product> products = List.of(
            new Product(1, "Product1Stock=false", productPrice1, true),
            new Product(2, "Product2Stock=true", productPrice2, false),
            new Product(3, "Product3Stock=false", productPrice1, false),
            new Product(4, "Product4Stock=true", productPrice2, true));
    @BeforeEach
    void setUp() {
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
    @Nested
    class CalculateTotal{
        @Test
        @DisplayName("check calculateTotal should set price and price with discount to each position")
        void checkCalculateTotalShouldSetPricesToEachPosition(){
            when(productProvider.getProductById(any(Integer.class)))
                    .thenReturn(
                            products.get(0),
                            products.get(1)
                    );
            when(stockProvider.findStock(any(Integer.class)))
                    .thenReturn(notOnStock)
                    .thenReturn(onStock);
            when(discountCardProvider.getDiscountCardById(any(Integer.class)))
                    .thenReturn(discountCard);

            List<ProductInReceipt> expectedPositions = List.of(
                    new ProductInReceipt(1, 1, productQty1,
                            FormatUtil.round(productPrice1*productQty1),
                            FormatUtil.round(productPrice1*productQty1*(1-discount/100))),
                    new ProductInReceipt(2, 2, productQty2,
                            FormatUtil.round(productPrice2*productQty2),
                            FormatUtil.round(productPrice2*productQty2*(1-discount/100)))
            );

            receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

            assertThat(receipt).hasFieldOrPropertyWithValue("positions", expectedPositions);
        }


        @Test
        @DisplayName("check calculateTotal should set total receipt price and total receipt price with discount")
        public void checkCalculateTotalShouldSetTotalAndTotalWithDiscount(){
            when(productProvider.getProductById(any(Integer.class)))
                    .thenReturn(
                            products.get(0),
                            products.get(1)
                    );
            when(stockProvider.findStock(any(Integer.class)))
                    .thenReturn(notOnStock).thenReturn(onStock);
            when(discountCardProvider.getDiscountCardById(any(Integer.class)))
                    .thenReturn(discountCard);

            double expectedTotal = FormatUtil.round(productPrice1*productQty1 + productPrice2*productQty2);
            double expectedTotalWithDiscount = FormatUtil.round(
                    productPrice1*productQty1*(1-discount/100)
                            +productPrice2*productQty2*(1-discount/100));

            receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

            assertThat(receipt.getTotalPrice()).isEqualTo(expectedTotal);
            assertThat(receipt.getTotalPriceWithDiscount()).isEqualTo(expectedTotalWithDiscount);
        }

        @Test
        public void checkCalculateTotalWithWithInvalidProductIdShouldSetPricesToValidPositionsAndZeroToInvalid(){
            when(productProvider.getProductById(1))
                    .thenReturn(products.get(0));
            when(productProvider.getProductById(2))
                    .thenThrow(new NoSuchElementException("Product not found - " + 2));
            when(stockProvider.findStock(any(Integer.class)))
                    .thenReturn(notOnStock).thenReturn(onStock);
            when(discountCardProvider.getDiscountCardById(any(Integer.class)))
                    .thenReturn(discountCard);

            List<ProductInReceipt> expectedPositions = List.of(
                    new ProductInReceipt(1, 1, productQty1, FormatUtil.round(productPrice1*productQty1),
                            FormatUtil.round(productPrice1*productQty1*(1-discount/100))),
                    new ProductInReceipt(2, 2, productQty2, 0,  0)
            );

            receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

            assertThat(receipt).hasFieldOrPropertyWithValue("positions", expectedPositions);
        }

        @Test
        public void checkCalculateTotalWithInvalidDiscountCardIdShouldSetPricesWithoutDiscountCard(){
            when(productProvider.getProductById(any(Integer.class)))
                    .thenReturn(
                            products.get(0),
                            products.get(3)
                    );
            when(stockProvider.findStock(any(Integer.class)))
                    .thenReturn(notOnStock).thenReturn(onStock);
            when(discountCardProvider.getDiscountCardById(1))
                    .thenThrow(new NoSuchElementException("Discount card not found - " + 1));

            List<ProductInReceipt> expectedPositions = List.of(
                    //нет скидки по скидочной карте
                    new ProductInReceipt(1, 1, productQty1, FormatUtil.round(productPrice1*productQty1),
                            FormatUtil.round(productPrice1*productQty1)),
                    //нет скидки по скидочной карте, есть скидка по акции
                    new ProductInReceipt(2, 2, productQty2, FormatUtil.round(productPrice2*productQty2),
                            FormatUtil.round(productPrice2*productQty2*(1-discount/100)))
            );

            receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

            assertThat(receipt).hasFieldOrPropertyWithValue("positions", expectedPositions);
        }

        @Test
        public void checkCalculateTotalWithInvalidDiscountCardIdShouldSetDiscountCardToNull(){
            when(productProvider.getProductById(any(Integer.class)))
                    .thenReturn(
                            products.get(0),
                            products.get(3)
                    );
            when(stockProvider.findStock(any(Integer.class)))
                    .thenReturn(notOnStock).thenReturn(onStock);
            when(discountCardProvider.getDiscountCardById(1))
                    .thenThrow(new NoSuchElementException("Discount card not found - " + 1));

            receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

            assertThat(receipt.getDiscountCardId()).isNull();
        }

        @Test
        public void checkCalculateTotalShouldPassCardId1AsArgument(){
            when(productProvider.getProductById(any(Integer.class)))
                    .thenReturn(
                            products.get(0),
                            products.get(3)
                    );
            when(stockProvider.findStock(any(Integer.class)))
                    .thenReturn(notOnStock).thenReturn(onStock);
            when(discountCardProvider.getDiscountCardById(any(Integer.class)))
                    .thenReturn(discountCard);

            receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

            ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(discountCardProvider).getDiscountCardById(argumentCaptor.capture());

            int capturedArgument = argumentCaptor.getValue();
            assertThat(capturedArgument).isEqualTo(1);
        }

        @Test
        @DisplayName("calculateTotal should never check discount card since there are no products that will be calculated at a discount")
        public void checkCalculateTotalShouldNotCheckDiscountCard(){
            when(productProvider.getProductById(any(Integer.class)))
                    .thenReturn(
                            products.get(2),
                            products.get(3)
                    );
            when(stockProvider.findStock(any(Integer.class)))
                    .thenReturn(notOnStock).thenReturn(onStock);

            receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

            verify(discountCardProvider, never()).getDiscountCardById(anyInt());
        }
    }

    @Nested
    class CreateReceipt{
        @Test
        public void checkCreateReceiptShouldReturnStringBuilderThatShouldContainSubstrings(){
            when(productProvider.getProductById(any(Integer.class)))
                    .thenReturn(
                            products.get(0),
                            products.get(1)
                    );

            StringBuilder actualReceipt = receipt.createReceipt(productProvider);

            assertThat(actualReceipt)
                    .contains("Time: 08:16:59")
                    .contains("Date: 10/01/2023")
                    .containsPattern("2\\s+Product1Stock=false\\s+\\$0,00\\s+\\$0,00")
                    .containsPattern("6\\s+Product2Stock=true\\s+\\$0,00\\s+\\$0,00")
                    .containsPattern("TAXABLE TOT\\.\\s+\\$0,00")
                    .containsPattern("DISCOUNT\\s+\\$0,00")
                    .containsPattern("TOTAL\\s+\\$0,00");
        }
        @Test
        public void checkCreateReceiptShouldReturnStringBuilderThatShouldNotContainSubstringWithInvalidProduct(){
            when(productProvider.getProductById(1))
                    .thenReturn(products.get(0));
            when(productProvider.getProductById(2))
                    .thenThrow(new NoSuchElementException("Product not found - " + 2));

            StringBuilder actualReceipt = receipt.createReceipt(productProvider);

            assertThat(actualReceipt)
                    .contains("Time: 08:16:59")
                    .contains("Date: 10/01/2023")
                    .containsPattern("2\\s+Product1Stock=false\\s+\\$0,00\\s+\\$0,00")
                    .doesNotContainPattern("6\\s+Product2Stock=true\\s+\\$0,00\\s+\\$0,00")
                    .containsPattern("TAXABLE TOT\\.\\s+\\$0,00")
                    .containsPattern("DISCOUNT\\s+\\$0,00")
                    .containsPattern("TOTAL\\s+\\$0,00");
        }
    }

    @Nested
    class writeReceipt{
        String path = "src/main/resources/receipts",
                name = "receipt-10_01_2023-08_16_59.txt";

        @AfterEach
        void tearDown() {
            File file = new File(path + File.separator + name);
            if (file.exists())
                file.delete();
        }

        @Test
        public void checkWriteReceiptCreateFile(){
            when(productProvider.getProductById(any(Integer.class)))
                    .thenReturn(
                            new Product(1, "Product1Stock=false", productPrice1, true),
                            new Product(2, "Product2Stock=true", productPrice2, false)
                    );

            receipt.writeReceipt(productProvider);

            File file = new File(path + File.separator + name);
            assertThat(file.exists()).isTrue();
        }

        @Test
        public void checkWriteReceiptShouldWriteReceiptToFile(){
            when(productProvider.getProductById(1))
                    .thenReturn(new Product(1, "Product1Stock=false", productPrice1, true));
            when(productProvider.getProductById(2))
                    .thenReturn(new Product(2, "Product2Stock=true", productPrice2, false));

            receipt.writeReceipt(productProvider);

            File file = new File(path + File.separator + name);
            StringBuilder actualReceipt = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                br.lines().forEach( str -> actualReceipt.append(str).append("\n"));
            } catch (IOException e) {
                System.out.println("error: " +e.getMessage());
            }

            assertThat(actualReceipt.toString()).isEqualTo(receipt.createReceipt(productProvider).toString());
        }
    }
}
