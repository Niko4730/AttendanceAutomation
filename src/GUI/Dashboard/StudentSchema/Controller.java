package GUI.Dashboard.StudentSchema;

import BE.Account;
import BE.Lesson;
import BE.Schema;
import BLL.AccountBLL;
import BLL.AttendanceBLL;
import BLL.SchemaBLL;
import GUI.Dashboard.Interfaces.ISubPage;
import UTIL.UserAlert;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class Controller implements ISubPage {
    private Account currentAccount;
    private AccountBLL accountBLL;
    private SchemaBLL schemaBLL;
    private AttendanceBLL attendanceBLL;
    private GUI.Dashboard.Controller mainController;

    private Lesson currentLesson;

    @FXML
    private TableView<Schema> studentSchema;
    @FXML
    private TableColumn<Schema,String> schemaMonday;
    @FXML
    private TableColumn<Schema,String> schemaTuesday;
    @FXML
    private TableColumn<Schema,String> schemaWednesday;
    @FXML
    private TableColumn<Schema,String> schemaThursday;
    @FXML
    private TableColumn<Schema,String> schemaFriday;

    @Override
    public void setCurrentAccount(Account a) {
        currentAccount = a;
    }

    @Override
    public void setAccountBLL(AccountBLL accountBLL) {
        this.accountBLL = accountBLL;
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

    @Override
    public void setSchemaBLL(SchemaBLL schemaBLL) {
        this.schemaBLL = schemaBLL;

        setItemsInSchema();
    }

    private void setItemsInSchema() {
        currentLesson = schemaBLL.getCurrentLesson();

        List<Schema> weekSchema = schemaBLL.getWeekSchemaFormatted();

        ObservableList<Schema> items = FXCollections.observableArrayList();
        items.addAll(weekSchema);

        studentSchema.setItems(items);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        schemaMonday.setCellValueFactory(c -> {
            Lesson l = c.getValue().getMonday();
            if(l == null || l.getCourseName() == "EMPTY") return new SimpleStringProperty();
            String sign = (attendanceBLL.hasAttended(currentAccount,l)) ? "✔" : "✖";
            sign = (SchemaBLL.isInFuture(l)) ? "" : sign;
            sign = (l.equals(currentLesson) && !attendanceBLL.hasAttended(currentAccount,l)) ? "⌛" : sign;
            return new SimpleStringProperty(l.getCourseName() + " " + sign + "\n" + sdf.format(l.getStartTime()) + " - " + sdf.format(l.getStopTime()));
        });
        schemaTuesday.setCellValueFactory(c -> {
            Lesson l = c.getValue().getTuesday();
            if(l == null || l.getCourseName() == "EMPTY") return new SimpleStringProperty();
            String sign = (attendanceBLL.hasAttended(currentAccount,l)) ? "✔" : "✖";
            sign = (SchemaBLL.isInFuture(l)) ? "" : sign;
            sign = (l.equals(currentLesson) && !attendanceBLL.hasAttended(currentAccount,l)) ? "⌛" : sign;
            return new SimpleStringProperty(l.getCourseName() + " " + sign + "\n" + sdf.format(l.getStartTime()) + " - " + sdf.format(l.getStopTime()));
        });
        schemaWednesday.setCellValueFactory(c -> {
            Lesson l = c.getValue().getWednesday();
            if(l == null || l.getCourseName() == "EMPTY") return new SimpleStringProperty();
            String sign = (attendanceBLL.hasAttended(currentAccount,l)) ? "✔" : "✖";
            sign = (SchemaBLL.isInFuture(l)) ? "" : sign;
            sign = (l.equals(currentLesson) && !attendanceBLL.hasAttended(currentAccount,l)) ? "⌛" : sign;
            return new SimpleStringProperty(l.getCourseName() + " " + sign + "\n" + sdf.format(l.getStartTime()) + " - " + sdf.format(l.getStopTime()));
        });
        schemaThursday.setCellValueFactory(c -> {
            Lesson l = c.getValue().getThursday();
            if(l == null || l.getCourseName() == "EMPTY") return new SimpleStringProperty();
            String sign = (attendanceBLL.hasAttended(currentAccount,l)) ? "✔" : "✖";
            sign = (SchemaBLL.isInFuture(l)) ? "" : sign;
            sign = (l.equals(currentLesson) && !attendanceBLL.hasAttended(currentAccount,l)) ? "⌛" : sign;
            return new SimpleStringProperty(l.getCourseName() + " " + sign + "\n" + sdf.format(l.getStartTime()) + " - " + sdf.format(l.getStopTime()));
        });
        schemaFriday.setCellValueFactory(c -> {
            Lesson l = c.getValue().getFriday();
            if(l == null || l.getCourseName() == "EMPTY") return new SimpleStringProperty();
            String sign = (attendanceBLL.hasAttended(currentAccount,l)) ? "✔" : "✖";
            sign = (SchemaBLL.isInFuture(l)) ? "" : sign;
            sign = (l.equals(currentLesson) && !attendanceBLL.hasAttended(currentAccount,l)) ? "⌛" : sign;
            return new SimpleStringProperty(l.getCourseName() + " " + sign + "\n" + sdf.format(l.getStartTime()) + " - " + sdf.format(l.getStopTime()));
        });

        schemaMonday.prefWidthProperty().bind(studentSchema.widthProperty().multiply(0.19));
        schemaTuesday.prefWidthProperty().bind(studentSchema.widthProperty().multiply(0.19));
        schemaWednesday.prefWidthProperty().bind(studentSchema.widthProperty().multiply(0.19));
        schemaThursday.prefWidthProperty().bind(studentSchema.widthProperty().multiply(0.19));
        schemaFriday.prefWidthProperty().bind(studentSchema.widthProperty().multiply(0.19));

        for(Object r : studentSchema.getItems()) {
            int i = 1;
            for (Object c : studentSchema.getColumns()) {
                TableColumn column = (TableColumn) c;

                if(i == LocalDate.now().getDayOfWeek().getValue())
                    column.setStyle("-fx-background-color:#e6e6fa;");
                i++;
            }
        }
    }

    public void registerAttendance(ActionEvent actionEvent) {
        if(currentLesson == null)
            UserAlert.showAlert("Åh nej!","Du har ingen lektioner lige nu, og du kan derfor ikke registrere din tilstedeværelse!", Alert.AlertType.WARNING);
        else {
            if(attendanceBLL.hasAttended(currentAccount,currentLesson)) {
                UserAlert.showAlert("Du er her allerede!","Du har allerede registreret din tilstedeværelse for denne lektion!", Alert.AlertType.INFORMATION);
                return;
            }
            try {
                attendanceBLL.setAttended(currentAccount, currentLesson);
                setItemsInSchema();
                UserAlert.showAlert("Registreret!","Din registrering blev gemt!", Alert.AlertType.CONFIRMATION);
            } catch (SQLException e) {
                UserAlert.showDatabaseError();
            }
        }
    }
}
