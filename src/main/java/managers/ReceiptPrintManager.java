package managers;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.*;
import model.ProductInReceipt;
import model.Receipt;
import providers.ProductProvider;
import utils.FormatUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

public class ReceiptPrintManager {

    String getFileName(LocalDate date, LocalTime time){
        String dateS = DateTimeFormatter.ofPattern("dd_MM_uuuu").format(date);
        String timeS = DateTimeFormatter.ofPattern("HH_mm_ss").format(time);
        return "receipt-" + dateS + "-" + timeS;
    }

    public void writeToTxt(Receipt receipt, ProductProvider productProvider){
        String name = getFileName(receipt.getDate(), receipt.getTime()) + ".txt";
        String pathReceiptsTxt = "src/main/resources/receipts";
        File dir = new File(pathReceiptsTxt);
        dir.mkdirs();

        try (
                FileWriter fileWriter = new FileWriter(pathReceiptsTxt + File.separator + name);
                PrintWriter printWriter = new PrintWriter(fileWriter)
        ){
            printWriter.print(createReceipt(receipt, productProvider));
        } catch (IOException e) {
            System.out.println("error: " +e.getMessage());
        }
    }

    public StringBuilder createReceipt(Receipt receipt, ProductProvider productProvider) {
        String title = "CASH RECEIPT";
        String nameStore = "LocalShop";
        String address = "Address";
        String number = "7717";

        int length = 50;
        int paddingQty = -4;
        int paddingPrice = 9;
        int paddingForDateTime = -(length*2/3)-paddingPrice*2+16;
        int paddingDesc = -(length*2/3)-paddingQty;
        int paddingFooter = -(length*2/3)-paddingPrice;

        String dateS = DateTimeFormatter.ofPattern("dd/MM/uuuu").format(receipt.getDate());
        String timeS = DateTimeFormatter.ofPattern("HH:mm:ss").format(receipt.getTime());

        StringBuilder receiptBuilder = new StringBuilder(FormatUtil.formatLineCenter(length, title + "\n"));
        receiptBuilder.append(FormatUtil.formatLineCenter(length, nameStore + "\n"));
        receiptBuilder.append(FormatUtil.formatLineCenter(length, address)).append("\n");
        receiptBuilder.append(FormatUtil.formatLineCenter(length, "Tel: " + number  + "\n"));

        receiptBuilder.append(FormatUtil.formatLineTwoCol(paddingForDateTime, 1,
                "CASHIER: #" + receipt.getCashier(), "Date: " + dateS+ "\n"));
        receiptBuilder.append(FormatUtil.formatLineTwoCol(paddingForDateTime, 1,
                "", "Time: " + timeS+ "\n"));

        receiptBuilder.append(FormatUtil.formatFourCol(paddingQty, paddingDesc, paddingPrice, paddingPrice,
                "QTY", "DESCRIPTION", "PRICE", "TOTAL")).append("\n");
        for(var position : receipt.getPositions()){
            try{
                receiptBuilder.append(FormatUtil.formatFourCol(paddingQty, paddingDesc, paddingPrice, paddingPrice,
                                String.valueOf(position.getQuantity()),
                                productProvider.getProductById(position.getIdProduct()).getName(),
                                "$" + FormatUtil.formatNum2(position.getPrice()),
                                "$" + FormatUtil.formatNum2(position.getTotal())))
                        .append("\n");
            }catch (NoSuchElementException ignored){
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        receiptBuilder.append("-".repeat(length)).append("\n");

        receiptBuilder.append(FormatUtil.formatLineTwoCol(paddingFooter, paddingPrice,
                        "TAXABLE TOT.", "$"+FormatUtil.formatNum2(receipt.getTotalPriceWithDiscount())))
                .append("\n");
        receiptBuilder.append(FormatUtil.formatLineTwoCol(paddingFooter, paddingPrice,
                        "DISCOUNT",
                        "$"+FormatUtil.formatNum2(
                                FormatUtil.round(receipt.getTotalPrice() - receipt.getTotalPriceWithDiscount()))))
                .append("\n");
        receiptBuilder.append(FormatUtil.formatLineTwoCol(paddingFooter, paddingPrice,
                        "TOTAL", "$"+ FormatUtil.formatNum2(receipt.getTotalPrice())))
                .append("\n");

        return receiptBuilder;
    }

    public void writeToPdf(Receipt receipt, ProductProvider productProvider)
            throws IOException, DocumentException {
        String pathReceiptsPdf = "src/main/resources/receiptsPdf";
        String filePath = pathReceiptsPdf + File.separator + getFileName(receipt.getDate(), receipt.getTime()) + ".pdf";
        String title = "CASH RECEIPT";
        String nameStore = "LocalShop";
        String address = "Address";
        String number = "7717";
        String dateT = DateTimeFormatter.ofPattern("dd/MM/uuuu").format(receipt.getDate());
        String timeT = DateTimeFormatter.ofPattern("HH:mm:ss").format(receipt.getTime());

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        String pathCltTemplate = "src/main/resources/pdf/Clevertec_Template.pdf";
        PdfReader reader = new PdfReader(pathCltTemplate);
        PdfImportedPage bcg = writer.getImportedPage(reader, 1);
        PdfContentByte contentByte = writer.getDirectContent();
        contentByte.addTemplate(bcg, 0, 0);
        document.add(new Phrase(" "));

        PdfPTable receiptHeaderTable = new PdfPTable(new float[]{100});
        addReceiptHeaderRows(receiptHeaderTable, title, nameStore, address, number);
        receiptHeaderTable.setSpacingBefore(100f);
        document.add(receiptHeaderTable);

        PdfPTable receiptInfoTable = new PdfPTable(new float[]{70, 30});
        addReceiptInfoRows(receiptInfoTable,
                "CASHIER: #" +  receipt.getCashier(),
                "Date: " + dateT,
                "Time: " + timeT);
        document.add(receiptInfoTable);

        PdfPTable positionTable = new PdfPTable(new float[]{7, 63, 15, 15});
        addPositionRows(positionTable, receipt.getPositions(), productProvider);
        document.add(positionTable);

        PdfPTable receiptFooterTable = new PdfPTable(new float[]{70, 30});
        addReceiptFooterRows(receiptFooterTable, receipt.getTotalPrice(), receipt.getTotalPriceWithDiscount());
        document.add(receiptFooterTable);

        document.close();
        writer.close();
    }


    private void addPositionRows(PdfPTable table, List<ProductInReceipt> positions, ProductProvider productProvider) {
        addPositionRow(table, "QTY", "DESCRIPTION", "PRICE", "TOTAL");
        for(var position : positions){
            try{
                addPositionRow(table,
                        String.valueOf(position.getQuantity()),
                        productProvider.getProductById(position.getIdProduct()).getName(),
                        "$" + FormatUtil.formatNum2(position.getPrice()),
                        "$" + FormatUtil.formatNum2(position.getTotal()));
            }catch (NoSuchElementException ignored){
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
    private void addPositionRow(PdfPTable table, String qty, String desc, String price, String total){
        addCellWithoutBorderToTable(table, qty, Element.ALIGN_CENTER);
        addCellWithoutBorderToTable(table, desc, Element.ALIGN_LEFT);
        addCellWithoutBorderToTable(table, price, Element.ALIGN_RIGHT);
        addCellWithoutBorderToTable(table, total, Element.ALIGN_RIGHT);
    }

    private void addReceiptInfoRows(PdfPTable table, String cashier, String date, String time){
        addCellWithoutBorderToTable(table, cashier, Element.ALIGN_LEFT);
        addCellWithoutBorderToTable(table, date, Element.ALIGN_RIGHT);
        addCellWithoutBorderToTable(table, "", Element.ALIGN_LEFT);
        addCellWithoutBorderToTable(table, time, Element.ALIGN_RIGHT);
    }
    private void addReceiptHeaderRows(PdfPTable table, String title, String storeName, String address, String phone){
        addCellWithoutBorderToTable(table, title, Element.ALIGN_CENTER);
        addCellWithoutBorderToTable(table, storeName, Element.ALIGN_CENTER);
        addCellWithoutBorderToTable(table, address, Element.ALIGN_CENTER);
        addCellWithoutBorderToTable(table, phone, Element.ALIGN_CENTER);
    }
    private void addReceiptFooterRows(PdfPTable table, double price, double total){
        addCellWithTopBorderToTable(table, "TAXABLE TOT.", Element.ALIGN_LEFT);
        addCellWithTopBorderToTable(table, "$" + FormatUtil.formatNum2(price), Element.ALIGN_RIGHT);
        addCellWithoutBorderToTable(table, "DISCOUNT", Element.ALIGN_LEFT);
        addCellWithoutBorderToTable(table, "$" + FormatUtil.formatNum2(FormatUtil.round(price-total)), Element.ALIGN_RIGHT);
        addCellWithoutBorderToTable(table, "TOTAL", Element.ALIGN_LEFT);
        addCellWithoutBorderToTable(table, "$" + FormatUtil.formatNum2(total), Element.ALIGN_RIGHT);
    }

    private void addCellWithoutBorderToTable(PdfPTable table, String value, int alignment){
        PdfPCell cell = new PdfPCell(new Phrase(value));
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidth(0);
        table.addCell(cell);
    }

    private void addCellWithTopBorderToTable(PdfPTable table, String value, int alignment){
        PdfPCell cell = new PdfPCell(new Phrase(value));
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidth(0);
        cell.setBorderWidthTop(1);
        table.addCell(cell);
    }
}
