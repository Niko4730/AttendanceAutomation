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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Controller implements ISubPage {

    @FXML
    private CategoryAxis xAxisDays;
    @FXML
    private NumberAxis yAxisDays;
    @FXML
    private BarChart<?, ?> barChartDays;
    @FXML
    private ComboBox<Course> courseSelector;
    private Course selectedCourse;
    private Account currentAccount;
    private AccountBLL accountBLL;
    private SchemaBLL schemaBLL;
    private AttendanceBLL attendanceBLL;
    private GUI.Dashboard.Controller mainController;

    private ObservableList<Course> observableCourses;
    private ObservableList<Account> observableAccounts;
    private ObservableList<Lesson> observableLessons;

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
        } catch (SQLException e) {
            e.printStackTrace();
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

        courseSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedCourse = newValue;
            update();
        });

        xAxisDays.setLabel("Dage");

        yAxisDays.setLabel("Fravær");
        yAxisDays.setAutoRanging(false);
        yAxisDays.setLowerBound(0.0);
        yAxisDays.setUpperBound(100.0);
    }

    public void update() {
        if (selectedCourse != null) {
            try {
                LocalDate firstDate = schemaBLL.getFirstLesson(selectedCourse).getStartTime().toLocalDateTime().toLocalDate();
                LocalDate lastDate = LocalDate.now();

                observableLessons.setAll(schemaBLL.getLessonsInterval(firstDate, lastDate, selectedCourse));
                observableAccounts.setAll(attendanceBLL.getAccountsFromCourse(selectedCourse));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        XYChart.Series daysBarChart = new XYChart.Series();
        int i = 1;
        for (double absence : attendanceBLL.getAllDaysAbsence(observableAccounts, selectedCourse, observableLessons)){
            String name;
            switch(i) {
                case 1 -> name = "Mandag";
                case 2 -> name = "Tirsdag";
                case 3 -> name = "Onsdag";
                case 4 -> name = "Torsdag";
                case 5 -> name = "Fredag";
                default -> name = "?";
            }
            daysBarChart.getData().add(new XYChart.Data(name, absence));
            i++;
        } barChartDays.getData().setAll(daysBarChart);
    }

}