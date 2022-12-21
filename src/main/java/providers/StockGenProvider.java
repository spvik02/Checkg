package providers;

import model.Product;
import model.Stock;
import resourses.DataGenerator;

import java.util.Optional;

public class StockGenProvider implements StockProvider{
    DataGenerator dataGenerator = DataGenerator.getInstance();

    StockGenProvider(){
        dataGenerator.generateStocks();
    }

    @Override
    public Optional<Stock> findStock(int idProduct) {
        return dataGenerator.getStocks().stream()
                .filter(stock -> stock.isProductOnStock(idProduct)).findFirst();
    }
}
