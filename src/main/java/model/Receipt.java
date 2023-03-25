package model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import providers.DiscountCardProvider;
import providers.ProductProvider;
import providers.StockProvider;
import utils.FormatUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Receipt {
    private int id;
    private List<ProductInReceipt> positions;
    private Integer discountCardId;
    private double totalPrice;
    private double totalPriceWithDiscount;
    private final int cashier;
    private final LocalDate date;
    private final LocalTime time;

    private Receipt(ReceiptBuilder builder){
        id = builder.id;
        positions = builder.positions;
        discountCardId = builder.discountCardId;
        cashier = builder.cashier;
        date = builder.date;
        time = builder.time;
    }

    public static class ReceiptBuilder{
        private int id;
        private List<ProductInReceipt> positions;
        private Integer discountCardId;
        private int cashier;
        private LocalDate date;
        private LocalTime time;

        public ReceiptBuilder(){}
        public ReceiptBuilder withCashier(int idCashier){
            cashier = 717;
            return this;
        }
        public ReceiptBuilder withDateTime(LocalDate date, LocalTime time){
            this.date = date;
            this.time = time;
            return this;
        }
        public ReceiptBuilder withDiscountCard(int id){
            this.discountCardId = id;
            return this;
        }
        public ReceiptBuilder withPositions(List<ProductInReceipt> positions){
            this.positions = positions;
            return this;
        }
        public Receipt build(){
            return new Receipt(this);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ProductInReceipt> getPositions() {
        return positions;
    }

    public void setPositions(List<ProductInReceipt> positions) {
        this.positions = positions;
    }

    public Integer getDiscountCardId() {
        return discountCardId;
    }

    public void setDiscountCardId(Integer discountCardId) {
        this.discountCardId = discountCardId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getTotalPriceWithDiscount() {
        return totalPriceWithDiscount;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = FormatUtil.round(totalPrice);
    }

    public void setTotalPriceWithDiscount(double totalPriceWithDiscount) {
        this.totalPriceWithDiscount = FormatUtil.round(totalPriceWithDiscount);
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void calculateTotal(ProductProvider productProvider, StockProvider stockProvider, DiscountCardProvider discountCardProvider){
        double totalPriceWithDiscount = 0;
        double totalPrice = 0;

        for(ProductInReceipt position : positions){
            //высчитывается начальная стоимость позиции
            double positionPrice;
            Product product;

            try{
                product = productProvider.getProductById(position.getIdProduct());
                positionPrice = product.getPrice()
                        * position.getQuantity();
            }catch (Exception e){
                System.out.println(e.getMessage() + " Product will not be counted.");
                position.setTotal(0);
                position.setPrice(0);
                continue;
            }
            position.setPrice(positionPrice);
            totalPrice += positionPrice;

            //расчет скидки по акции по количеству
            Optional<Stock> stock = stockProvider.findStock(position.getIdProduct());
            if(stock.isPresent() && position.getQuantity()>=stock.get().getQuantity())
                positionPrice -= positionPrice*stock.get().getSale()/100;
                //расчет скидки по скидочной карте
                //скидки не суммируются
                //если скидочная карты была предъявлена и скидка распространяется на товар
            else if (discountCardId!=null && product.isAtDiscount()) {
                try {
                    positionPrice -= positionPrice * discountCardProvider.getDiscountCardById(discountCardId)
                            .getPercentageOfDiscount()/100;
                } catch (Exception e) {
                    discountCardId = null;
                    System.out.println("The discount card is not registered. The discount will not be calculated. " + e.getMessage());
                }
            }

            positionPrice = FormatUtil.round(positionPrice);
            position.setTotal(positionPrice);
            //добавляем высчитанную стоимость в стоимость всего чека
            totalPriceWithDiscount +=positionPrice;
        }
        setTotalPrice(totalPrice);
        setTotalPriceWithDiscount(totalPriceWithDiscount);
    }

    public StringBuilder createReceipt(ProductProvider productProvider) {
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

        String dateS = DateTimeFormatter.ofPattern("dd/MM/uuuu").format(date);
        String timeS = DateTimeFormatter.ofPattern("HH:mm:ss").format(time);

        StringBuilder receipt= new StringBuilder(FormatUtil.formatLineCenter(length, title + "\n"));
        receipt.append(FormatUtil.formatLineCenter(length, nameStore + "\n"));
        receipt.append(FormatUtil.formatLineCenter(length, address)).append("\n");
        receipt.append(FormatUtil.formatLineCenter(length, "Tel: " + number  + "\n"));

        receipt.append(FormatUtil.formatLineTwoCol(paddingForDateTime, 1,"CASHIER: #" + cashier, "Date: " + dateS+ "\n"));
        receipt.append(FormatUtil.formatLineTwoCol(paddingForDateTime, 1, "", "Time: " + timeS+ "\n"));

        receipt.append(FormatUtil.formatFourCol(paddingQty, paddingDesc, paddingPrice, paddingPrice, "QTY", "DESCRIPTION", "PRICE", "TOTAL")).append("\n");
        for(var position : positions){
            try{
                receipt.append(FormatUtil.formatFourCol(paddingQty, paddingDesc, paddingPrice, paddingPrice,
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
        receipt.append("-".repeat(length)).append("\n");

        receipt.append(FormatUtil.formatLineTwoCol(paddingFooter, paddingPrice, "TAXABLE TOT.", "$"+FormatUtil.formatNum2(totalPriceWithDiscount))).append("\n");
        receipt.append(FormatUtil.formatLineTwoCol(paddingFooter, paddingPrice, "DISCOUNT", "$"+FormatUtil.formatNum2(FormatUtil.round(totalPrice-totalPriceWithDiscount)))).append("\n");
        receipt.append(FormatUtil.formatLineTwoCol(paddingFooter, paddingPrice, "TOTAL", "$"+ FormatUtil.formatNum2(totalPrice))).append("\n");

        return receipt;
    }

    public void writeReceipt(ProductProvider productProvider){
        String dateS = DateTimeFormatter.ofPattern("dd_MM_uuuu").format(date);
        String timeS = DateTimeFormatter.ofPattern("HH_mm_ss").format(time);
        String path = "src/main/resources/receipts";
        String name = "receipt-" + dateS + "-" + timeS + ".txt";

        File dir = new File(path);
        dir.mkdirs();

        try (
                FileWriter fileWriter = new FileWriter(path + File.separator + name);
                PrintWriter printWriter = new PrintWriter(fileWriter)
        ){
            printWriter.print(createReceipt(productProvider));
        } catch (IOException e) {
            System.out.println("error: " +e.getMessage());
        }
    }

    public void writeToPdf(ProductProvider productProvider) throws IOException, DocumentException {
        String dateT = DateTimeFormatter.ofPattern("dd/MM/uuuu").format(date);
        String timeT = DateTimeFormatter.ofPattern("HH:mm:ss").format(time);
        String path = "src/main/resources/receiptsPdf";
        String pathCLT = "src/main/resources/pdf/Clevertec_Template.pdf";
        String name = "receipt-" + DateTimeFormatter.ofPattern("dd_MM_uuuu").format(date) + "-"
                + DateTimeFormatter.ofPattern("HH_mm_ss").format(time) + ".pdf";
        String title = "CASH RECEIPT";
        String nameStore = "LocalShop";
        String address = "Address";
        String number = "7717";

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path + File.separator + name));
        document.open();

        PdfReader reader = new PdfReader(pathCLT);
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
                "CASHIER: #" + cashier,
                "Date: " + dateT,
                "Time: " + timeT);
        document.add(receiptInfoTable);

        PdfPTable positionTable = new PdfPTable(new float[]{7, 63, 15, 15});
        addPositionRows(positionTable, productProvider);
        document.add(positionTable);

        PdfPTable receiptFooterTable = new PdfPTable(new float[]{70, 30});
        addReceiptFooterRows(receiptFooterTable, totalPrice, totalPriceWithDiscount);
        document.add(receiptFooterTable);

        document.close();
        writer.close();
    }


    private void addPositionRows(PdfPTable table, ProductProvider productProvider) {
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
