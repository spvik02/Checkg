import com.itextpdf.text.DocumentException;
import model.Receipt;
import providers.*;
import resourses.SourceType;
import utils.ParametersUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

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

        productProvider = productProviderFactory.createProductProvider(source);
        stockProvider = stockProviderFactory.createProductProvider(source);
        discountCardProvider = discountCardProviderFactory.createDiscountCardProvider(source);

        receipt.calculateTotal(productProvider, stockProvider, discountCardProvider);

        System.out.println(receipt.createReceipt(productProvider));
        receipt.writeReceipt(productProvider);
        try {
            receipt.writeToPdf(productProvider);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
