# Лабораторная работа №7
**вариант:** 12333

**Сборка:**
```
gradle :client:shadowJar #компиляция клиента
gradle :server:shadowJar #компиляция сервера
```
Получившиеся артефакты будут лежать в `./client/build/libs` и `./server/build/libs` соответственно.

**Запуск:**
```
java -jar ./server-all.jar <имя_csv_файла> #сервер
java -jar ./client-all.jar <адрес_сервера> <порт> #клиент
```
**Текст задания:**

Доработать программу из лабораторной работы №6 следующим образом:
1. Организовать хранение коллекции в реляционной СУБД (PostgresQL). Убрать хранение коллекции в файле.
1. Для генерации поля id использовать средства базы данных (sequence).
1. Обновлять состояние коллекции в памяти только при успешном добавлении объекта в БД
1. Все команды получения данных должны работать с коллекцией в памяти, а не в БД
1. Организовать возможность регистрации и авторизации пользователей. У пользователя есть возможность указать пароль.
1. Пароли при хранении хэшировать алгоритмом SHA-512
1. Запретить выполнение команд не авторизованным пользователям.
1. При хранении объектов сохранять информацию о пользователе, который создал этот объект.
1. Пользователи должны иметь возможность просмотра всех объектов коллекции, но модифицировать могут только принадлежащие им.
1. Для идентификации пользователя отправлять логин и пароль с каждым запросом.

Необходимо реализовать многопоточную обработку запросов.
1. Для многопоточного чтения запросов использовать создание нового потока _(java.lang.Thread)_
1. Для многопотчной обработки полученного запроса использовать _Cached thread pool_
1. Для многопоточной отправки ответа использовать _ForkJoinPool_
1. Для синхронизации доступа к коллекции использовать _java.util.Collections.synchronizedXXX_

## Порядок выполнения работы:
1. В качестве базы данных использовать PostgreSQL.
1. Для подключения к БД на кафедральном сервере использовать хост pg, имя базы данных - studs, имя пользователя/пароль совпадают с таковыми для подключения к серверу.

## Отчёт по работе должен содержать:

1. Текст задания.
1. Диаграмма классов разработанной программы.
1. Исходный код программы.
1. Выводы по работе.

## Вопросы к защите лабораторной работы:

1. Многопоточность. Класс Thread, интерфейс Runnable. Модификатор synchronized.
1. Методы wait(), notify() класса Object, интерфейсы Lock и Condition.
1. Классы-сихронизаторы из пакета java.util.concurrent.
1. Модификатор volatile. Атомарные типы данных и операции.
1. Коллекции из пакета java.util.concurrent.
1. Интерфейсы Executor, ExecutorService, Callable, Future
1. Пулы потоков
1. JDBC. Порядок взаимодействия с базой данных. Класс DriverManager. Интерфейс Connection
1. Интерфейсы Statement, PreparedStatement, ResultSet, RowSet
1. Шаблоны проектирования.
