package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FormatUtil {
    //выравнивает строку по центру
    public static String formatLineCenter(int len, String text){
        //высчитывает отступ слева, чтобы надпись была по центру
        int left = (len+text.length())/2;
        return String.format("%"+left+"s", text);
    }
    //выравнивает строку в две колонки с указанными отступами
    public static String formatLineTwoCol(int padding1, int padding2, String col1, String col2){
        return String.format("%"+padding1+"s%"+padding2+"s", col1, col2);
    }
    //выравнивает строку в 4 колонки с указанными отступами
    public static String formatFourCol(int padding1, int padding2, int padding3, int padding4, String col1, String col2, String col3, String col4){
        return String.format("%"+padding1+"s%"+padding2+"s"+"%"+padding3+"s%"+padding4+"s",
                col1, col2, col3, col4);
    }
    //округляет double до двух знаком после точки
    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    //форматирует double для чека, чтобы отображалось всегда два символа после точки
    public static String formatNum2(double value){
        return String.format("%.2f", value);
    }
}
