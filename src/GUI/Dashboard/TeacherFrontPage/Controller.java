package GUI.Dashboard.TeacherFrontPage;

import BE.Account;
import BE.Course;
import BE.Lesson;
import BLL.AccountBLL;
import BLL.AttendanceBLL;
import BLL.SchemaBLL;
import GUI.Dashboard.Interfaces.ISubPage;
import UTIL.UserAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import jdk.jfr.Category;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class Controller implements ISubPage {
    private Account currentAccount;
    private AccountBLL accountBLL;
    private SchemaBLL schemaBLL;
    private AttendanceBLL attendanceBLL;
    private GUI.Dashboard.Controller mainController;

    private ObservableList<Lesson> periodLessons;

    private LocalDate fromDate;
    private LocalDate toDate;

    private List<Course> teacherCourses;

    @FXML
    private DatePicker dateFromSelector;
    @FXML
    private DatePicker dateToSelector;
    @FXML
    private BarChart<?,?> attendanceChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

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
    }

    @Override
    public void setAttendanceBLL(AttendanceBLL attendanceBLL) { this.attendanceBLL = attendanceBLL; }

    @Override
    public void setMainController(GUI.Dashboard.Controller controller) {
        this.mainController = controller;
    }

    @Override
    public void load() {
        try {
            teacherCourses = schemaBLL.getUserCourses(currentAccount);
        } catch(SQLException e) {
            UserAlert.showDatabaseError();
        }

        dateFromSelector.setValue(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()));
        dateToSelector.setValue(LocalDate.now());
    }

    public void initialize() {
        periodLessons = FXCollections.observableArrayList();
        dateFromSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromDate = newValue;
            update();
        });
        dateToSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            toDate = newValue;
            update();
        });

        xAxis.setLabel("Fag");
        yAxis.setLabel("Tilstedeværelse");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0.0);
        yAxis.setUpperBound(100.0);
    }

    private void update() {
        if(fromDate != null && toDate != null) {
            periodLessons.setAll(schemaBLL.getLessonsInterval(fromDate, toDate, null));

            XYChart.Series ds = new XYChart.Series();
            for(Course c : teacherCourses) {
                try {
                    double att = attendanceBLL.getTotalAttendance(c,fromDate,toDate);
                    ds.getData().add(new XYChart.Data<>(c.getName(),att));
                } catch(SQLException e) {

                }

            }
            attendanceChart.getData().setAll(ds);
        }
    }
}
