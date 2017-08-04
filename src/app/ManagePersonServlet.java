package app;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class ManagePersonServlet extends HttpServlet {

    //    Идентификатор для сериализации / десериализации
    private static final long serialVersionUID = 1L;

    //    Основной объект, хранящий данные телефонной книги
    private PhoneBook phoneBook;

    public ManagePersonServlet() {
//        Вызов родительского конструктора
        super();

//        Создание экземпляра телефонной книги
        try {
            this.phoneBook = PhoneBook.getInstance();
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        } catch (SQLException e) {
            System.out.println("SQLException");
        }
    }

    //    Валидация ФИО и генерация сообщения об ошибке в случае невалидных данных
    private String validatePersonFMLName(Person person) {
        String errorMessage = "";

        if (!person.validateFMLNamePart(person.getName(), false)) {
            errorMessage += "Имя должно быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
        }

        if (!person.validateFMLNamePart(person.getSurname(), false)) {
            errorMessage += "Фамилия должна быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
        }
        if (!person.validateFMLNamePart(person.getMiddleName(), true)) {
            errorMessage += "Отчество должно быть строкой от 0 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
        }

        return errorMessage;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Обязательно ДО обращения к любому параметру нужно переключиться в UTF-8,
        // иначе русский язык при передаче GET/POST-параметрами превращается в "кракозябры".
        request.setCharacterEncoding("UTF-8");

        // В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
        // но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
        request.setAttribute("phonebook", this.phoneBook);

        // Хранилище параметров для передачи в JSP.
        HashMap<String, String> jspParameters = new HashMap<>();

//        Диспетчеры для передачи управления на разные JSP (разные представления(view))
        RequestDispatcher dispatcherForManager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcherForList = request.getRequestDispatcher("/List.jsp");
    }
}
