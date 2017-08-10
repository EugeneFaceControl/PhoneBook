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

//        Действие (action) и идентификатор записи (id) над которой выполняется это действие
        String action = request.getParameter("action");
        String id = request.getParameter("id");

//        Если идентификатор и действие не указаны, мы находимся в состоянии
//        "просто показать список и ничего не делать"
        if (action == null && id == null) {
            request.setAttribute("jsp_parameters", jspParameters);
            dispatcherForList.forward(request, response);
        }
//        Если же действие указано, то
        else {
            switch (action) {
                case "add":
                    Person emptyPerson = new Person();

//                    Подготовка параметров для JSP
                    jspParameters.put("current_action", "add");
                    jspParameters.put("next_action", "add_go");
                    jspParameters.put("next_action_label", "Добавить");

//                    Установка параметров JSP
                    request.setAttribute("person", emptyPerson);
                    request.setAttribute("jsp_parameters", jspParameters);

//                    Передача запроса в JSP
                    dispatcherForManager.forward(request, response);
                    break;

                case "edit":
                    Person editablePerson = this.phoneBook.getPerson(id);

//                    Подготовка параметров для JSP
                    jspParameters.put("current_action", "edit");
                    jspParameters.put("next_action", "edit_go");
                    jspParameters.put("next_action_label", "Сохранить");
                    //                    Установка параметров JSP
                    request.setAttribute("person", editablePerson);
                    request.setAttribute("jsp_parameters", jspParameters);

//                    Передача запроса в JSP
                    dispatcherForManager.forward(request, response);
                    break;
//                    Удаление записи
                case "delete":
                    if (phoneBook.deletePerson(id)) {
                        jspParameters.put("current_action_result", "DELETION_SUCCESS");
                        jspParameters.put("current_action_result_label", "Удаление выполнено успешно");
                    } else {
                        jspParameters.put("current_action_result", "DELETE_FAILURE");
                        jspParameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена");
                    }
//                    Установка параметров JSP
                    request.setAttribute("jsp_parameters", jspParameters);

//                    Передача запроса в JSP
                    dispatcherForList.forward(request, response);
                    break;
            }
        }
    }

    //    Реакция на POST-запросы
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Обязательно ДО обращения к любому параметру нужно переключиться в UTF-8,
        // иначе русский язык при передаче GET/POST-параметрами превращается в "кракозябры".
        request.setCharacterEncoding("UTF-8");

        // В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
        // но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
        request.setAttribute("phonebook", this.phoneBook);

//        Хранилище параметров для передачи в JSP
        HashMap<String, String> jspParameters = new HashMap<>();
//        Диспетчеры для передачи управления на разые JSP (разные представления (view))
        RequestDispatcher dispatcherForManager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcherForList = request.getRequestDispatcher("/List.jsp");

//        Действие (addGo, editGo) и идентификатор записи (id), над которой выполнятемя это действие
        String addGo = request.getParameter("add_go");
        String editGo = request.getParameter("edit_go");
        String id = request.getParameter("id");


        if (addGo != null) {
//            Создание записи на основе данных из формы
            Person newPerson = new Person(request.getParameter("name"), request.getParameter("surname"), request.getParameter("middlename"));

//            Валидация ФИО
            String errorMessage = this.validatePersonFMLName(newPerson);

//          Если данные верные, можно производить добавление
            if (errorMessage.equals("")) {
//                Если запись удалось добавить
                if (this.phoneBook.addPerson(newPerson)) {
                    jspParameters.put("current_action_result", "ADDICTION_SUCCESS");
                    jspParameters.put("current_action_result_label", "Добавление выполнено успешно");
                } else {
                    jspParameters.put("current_action_result", "ADDICTION_FAILURE");
                    jspParameters.put("current_action_result_label", "Ошибка добавления");
                }

                request.setAttribute("jsp_parameters", jspParameters);

                dispatcherForList.forward(request, response);
            }
//            Усли в данных были ошибки, надо заново показать форму и сообщить об ошибках
            else {
//                Подготовка параметров для JSP
                jspParameters.put("current_action", "add");
                jspParameters.put("next_action", "add_go");
                jspParameters.put("next_action_label", "Добавить");
                jspParameters.put("error_message", errorMessage);

//                Установка параметров JSP
                request.setAttribute("person", newPerson);
                request.setAttribute("jsp_Parameters", jspParameters);

//                Передача запроса в JSP
                dispatcherForManager.forward(request, response);
            }
        }

//        Редактирование записи
        if (editGo != null) {
//            Получение записи и ее обновление на основе данных из формы
            Person updatePerson = this.phoneBook.getPerson(request.getParameter("id"));
            updatePerson.setName(request.getParameter("name"));
            updatePerson.setName(request.getParameter("surname"));
            updatePerson.setName(request.getParameter("middlename"));

//            Валидация ФИО
        }
    }
}


