package providers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import resourses.SourceType;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountCardProviderFactoryTest {

    private DiscountCardProviderFactory factory;
    @BeforeEach
    void setUp() {
        factory = new DiscountCardProviderFactory();
    }

    @Test
    @DisplayName("createDiscountCardProvider with SourceType.FILE should return exactly instance of DiscountCardFileProvider")
    void checkCreateDiscountCardProviderWithFileSourceReturnDiscountCardFileProvider(){
        assertThat(factory.createDiscountCardProvider(SourceType.FILE))
                .isExactlyInstanceOf(DiscountCardFileProvider.class);
    }
    @Test
    @DisplayName("createDiscountCardProvider with SourceType.GENERATOR should return exactly instance of DiscountCardGenProvider")
    void checkCreateDiscountCardProviderWithGenSourceReturnDiscountCardGenProvider(){
        assertThat(factory.createDiscountCardProvider(SourceType.GENERATOR))
                .isExactlyInstanceOf(DiscountCardGenProvider.class);
    }

    @ParameterizedTest
    @EnumSource(SourceType.class)
    @DisplayName("createDiscountCardProvider with any SourceType should return an instance of DiscountCardProvider")
    void checkCreateDiscountCardProviderWithAnySourceShouldReturnDiscountCardProvider(SourceType type){
        assertThat(factory.createDiscountCardProvider(type))
                .isInstanceOf(DiscountCardProvider.class);
    }
}
