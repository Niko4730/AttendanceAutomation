package GUI.Dashboard.StudentRegistrations;

import BE.Account;
import BE.Attendance;
import BE.Course;
import BE.Lesson;
import BLL.AccountBLL;
import BLL.AttendanceBLL;
import BLL.SchemaBLL;
import GUI.Dashboard.Interfaces.ISubPage;
import UTIL.UserAlert;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

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
    private TableView<Lesson> tblViewLessons;
    @FXML
    private TableColumn<Lesson,String> clmnDateTime;
    @FXML
    private TableColumn<Lesson,String> clmnStartTime;
    @FXML
    private TableColumn<Lesson,String> clmnStopTime;
    @FXML
    private TableColumn<Lesson,String> clmnCourse;
    @FXML
    private TableColumn<Lesson,String> clmnAttendance;

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
            UserAlert.showDatabaseError();
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
            update();
        });
        dateToSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            toDate = newValue;
            update();
        });
        courseSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedCourse = newValue;
            update();
        });

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M-y");

        clmnDateTime.setCellValueFactory(c -> {
            return new SimpleStringProperty(dateFormat.format(c.getValue().getStartTime()));
        });

        clmnStartTime.setCellValueFactory(c -> {
            return new SimpleStringProperty(timeFormat.format(c.getValue().getStartTime()));
        });

        clmnStopTime.setCellValueFactory(c -> {
            return new SimpleStringProperty(timeFormat.format(c.getValue().getStopTime()));
        });

        clmnCourse.setCellValueFactory(c -> {
            return new SimpleStringProperty(c.getValue().getCourseName());
        });

        clmnAttendance.setCellValueFactory(c -> {
            return new SimpleStringProperty( (attendanceBLL.hasAttended(currentAccount,c.getValue())) ? "Ja" : "Nej" );
        });

        tblViewLessons.getSelectionModel().setCellSelectionEnabled(true);
        tblViewLessons.setEditable(true);
        clmnAttendance.setEditable(true);

        ObservableList<String> comboBoxList = FXCollections.observableArrayList("Ja","Nej");
        Callback<TableColumn<Lesson, String>, TableCell<Lesson, String>> defaultDropDownCellFactory = ComboBoxTableCell.forTableColumn(comboBoxList);
        clmnAttendance.setCellFactory(col -> {
            TableCell<Lesson, String> cell = defaultDropDownCellFactory.call(col);
            cell.itemProperty().addListener((observable, oldValue, newValue) -> {
                TableRow<Lesson> row = cell.getTableRow();
                if(row == null)
                    cell.setEditable(false);
                else {
                    Lesson l = row.getItem();
                    if(l != null) {
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime startTime = l.getStartTime().toLocalDateTime();
                        LocalDateTime stopTime = l.getStopTime().toLocalDateTime();

                        if (startTime.isAfter(now)) {
                            cell.setEditable(false);
                        }
                        else {
                            cell.setEditable(true);
                        }
                    } else
                        cell.setEditable(false);
                }
            });
            return cell;
        });

        clmnAttendance.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Lesson, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Lesson, String> lessonStringCellEditEvent) {
                        String oldVal = lessonStringCellEditEvent.getOldValue();
                        String newVal = lessonStringCellEditEvent.getNewValue();
                        if(newVal.equals(oldVal))
                            return;

                        if(newVal.equals("Ja")) {
                            try {
                                attendanceBLL.setAttended(currentAccount,lessonStringCellEditEvent.getRowValue());
                            } catch (SQLException throwables) {
                                UserAlert.showDatabaseError();
                            }
                        } else {
                            try {
                                attendanceBLL.removeAttendance(currentAccount, lessonStringCellEditEvent.getRowValue());
                            } catch(SQLException throwables) {
                                UserAlert.showDatabaseError();
                            }
                        }
                    }
                }
        );
    }

    private void update() {
        if(fromDate != null && toDate != null) {
            periodLessons.setAll(schemaBLL.getLessonsInterval(fromDate, toDate, selectedCourse));
            allPeriodLessons.setAll(schemaBLL.getLessonsInterval(fromDate, toDate, null));
            periodAttendance.setAll(attendanceBLL.getAttendanceInterval(fromDate, toDate, selectedCourse));

            tblViewLessons.setItems(periodLessons);
        }
    }
}