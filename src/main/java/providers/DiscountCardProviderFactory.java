package providers;

import resourses.SourceType;

public class DiscountCardProviderFactory {
    public DiscountCardProvider createDiscountCardProvider(SourceType sourceType){
        DiscountCardProvider cardProvider = null;

        if(sourceType == SourceType.FILE){
            cardProvider = new DiscountCardFileProvider();
        } else if (sourceType == SourceType.GENERATOR) {
            cardProvider = new DiscountCardGenProvider();
        }

        return  cardProvider;
    }
}
