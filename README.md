# Checkg

The program caculate and print a shop receipt with the specified data in the parameters

Java 17, gradle 7.5

to compile: javac -cp ./src/main/java -d ./build/classes/java/main ./src/main/java/CheckRunner.java

or using gradle: gradle build

to run: java -cp ./build/classes/java/main CheckRunner <set_of_parameters>

Where the set of parameters in the format itemId-quantity (ItemId is the identifier of the product, quantity is its quantity). 
Add parameter Card-cardId if discount card was provided.

For example: java -cp ./build/classes/java/main CheckRunner 1-2 2-5 3-5 4-2 5-5 6-2 Card-2

Use this line if you want source data (products and cards) and parameters were readed from a file with fileName (filepath is src/main/resources/dataFiles/):

java -cp ./build/classes/java/main CheckRunner File-fileName

For example: java -cp ./build/classes/java/main CheckRunner File-parameters1.txt

(the parameters in the file are written in the same format as described above)

output:
                  CASH RECEIPT                     
                    LocalShop                      
                     Address                       
                    Tel: 7717                      
CASHIER: #717                      Date: 21/12/2022
                                   Time: 18:24:15  
QTY DESCRIPTION                      PRICE    TOTAL
2   milk                             $5,60    $4,76
5   bread                           $11,00    $9,35
5   tea                             $21,50   $18,28
2   coffee                          $24,00   $20,40
5   water                           $11,00    $9,90
2   alcohol                         $14,40   $14,40
--------------------------------------------------
TAXABLE TOT.                                 $77,09
DISCOUNT                                     $10,41
TOTAL                                        $87,50

the receipt is also written to a file in the folder src/main/resources/receipts 
