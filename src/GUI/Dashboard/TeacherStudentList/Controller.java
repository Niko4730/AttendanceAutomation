package GUI.Dashboard.TeacherStudentList;

import BE.Account;
import BE.Course;
import BE.Lesson;
import BLL.AccountBLL;
import BLL.AttendanceBLL;
import BLL.SchemaBLL;
import GUI.Dashboard.Interfaces.ISubPage;
import UTIL.UserAlert;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Formatter;

public class Controller implements ISubPage {
    private Account currentAccount;
    private AccountBLL accountBLL;
    private SchemaBLL schemaBLL;
    private AttendanceBLL attendanceBLL;
    private GUI.Dashboard.Controller mainController;

    private Course selectedCourse;

    private ObservableList<Course> observableCourses;
    private ObservableList<Account> observableAccounts;
    private ObservableList<Lesson> observableLessons;

    @FXML
    private TableView<Account> tblViewStudents;
    @FXML
    private ComboBox<Course> courseSelector;
    @FXML
    private TableColumn<Account,String> clmnStudentFirstName;
    @FXML
    private TableColumn<Account,String> clmnStudentLastName;
    @FXML
    private TableColumn<Account,Number> clmnStudentPhone;
    @FXML
    private TableColumn<Account,String> clmnStudentEmail;
    @FXML
    private TableColumn<Account,Number> clmnStudentAbsence;

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
        } catch(SQLException e) {
            UserAlert.showDatabaseError();
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
        observableCourses = FXCollections.observableArrayList();
        observableAccounts = FXCollections.observableArrayList();
        observableLessons = FXCollections.observableArrayList();

        tblViewStudents.setItems(observableAccounts);

        clmnStudentFirstName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFirstName()));
        clmnStudentLastName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastName()));
        clmnStudentPhone.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPhone()));
        clmnStudentEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        clmnStudentAbsence.setCellValueFactory(c -> {
            double attendance = attendanceBLL.getCourseAttendance(c.getValue(),selectedCourse,observableLessons,null);
            double absence = 100 - attendance;
            BigDecimal bd = new BigDecimal(absence).setScale(2, RoundingMode.HALF_DOWN);

            return new SimpleDoubleProperty(bd.doubleValue());
        });

        courseSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedCourse = newValue;
            update();
        });

        tblViewStudents.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    Account selectedAccount = tblViewStudents.getSelectionModel().getSelectedItem();
                    if(selectedAccount != null)
                        mainController.setPage("TeacherStudentStatistics",selectedAccount);
                }
            }
        });
    }

    private void update() {
        if(selectedCourse != null) {
            try {
                LocalDate firstDate = schemaBLL.getFirstLesson(selectedCourse).getStartTime().toLocalDateTime().toLocalDate();
                LocalDate lastDate = LocalDate.now();

                observableLessons.setAll(schemaBLL.getLessonsInterval(firstDate,lastDate,selectedCourse));

                observableAccounts.setAll(attendanceBLL.getAccountsFromCourse(selectedCourse));
            } catch(SQLException e) {
                UserAlert.showDatabaseError();
            }
        }
    }
}