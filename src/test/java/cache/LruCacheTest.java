package cache;

import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LruCacheTest{
    LruCache<Integer, Product> lruCache;
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
        lruCache = new LruCache<>(2);
    }
    @Nested
    class Get{
        @Test
        void checkGetShouldReturnNullWhenEmpty(){
            assertThat(lruCache.get(1)).isNull();
        }
        @Test
        void checkGetShouldReturnNullWhenDoesNotContain(){
            lruCache.put(0, products.get(1));
            assertThat(lruCache.get(1)).isNull();
        }

        @Test
        void checkGetShouldReturnProductWhenContain(){
            lruCache.put(1, products.get(1));
            assertThat(lruCache.get(1)).isEqualTo(products.get(1));
        }
        @Test
        void checkGetShouldChangeLeastRecentlyUsed(){
            lruCache.put(1, products.get(1));
            lruCache.put(2, products.get(2));
            lruCache.get(1);
            lruCache.put(3, products.get(3));
            assertThat(lruCache.get(2)).isNull();
        }
    }
    @Nested
    class Put{
        @Test
        void checkPutShouldRemoveLeastRecentlyUsedFromCacheWhenAboveCapacity(){
            lruCache.put(1, products.get(1));
            lruCache.put(2, products.get(2));
            lruCache.put(3, products.get(3));
            assertThat(lruCache.get(1)).isNull();
        }
        @Test
        void checkPutShouldChangeLeastRecentlyUsed(){
            lruCache.put(1, products.get(1));
            lruCache.put(2, products.get(2));
            lruCache.put(1, products.get(0));
            lruCache.put(3, products.get(3));
            assertThat(lruCache.get(2)).isNull();
        }
        @Test
        void checkPutShouldWriteNewInsteadOfLeastRecentlyUsedWhenAboveCapacity(){
            lruCache.put(1, products.get(1));
            lruCache.put(2, products.get(2));
            lruCache.put(3, products.get(3));
            assertAll(
                    () -> assertThat(lruCache.get(1)).isNull(),
                    () -> assertThat(lruCache.get(2)).isEqualTo(products.get(2)),
                    () -> assertThat(lruCache.get(3)).isEqualTo(products.get(3))
            );
        }
    }
    @Nested
    class Delete{
        @Test
        void checkDeleteShouldDeleteFromCache(){
            lruCache.put(1, products.get(1));
            lruCache.delete(1);
            assertThat(lruCache.get(1)).isNull();
        }
        @Test
        void checkDeleteShouldPutProduct3(){
            lruCache.put(1, products.get(1));
            lruCache.put(2, products.get(2));
            lruCache.get(1);
            lruCache.get(1);
            lruCache.delete(1);
            lruCache.put(3, products.get(3));
            assertAll(
                    () -> assertThat(lruCache.get(1)).isNull(),
                    () -> assertThat(lruCache.get(2)).isEqualTo(products.get(2)),
                    () -> assertThat(lruCache.get(3)).isEqualTo(products.get(3))
            );
        }
    }
}
