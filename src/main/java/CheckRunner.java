import model.ProductInReceipt;
import model.Receipt;
import providers.*;
import resourses.SourceType;
import utils.ParametersUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

public class CheckRunner {


    public static void main(String[] args) {
        SourceType source = SourceType.GENERATOR;
        ProductProviderFactory productProviderFactory = new ProductProviderFactory();
        StockProviderFactory stockProviderFactory = new StockProviderFactory();
        DiscountCardProviderFactory discountCardProviderFactory = new DiscountCardProviderFactory();
        ProductProvider productProvider;
        StockProvider stockProvider;
        DiscountCardProvider discountCardProvider;

        Receipt receipt = new Receipt.ReceiptBuilder()
                .withCashier(717)
                .withDateTime(LocalDate.now(), LocalTime.now())
                .build();

        //добавляем позиции чека из параметров/файла
        if (args[0].contains("File")){
            source = SourceType.FILE;
            String fileName = args[0].substring(args[0].indexOf("-") + 1);
                String [] parameters = ParametersUtil.readParametersFromFile(fileName);
            if(parameters != null)
                ParametersUtil.parseParameters(parameters, receipt);
            else return;
        }else{
            ParametersUtil.parseParameters(args, receipt);
        }

        //создаем провайдеры для данных в зависимости от указанного источника данных
        productProvider = productProviderFactory.createProductProvider(source);
        stockProvider = stockProviderFactory.createProductProvider(source);
        discountCardProvider = discountCardProviderFactory.createDiscountCardProvider(source);


        receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);
        //print receipt to console
        System.out.println(receipt.createReceipt(productProvider));
        //write receipt to file
        receipt.writeReceipt(productProvider);

    }
}
