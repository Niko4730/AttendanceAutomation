package GUI.Dashboard;

import BE.Account;
import BE.UserType;
import BLL.AccountBLL;
import BLL.AttendanceBLL;
import BLL.SchemaBLL;
import GUI.Dashboard.Interfaces.ISideMenu;
import GUI.Dashboard.Interfaces.ISubPage;
import UTIL.UserAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.SQLException;

public class Controller {
    private AccountBLL accountBLL;
    private SchemaBLL schemaBLL;
    private AttendanceBLL attendanceBLL;

    public Label accountName;
    private Account loggedInUser;
    private Controller currentController;

    @FXML
    private AnchorPane dRoot;

    @FXML
    private BorderPane contentBorderPane;

    public void exit(){
        System.exit(0);
    }

    public void minimize(MouseEvent mouseEvent) {
        Stage stage = (Stage) dRoot.getScene().getWindow();
        stage.setIconified(true);
    }

    public void maximize(MouseEvent mouseEvent) {

    }

    public void logout(ActionEvent actionEvent) throws IOException {
        Stage root1 = (Stage) dRoot.getScene().getWindow();

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../Login/View.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();

        root1.close();
    }

    public void setAccount(Account loggedInUser) {
        try {
            this.attendanceBLL = new AttendanceBLL(loggedInUser);
            this.accountBLL = new AccountBLL();
        } catch (SQLException e) {
            UserAlert.showDatabaseError();
        }
        this.loggedInUser = loggedInUser;
        try {
            schemaBLL = new SchemaBLL(loggedInUser);
        } catch(SQLException e) {
            UserAlert.showDatabaseError();
        }
        String name = loggedInUser.getFirstName() + " " + loggedInUser.getLastName();

        switch(loggedInUser.getType()) {
            case STUDENT:
                setMenu(UserType.STUDENT,name);
                setPage("StudentSchema");
                break;
            case TEACHER:
                setMenu(UserType.TEACHER,name);
                setPage("TeacherFrontPage");
                break;
        }
    }

    public void setPage(String page) {
        String path = "./" + page + "/View.fxml";
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(path));
            Parent p = loader.load();

            contentBorderPane.setCenter(p);

            ISubPage controller = loader.getController();
            controller.setAttendanceBLL(attendanceBLL);
            controller.setAccountBLL(accountBLL);
            controller.setCurrentAccount(loggedInUser);
            controller.setSchemaBLL(schemaBLL);
            controller.setMainController(this);
            controller.load();
        } catch(IOException e) {
            UserAlert.showDatabaseError();
        }
    }

    public void setPage(String page, Account a) {
        String path = "./" + page + "/View.fxml";
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(path));
            Parent p = loader.load();

            contentBorderPane.setCenter(p);

            try {
                ISubPage controller = loader.getController();
                controller.setAttendanceBLL(new AttendanceBLL(a));
                controller.setAccountBLL(new AccountBLL());
                controller.setCurrentAccount(a);
                controller.setSchemaBLL(new SchemaBLL(a));
                controller.setMainController(this);
                controller.load();
            } catch(SQLException e) {
                UserAlert.showDatabaseError();
            }
        } catch(IOException e) {
            UserAlert.showDatabaseError();
        }
    }

    private void setMenu(UserType type, String name) {
        String path = "./" + ((type == UserType.STUDENT) ? "StudentMenu" : "TeacherMenu") + "/Menu.fxml";
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(path));
            Parent p = loader.load();

            contentBorderPane.setLeft(p);

            ISideMenu controller = loader.getController();
            controller.setName(name);
            controller.setController(this);
        } catch (IOException e) {
            UserAlert.showDatabaseError();
        }
    }
}
