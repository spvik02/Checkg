package providers;

import model.DiscountCardClassic;
import model.DiscountCard;
import resourses.DataFileSource;

import java.util.List;
import java.util.NoSuchElementException;

public class DiscountCardFileProvider implements DiscountCardProvider{
    final List<DiscountCard> cardList = DataFileSource.readCards();

    public List<DiscountCard> getCardList() {
        return cardList;
    }

    DiscountCardFileProvider(){};
    @Override
    public DiscountCard getDiscountCardById(int id) {
        return this.getCardList().stream().filter(card -> id == card.getId()).findFirst()
                .orElseThrow(()-> new NoSuchElementException("Discount card not found - " + id));
    }
}
