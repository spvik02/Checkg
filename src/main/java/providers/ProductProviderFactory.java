package providers;

import resourses.SourceType;

public class ProductProviderFactory {
    public ProductProvider createProductProvider(SourceType sourceType){
        ProductProvider productProvider = null;

        if(sourceType == SourceType.FILE){
            productProvider = new ProductFileProvider();
        } else if (sourceType == SourceType.GENERATOR) {
            productProvider = new ProductGenProvider();
        }

        return  productProvider;
    }
}
