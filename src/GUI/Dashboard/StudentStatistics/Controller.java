package GUI.Dashboard.StudentStatistics;

import BE.Account;
import BE.Attendance;
import BE.Course;
import BE.Lesson;
import BLL.AccountBLL;
import BLL.AttendanceBLL;
import BLL.SchemaBLL;
import GUI.Dashboard.Interfaces.ISubPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Double.NaN;

public class Controller implements ISubPage {
    private Account currentAccount;
    private AccountBLL accountBLL;
    private SchemaBLL schemaBLL;
    private AttendanceBLL attendanceBLL;
    private GUI.Dashboard.Controller mainController;

    private LocalDate fromDate;
    private LocalDate toDate;
    private Course selectedCourse;

    private ObservableList<Course> observableCourses;
    private ObservableList<Lesson> allPeriodLessons;
    private ObservableList<Lesson> periodLessons;
    private ObservableList<Attendance> periodAttendance;

    @FXML
    private ComboBox<Course> courseSelector;
    @FXML
    private DatePicker dateFromSelector;
    @FXML
    private DatePicker dateToSelector;
    @FXML
    private BarChart<?, ?> attendanceChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private BarChart<?, ?> absenceDaysChart;
    @FXML
    private CategoryAxis xAxisDays;
    @FXML
    private NumberAxis yAxisDays;

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
            observableCourses.add(new Course(-1,"Alle fag"));
            observableCourses.addAll(schemaBLL.getUserCourses(currentAccount));
            courseSelector.setItems(observableCourses);
            courseSelector.getSelectionModel().selectFirst();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        dateFromSelector.setValue(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
        dateToSelector.setValue(LocalDate.now());
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
        periodLessons = FXCollections.observableArrayList();

        allPeriodLessons = FXCollections.observableArrayList();

        periodAttendance = FXCollections.observableArrayList();

        observableCourses = FXCollections.observableArrayList();

        dateFromSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            fromDate = newValue;
            update(true);
        });
        dateToSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            toDate = newValue;
            update(true);
        });
        courseSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedCourse = newValue;
            update(false);
        });

        xAxis.setLabel("Fag");

        yAxis.setLabel("Tilstedeværelse");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0.0);
        yAxis.setUpperBound(100.0);

        xAxisDays.setLabel("Dage");

        yAxisDays.setLabel("Fravær");
        yAxisDays.setAutoRanging(false);
        yAxisDays.setLowerBound(0.0);
        yAxisDays.setUpperBound(100.0);
    }

    private void update(boolean all) {
        if(fromDate != null && toDate != null) {
            periodLessons.setAll(schemaBLL.getLessonsInterval(fromDate, toDate, selectedCourse));
            allPeriodLessons.setAll(schemaBLL.getLessonsInterval(fromDate,toDate,null));
            periodAttendance.setAll(attendanceBLL.getAttendanceInterval(fromDate,toDate,selectedCourse));

            if(all) {
                XYChart.Series ds = new XYChart.Series();
                for (Course c : observableCourses) {
                    double att = attendanceBLL.getCourseAttendance(currentAccount, c, allPeriodLessons, periodAttendance);
                    String name = c.getName();

                    ds.getData().add(new XYChart.Data(name, att));
                }

                attendanceChart.getData().setAll(ds);
            }

            XYChart.Series ds2 = new XYChart.Series();
            int i = 1;
            for(double abs : attendanceBLL.getDaysAbsence(currentAccount,selectedCourse,periodLessons,periodAttendance)) {
                String name;
                switch(i) {
                    case 1 -> name = "Mandag";
                    case 2 -> name = "Tirsdag";
                    case 3 -> name = "Onsdag";
                    case 4 -> name = "Torsdag";
                    case 5 -> name = "Fredag";
                    default -> name = "?";
                }

                ds2.getData().add(new XYChart.Data(name,abs));
                i++;
            }
            absenceDaysChart.getData().setAll(ds2);
        }
    }
    }
