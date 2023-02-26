package providers;

import model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import resourses.DataGenerator;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductGenProviderTest {

    private final List<Product> products = List.of(
            new Product(1, "atDisc1", 2, true),
            new Product(2, "atDisc2", 2, true),
            new Product(3, "atDisc3", 4, true),
            new Product(4, "atDisc4", 12, true),
            new Product(5, "notAtDisc1", 2, false),
            new Product(6, "notAtDisc2", 7, false)
    );

    @InjectMocks
    private ProductGenProvider productProvider = new ProductGenProvider();

    @Mock
    private DataGenerator dataGenerator;

    @Test
    void checkGetProductByIdShouldReturnProductWithId2() {
        when(dataGenerator.getProducts()).thenReturn(products);

        Product actualProduct = productProvider.getProductById(2);

        assertThat(actualProduct).isEqualTo(products.get(1));
    }

    @Test
    void checkGetProductByIdShouldThrowNoSuchElementException() {
        when(dataGenerator.getProducts()).thenReturn(products);

        assertThatThrownBy(()-> productProvider.getProductById(10))
                .isInstanceOf(NoSuchElementException.class);
    }
}
