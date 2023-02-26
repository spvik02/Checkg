package utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.FormatUtil.formatLineCenter;

class FormatUtilTest {
    @Nested
    class FormatLineCenter{
        @Test
        void checkFormatLineCenterReturnCenteredString() {
            //полоивна строки + половина длины текста = 5 + 3
            String expectedLine = String.format("%8s", "center");
            String actualLine = formatLineCenter(10, "center");
            assertThat(actualLine).isEqualTo(expectedLine);
        }
        @Test
        void checkFormatLineCenterWithOddTextLengthReturnCenteredString() {
            //полоивна строки + половина длины текста, округленная до нижней границы = 5 + 3.5
            String expectedLine = String.format("%8s", "centero");
            String actualLine = formatLineCenter(10, "centero");
            assertThat(actualLine).isEqualTo(expectedLine);
        }
        @Test
        void checkFormatLineCenterWithOddLengthReturnCenteredString() {
            //полоивна строки, округленная до нижней гарницы + половина длины текста = 5.5 + 3
            String expectedLine = String.format("%8s", "center");
            String actualLine = formatLineCenter(11, "center");
            assertThat(actualLine).isEqualTo(expectedLine);
        }
    }
}
