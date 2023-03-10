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

# Cache

На ветке feature/cache.
- Создана еализация кэша, используя алгоритмы LRU и LFU: [src/main/java/cache/](https://github.com/spvik02/Checkg/tree/feature/cache/src/main/java/cache/)
- Алгоритм и максимальный размер коллекции считываются из файла resources/application.yml. Если значения не заданы, то ставятся по умолчанию: [createCache()](https://github.com/spvik02/Checkg/blob/feature/cache/src/main/java/cache/CacheFactory.java)
- Коллекция инициализируется через фабрику: [src/main/java/cache/CacheFactory.java](https://github.com/spvik02/Checkg/blob/feature/cache/src/main/java/cache/CacheFactory.java)
- Код содержbn javadoc и описанный README.md.
- Кеши покрыты тестами: [src/test/java/cache/](https://github.com/spvik02/Checkg/tree/feature/cache/src/test/java/cache/)
- Создана entity Cashier, в ней  поле id и еще 3 поля: [ src/main/java/model/Cashier.java](https://github.com/spvik02/Checkg/blob/feature/cache/src/main/java/model/Cashier.java) 
- Добавлено поле, проверяемое regex.
- В приложении созданы слои service ([src/main/java/service/](https://github.com/spvik02/Checkg/tree/feature/cache/src/main/java/service/)) и dao ([src/main/java/dao/](https://github.com/spvik02/Checkg/tree/feature/cache/src/main/java/dao/)) для Cashier (service вызывает слой dao, слой dao - временная замена database). В этих сервисах реализованы CRUD операции для работы с entity
- Результат работы dao синхронизируется с кешем через proxy ([src/main/java/service](https://github.com/spvik02/Checkg/tree/feature/cache/src/main/java/service/)) (кастомная аннотация: ([src/main/java/annotation/](https://github.com/spvik02/Checkg/tree/feature/cache/src/main/java/annotation))). При работе с entity оперируем id. Алгоритм работы с кешем: 
  - GET - ищем в кеше и если там данных нет, то достаем объект из dao, сохраняем в кеш и возвращаем
  - POST - сохраняем в dao и потом сохраняем в кеше
  - DELETE - удаляем из dao и потом удаляем в кеша
  - PUT - обновление/вставка в dao и потом обновление/вставка в кеше.
