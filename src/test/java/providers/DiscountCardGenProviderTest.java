package providers;

import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import resourses.DataGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountCardGenProviderTest {

    private final List<DiscountCard> cards = List.of(
            new DiscountCardClassic(1, 10, LocalDate.of(2022, 2, 18)),
            new DiscountCardClassic(2, 5, LocalDate.of(2022, 1, 20)),
            new DiscountCardMonth( new DiscountCardClassic(3, 15,
                    LocalDate.of(2022, 6, 16))));

    @InjectMocks
    private DiscountCardGenProvider cardProvider = new DiscountCardGenProvider();

    @Mock
    private DataGenerator dataGenerator;

    @Test
    void checkGetDiscountCardByIdShouldReturnCardWithId2() {
        when(dataGenerator.getCards()).thenReturn(cards);

        DiscountCard actualCard = cardProvider.getDiscountCardById(2);

        assertThat(actualCard).isEqualTo(cards.get(1));
    }

    @Test
    void checkGetDiscountCardByIdShouldThrowNoSuchElementException() {
        when(dataGenerator.getCards()).thenReturn(cards);

        assertThatThrownBy(()-> cardProvider.getDiscountCardById(10))
                .isInstanceOf(NoSuchElementException.class);
    }
}
