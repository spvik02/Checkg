package providers;

import model.Product;
import resourses.DataFileSource;

import java.util.List;
import java.util.NoSuchElementException;

public class ProductFileProvider implements ProductProvider{
    private final List<Product> productList = DataFileSource.readProducts();
    @Override
    public Product getProductById(int id) {
        return productList.stream()
                .filter(product -> id == product.getId()).findFirst()
                .orElseThrow(()-> new NoSuchElementException("Product not found - " + id));
    }
}
