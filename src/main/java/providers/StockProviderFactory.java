package providers;

import resourses.SourceType;

public class StockProviderFactory {
    public StockProvider createProductProvider(SourceType sourceType){
        StockProvider stockProvider;

        stockProvider = new StockGenProvider();

        return  stockProvider;
    }
}
