package GUI.Dashboard.TeacherStatistics;

import BE.Account;
import BE.Course;
import BE.Lesson;
import BLL.AccountBLL;
import BLL.AttendanceBLL;
import BLL.SchemaBLL;
import GUI.Dashboard.Interfaces.ISubPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Controller implements ISubPage {
    private Course selectedCourse;
    private Account currentAccount;
    private AccountBLL accountBLL;
    private SchemaBLL schemaBLL;
    private AttendanceBLL attendanceBLL;
    private GUI.Dashboard.Controller mainController;
    private ObservableList<Course> observableCourses;
    private ObservableList<Lesson> observableLessons;
    private ObservableList<Account> observableAccount;


    @FXML
    private ComboBox<Course>  courseSelector;

    @Override
    public void setCurrentAccount(Account a) {
        currentAccount = a;
    }

    @Override
    public void setAccountBLL(AccountBLL accountBLL) {
        this.accountBLL = accountBLL;
    }

    @Override
    public void setSchemaBLL(SchemaBLL schemaBLL) {
        this.schemaBLL = schemaBLL;

        try {
            observableCourses.addAll(schemaBLL.getUserCourses(currentAccount));
            courseSelector.setItems(observableCourses);
            courseSelector.getSelectionModel().selectFirst();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void setAttendanceBLL(AttendanceBLL attendanceBLL) { this.attendanceBLL = attendanceBLL; }

    @Override
    public void setMainController(GUI.Dashboard.Controller controller) {
        this.mainController = controller;
    }

    @Override
    public void load() {
    }

    public void initialize() {
        observableAccount = FXCollections.observableArrayList();
        observableCourses = FXCollections.observableArrayList();
        observableLessons = FXCollections.observableArrayList();


        courseSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedCourse = newValue;
            try {
                update();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void update () throws SQLException {
        if (selectedCourse != null) {
            LocalDate firstDate = schemaBLL.getFirstLesson(selectedCourse).getStartTime().toLocalDateTime().toLocalDate();
            LocalDate lastDate = LocalDate.now();
            observableLessons.setAll(schemaBLL.getLessonsInterval(firstDate,lastDate,selectedCourse));
            observableAccount.setAll(attendanceBLL.getAccountsFromCourse(selectedCourse));

        }


    }
}