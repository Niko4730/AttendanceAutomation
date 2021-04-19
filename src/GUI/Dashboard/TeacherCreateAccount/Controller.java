package GUI.Dashboard.TeacherCreateAccount;

import BE.Account;
import BE.Course;
import BE.UserType;
import BLL.AccountBLL;
import BLL.AttendanceBLL;
import BLL.SchemaBLL;
import GUI.Dashboard.Interfaces.ISubPage;
import UTIL.UserAlert;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Controller implements ISubPage {
    private Account currentAccount;
    private AccountBLL accountBLL;
    private SchemaBLL schemaBLL;
    private AttendanceBLL attendanceBLL;
    private GUI.Dashboard.Controller mainController;

    private ObservableList<Course> observableCoursesToAdd;
    private ObservableList<Course> observableCoursesAdded;

    private Course selectedCourseToAdd;
    private Course selectedCourseToRemove;

    @FXML
    private ListView<Course> coursesToAdd;

    @FXML
    private ListView<Course> addedCourses;

    @FXML
    private JFXTextField firstNameField;

    @FXML
    private JFXTextField lastNameField;

    @FXML
    private JFXTextField emailField;

    @FXML
    private JFXTextField phoneField;

    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXPasswordField passwordAgainField;

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
            observableCoursesToAdd.setAll(schemaBLL.getAllCourses());
            coursesToAdd.setItems(observableCoursesToAdd);
            addedCourses.setItems(observableCoursesAdded);
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
        observableCoursesToAdd = FXCollections.observableArrayList();
        observableCoursesAdded = FXCollections.observableArrayList();

        coursesToAdd.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedCourseToAdd = newValue;
        });

        addedCourses.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedCourseToRemove = newValue;
        });
    }

    @FXML
    private void addCourse() {
        if(selectedCourseToAdd != null) {
            if(!observableCoursesAdded.contains(selectedCourseToAdd)) {
                observableCoursesAdded.add(selectedCourseToAdd);
                observableCoursesToAdd.remove(selectedCourseToAdd);
            }
        }
    }

    @FXML
    private void removeCourse() {
        if(selectedCourseToRemove != null) {
            if(!observableCoursesToAdd.contains(selectedCourseToRemove)) {
                observableCoursesToAdd.add(selectedCourseToRemove);
                observableCoursesAdded.remove(selectedCourseToRemove);
            }
        }
    }

    @FXML
    private void reset() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        passwordAgainField.setText("");
        observableCoursesAdded.clear();
        try {
            observableCoursesToAdd.setAll(schemaBLL.getAllCourses());
        } catch(SQLException e) {
            UserAlert.showDatabaseError();
        }
    }

    @FXML
    private void save() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        int phone = Integer.parseInt(phoneField.getText());
        String username = usernameField.getText();
        String password = passwordField.getText();
        String passwordAgain = passwordAgainField.getText();
        List<Course> userCourses = new ArrayList<>(observableCoursesAdded);

        if(!password.equals(passwordAgain)) {
            UserAlert.showAlert("Ups!","De indtastede koder er ikke ens!", Alert.AlertType.ERROR);
            return;
        }

        try {
            if (accountBLL.usernameExists(username)) {
                UserAlert.showAlert("Ups!", "Brugernavnet er allerede optaget!", Alert.AlertType.ERROR);
                return;
            }

            Account toSave = new Account(username,password, UserType.STUDENT,firstName,lastName,email,phone);
            if(!accountBLL.newAccount(toSave)) {
                UserAlert.showAlert("Åh nej!", "Der opstod en fejl under oprettelsen. Prøv igen!", Alert.AlertType.ERROR);
                return;
            }

            accountBLL.addUserCourses(toSave,userCourses);

            UserAlert.showAlert("Sådan!","Brugeren blev oprettet!", Alert.AlertType.INFORMATION);
            reset();
        } catch(SQLException e) {
            UserAlert.showDatabaseError();
        }
    }
}