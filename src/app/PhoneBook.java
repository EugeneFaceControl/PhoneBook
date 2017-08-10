package app;

import util.DBWorker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class PhoneBook {

    //    Хранилище записей о людях
    private HashMap<String, Person> persons = new HashMap<>();

    //    Объект для работы с БД
    private DBWorker db = DBWorker.getInstance();

    //    Указатель на экземпляр класса
    private static PhoneBook instance = null;

    //    Метод получения экземпляра класса (реализован Singleton - шаблон-одиночка)
    public static PhoneBook getInstance() throws ClassNotFoundException, SQLException {
        if (instance == null) {
            instance = new PhoneBook();
        }
        return instance;
    }

    protected PhoneBook() throws ClassNotFoundException, SQLException {
        ResultSet dbData = this.db.getDBData("SELECT * FROM 'person' ORDER BY 'surname' ASC");
        while (dbData.next()) {
            this.persons.put(dbData.getString("id"), new Person(dbData.getString("id"), dbData.getString("name"), dbData.getString("surname"), dbData.getString("middlename")));
        }
    }

    public boolean addPerson(Person person) {
        ResultSet dbResult;
        String query;
        StringBuilder queryBuilder = new StringBuilder();

//        У человека может не быть отчества
        if (!person.getSurname().equals("")) {
            queryBuilder.append("INSERT INTO 'person' ('name', 'surname', 'middlename') VALUES ('").append(person.getName());
            queryBuilder.append("', '").append(person.getSurname());
            queryBuilder.append("', '").append(person.getMiddleName()).append("')");
        } else {
            queryBuilder.append("INSERT INTO 'person' ('name', 'surname') VALUES ('").append(person.getName());
            queryBuilder.append("', '").append(person.getSurname()).append("')");
        }
        query = queryBuilder.toString();

        Integer affectedRows = this.db.changeDBData(query);

//        Если добавление прошло успешно...
        if (affectedRows > 0) {
            person.setId(this.db.getLastInsertId().toString());

//            Добавляем запись о человека в общий список
            this.persons.put(person.getId(), person);

            return true;
        } else {
            return false;
        }
    }

    public boolean updatePerson(String id, Person person) {
        Integer idFiltered = Integer.parseInt(person.getId());
        String query = "";
        StringBuilder queryBuilder = new StringBuilder();

        if (!person.getSurname().equals("")) {
            queryBuilder.append("UPDATE 'person' SET 'name' = '").append(person.getName());
            queryBuilder.append("', 'surname' = '").append(person.getSurname());
            queryBuilder.append("', 'middlename' = '").append(person.getMiddleName());
            queryBuilder.append("' WHERE 'id' = ").append(idFiltered);
        } else {
            queryBuilder.append("UPDATE 'person' SET 'name' = '").append(person.getName());
            queryBuilder.append("', 'surname' = '").append(person.getSurname());
            queryBuilder.append("' WHERE 'id' = ").append(idFiltered);
        }
        query = queryBuilder.toString();
        Integer affectedRows = this.db.changeDBData(query);

        if (affectedRows > 0) {
//            Обновляется запись о человеке в общем списке
            this.persons.put(person.getId(), person);
            return true;
        } else {
            return false;
        }
    }

    public boolean deletePerson(String id) {
        if ((id != null) && (!id.equals("null"))) {
            int filteredId = Integer.parseInt(id);
            Integer affectedRows = this.db.changeDBData("DELETE FROM 'person' WHERE 'id'=" + filteredId);

            if (affectedRows > 0) {
                this.persons.remove(id);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

//    Геттеры и сеттеры
//    ---------------------------------------------

    public HashMap<String, Person> getPersons() {
        return persons;
    }

    public Person getPerson(String id) {
        return this.persons.get(id);
    }

//    ---------------------------------------------

}
