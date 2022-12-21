package providers;

import resourses.SourceType;

public class StockProviderFactory {
    public StockProvider createProductProvider(SourceType sourceType){
        StockProvider stockProvider = null;

        stockProvider = new StockGenProvider();

        return  stockProvider;
    }
}
