package GUI.Dashboard.UserSettings;

import BE.Account;
import BLL.AccountBLL;
import BLL.AttendanceBLL;
import BLL.SchemaBLL;
import GUI.Dashboard.Interfaces.ISubPage;
import UTIL.UserAlert;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class Controller implements ISubPage {
    private Account currentAccount;
    private AccountBLL accountBLL;
    private SchemaBLL schemaBLL;
    private AttendanceBLL attendanceBLL;
    private GUI.Dashboard.Controller mainController;

    @FXML
    private JFXTextField firstNameField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXTextField phoneField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXPasswordField passwordAgainField;
    @FXML
    private Label passwordAgainLabel;

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
        firstNameField.setText(currentAccount.getFirstName());
        lastNameField.setText(currentAccount.getLastName());
        emailField.setText(currentAccount.getEmail());
        phoneField.setText(String.valueOf(currentAccount.getPhone()));
        passwordField.clear();
    }

    public void initialize() {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.length() > 0) {
                passwordAgainLabel.setVisible(true);
                passwordAgainField.setVisible(true);
            } else {
                passwordAgainField.setVisible(!passwordAgainField.isVisible());
                passwordAgainLabel.setVisible(!passwordAgainLabel.isVisible());
            }
        });
    }

    @FXML
    private void saveSettings() {
        currentAccount.setFirstName(firstNameField.getText());
        currentAccount.setLastName(lastNameField.getText());
        currentAccount.setEmail(emailField.getText());
        currentAccount.setPhone(Integer.parseInt(phoneField.getText()));
        if(passwordField.getText().length() > 0) {
            if(passwordField.getText().equals(passwordAgainField.getText()))
                currentAccount.setPassword(passwordField.getText());
            else
                UserAlert.showAlert("Ups!","De to koder er ikke ens... Prøv igen!", Alert.AlertType.WARNING);
        }

        try {
            accountBLL.updateAccount(currentAccount);
            UserAlert.showAlert("Sådan!","Dine indstillinger blev gemt!", Alert.AlertType.CONFIRMATION);
            load();
        } catch(SQLException e) {
            UserAlert.showDatabaseError();
        }
    }

    @FXML
    private void resetSettings() {
        load();
    }
}