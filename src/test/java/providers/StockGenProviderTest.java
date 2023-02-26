package providers;

import model.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import resourses.DataGenerator;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockGenProviderTest {
    private final List<Stock> stocks = List.of(
            new Stock(1, 5, 10, "buy 5 and get 10% discount", List.of(1, 4)),
            new Stock(2, 3, 5, "buy 3 and get 5% discount", List.of(2, 5)),
            new Stock(3, 7, 7, "buy 7 and get 7% discount", List.of(3, 6)));
    @InjectMocks
    private StockGenProvider stockProvider = new StockGenProvider();
    @Mock
    private DataGenerator dataGenerator;
    @Test
    void checkFindStockShouldReturnOptionalStockContainingProduct() {
        when(dataGenerator.getStocks()).thenReturn(stocks);

        Optional<Stock> actualStock = stockProvider.findStock(2);

        assertThat(actualStock).isEqualTo(Optional.of(stocks.get(1)));
    }
    @Test
    void checkFindStockShouldReturnEmptyOptional() {
        when(dataGenerator.getStocks()).thenReturn(stocks);

        Optional<Stock> actualStock = stockProvider.findStock(10);

        assertThat(actualStock).isEqualTo(Optional.empty());
    }
}
