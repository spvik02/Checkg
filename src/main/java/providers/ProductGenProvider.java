package providers;

import model.Product;
import resourses.DataGenerator;

import java.util.NoSuchElementException;

public class ProductGenProvider implements ProductProvider{
    DataGenerator dataGenerator = DataGenerator.getInstance();

    ProductGenProvider(){

        dataGenerator.generateProducts();
    }

    @Override
    public Product getProductById(int id) {
        return dataGenerator.getProducts().stream()
                .filter(product -> id == product.getId()).findFirst()
                .orElseThrow(()-> new NoSuchElementException("Product not found - " + id));
    }
}
