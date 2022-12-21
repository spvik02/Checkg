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
![image](https://user-images.githubusercontent.com/111181469/208943385-917a3cbe-5ec2-41c8-88c3-8fc7d7779f28.png)

the receipt is also written to a file in the folder src/main/resources/receipts 
