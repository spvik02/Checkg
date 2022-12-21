package resourses;

import model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DataGenerator {
    private static DataGenerator instance;
    private List<Product> products;
    private List<DiscountCard> cards;
    private List<Stock> stocks;

    private DataGenerator(){}
    public static DataGenerator getInstance() {
        if (instance == null) {
            instance = new DataGenerator();
        }
        return instance;
    }


    public void generateProducts(){
        products = List.of(
                new Product(1, "milk", 2.8, true),
                new Product(2, "bread", 2.2, true),
                new Product(3, "tea", 4.3, true),
                new Product(4, "coffee", 12, true),
                new Product(5, "water", 2.2, false),
                new Product(6, "alcohol", 7.2, false)

                //1-2 2-5 3-5 4-2 5-5 6-2 Card-2
                //1 по скидочной карте и скидка по количеству
                //2 по скидочной карте
                //3 по скидочной карте
                //4 по скидочной карте
                //5 скидка по количеству
                //6 без скидок
        );
    }

    public void generateDiscountCards(){
        cards = List.of(
                new DiscountCardClassic(1, 10, LocalDate.parse("17/02/2014", DateTimeFormatter.ofPattern("dd/MM/uuuu"))),
                new DiscountCardClassic(2, 15, LocalDate.parse("07/04/2021", DateTimeFormatter.ofPattern("dd/MM/uuuu"))),
                new DiscountCardMonth(new DiscountCardClassic(6, 10, LocalDate.parse("07/04/2020", DateTimeFormatter.ofPattern("dd/MM/uuuu"))))
        );
    }

    public void generateStocks(){
        stocks = List.of(
                new Stock(1, 5, 10,
                        "Среди товаров предусмотреть акционные. Если их в чеке больше пяти, то сделать скидку 10% по этой позиции.",
                        List.of(1, 5)
                        )
        );
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<DiscountCard> getCards() {
        return cards;
    }

    public List<Stock> getStocks() {
        return stocks;
    }
}
