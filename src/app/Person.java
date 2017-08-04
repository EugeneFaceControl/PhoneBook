package app;

import util.DBWorker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Person {
    //    Данные о человеке
    private String id = "";
    private String name = "";
    private String surname = "";
    private String middleName = "";
    private HashMap<String, String> phones = new HashMap<>();

    //    Конструктор для создания записи о человеке на основе данных из БД
    public Person(String id, String name, String surname, String middleName) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;

//        Извлечение телефонов человека из БД
        ResultSet dbData = DBWorker.getInstance().getDBData("SELECT * FROM 'phone' WHERE 'owner'=" + id);

        try {
            if (dbData != null) {
                while (dbData.next()) {
                    this.phones.put(dbData.getString("id"), dbData.getString("number"));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException");
        }
    }

    //    Конструкто для создания пустой записи человеке
    public Person() {
        this.id = "0";
        this.name = "";
        this.surname = "";
        this.middleName = "";
    }

    //    Конструктор для создания записи, предназначенной для добавления в БД
    public Person(String name, String surname, String middleName) {
        this.id = "0";
        this.name = "";
        this.surname = "";
        this.middleName = "";
    }

    //    Валидация частей ФИО. Для отчества можно передавать второй параметр == true,
//    тогда допускается второе значение.
    public boolean validateFMLNamePart(String fmlNamePart, boolean emptyAllowed) {
        if (emptyAllowed) {
            Matcher matcher = Pattern.compile("[\\w-]{0,150}").matcher(fmlNamePart);
            return matcher.matches();
        } else {
            Matcher matcher = Pattern.compile("[\\w-]{1,150}").matcher(fmlNamePart);
            return matcher.matches();
        }
    }

//    Геттеры и сеттеры
//    ---------------------------------------------

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getMiddleName() {
        if ((this.middleName != null) && (!this.middleName.equals("null"))){
            return this.middleName;
        } else {
            return "";
        }
    }

    public HashMap<String, String> getPhones() {
        return phones;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setPhones(HashMap<String, String> phones) {
        this.phones = phones;
    }

    //   ---------------------------------------------

}
