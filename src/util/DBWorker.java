package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBWorker {

    private StringBuilder url;

//    Инициализируем url
    {
        try {
            url = new StringBuilder();
            String pathToPropertiesFile = "passwordAndUser.pswrd";
            FileInputStream fileInputStream = new FileInputStream(pathToPropertiesFile);
            Properties properties = new Properties();
            properties.load(fileInputStream);
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            url.append("jdbc:mysql://localhost/phonebook?user=").append(user);
            url.append("&password=").append(password);
            url.append("&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8&connectionCollation=utf8_general_ci");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    Количество рядов таблицы, затронутых послеследним запросом
    private Integer affectedRows = 0;

    // Значение автоинкрементируемого первичного ключа,
// полученное после добавления новой записи
    private Integer lastInsertId = 0;

    //    Указатель на экземпляр класса
    private static DBWorker instance = null;

    //    Метод для получения экземпляра класса (реализован Singleton)
    public static DBWorker getInstance() {
        if (instance == null) {
            instance = new DBWorker();
        }
        return instance;
    }

    private DBWorker() {
//        Загушка
    }

    //    Выполнение запросов на выборку данных
    public ResultSet getDBData(String query) {
        Statement statement;
        Connection connect;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connect = DriverManager.getConnection(url.toString());
            statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (InstantiationException e) {
            System.out.println("InstantiationException");
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        } catch (SQLException e) {
            System.out.println("SQLException");
        }
        System.out.println("There is nothing to show in getDBData()!");
        return null;
    }

    //    Выполнение запросов на модификацию данных
    public Integer changeDBData(String query) {
        Statement statement;
        Connection connect;
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connect = DriverManager.getConnection(url.toString());
            statement = connect.createStatement();
            this.affectedRows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

//            Получаем last_insert_id() для операции вставки
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()){
                this.lastInsertId = resultSet.getInt(1);
            }

            return this.affectedRows;
        } catch (InstantiationException e) {
            System.out.println("InstantiationException");
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        } catch (SQLException e) {
            System.out.println("SQLException");
        }
        return null;
    }

//    Геттеры и сеттеры
//    ---------------------------------------------

    public Integer getAffectedRows() {
        return affectedRows;
    }

    public Integer getLastInsertId() {
        return lastInsertId;
    }

//    ---------------------------------------------
}
