package providers;

import model.Stock;

import java.util.Optional;

public interface StockProvider {
    Optional<Stock> findStock(int idProduct);
}
