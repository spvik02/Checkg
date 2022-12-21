package providers;

import model.DiscountCard;
import resourses.DataGenerator;

import java.util.NoSuchElementException;

public class DiscountCardGenProvider implements DiscountCardProvider{
    DataGenerator dataGenerator = DataGenerator.getInstance();

    DiscountCardGenProvider(){
        dataGenerator.generateDiscountCards();
    }
    @Override
    public DiscountCard getDiscountCardById(int id) {
        return dataGenerator.getCards().stream().filter(card -> id == card.getId()).findFirst().orElseThrow(()-> new NoSuchElementException("Discount card not found - " + id));
    }
}
