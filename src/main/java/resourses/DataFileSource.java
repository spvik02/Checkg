package resourses;

import model.DiscountCard;
import model.DiscountCardClassic;
import model.Product;
import providers.DiscountCardFileProvider;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DataFileSource {
    static final private String path = "src/main/resources/dataFiles";
    static final private String nameProductFile = "products.txt";
    static final private String nameDiscountCardsFile = "cards.txt";

    public static List<Product> readProducts(){
        return readData(nameProductFile, DataFileSource::makeProduct);
    }

    public static List<DiscountCard> readCards(){
        return readData(nameDiscountCardsFile, DataFileSource::makeCard);
    }

    static <T> List<T> readData(String fileName, Function<String, T> func){
        File cardFile = new File(path+ File.separator+fileName);
        List<T> list = new ArrayList<>();
        try{
            if(cardFile.exists()){
                BufferedReader reader = new BufferedReader(new FileReader(cardFile));
                String line;
                while ((line = reader.readLine()) != null){
                    list.add(func.apply(line));
                }
                reader.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    static Product makeProduct(String lineToParse){
        String[] result = lineToParse.split("; ");
        return new Product(
                Integer.parseInt(result[0]),
                result[1],
                Double.parseDouble(result[2]),
                Boolean.parseBoolean(result[3])
        );
    }
    static DiscountCardClassic makeCard(String lineToParse){
        String[] result = lineToParse.split("; ");
        return new DiscountCardClassic(
                Integer.parseInt(result[0]),
                Double.parseDouble(result[1]),
                LocalDate.parse(result[2], DateTimeFormatter.ofPattern("dd/MM/uuuu"))
        );
    }
}
