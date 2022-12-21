package providers;

import model.Product;
import resourses.DataFileSource;

import java.util.List;

public class ProductFileProvider implements ProductProvider{
    private final List<Product> productList = DataFileSource.readProducts();
    @Override
    public Product getProductById(int id) {
        return productList.stream()
                .filter(product -> id == product.getId()).findFirst().orElseThrow();
    }
}
