package providers;

import org.junit.jupiter.api.Test;
import resourses.SourceType;

import static org.junit.jupiter.api.Assertions.*;

class DiscountCardProviderFactoryTest {
    DiscountCardProviderFactory factory = new DiscountCardProviderFactory();

    @Test
    void givenFileSource(){
        assertEquals(DiscountCardFileProvider.class, factory.createDiscountCardProvider(SourceType.FILE).getClass());
    }
    @Test
    void givenGenSource(){
        assertEquals(DiscountCardGenProvider.class, factory.createDiscountCardProvider(SourceType.GENERATOR).getClass());
    }

}