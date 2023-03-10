package cache;

import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LfuCacheTest {

    LfuCache<Integer, Product> lfuCache;
    List<Product> products = List.of(
            new Product(0, "kiwi", 2, false),
            new Product(1, "milk", 2.8, true),
            new Product(2, "bread", 2.2, true),
            new Product(3, "tea", 4.3, true),
            new Product(4, "coffee", 12, true),
            new Product(5, "water", 2.2, false),
            new Product(6, "alcohol", 7.2, false)
    );
    @BeforeEach
    void setUp() {
        lfuCache = new LfuCache<>(2);
    }

    @Nested
    class Get{
        @Test
        void checkGetShouldReturnNullWhenEmpty(){
            assertThat(lfuCache.get(1)).isNull();
        }
        @Test
        void checkGetShouldReturnNullWhenDoesNotContain(){
            lfuCache.put(0, products.get(1));
            assertThat(lfuCache.get(1)).isNull();
        }
        @Test
        void checkGetShouldReturnProductWhenContain(){
            lfuCache.put(1, products.get(1));
            assertThat(lfuCache.get(1)).isEqualTo(products.get(1));
        }
        @Test
        void checkGetShouldChangeLessFrequentlyUsed(){
            lfuCache.put(1, products.get(1));
            lfuCache.put(2, products.get(2));
            lfuCache.get(1);
            lfuCache.put(3, products.get(3));
            assertThat(lfuCache.get(2)).isNull();
        }
    }

    @Nested
    class Put{
        @Test
        void checkPutShouldRemoveOldestFromCacheWhenAboveCapacity(){
            lfuCache.put(1, products.get(1));
            lfuCache.put(2, products.get(2));
            lfuCache.put(3, products.get(3));
            assertThat(lfuCache.get(1)).isNull();
        }
        @Test
        void checkPutShouldChangeLessFrequentlyUsed(){
            lfuCache.put(1, products.get(1));
            lfuCache.put(2, products.get(2));
            lfuCache.put(1, products.get(0));
            lfuCache.put(3, products.get(3));
            assertThat(lfuCache.get(2)).isNull();
        }
        @Test
        void checkPutShouldWriteNewInsteadOfLessFrequentlyUsedWhenAboveCapacity(){
            lfuCache.put(1, products.get(1));
            lfuCache.put(2, products.get(2));
            lfuCache.put(3, products.get(3));
            assertAll(
                    () -> assertThat(lfuCache.get(1)).isNull(),
                    () -> assertThat(lfuCache.get(2)).isEqualTo(products.get(2)),
                    () -> assertThat(lfuCache.get(3)).isEqualTo(products.get(3))
            );
        }
    }

    @Nested
    class Delete{
        @Test
        void checkDeleteShouldDeleteFromCache(){
            lfuCache.put(1, products.get(1));
            lfuCache.delete(1);
            assertThat(lfuCache.get(1)).isNull();
        }
        @Test
        void checkDeleteShouldDeleteFromCacheFromAllMaps(){
            lfuCache.put(1, products.get(1));
            lfuCache.put(2, products.get(2));
            lfuCache.get(1);
            lfuCache.get(1);
            lfuCache.delete(1);
            lfuCache.put(3, products.get(3));
            assertAll(
                    () -> assertThat(lfuCache.get(1)).isNull(),
                    () -> assertThat(lfuCache.get(2)).isEqualTo(products.get(2)),
                    () -> assertThat(lfuCache.get(3)).isEqualTo(products.get(3))
            );
        }
        @Test
        void checkDeleteShouldResetMin(){
            lfuCache.put(2, products.get(2));
            lfuCache.put(1, products.get(1));
            lfuCache.get(1);
            lfuCache.get(1);
            lfuCache.delete(2);
            lfuCache.put(3, products.get(3));
            assertAll(
                    () -> assertThat(lfuCache.get(2)).isNull(),
                    () -> assertThat(lfuCache.get(1)).isEqualTo(products.get(1)),
                    () -> assertThat(lfuCache.get(3)).isEqualTo(products.get(3))
            );
        }
    }
}
